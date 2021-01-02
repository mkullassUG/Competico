package com.projteam.app.service;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static com.projteam.app.domain.Account.LECTURER_ROLE;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
import com.projteam.app.config.SecurityContextConfig;
import com.projteam.app.dao.AccountDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;

@Service
public class AccountService implements UserDetailsService
{
	private AccountDAO accDao;
	private PasswordEncoder passEnc;
	private AuthenticationManager authManager;
	private SecurityContextConfig secConConf;
	
	@Autowired
	public AccountService(
			AccountDAO accDao,
			PasswordEncoder passEnc,
			AuthenticationManager authManager,
			SecurityContextConfig secConConf)
	{
		this.accDao = accDao;
		this.passEnc = passEnc;
		this.authManager = authManager;
		this.secConConf = secConConf;
	}
	
	public void register(HttpServletRequest req, RegistrationDTO regDto, boolean autoAuthenticate)
	{
		String passHash = passEnc.encode(regDto.getPassword());
		
		Account acc = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail(regDto.getEmail())
				.withUsername(regDto.getUsername())
				.withNickname(regDto.getUsername())
				.withPasswordHash(passHash)
				.withRoles(List.of(regDto.isPlayer()?PLAYER_ROLE:LECTURER_ROLE))
				.build();
		acc = accDao.save(acc);
		if (autoAuthenticate)
			authenticate(req, regDto.getEmail(), regDto.getPassword());
	}
	public boolean login(HttpServletRequest req, LoginDTO loginDto)
	{
		Account acc = selectByEmailOrUsername(loginDto.getEmail()).orElse(null);
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
	
	private Optional<Account> selectByEmailOrUsername(String emailOrUsername)
	{
		return accDao.findOne(Example.of(new Account.Builder()
					.withEmail(emailOrUsername)
					.withUsername(emailOrUsername)
					.build(),
				ExampleMatcher.matchingAny()
					.withMatcher("email", ExampleMatcher.GenericPropertyMatchers.exact())
					.withMatcher("username", ExampleMatcher.GenericPropertyMatchers.exact())));
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
		Authentication auth = secConConf.getContext().getAuthentication();
		if (auth == null || (auth instanceof AnonymousAuthenticationToken) || !auth.isAuthenticated())
			return null;
		return auth;
	}
	public boolean isAuthenticated()
	{
		return getAuthentication() != null;
	}
	public Optional<Account> getAuthenticatedAccount()
	{
		return Optional.ofNullable(getAuthentication())
				.map(auth -> (Account) auth.getPrincipal());
	}

	public Optional<Account> findByUsername(String username)
	{
		return accDao.findOne(Example.of(new Account.Builder()
				.withUsername(username)
				.build(),
			ExampleMatcher.matchingAny()
				.withMatcher("username", ExampleMatcher.GenericPropertyMatchers.exact())));
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return selectByEmailOrUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
	}
}
