package com.PayVang.Mobile.DataAccess.Repositories;

import com.PayVang.Mobile.DataAccess.Models.ForgotPasswordStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordStoreRepository extends JpaRepository<ForgotPasswordStore, Long> {
    ForgotPasswordStore findByEncryptedUsername(String encryptedUsername);
}
