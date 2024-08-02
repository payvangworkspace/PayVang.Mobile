package com.PayVang.Mobile.Domain;
import com.PayVang.Mobile.Constants.PropertyConstants;
import com.PayVang.Mobile.Properties.ConfigurationManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender)
	{
		this.mailSender = mailSender;
	}

	@Async
	public void sendEmail(String receiver, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(ConfigurationManager.getProperty(PropertyConstants.senderEmailAddress));
		message.setTo(receiver);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
	}
}