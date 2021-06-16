package com.projteam.competico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService
{
	private JavaMailSender sender;
	
	@Autowired
	public EmailService(JavaMailSender jms)
	{
		sender = jms;
	}
	
	public void sendEmail(String emailAddress, String subject, String message) throws MailException
	{
		SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(emailAddress);
        email.setSubject(subject);
        email.setText(message);
        sender.send(email);
        log.debug("Sent \"" + subject + "\" to " + emailAddress);
	}
}
