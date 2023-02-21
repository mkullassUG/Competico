package com.projteam.competico.service;

import static com.projteam.competico.domain.Account.ACTUATOR_ADMIN;
import static com.projteam.competico.domain.Account.LECTURER_ROLE;
import static com.projteam.competico.domain.Account.PLAYER_ROLE;
import static com.projteam.competico.domain.Account.SWAGGER_ADMIN;
import static com.projteam.competico.domain.Account.TASK_DATA_ADMIN;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.CannotAcquireLockException;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.projteam.competico.config.SecurityContextConfig;
import com.projteam.competico.dao.AccountDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.EmailVerificationToken;
import com.projteam.competico.domain.PasswordResetToken;
import com.projteam.competico.domain.TokenStatus;
import com.projteam.competico.dto.LoginDTO;
import com.projteam.competico.dto.RegistrationDTO;
import com.projteam.competico.utils.Initializable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountService implements UserDetailsService
{
	private AccountDAO accDao;
	private TokenService tokenServ;
	private PasswordEncoder passEnc;
	private AuthenticationManager authManager;
	private SecurityContextConfig secConConf;
	private EmailService emailServ;
	
	@Value("${app.generateAdminPass:false}")
	private boolean generateAdminPass;
	
	@Value("${app.url:localhost}")
	private String APP_URL = "localhost";
	
	@Autowired
	public AccountService(
			AccountDAO accDao,
			TokenService tokenServ,
			PasswordEncoder passEnc,
			AuthenticationManager authManager,
			SecurityContextConfig secConConf,
			EmailService emailServ)
	{
		this.accDao = accDao;
		this.passEnc = passEnc;
		this.authManager = authManager;
		this.secConConf = secConConf;
		this.emailServ = emailServ;
		this.tokenServ = tokenServ;
	}
	
	@EventListener(ContextRefreshedEvent.class)
	@Transactional
	public void attemptToInitAdminAccount()
	{
		if (generateAdminPass)
			initAdminAccount();
	}
	
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
						.withEmailVerified(false)
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
		if (!validateUsername(regDto.getUsername()))
			throw new IllegalArgumentException("BAD_USERNAME");
		if (!validateEmail(regDto.getEmail()))
			throw new IllegalArgumentException("BAD_EMAIL");
		if (!validatePassword(regDto.getPassword()))
			throw new IllegalArgumentException("BAD_PASSWORD");
		
		if (accDao.existsByUsernameOrEmail(
				regDto.getUsername(), regDto.getEmail()))
			throw new IllegalArgumentException("DATA_ALREADY_USED");
		
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
			log.error("An error occurred while fetching authentication, logging out user. "
					+ "Cause: " + e.getClass().getTypeName() + ": " + e.getMessage());
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
	public void changeEmail(String newEmail, CharSequence password)
	{
		Optional<Account> acc = getAuthenticatedAccount();
		if (acc.isPresent())
			changeEmail(acc.get(), newEmail, password);
		else
			throw new IllegalArgumentException("NOT_AUTHENTICATED");
	}
	@Transactional
	public void changeEmail(Account acc, String newEmail, CharSequence password)
	{
		if (acc == null)
			throw new IllegalArgumentException("NOT_AUTHENTICATED");
		
		if (!passEnc.matches(password, acc.getPassword()))
			throw new IllegalArgumentException("BAD_PASSWORD");
		
		if (!validateEmail(newEmail))
			throw new IllegalArgumentException("BAD_EMAIL");
		if (newEmail.equals(acc.getEmail()))
			throw new IllegalArgumentException("SAME_EMAIL");
		if (accDao.findByEmail(newEmail).isPresent())
			throw new IllegalArgumentException("USED_EMAIL");
		
		acc = accDao.findByUsername(acc.getUsername()).orElse(null);
		if (acc == null)
			throw new IllegalArgumentException("NOT_AUTHENTICATED");
		
		Initializable.init(acc);
		acc.setEmailVerified(false);
		acc.setEmail(newEmail);
		acc = accDao.saveAndFlush(acc);
		
		refreshAuth(acc);
	}
	@Transactional
	public void changeNickname(String newNickname)
	{
		Optional<Account> acc = getAuthenticatedAccount();
		if (acc.isPresent())
			changeNickname(acc.get(), newNickname);
		else
			throw new IllegalArgumentException("NOT_AUTHENTICATED");
	}
	@Transactional
	public void changeNickname(Account acc, String newNickname)
	{
		if (acc == null)
			throw new IllegalArgumentException("NOT_AUTHENTICATED");
		
		if (!validateNickname(newNickname))
			throw new IllegalArgumentException("BAD_NICKNAME");
		
		acc = accDao.findByUsername(acc.getUsername()).orElse(null);
		if (acc == null)
			throw new IllegalArgumentException("NOT_AUTHENTICATED");
		
		Initializable.init(acc);
		acc.setNickname(newNickname);
		acc = accDao.saveAndFlush(acc);
		
		refreshAuth(acc);
	}
	@Transactional
	public void changePassword(CharSequence oldPassword, CharSequence newPassword)
	{
		Optional<Account> acc = getAuthenticatedAccount();
		if (acc.isPresent())
			changePassword(acc.get(), oldPassword, newPassword);
		else
			throw new IllegalArgumentException("NOT_AUTHENTICATED");
	}
	@Transactional
	public void changePassword(Account acc, CharSequence oldPassword, CharSequence newPassword)
	{
		if (acc == null)
			throw new IllegalArgumentException("NOT_AUTHENTICATED");

		if (!validatePassword(newPassword))
			throw new IllegalArgumentException("BAD_NEW_PASSWORD");
		
		String currPassword = acc.getPassword();
		if ((currPassword != null) && !passEnc.matches(oldPassword, currPassword))
			throw new IllegalArgumentException("BAD_OLD_PASSWORD");
		if ((currPassword != null) && passEnc.matches(newPassword, currPassword))
			throw new IllegalArgumentException("SAME_PASSWORD");
		
		acc = accDao.findByUsername(acc.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("ACCOUNT_NOT_PRESENT"));

		Initializable.init(acc);
		acc.setPassword(passEnc.encode(newPassword));
		acc = accDao.saveAndFlush(acc);
		
		refreshAuth(acc.getUsername(), newPassword);
	}
	
	@Transactional
	public void requestPasswordReset(String emailOrUsername)
	{
		if (emailOrUsername == null)
			throw new IllegalArgumentException("ACCOUNT_NOT_PRESENT");
		Account acc = selectByEmailOrUsername(emailOrUsername)
				.orElseThrow(() -> new IllegalArgumentException("ACCOUNT_NOT_PRESENT"));
		
		String email = acc.getEmail();
		PasswordResetToken token = retry(() ->
				tokenServ.generatePasswordResetToken(email));
		
		String link = APP_URL + "/resetpassword/" + token.getId();
		
		String subject = "Password reset";
		String message = "Reset your password by clicking the link below:\r\n" + link;
		
		try
		{
			emailServ.sendEmailAsync(email, subject, message);
		}
		catch (Exception e)
		{
			log.error(e.getClass().getTypeName() + ": " + e.getMessage());
			throw new RuntimeException("MAIL_ERROR");
		}
	}

	@Transactional
	public TokenStatus getPasswordResetTokenStatus(String token)
	{
		return tokenServ.getPasswordResetTokenStatus(token);
	}
	@Transactional
	public void resetPassword(String token, CharSequence newPassword)
	{
		if (token == null)
			throw new IllegalArgumentException("INVALID_TOKEN");
		if (!validatePassword(newPassword))
			throw new IllegalArgumentException("BAD_NEW_PASSWORD");
		
		
		PasswordResetToken prt = retry(() ->
				tokenServ.consumePasswordResetToken(token));
		String email = prt.getEmail();
		
		Account acc = accDao.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("ACCOUNT_NOT_PRESENT"));

		secConConf.clearContext();
		
		Initializable.init(acc);
		acc.setPassword(passEnc.encode(newPassword));
		acc = accDao.saveAndFlush(acc);
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void requestEmailVerification()
	{
		requestEmailVerification(getAuthenticatedAccount()
			.orElseThrow(() -> new IllegalArgumentException("NOT_AUTHENTICATED")));
	}
	@Transactional
	public void requestEmailVerification(Account acc)
	{
		String email = acc.getEmail();
		EmailVerificationToken token = retry(() ->
				tokenServ.generateEmailVerificationToken(email));
		
		String link = APP_URL + "/verifyemail/" + token.getId();
		
		String subject = "Email verification";
		String message = "Verify your email by clicking the link below:\r\n" + link;
		
		try
		{
			emailServ.sendEmailAsync(email, subject, message);
		}
		catch (Exception e)
		{
			log.error(e.getClass().getTypeName() + ": " + e.getMessage());
			throw new IllegalArgumentException("MAIL_ERROR");
		}
	}

	@Transactional
	public TokenStatus getEmailVerificationTokenStatus(String token)
	{
		return tokenServ.getEmailVerificationTokenStatus(token);
	}
	@Transactional
	public void verifyEmail(String token)
	{
		if (token == null)
			throw new IllegalArgumentException("INVALID_TOKEN");
		
		EmailVerificationToken evt = retry(() ->
				tokenServ.consumeEmailVerificationToken(token));
		String email = evt.getEmail();
		
		Account acc = accDao.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("ACCOUNT_NOT_PRESENT"));
		
		Initializable.init(acc);
		acc.setEmailVerified(true);
		acc = accDao.saveAndFlush(acc);
		Account currAcc = acc;
		
		if (getAuthenticatedAccount()
				.map(a -> a.getId().equals(currAcc.getId()))
				.orElse(false))
			refreshAuth(acc);
		else
			secConConf.clearContext();
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return accDao.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
	}
	
	private boolean validateEmail(String email)
	{
		if (email == null)
			return false;
		
		return email.matches("(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+"
				+ "(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\\\"(?:["
				+ "\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b"
				+ "\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*"
				+ "\\\")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+"
				+ "[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|"
				+ "2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4]"
				+ "[0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-"
				+ "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\"
				+ "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
	}
	private boolean validateUsername(String username)
	{
		if (username == null)
			return false;
		
		return username.matches("^(?=.{4,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
	}
	private boolean validateNickname(String nickname)
	{
		if (nickname == null)
			return false;
		
//		return nickname.matches("^(?=.{1,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
		return nickname.matches(
				"^(?:[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ!#$%^&\\(\\)_+}{\":?|~`]|"
				+ "(?:\\u00a9|\\u00ae|[\\uD83C-\\uDBFF\\uDC00-\\uDFFF])){3,32}$");
	}
	private boolean validatePassword(CharSequence password)
	{
		if (password == null)
			return false;
		
		return Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", password);
	}

	private void sleep(int duration)
	{
		try
		{
			Thread.sleep(duration);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	private <T> T retry(Supplier<T> supplier)
	{
		T ret = null;
		do
		{
			try
			{
				ret = supplier.get();
			}
			catch (CannotAcquireLockException e)
			{
				sleep(25);
			}
		}
		while (ret == null);
		return ret;
	}
	
	private static <T extends Initializable> Optional<T> init(Optional<T> in)
	{
		in.ifPresent(i -> i.initialize());
		return in;
	}
}
