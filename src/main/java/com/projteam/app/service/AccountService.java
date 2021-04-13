package com.projteam.app.service;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static com.projteam.app.domain.Account.SWAGGER_ADMIN;
import static com.projteam.app.domain.Account.TASK_DATA_ADMIN;
import static com.projteam.app.domain.Account.ACTUATOR_ADMIN;
import static com.projteam.app.domain.Account.LECTURER_ROLE;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import com.projteam.app.config.SecurityContextConfig;
import com.projteam.app.dao.AccountDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;
import com.projteam.app.utils.Initializable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	@EventListener(ContextRefreshedEvent.class)
	@Transactional
	public void initAdminAccount()
	{
		char[] passChars = IntStream.range(0, 255)
				.filter(AccountService::isPasswordChar)
				.collect(StringBuilder::new, (sb, c) -> sb.append((char) c), StringBuilder::append)
				.toString()
				.toCharArray();
		
		String password = new Random()
				.ints(16, 0, passChars.length)
				.map(i -> passChars[i])
				.collect(StringBuilder::new, (sb, c) -> sb.append((char) c), StringBuilder::append)
				.toString();
		
		log.info("Generated Admin password: " + password);
		
		Account admin = accDao.findByUsername("Admin")
				.map(acc -> Initializable.init(acc))
				.map(acc ->
				{
					acc.setPassword(passEnc.encode(password));
					return acc;
				})
				.orElseGet(() -> new Account.Builder()
						.withID(UUID.randomUUID())
						.withEmail("mockAdminEmail@mock.pl")
						.withUsername("Admin")
						.withNickname("Admin")
						.withPassword(passEnc.encode(password))
						.withRoles(List.of(ACTUATOR_ADMIN, SWAGGER_ADMIN, TASK_DATA_ADMIN, LECTURER_ROLE))
						.enabled(true)
						.nonExpired(true)
						.nonLocked(true)
						.credentialsNonExpired(true)
						.build());
		
		accDao.save(admin);
	}
	private static boolean isPasswordChar(int c)
	{
		return (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9')
				|| (c == '-') || (c == '_');
	}

	@Transactional
	public void register(HttpServletRequest req, RegistrationDTO regDto, boolean autoAuthenticate)
	{
		if (containsNull(regDto))
			throw new NullPointerException("Registration DTO contains null values: " + regDto);
		
		String passHash = passEnc.encode(regDto.getPassword());
		
		Account acc = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail(regDto.getEmail())
				.withUsername(regDto.getUsername())
				.withNickname(regDto.getUsername())
				.withPassword(passHash)
				.withRoles(List.of(regDto.isPlayer()?PLAYER_ROLE:LECTURER_ROLE))
				.build();
		acc = accDao.save(acc);
		if (autoAuthenticate)
			authenticate(req, acc.getUsername(), regDto.getPassword());
	}
	@Transactional
	public boolean login(HttpServletRequest req, LoginDTO loginDto)
	{
		Account acc = selectByEmailOrUsername(loginDto.getEmail()).orElse(null);
		if (acc == null)
			return false;
		if (passEnc.matches(loginDto.getPassword(), acc.getPassword()))
		{
			authenticate(req, acc.getUsername(), loginDto.getPassword());
			return true;
		}
		else
			return false;
	}
	
	/* Manual authentication, required due to lack of
	 * Json authentication support from Spring Security
	 * */
	private void authenticate(HttpServletRequest req,
			String username, CharSequence password)
	{
		UsernamePasswordAuthenticationToken authReq
			= new UsernamePasswordAuthenticationToken(username, password);
	    Authentication auth = authManager.authenticate(authReq);
	    
	    SecurityContext sc = secConConf.getContext();
	    sc.setAuthentication(auth);
	    
	    HttpSession session = req.getSession(true);
	    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
	}
	private void refreshAuth(Account acc)
	{
		SecurityContext sc = secConConf.getContext();
		Authentication auth = sc.getAuthentication();
		PreAuthenticatedAuthenticationToken newAuth
			= new PreAuthenticatedAuthenticationToken(
					acc, auth.getCredentials(), auth.getAuthorities());
		newAuth.setAuthenticated(true);
	    sc.setAuthentication(newAuth);
	}
	private void refreshAuth(String username, CharSequence newPassword)
	{
		SecurityContext sc = secConConf.getContext();
		Authentication newAuth = new UsernamePasswordAuthenticationToken(
				username, newPassword);
		newAuth = authManager.authenticate(newAuth);
	    sc.setAuthentication(newAuth);
	}
	private Authentication getAuthentication()
	{
		Authentication auth = secConConf.getContext().getAuthentication();
		if (auth == null || (auth instanceof AnonymousAuthenticationToken) || !auth.isAuthenticated())
			return null;
		return auth;
	}
	@Transactional
	public boolean isAuthenticated()
	{
		return getAuthentication() != null;
	}
	@Transactional
	public Optional<Account> getAuthenticatedAccount()
	{
		try
		{
			return init(Optional.ofNullable(getAuthentication())
					.map(auth -> (Account) auth.getPrincipal()));
		}
		catch (Exception e)
		{
			System.err.println("An error occurred while fetching authentication, logging out user.");
			e.printStackTrace();
			secConConf.clearContext();
			return Optional.empty();
		}
	}
	private Optional<Account> selectByEmailOrUsername(String emailOrUsername)
	{
		return init(accDao.findByEmailOrUsername(emailOrUsername, emailOrUsername));
	}
	@Transactional
	public Optional<Account> findByID(UUID id)
	{
		return init(accDao.findById(id));
	}
	@Transactional
	public Optional<Account> findByUsername(String username)
	{
		return init(accDao.findByUsername(username));
	}
	
	@Transactional
	public boolean changeEmail(String newEmail)
	{
		Optional<Account> acc = getAuthenticatedAccount();
		if (acc.isPresent())
			return changeEmail(acc.get(), newEmail);
		return false;
	}
	@Transactional
	public boolean changeEmail(Account acc, String newEmail)
	{
		if (acc == null)
			return false;
		
		try
		{
			if (accDao.findByEmail(newEmail).isPresent())
				return false;
			
			acc = accDao.findByUsername(acc.getUsername()).orElse(null);
			if (acc == null)
				return false;
			
			Initializable.init(acc);
			acc.setEmail(newEmail);
			acc = accDao.saveAndFlush(acc);
			
			refreshAuth(acc);
			return true;
		}
		catch (Exception e)
		{}
		return false;
	}
	@Transactional
	public boolean changeNickname(String newNickname)
	{
		Optional<Account> acc = getAuthenticatedAccount();
		if (acc.isPresent())
			return changeNickname(acc.get(), newNickname);
		return false;
	}
	@Transactional
	public boolean changeNickname(Account acc, String newNickname)
	{
		if (acc == null)
			return false;
		
		try
		{
			acc = accDao.findByUsername(acc.getUsername()).orElse(null);
			if (acc == null)
				return false;
			
			Initializable.init(acc);
			acc.setNickname(newNickname);
			acc = accDao.saveAndFlush(acc);
			
			refreshAuth(acc);
			return true;
		}
		catch (Exception e)
		{}
		return false;
	}
	@Transactional
	public boolean changePassword(CharSequence oldPassword, CharSequence newPassword)
	{
		Optional<Account> acc = getAuthenticatedAccount();
		if (acc.isPresent())
			return changePassword(acc.get(), oldPassword, newPassword);
		return false;
	}
	@Transactional
	public boolean changePassword(Account acc, CharSequence oldPassword, CharSequence newPassword)
	{
		if (acc == null)
			return false;

		try
		{
			if (passEnc.matches(oldPassword, acc.getPassword()))
			{
				acc = accDao.findByUsername(acc.getUsername()).orElse(null);
				if (acc == null)
					return false;

				Initializable.init(acc);
				acc.setPassword(passEnc.encode(newPassword));
				acc = accDao.saveAndFlush(acc);
				
				refreshAuth(acc.getUsername(), newPassword);
				return true;
			}
		}
		catch (Exception e)
		{}
		return false;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return accDao.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
	}
	
	private boolean containsNull(RegistrationDTO regDto)
	{
		return (regDto == null)
				|| (regDto.getEmail() == null)
				|| (regDto.getUsername() == null)
				|| (regDto.getPassword() == null);
	}

	private static <T extends Initializable> Optional<T> init(Optional<T> in)
	{
		in.ifPresent(i -> i.initialize());
		return in;
	}
}
