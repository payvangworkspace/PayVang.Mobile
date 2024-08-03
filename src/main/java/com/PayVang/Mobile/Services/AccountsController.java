package com.PayVang.Mobile.Services;

import com.PayVang.Mobile.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.PayVang.Mobile.Domain.AccountsService;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @PostMapping("/login")
    public EncryptedKeyGenericResponse login(@RequestBody LoginRequest loginRequest) {
        return accountsService.authenticateUser(loginRequest);
    }

    @PostMapping("/verifylogin")
    public VerifyLoginResponse login(@RequestBody VerifyLoginRequest verifyLoginRequest) {
        return accountsService.verifyLogin(verifyLoginRequest);
    }

    @PostMapping("/forgotpassword")
    public MessageResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return accountsService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/verifyforgotpassword")
    public EncryptedKeyGenericResponse
    verifyForgotPassword(@RequestBody VerifyForgotPasswordRequest verifyForgotPasswordRequest)
    {
        return accountsService.verifyForgotPasswordLink(verifyForgotPasswordRequest.encryptedUsername, verifyForgotPasswordRequest.expiryMinutes);
    }

    @PostMapping("/changepassword")
    public String forgotPassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return accountsService.changePassword(changePasswordRequest);
    }
}