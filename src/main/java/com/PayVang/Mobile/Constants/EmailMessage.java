package com.PayVang.Mobile.Constants;

import com.PayVang.Mobile.Properties.ConfigurationManager;
import org.springframework.stereotype.Component;

@Component
public class EmailMessage {
    public EmailMessageStructure forgotPasswordRedirectionMessage(String expiryMinutes, String encryptedUsername) {
        String redirectUrlBase = ConfigurationManager.getProperty(PropertyConstants.forgotPasswordRedirectUrl);
        return new EmailMessageStructure("PayVang password reset", "Follow the link to change your PayVang mobile app password: " +
        String.format("%s?et=%s&eu=%s", redirectUrlBase, expiryMinutes, encryptedUsername));
    }

    public EmailMessageStructure loginOtpMessage() {
        return new EmailMessageStructure("OTP for login in PayV Mobile", "Here is your OTP(One Time Password) for logging into PayV Mobile - ");
    }

    public static class EmailMessageStructure {
        public EmailMessageStructure(String subject, String body) {
            this.subject = subject;
            this.body = body;
        }

        public String subject;
        public String body;
    }

}