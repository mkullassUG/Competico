package com.projteam.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.AccountDAO;
import com.projteam.app.domain.Account;

@Service
public class AccountService
{
	private AccountDAO accDao;
	private PasswordEncoder passEnc;
	
	@Autowired
	public AccountService(
			@Qualifier("LocalAccountDAO") AccountDAO accDao,
			@Qualifier("bCryptPasswordEncoder") PasswordEncoder passEnc)
	{
		this.accDao = accDao;
		this.passEnc = passEnc;
	}
	
	public void register(String email, String username, String password)
	{
		String passHash = passEnc.encode(password);
		Account acc = new Account(email, username, passHash);
		acc = accDao.insertAccount(acc);
		authenticate(acc);
	}
	public boolean login(String email, String password)
	{
		Account acc = accDao.selectAccount(email);
		if (acc == null)
			return false;
		if (passEnc.matches(password, acc.getPasswordHash()))
		{
			authenticate(acc);
			return true;
		}
		else
			return false;
	}
	public void logout()
	{
		deauthenticate();
	}
	
	private void authenticate(Account acc)
	{
		//SessionRegistry
//		sessionRegistry.registerNewSession(acc.getId().toString(), acc);
//		
//		System.out.println("New session registered.");
//		System.out.println("Current sessions:");
//		sessionRegistry.getAllPrincipals()
//			.stream()
//			.flatMap(prin -> sessionRegistry.getAllSessions(prin, true).stream())
//			.forEach(sess -> System.out.println(
//					"[Session: " + sess.getSessionId()
//					+ ", " + sess.getPrincipal()
//					+ ", " + sess.getLastRequest()
//					+ "]"));
		
		//TODO implement
	}
	private void deauthenticate()
	{
		//TODO implement
	}
}
