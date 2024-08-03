package com.PayVang.Mobile.Models;

public class VerifyLoginResponse {
    public String accessToken;
    public String message;
    public VerifyLoginResponse(String accessToken, String message){
        this.accessToken = accessToken;
        this.message = message;
    }
}
