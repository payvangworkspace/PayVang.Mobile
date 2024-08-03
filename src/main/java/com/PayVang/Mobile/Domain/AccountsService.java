package com.PayVang.Mobile.Domain;

import java.time.LocalDateTime;
import java.util.Collections;

import com.PayVang.Mobile.Constants.EmailMessage;
import com.PayVang.Mobile.Constants.ErrorConstants;
import com.PayVang.Mobile.Constants.SuccessMessage;
import com.PayVang.Mobile.CustomExceptions.InternalServerException;
import com.PayVang.Mobile.DataAccess.Models.ForgotPasswordStore;
import com.PayVang.Mobile.DataAccess.Repositories.ForgotPasswordStoreRepository;
import com.PayVang.Mobile.Models.*;
import com.PayVang.Mobile.Util.JwtUtil;
import org.springframework.stereotype.Service;

import com.PayVang.Mobile.CustomExceptions.InvalidRequestException;
import com.PayVang.Mobile.CustomExceptions.UnauthorizedException;
import com.PayVang.Mobile.DataAccess.Models.LoginDetails;
import com.PayVang.Mobile.DataAccess.Repositories.LoginDetailRepository;
import com.PayVang.Mobile.DataAccess.Repositories.UserRepository;
import com.PayVang.Mobile.Util.AESEncryptUtility;
import com.PayVang.Mobile.Util.PasswordHasher;
import com.PayVang.Mobile.Util.UserStatusType;

