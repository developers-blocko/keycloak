<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
  <form id="kc-register-form" class="instsign-form" action="${url.registrationAction}"
        method="post">
    <script>

      function onLoadFunction() {
        console.log('onLoadFunction');
        isAgreementRequired(false);
        validateAllFields();
      }

      window.addEventListener('load', onLoadFunction);
    </script>
    <div class="instsign-content instsign-content-register">
      <div class="instsign-content-title">
          ${msg("register-content-title")}
      </div>
      <br/>
      <div class="instsign-content-input-group margin-top-30">
        <div class="instsign-content-input-label">
          <span>${msg("register-content-email")}</span>
        </div>
        <div class="instsign-content-input-text">
          <input id="email" name="email" type="text"
                 autocomplete="email" onchange="isRequiredFieldValueEntered(this, true)"
                 value="${(register.formData.email!'')}"
                 maxlength="100"
                 placeholder="${msg("instsign-content-email-placeholder")}"/>
        </div>
      </div>
      <div class="instsign-content-input-group margin-top-20">
        <div class="instsign-content-input-label">
          <span>${msg("register-content-password")}</span>
          <span
              class="register-content-label-password-description">${msg("register-content-password-description")}</span>
        </div>
        <div class="instsign-content-input-text">
          <input id="password" name="password" type="password" autocomplete="new-password"
                 maxlength="12"
                 placeholder="${msg("instsign-content-password-placeholder")}"
                 onchange="validatePassword(this)"/>
          <input id="password-confirm" name="password-confirm" type="hidden"/>
          <div id="instsign-password-strength-group"
               class="instsign-password-strength instsign-content-hide">
            <div class="instsign-password-strength-text">
              <span>${msg("instsign-password-strength-text")} :</span>
            </div>
            <div class="instsign-password-strength-value">
              <span id="instsign-password-strength-value-span"></span>
            </div>
          </div>
        </div>
      </div>
      <div class="instsign-content-input-group margin-top-20">
        <div class="instsign-content-input-label">
          <span>${msg("register-content-name")}</span>
        </div>
        <div class="instsign-content-input-text">
          <input id="name" name="name" type="text"
                 onchange="isRequiredFieldValueEntered(this, true)"
                 value="${(register.formData.name!'')}"
                 maxlength="50"
                 placeholder="${msg("instsign-content-name-placeholder")}"/>
        </div>
      </div>
      <div class="instsign-content-input-group margin-top-20">
        <div class="instsign-content-input-label">
          <span>${msg("register-content-mobile-phone-number")}</span>
        </div>
        <div class="instsign-content-input-text">
          <input id="mobilePhoneNumber" name="mobilePhoneNumber" type="text"
                 autocomplete="mobilePhoneNumber"
                 onchange="isRequiredFieldValueEntered(this, true)"
                 value="${(register.formData.mobilePhoneNumber!'')}"
                 maxlength="20"
                 placeholder="${msg("instsign-content-mobile-phone-number-placeholder")}"/>
        </div>
      </div>
      <div class="instsign-content-input-group margin-top-20">
        <div class="instsign-content-input-label">
          <span>${msg("register-content-company")}</span>
        </div>
        <div class="instsign-content-input-text">
          <input id="company" name="company" type="text"
                 autocomplete="off"
                 maxlength="100"
                 value="${(register.formData.company!'')}"
                 placeholder="${msg("instsign-content-company-placeholder")}"/>
        </div>
      </div>
      <div class="instsign-content-input-group margin-top-20">
        <div class="instsign-content-input-label">
          <span>${msg("register-content-referral-code")}</span>
        </div>
        <div class="instsign-content-input-text">
            <#if register.formData.referredByCode?? && register.formData.referredByCode?has_content >
              <input id="referredByCode" name="referredByCode1" type="text"
                     disabled  class="instsign-content-disabled"
                     value="${(register.formData.referredByCode!'')}"/>
              <input name="referredByCode" type="hidden"
                     value="${(register.formData.referredByCode!'')}"/>
            <#else>
              <input id="referredByCode" name="referredByCode" type="text"
                     autocomplete="off" maxlength="6"
                     value="${(register.formData.referredByCode!'')}"/>
            </#if>
        </div>
      </div>
      <div id="register-agreement-section" class="register-agreement">
        <div class="register-agreement-select">
          <div class="register-agreement-checkbox" onclick="onAllAgreementClick()">
            <div id="all-agreement-checkbox"
                 class="all-agreement-checkbox-checked-svg instsign-content-hide"></div>
          </div>
          <input id="allAgreement" name="allAgreement" type="hidden"/>
          <input id="all-agreement-dropdown" name="allAgreementDropdown" type="hidden"/>
          <div class="register-agreement-text">
            <span>${msg("register-content-all-agreement")}</span>
          </div>
          <div id="div-all-agreement-dropdown" class="all-agreement-dropdown dropdown-default"
               onclick="onAllAgreementDropdownClick(event)">
          </div>
        </div>
        <div id="register-agreement-option-section"
             class="register-agreement-select-options instsign-content-hide">
          <div class="register-agreement-select-section-row">
            <div class="register-agreement-checkbox-small"
                 onclick="onServiceAgreementClick(undefined, true, true)">
              <div id="service-agreement-checkbox"
                   class="small-agreement-checkbox-checked-svg instsign-content-hide"></div>
            </div>
            <input id="serviceAgreement" name="serviceAgreement" type="hidden">
            <div class="register-agreement-text-small"
                 onclick="openNewWindows('https://www.instsign.com/service/', 800, 800)">
              <span>${msg("register-content-service-agreement")}</span>
            </div>
          </div>
          <div class="register-agreement-select-section-row">
            <div class="register-agreement-checkbox-small"
                 onclick="onPrivacyAgreementClick(undefined, true, true)">
              <div id="privacy-agreement-checkbox"
                   class="small-agreement-checkbox-checked-svg instsign-content-hide"></div>
            </div>
            <input id="privacyAgreement" name="privacyAgreement" type="hidden">
            <div class="register-agreement-text-small"
                 onclick="openNewWindows('https://www.instsign.com/privacy/', 800, 800)">
              <span>${msg("register-content-privacy-agreement")}</span>
            </div>
          </div>
          <div class="register-agreement-select-section-row">
            <div class="register-agreement-checkbox-small"
                 onclick="onMarketingAgreementClick(undefined, true, true)">
              <div id="marketing-agreement-checkbox"
                   class="small-agreement-checkbox-checked-svg instsign-content-hide"></div>
            </div>
            <input id="marketingAgreement" name="marketingAgreement" type="hidden">
            <div class="register-agreement-text-small"
                 onclick="openNewWindows('https://www.instsign.com/agreement-marketing/', 800, 800)">
              <span>${msg("register-content-marketing-agreement")}</span>
            </div>
          </div>
        </div>
        <div id="register-validation-accept-agreement"
             class="register-validation-agreement instsign-content-hide">
          <span>${msg("instsign-validation-require-accept")}</span>
        </div>
      </div>
      <div id="register-button" class="instsign-button"
           onclick="registerSubmit('kc-register-form')">
        <span>${msg("register-button")}</span>
      </div>
      <div class="register-login-text">
        <span>${msg("register-login-msg")}</span>
      </div>
      <div class="register-login" onclick="goToUrl('${url.loginUrl}')">
        <span>${msg("register-login-link")}</span>
      </div>
      <div class="instsign-content-or margin-bottom-10">
        <div class="instsign-content-or-divider">
        </div>
        <div class="instsign-content-or-text">
          <span>or</span>
        </div>
        <div class="instsign-content-or-divider">
        </div>
      </div>
        <#if realm.password && social.providers??>
          <div class="instsign-login-social-link-group">
              <#list social.providers as p>
                  <#if p.alias == "google">
