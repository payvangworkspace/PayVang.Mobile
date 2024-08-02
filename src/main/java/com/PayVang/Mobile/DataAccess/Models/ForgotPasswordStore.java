package com.PayVang.Mobile.DataAccess.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "ForgotPasswordStore")
public class ForgotPasswordStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String encryptedUsername;
    private LocalDateTime triggeredTime;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedUsername() {
        return encryptedUsername;
    }

    public void setEncryptedUsername(String encryptedUsername) {
        this.encryptedUsername = encryptedUsername;
    }

    public LocalDateTime getTriggeredTime() {
        return triggeredTime;
    }

    public void setTriggeredTime(LocalDateTime triggeredTime) {
        this.triggeredTime = triggeredTime;
    }
}
