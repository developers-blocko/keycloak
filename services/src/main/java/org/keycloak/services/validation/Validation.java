/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.services.validation;

import org.keycloak.authentication.requiredactions.util.UpdateProfileContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Validation {

    public static final String FIELD_PASSWORD_CONFIRM = "password-confirm";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_LAST_NAME = "lastName";
    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NAVER_ID_REQUIRED = "naverIdRequired";
    public static final String FIELD_REFERRAL_CODE = "referralCode";
    public static final String FIELD_REFERRED_BY_CODE = "referredByCode";
    public static final String FIELD_MOBILE_PHONE_NUMBER = "mobilePhoneNumber";
    public static final String FIELD_PROFILE_IMAGE_URL = "profileImageUrl";
    public static final String FIELD_PROFILE_THUMBNAIL_IMAGE_URL = "profileThumbnailImageUrl";
    public static final String FIELD_ENTER_REQUIRED = "enterRequired";
    public static final String FIELD_BIRTH_DATE = "birthDate";
    public static final String FIELD_LOCALE = "locale";
    public static final String FIELD_GENDER_CODE = "genderCode";
    public static final String FIELD_COMPANY = "company";
    public static final String FIELD_SERVICE_AGREEMENT = "serviceAgreement";
    public static final String FIELD_PRIVACY_AGREEMENT = "privacyAgreement";
    public static final String FIELD_MARKETING_AGREEMENT = "marketingAgreement";
    public static final String REGEX_NUMBER = "[0-9]";
    public static final String REGEX_LOWER_CASE_ALPHABET = "[a-z]+";
    public static final String REGEX_UPPER_CASE_ALPHABET = "[A-Z]+";
    public static final String REGEX_SPECIAL_CHARACTER = "[~!@#$%^&*()_+|<>?:{}]+";

    // Actually allow same emails like angular. See ValidationTest.testEmailValidation()
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*");
    private static final Pattern MOBILE_PHONE_NUMBER_PATTERN = Pattern.compile("^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$");

    public static void validateProfileForm(MultivaluedMap<String, String> formData, List<FormMessage> errors, boolean validatePassword) {
        String email = formData.getFirst(FIELD_EMAIL);
        String username = formData.getFirst(FIELD_USERNAME);
        String password = formData.getFirst(FIELD_PASSWORD);
        String name = formData.getFirst(FIELD_NAME);
        String mobilePhoneNumber = formData.getFirst(FIELD_MOBILE_PHONE_NUMBER);

//        if (isBlank(formData.getFirst(FIELD_FIRST_NAME))) {
//            addError(errors, FIELD_FIRST_NAME, Messages.MISSING_FIRST_NAME);
//        }

//        if (isBlank(formData.getFirst(FIELD_LAST_NAME))) {
//            addError(errors, FIELD_LAST_NAME, Messages.MISSING_LAST_NAME);
//        }

        if (isBlank(email)) {
            addError(errors, FIELD_EMAIL, Messages.MISSING_EMAIL);
        } else if (!isEmailValid(email)) {
            addError(errors, FIELD_EMAIL, Messages.INVALID_EMAIL);
        }

        if (isBlank(mobilePhoneNumber)) {
            addError(errors, FIELD_MOBILE_PHONE_NUMBER, Messages.MISSING_MOBILE_PHONE_NUMBER);
        } else if (!isMobilePhoneNumberValid(mobilePhoneNumber)) {
            addError(errors, FIELD_MOBILE_PHONE_NUMBER, Messages.INVALID_MOBILE_PHONE_NUMBER);
        }

        if (validatePassword) {
            if (isBlank(password)) {
                addError(errors, FIELD_PASSWORD, Messages.MISSING_PASSWORD);
            } else if (!isPasswordValid(password)) {
                addError(errors, FIELD_PASSWORD, Messages.INVALID_PASSWORD_STRENGTH);
            }
        }

        validateAgreementForm(formData, errors);

        if (isBlank(email) || (validatePassword && isBlank(password)) ||
            isBlank(name) || isBlank(mobilePhoneNumber)) {
            addError(errors, FIELD_ENTER_REQUIRED, Messages.MISSING_REQUIRED_FIELDS);
        }

    }

    public static void validateAgreementForm(MultivaluedMap<String, String> formData, List<FormMessage> errors) {
        String serviceAgreementString = formData.getFirst(FIELD_SERVICE_AGREEMENT);
        String privacyAgreementString = formData.getFirst(FIELD_PRIVACY_AGREEMENT);
        Boolean serviceAgreement = null;
        Boolean privacyAgreement = null;
        try {
            serviceAgreement = Boolean.parseBoolean(serviceAgreementString);
            privacyAgreement = Boolean.parseBoolean(privacyAgreementString);
        } catch (Exception e){
            serviceAgreement = null;
            privacyAgreement = null;
        }
        if (isBlank(serviceAgreementString) || !Boolean.TRUE.equals(serviceAgreement)) {
            addError(errors, FIELD_SERVICE_AGREEMENT, Messages.MISSING_SERVICE_AGREEMENT);
        }
        if (isBlank(privacyAgreementString) || !Boolean.TRUE.equals(privacyAgreement)) {
            addError(errors, FIELD_PRIVACY_AGREEMENT, Messages.MISSING_PRIVACY_AGREEMENT);
        }
    }

    public static List<FormMessage> validateRegistrationForm(KeycloakSession session, RealmModel realm, MultivaluedMap<String, String> formData, List<String> requiredCredentialTypes, PasswordPolicy policy) {
        List<FormMessage> errors = new ArrayList<>();

        if (!realm.isRegistrationEmailAsUsername() && isBlank(formData.getFirst(FIELD_USERNAME))) {
            addError(errors, FIELD_USERNAME, Messages.MISSING_USERNAME);
        }

        validateProfileForm(formData, errors, true);

        if (requiredCredentialTypes.contains(CredentialRepresentation.PASSWORD)) {
            if (isBlank(formData.getFirst(FIELD_PASSWORD))) {
                addError(errors, FIELD_PASSWORD, Messages.MISSING_PASSWORD);
            } else if (!formData.getFirst(FIELD_PASSWORD).equals(formData.getFirst(FIELD_PASSWORD_CONFIRM))) {
                addError(errors, FIELD_PASSWORD_CONFIRM, Messages.INVALID_PASSWORD_CONFIRM);
            }
        }

        if (formData.getFirst(FIELD_PASSWORD) != null) {
            PolicyError err = session.getProvider(PasswordPolicyManagerProvider.class).validate(realm.isRegistrationEmailAsUsername() ? formData.getFirst(FIELD_EMAIL) : formData.getFirst(FIELD_USERNAME), formData.getFirst(FIELD_PASSWORD));
            if (err != null)
                errors.add(new FormMessage(FIELD_PASSWORD, err.getMessage(), err.getParameters()));
        }

        return errors;
    }

    private static void addError(List<FormMessage> errors, String field, String message) {
        errors.add(new FormMessage(field, message));
    }

    public static List<FormMessage> validateUpdateProfileForm(RealmModel realm, MultivaluedMap<String, String> formData) {
        List<FormMessage> errors = new ArrayList<>();

        if (!realm.isRegistrationEmailAsUsername() && realm.isEditUsernameAllowed() && isBlank(formData.getFirst(FIELD_USERNAME))) {
            addError(errors, FIELD_USERNAME, Messages.MISSING_USERNAME);
        }
        validateProfileForm(formData, errors, false);

        return errors;
    }

    /**
     * Validate if user object contains all mandatory fields.
     *
     * @param realm user is for
     * @param user  to validate
     * @return true if user object contains all mandatory values, false if some mandatory value is missing
     */
    public static boolean validateUserMandatoryFields(RealmModel realm, UpdateProfileContext user) {
        List<String> mobilePhoneNumberValueList = user.getAttribute(Validation.FIELD_MOBILE_PHONE_NUMBER);
        List<String> serviceAgreementValueList = user.getAttribute(Validation.FIELD_SERVICE_AGREEMENT);
        List<String> privacyAgreementValueList = user.getAttribute(Validation.FIELD_PRIVACY_AGREEMENT);
        String mobilePhoneNumber = null;
        if (mobilePhoneNumberValueList != null && mobilePhoneNumberValueList.size() > 0 &&
            mobilePhoneNumberValueList.get(0) != null && !mobilePhoneNumberValueList.get(0).isEmpty()) {
            mobilePhoneNumber = mobilePhoneNumberValueList.get(0);
        }
        boolean isServiceAgreement = false;
        if (serviceAgreementValueList != null && serviceAgreementValueList.size() > 0 &&
            serviceAgreementValueList.get(0) != null && !serviceAgreementValueList.get(0).isEmpty()) {
            try {
                isServiceAgreement = Boolean.parseBoolean(serviceAgreementValueList.get(0));
            } catch (Exception e){
                isServiceAgreement = false;
            }
        }
        boolean isPrivacyAgreement = false;
        if (privacyAgreementValueList != null && privacyAgreementValueList.size() > 0 &&
            privacyAgreementValueList.get(0) != null && !privacyAgreementValueList.get(0).isEmpty()) {
            try {
                isPrivacyAgreement = Boolean.parseBoolean(privacyAgreementValueList.get(0));
            } catch (Exception e){
                isPrivacyAgreement = false;
            }
        }
        return !(isBlank(user.getEmail()) || isBlank(user.getName()) || isBlank(mobilePhoneNumber) ||
            !isMobilePhoneNumberValid(mobilePhoneNumber) ||
            !isServiceAgreement || !isPrivacyAgreement);
    }

    /**
     * Check if string is empty (null or lenght is 0)
     *
     * @param s to check
     * @return true if string is empty
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Check if string is blank (null or lenght is 0 or contains only white characters)
     *
     * @param s to check
     * @return true if string is blank
     */
    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isMobilePhoneNumberValid(String mobilePhoneNumber) {
        return MOBILE_PHONE_NUMBER_PATTERN.matcher(mobilePhoneNumber).matches();
    }

    public static boolean isPasswordValid(String password){
        boolean numberExists = false;
        boolean lowerCaseAlphabetExists = false;
        boolean upperCaseAlphabetExists = false;
        boolean specialCharacterExists = false;
        boolean lengthGreaterThanSeven = false;
        int validCount = 0;
        boolean returnValue = false;
        if (password != null && password.trim().length() > 0) {
            password = password.trim();
            if (Pattern.compile(REGEX_NUMBER).matcher(password).find()) {
                numberExists = true;
                validCount++;
            }
            if (Pattern.compile(REGEX_LOWER_CASE_ALPHABET).matcher(password).find()) {
                lowerCaseAlphabetExists = true;
                validCount++;
            }
            if (Pattern.compile(REGEX_UPPER_CASE_ALPHABET).matcher(password).find()) {
                upperCaseAlphabetExists = true;
                validCount++;
            }
            if (Pattern.compile(REGEX_SPECIAL_CHARACTER).matcher(password).find()) {
                specialCharacterExists = true;
                validCount++;
            }
            if (password.length() >= 8) {
                lengthGreaterThanSeven = true;
            } else {
                validCount = 0;
            }
            if (validCount >= 3 && lengthGreaterThanSeven == true) {
                // strong
                returnValue = true;
            } else if (validCount == 2 && lengthGreaterThanSeven == true) {
                // normal
                returnValue = true;
            } else {
                // weaka
                returnValue = false;
            }
        }
        return returnValue;
    }
}