<#--                    <div class="instsign-login-social-link instsign-login-social-link-${p.alias}" onclick="goToUrl('${p.loginUrl}')">-->
                    <div class="instsign-login-social-link instsign-login-social-link-${p.alias}"
                         onclick="displayAlert('${msg("alert-preparing")}')">
                      <div class="instsign-login-social-link-img instsign-login-social-link-${p.alias}-svg">
                      </div>
                      <div class="instsign-login-social-link-text">
                        <span>${msg("login-social-${p.alias}")}</span>
                      </div>
                    </div>
                  </#if>
              </#list>
              <#list social.providers as p>
                  <#if p.alias == "kakao">
                    <div class="instsign-login-social-link instsign-login-social-link-${p.alias}"
                         onclick="goToUrl('${p.loginUrl}')">
                      <div class="instsign-login-social-link-img instsign-login-social-link-${p.alias}-svg">
                      </div>
                      <div class="instsign-login-social-link-text">
                        <span>${msg("login-social-${p.alias}")}</span>
                      </div>
                    </div>
                  </#if>
              </#list>
              <#list social.providers as p>
                  <#if p.alias == "naver">
                      <div class="instsign-login-social-link instsign-login-social-link-${p.alias}"
                           onclick="goToUrl('${p.loginUrl}')">
                      <div class="instsign-login-social-link-img instsign-login-social-link-${p.alias}-svg">
                      </div>
                      <div class="instsign-login-social-link-text">
                        <span>${msg("login-social-${p.alias}")}</span>
                      </div>
                    </div>
                  </#if>
              </#list>
              <#--                <#list social.providers as p>-->
              <#--                    <#if p.alias == "facebook">-->
              <#--                      <div class="instsign-login-social-link instsign-login-social-link-${p.alias}" onclick="goToUrl('${p.loginUrl}')">-->
              <#--                        <div class="instsign-login-social-link-img instsign-login-social-link-${p.alias}-svg">-->
              <#--                        </div>-->
              <#--                        <div class="instsign-login-social-link-text">-->
              <#--                          <span>${msg("login-social-${p.alias}")}</span>-->
              <#--                        </div>-->
              <#--                      </div>-->
              <#--                    </#if>-->
              <#--                </#list>-->
          </div>
        </#if>
    </div>
  </form>
</@layout.registrationLayout>