// security
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class AccountsService implements UserDetailsService {
	
    private final EmailService emailService;
    private final LoginDetailRepository loginDetailRepository;
    private final UserRepository userRepository;
	private final ForgotPasswordStoreRepository forgotPasswordStoreRepository;
    private final EmailMessage emailMessage;
	private final JwtUtil jwtUtil;

    public AccountsService(EmailService emailService, LoginDetailRepository loginDetailRepository,
						   UserRepository userRepository,
						   EmailMessage emailMessage,
						   ForgotPasswordStoreRepository forgotPasswordStoreRepository,
						   JwtUtil jwtUtil)
    {
        this.emailService = emailService;
        this.loginDetailRepository = loginDetailRepository;
        this.userRepository = userRepository;
		this.emailMessage = emailMessage;
		this.forgotPasswordStoreRepository = forgotPasswordStoreRepository;
		this.jwtUtil = jwtUtil;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Fetch user from the database using the UserRepository
		var user = userRepository.findByEmailId(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		// Override - Return a UserDetails object containing the user's information without authorities
		return new User(user.getEmailId(), user.getPassword(), Collections.emptyList());
	}

    public EncryptedKeyGenericResponse authenticateUser(LoginRequest loginRequest) {
    	
    	if(loginRequest.getUsername() == null || loginRequest.getPassword() == null)
    	{
    		throw new InvalidRequestException(ErrorConstants.usernamePasswordNotFound);
    	}
    	
    	var username = loginRequest.getUsername();
    	var password = loginRequest.getPassword();
    	
    	var user = userRepository.findByEmailId(username);
    	if (user == null)
    	{
    		throw new UnauthorizedException(ErrorConstants.usernameInvalid);
    	}
    	
    	String userStatus = user.getUserStatus().getStatus();
    	String activeStatus = UserStatusType.ACTIVE.getStatus();
    	
		if (!userStatus.equals(activeStatus))
		{
			throw new UnauthorizedException(ErrorConstants.userInactive);
		}
		
    	// write password extract logic
    	var userDBPassword = user.getPassword();
    	password = PasswordHasher.hashPassword(password,user.getAppId());
        if (password.equals(userDBPassword)) {
            // Generate OTP
            String otp = OtpService.generateOTP();
            // Check if login detail already exists
            LoginDetails loginDetail = loginDetailRepository.findByUsername(username);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiryTime = now.plusMinutes(3); // Time set to 3 minutes

            if (loginDetail == null) {
                // Create new login detail entry
                loginDetail = new LoginDetails();
                loginDetail.setUsername(username);
                loginDetail.setOtp(otp);
                loginDetail.setExpiryTime(expiryTime);
            } else {
                // Update existing login detail entry
                loginDetail.setOtp(otp);
                loginDetail.setExpiryTime(expiryTime);
            }

            // Save login detail
            loginDetailRepository.save(loginDetail);
            
            // send email for OTP, TODO: make template for it

			var emailMessage = this.emailMessage.loginOtpMessage();
            emailService.sendEmail(username, emailMessage.subject, emailMessage.body + otp);
            
            String encryptedUsername = AESEncryptUtility.encrypt(username);
			return new EncryptedKeyGenericResponse(encryptedUsername, SuccessMessage.otpSentToEmail);
        }
        else {
        	throw new UnauthorizedException(ErrorConstants.incorrectPassword);
		}
    }
    
    public VerifyLoginResponse verifyLogin(VerifyLoginRequest verifyLoginRequest) {
    	if(verifyLoginRequest.getEncryptedKey() == null || verifyLoginRequest.getOTP() == null)
    	{
    		throw new InvalidRequestException(ErrorConstants.otpEncryptedKeyNotFound);
    	}
    	
    	try {
    		 String decryptedUsername = AESEncryptUtility.decrypt(verifyLoginRequest.getEncryptedKey());
    		 String otp = verifyLoginRequest.getOTP();
    		 
    		 if(decryptedUsername == null) {
    			 throw new UnauthorizedException(ErrorConstants.incorrectEncryptedUsername);
    		 }
    		 
    		 var loginDetails = loginDetailRepository.findByUsername(decryptedUsername);
    		 
    		 
    		 if(loginDetails == null) {
    			 throw new UnauthorizedException(ErrorConstants.userDoesNotExists);
    		 }
    		 
    		 
    		 if(!otp.equals(loginDetails.getOtp()))
    		 {
    			 throw new UnauthorizedException(ErrorConstants.incorrectOtp);
    		 }
    		 
    		 if(loginDetails.getExpiryTime().isBefore(LocalDateTime.now()))
    		 {
    			 throw new UnauthorizedException(ErrorConstants.otpHasExpired);
    		 }

			 var accessToken = jwtUtil.generateToken(decryptedUsername);
    		 return new VerifyLoginResponse(accessToken,SuccessMessage.loginSuccess);
    	}
    	catch (Exception e) {
    		throw e;
		}
    }
    
    public MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    	var username = forgotPasswordRequest.getUsername();
    	
    	var user = userRepository.findByEmailId(username);
    	if(user == null)
    	{
    		throw new InternalServerException(ErrorConstants.usernameInvalid);
    	}
		var encryptedUsername = AESEncryptUtility.encrypt(username);
		LocalDateTime triggeredTime = LocalDateTime.now();

		ForgotPasswordStore forgotPasswordEntry = forgotPasswordStoreRepository.findByEncryptedUsername(encryptedUsername);

		if (forgotPasswordEntry == null) {
			// Create new login detail entry
			forgotPasswordEntry = new ForgotPasswordStore();
			forgotPasswordEntry.setEncryptedUsername(encryptedUsername);
			forgotPasswordEntry.setTriggeredTime(triggeredTime);
		} else {
			// Update existing login detail entry
			forgotPasswordEntry.setEncryptedUsername(encryptedUsername);
			forgotPasswordEntry.setTriggeredTime(triggeredTime);
		}

		// Save login detail
		forgotPasswordStoreRepository.save(forgotPasswordEntry);

		var emailMessage = this.emailMessage.forgotPasswordRedirectionMessage("3",encryptedUsername);
    	emailService.sendEmail(user.getEmailId(), emailMessage.subject, emailMessage.body);
    	
    	return new MessageResponse(SuccessMessage.passwordUpdateLinkSent);
    }

	public EncryptedKeyGenericResponse verifyForgotPasswordLink(String encryptedUsername, int expiryMinutes)
	{
		if(expiryMinutes != 3)
		{
			throw new InternalServerException(ErrorConstants.defaultForgotPasswordExpiryAltered);
		}
		try {
			var forgotPasswordEntry = forgotPasswordStoreRepository.findByEncryptedUsername(encryptedUsername);
			if(forgotPasswordEntry == null)
			{
				throw new InternalServerException(ErrorConstants.forgotPasswordEntryNotFound);
			}
			LocalDateTime expiryTime = forgotPasswordEntry.getTriggeredTime().plusMinutes(expiryMinutes);
			if (expiryTime.isBefore(LocalDateTime.now()))
			{
				throw new InternalServerException(ErrorConstants.forgotPasswordLinkExpired);
			}
			return new EncryptedKeyGenericResponse(encryptedUsername, SuccessMessage.linkValidated);
		}
		catch (Exception e)
		{
			throw e;
		}
	}

    public String changePassword(ChangePasswordRequest changePasswordRequest) {
    	try {
	    	var encryptedUserName = changePasswordRequest.encryptedUserName;
	    	var username = AESEncryptUtility.decrypt(encryptedUserName);
	    	
	    	var user = userRepository.findByEmailId(username);
	    	if(user == null)
	    	{
	    		throw new InternalServerException(ErrorConstants.incorrectEncryptedUsername);
	    	}
	    	
	    	String newPassword = PasswordHasher.hashPassword(changePasswordRequest.getNewPassword(),user.getAppId());
	    	userRepository.updatePasswordByEmail(username, newPassword);
	    	return SuccessMessage.passwordResetSuccessful;
    	}
    	catch (Exception e) {
			throw e;
		}
    }
    
}
