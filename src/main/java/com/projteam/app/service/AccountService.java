package com.projteam.app.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.AccountDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;

@Service
public class AccountService implements UserDetailsService
{
	private AccountDAO accDao;
	private PasswordEncoder passEnc;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	public AccountService(
			AccountDAO accDao,
			PasswordEncoder passEnc)
	{
		this.accDao = accDao;
		this.passEnc = passEnc;
	}
	
	public void register(HttpServletRequest req, RegistrationDTO regDto)
	{
		String passHash = passEnc.encode(regDto.getPassword());
		Account acc = new Account(regDto.getEmail(), regDto.getUsername(), passHash,
				List.of(regDto.isPlayer()?"PLAYER":"LECTURER"));
		acc = accDao.insertAccount(acc);
		authenticate(req, regDto.getEmail(), regDto.getPassword());
	}
	public boolean login(HttpServletRequest req, LoginDTO loginDto)
	{
		Account acc = accDao.selectAccount(loginDto.getEmail());
		if (acc == null)
			return false;
		if (passEnc.matches(loginDto.getPassword(), acc.getPassword()))
		{
			authenticate(req, loginDto.getEmail(), loginDto.getPassword());
			return true;
		}
		else
			return false;
	}
	
	private void authenticate(HttpServletRequest req, String email, CharSequence password)
	{
		UsernamePasswordAuthenticationToken authReq
			= new UsernamePasswordAuthenticationToken(email, password);
	    Authentication auth = authManager.authenticate(authReq);
	    
	    SecurityContext sc = SecurityContextHolder.getContext();
	    sc.setAuthentication(auth);
	    HttpSession session = req.getSession(true);
	    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
	}
	
	private Authentication getAuthentication()
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || (auth instanceof AnonymousAuthenticationToken) || !auth.isAuthenticated())
			return null;
		return auth;
	}
	public boolean isAuthenticated()
	{
		return getAuthentication() != null;
	}
	public Account getAuthenticatedAccount()
	{
		Authentication auth = getAuthentication();
		return (auth == null)?null:((Account) auth.getPrincipal());
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		Account acc = accDao.selectAccount(username);
		if (acc == null)
			throw new UsernameNotFoundException("Invalid email or password.");
		return acc;
	}
}
