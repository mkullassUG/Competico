package com.projteam.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTests
{
	private @Mock JavaMailSender mailSender;
	
	private @InjectMocks EmailService emailService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads()
	{
		assertNotNull(emailService);
	}

	@Test
	public void canSendEmail()
	{
		String address = "testmail@mock.com";
		String subject = "Test subject";
		String message = "Test message";
		
		emailService.sendEmail(address, subject, message);
		
		verify(mailSender, times(1)).send((SimpleMailMessage) any());
	}
}
