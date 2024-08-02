package com.PayVang.Mobile.Domain;

import java.time.LocalDateTime;
import java.util.Random;

import com.PayVang.Mobile.Constants.ErrorConstants;
import com.PayVang.Mobile.Constants.SuccessMessage;
import com.PayVang.Mobile.CustomExceptions.InternalServerException;
import org.springframework.stereotype.Service;

import com.PayVang.Mobile.CustomExceptions.UnauthorizedException;
import com.PayVang.Mobile.DataAccess.Models.Otp;
import com.PayVang.Mobile.DataAccess.Repositories.OtpRepository;
import com.PayVang.Mobile.Models.EncryptedKeyGenericResponse;
import com.PayVang.Mobile.Models.TriggerOtpRequest;
import com.PayVang.Mobile.Models.ValidateOtpRequest;
import com.PayVang.Mobile.Util.AESEncryptUtility;

@Service
public class OtpService {
	
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    
    public OtpService(OtpRepository otpRepository, EmailService emailService)
    {
    	this.otpRepository = otpRepository;
    	this.emailService = emailService;
    }
    
    public EncryptedKeyGenericResponse triggerOtp(TriggerOtpRequest triggerOtpRequest) 
    {
    	try 
    	{
    	String otp = OtpService.generateOTP();
    	String recipient = triggerOtpRequest.getRecipient();
        // Check if OTP Details already exists
        Otp otpEntry = otpRepository.findByRecipient(recipient);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(3);

        if (otpEntry == null) {
            // Create new login detail entry
        	otpEntry = new Otp();
        	otpEntry.setRecipient(recipient);
        	otpEntry.setOtp(otp);
        	otpEntry.setExpiryTime(expiryTime);
         } else {
             // Update existing login detail entry
        	 otpEntry.setOtp(otp);
        	 otpEntry.setExpiryTime(expiryTime);
         }

         // Save login detail
        otpRepository.save(otpEntry);
         
         // send email for OTP, TODO: make template for it
         emailService.sendEmail(recipient, triggerOtpRequest.emailSubject, "Here is your OTP(One Time Password) for "
         		+ triggerOtpRequest.emailBodyCore + " " + otp);
         
         String encryptedRecipient = AESEncryptUtility.encrypt(recipient);
		 return new EncryptedKeyGenericResponse(encryptedRecipient, SuccessMessage.otpSentToEmail);
    	}
    	catch (Exception e) {
			throw e;
		}
    }
    
	public EncryptedKeyGenericResponse validateOtp(ValidateOtpRequest validateOtpRequest) 
	{
		try 
		{
			var encryptedRecipient = validateOtpRequest.getEncryptedRecipient();
			String decryptedRecipient = AESEncryptUtility.decrypt(encryptedRecipient);
   		 	String otp = validateOtpRequest.getOTP();
   		 
	   		 if(decryptedRecipient == null) {
	   			 throw new InternalServerException(ErrorConstants.invalidRecipient);
	   		 }
	   		 
	   		 var loginDetails = otpRepository.findByRecipient(decryptedRecipient);
	   		 
	   		 
	   		 if(loginDetails == null) {
	   			 throw new InternalServerException(ErrorConstants.retryTriggerOtp);
	   		 }
	   		 
	   		 
	   		 if(!otp.equals(loginDetails.getOtp()))
	   		 {
	   			 throw new UnauthorizedException(ErrorConstants.incorrectOtp);
	   		 }
	   		 
	   		 if(loginDetails.getExpiryTime().isBefore(LocalDateTime.now()))
	   		 {
	   			 throw new UnauthorizedException(ErrorConstants.otpHasExpired);
	   		 }

			return new EncryptedKeyGenericResponse(encryptedRecipient, SuccessMessage.otpValidated);
	   	}
	   	catch (Exception e) 
		{
	   		throw e;
		}
	}
    
	public static String generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otp = 10000 + random.nextInt(90000);
        return String.valueOf(otp);
    }
}
