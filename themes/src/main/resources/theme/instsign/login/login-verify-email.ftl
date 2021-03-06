<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>
  <div class="instsign-content instsign-content-verify-email">
    <div class="instsign-content-title">
        <span>${msg("verify-email-content-title")}</span>
    </div>
    <div class="instsign-content-subtitle">
        ${msg("verify-email-content-subtitle", '${email}')?no_esc}
    </div>
<#--    <div class="instsign-content-email-address">-->
<#--      <span>${Session["authenticatedUser"]["email"]}</span>-->
<#--      <span>${account.email}</span>-->
<#--    </div>-->
    <div id="verify-email-button" class="instsign-button" onclick="goToUrl('${url.loginAction}')">
      <span>${msg("verify-email-link")}</span>
    </div>
  </div>
</@layout.registrationLayout>