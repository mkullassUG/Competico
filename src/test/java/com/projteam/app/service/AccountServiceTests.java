package com.projteam.app.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.projteam.app.config.SecurityContextConfig;
import com.projteam.app.dao.AccountDAO;
import com.projteam.app.dao.EmailVerificationTokenDAO;
import com.projteam.app.dao.PasswordResetTokenDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.EmailVerificationToken;
import com.projteam.app.domain.PasswordResetToken;
import com.projteam.app.domain.TokenStatus;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;

public class AccountServiceTests
{
	private @Mock AccountDAO accDao;
	private @Mock PasswordEncoder passEnc;
	private @Mock AuthenticationManager authManager;
	private @Mock SecurityContextConfig secConConf;
	private @Mock PasswordResetTokenDAO prtDao;
	private @Mock EmailVerificationTokenDAO evtDao;
	private @Mock EmailService emailServ;
	
	private @InjectMocks AccountService accountService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads()
	{
		assertNotNull(accountService);
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldAuthenticateWhenRegisteringWithAutoAuthentication(RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		SecurityContext sc = mock(SecurityContext.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(accDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		
		accountService.register(req, mockRegDto, true);
		
		verify(passEnc, times(1)).encode(mockRegDto.getPassword());
		verify(authManager, times(1)).authenticate(any());
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldNotAuthenticateWhenRegisteringWithoutAutoAuthentication(
			RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		
		accountService.register(req, mockRegDto, false);
		
		verify(passEnc, times(1)).encode(mockRegDto.getPassword());
	}
	@ParameterizedTest
	@MethodSource("mockNullRegistrationDataAndAutoAuth")
	public void shouldThrowWhenRegisteringWithNullValues(RegistrationDTO mockRegDto,
			boolean autoAuthenticate)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		
		try
		{
			accountService.register(req, mockRegDto, autoAuthenticate);
			fail();
		}
		catch (NullPointerException | IllegalArgumentException exc)
		{}
		catch (Exception e)
		{
			fail();
		}
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldAuthenticateWithRightCredentialsWhenLoggingIn(RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		LoginDTO mockLoginDto = new LoginDTO(mockRegDto.getEmail(), mockRegDto.getPassword());
		SecurityContext sc = mock(SecurityContext.class);
		
		when(secConConf.getContext()).thenReturn(sc);
		when(passEnc.matches(mockRegDto.getPassword(),
				mockRegDto.getPassword().toString()))
			.thenReturn(true);
		when(accDao.findByEmailOrUsername(mockRegDto.getEmail(), mockRegDto.getEmail()))
			.thenReturn(Optional.of(new Account.Builder()
					.withID(UUID.randomUUID())
					.withEmail(mockRegDto.getEmail())
					.withUsername(mockRegDto.getUsername())
					.withPassword(mockRegDto.getPassword().toString())
					.build()));
		
		boolean loggedIn = accountService.login(req, mockLoginDto);
		
		assertTrue(loggedIn);
		ArgumentCaptor<Authentication> authCap = ArgumentCaptor.forClass(Authentication.class);
		verify(authManager, times(1)).authenticate(authCap.capture());
		Authentication auth = authCap.getValue();
		assertEquals(auth.getPrincipal(), mockRegDto.getUsername());
		assertEquals(auth.getCredentials(), mockRegDto.getPassword());
		verify(passEnc, times(1)).matches(mockRegDto.getPassword(), mockRegDto.getPassword().toString());
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldNotAuthenticateWithIncorrectPasswordWhenLoggingIn(RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		LoginDTO mockLoginDto = new LoginDTO(mockRegDto.getEmail(), mockRegDto.getPassword());
		
		when(passEnc.matches(mockRegDto.getPassword(),
				mockRegDto.getPassword().toString()))
			.thenReturn(false);
		when(accDao.findByEmailOrUsername(mockRegDto.getEmail(), mockRegDto.getEmail()))
			.thenReturn(Optional.of(new Account.Builder()
					.withID(UUID.randomUUID())
					.withEmail(mockRegDto.getEmail())
					.withUsername(mockRegDto.getUsername())
					.withPassword(mockRegDto.getPassword().toString())
					.build()));
		
		boolean loggedIn = accountService.login(req, mockLoginDto);
		
		assertFalse(loggedIn);
		verify(passEnc, times(1)).matches(mockRegDto.getPassword(), mockRegDto.getPassword().toString());
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldNotAuthenticateWithIncorrectUsernameWhenLoggingIn(RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		LoginDTO mockLoginDto = new LoginDTO(mockRegDto.getEmail(), mockRegDto.getPassword());
		
		when(accDao.findByEmailOrUsername(mockRegDto.getEmail(), mockRegDto.getEmail())).thenReturn(Optional.empty());
		
		boolean loggedIn = accountService.login(req, mockLoginDto);
		
		assertFalse(loggedIn);
	}
	
	@Test
	public void shouldNotBeAuthenticatedWhenAuthenticationIsNull()
	{
		SecurityContext sc = mock(SecurityContext.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(null);
		
		assertFalse(accountService.isAuthenticated());
	}
	@Test
	public void shouldNotBeAuthenticatedWhenAuthenticationIsAnonymous()
	{
		SecurityContext sc = mock(SecurityContext.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(mock(AnonymousAuthenticationToken.class));
		
		assertFalse(accountService.isAuthenticated());
	}
	@Test
	public void shouldNotBeAuthenticatedWhenNotAuthenticated()
	{
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(false);
		
		assertFalse(accountService.isAuthenticated());
	}
	@Test
	public void shouldBeAuthenticatedWhenIsAuthenticated()
	{
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		
		assertTrue(accountService.isAuthenticated());
	}
	@Test
	public void shouldReturnNothingWhenAuthenticationIsNull()
	{
		SecurityContext sc = mock(SecurityContext.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(null);
		
		assertTrue(accountService.getAuthenticatedAccount().isEmpty());
	}
	@Test
	public void shouldReturnNothingWhenAuthenticationIsAnonymous()
	{
		SecurityContext sc = mock(SecurityContext.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(mock(AnonymousAuthenticationToken.class));
		
		assertTrue(accountService.getAuthenticatedAccount().isEmpty());
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void shouldReturnAccountWhenAuthenticationIsNotAnonymous(Account acc)
	{
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		assertTrue(accountService.getAuthenticatedAccount().filter(a -> a.equals(acc)).isPresent());
	}
	
	@ParameterizedTest
	@MethodSource("mockAccountAndUsername")
	public void shouldFindAccountByUsernameWhenOneExists(String username, Account acc)
	{
		when(accDao.findByUsername(acc.getUsername())).thenReturn(Optional.of(acc));
		
		Optional<Account> res = accountService.findByUsername(username);
		
		assertTrue(res.filter(a -> a.equals(acc)).isPresent());
	}
	@ParameterizedTest
	@MethodSource("mockUsername")
	public void shouldNotFindAccountByUsernameWhenNoneExist(String username)
	{
		when(accDao.findByUsername(username)).thenReturn(Optional.empty());
		
		Optional<Account> res = accountService.findByUsername(username);
		
		assertTrue(res.isEmpty());
		verify(accDao, times(1)).findByUsername(username);
	}
	@ParameterizedTest
	@MethodSource("mockAccountAndUsername")
	public void shouldLoadUserByUsernameOrEmailWhenOneExists(String username, Account acc)
	{
		when(accDao.findByUsername(acc.getUsername())).thenReturn(Optional.of(acc));
		
		UserDetails res = accountService.loadUserByUsername(username);
		
		assertNotNull(res);
	}
	@ParameterizedTest
	@MethodSource("mockUsername")
	public void shouldNotLoadUserByUsernameOrEmailWhenNoneExist(String username)
	{
		when(accDao.findByEmailOrUsername(any(), any())).thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class,
				() -> accountService.loadUserByUsername(username));
	}
	@ParameterizedTest
	@MethodSource("mockIdAndEmptyOrFullAccountOptional")
	public void canFindUserById(
			UUID id, Optional<Account> acc)
	{
		when(accDao.findById(id)).thenReturn(acc);
		
		assertEquals(accountService.findByID(id), acc);
	}
	
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canChangeEmail(Account acc)
	{
		String newEmail = "newMockEmail@mock.pl";
		when(accDao.findByUsername(acc.getUsername()))
			.thenReturn(Optional.of(acc));
		when(accDao.save(acc)).thenReturn(acc);
		when(accDao.saveAndFlush(acc)).thenReturn(acc);
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		when(passEnc.matches(acc.getPassword(), acc.getPassword()))
			.thenReturn(true);
		
		assertDoesNotThrow(() -> accountService.changeEmail(acc, newEmail, acc.getPassword()));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canChangeEmailWithAuthenticatedAccount(Account acc)
	{
		String newEmail = "newMockEmail@mock.pl";
		when(accDao.findByUsername(acc.getUsername()))
			.thenReturn(Optional.of(acc));
		when(accDao.save(acc)).thenReturn(acc);
		when(accDao.saveAndFlush(acc)).thenReturn(acc);
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		when(passEnc.matches(acc.getPassword(), acc.getPassword()))
			.thenReturn(true);
		
		assertDoesNotThrow(() -> accountService.changeEmail(newEmail, acc.getPassword()));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canChangeNickname(Account acc)
	{
		String newNickname = "newMockAccNick";
		when(accDao.findByUsername(acc.getUsername()))
			.thenReturn(Optional.of(acc));
		when(accDao.save(acc)).thenReturn(acc);
		when(accDao.saveAndFlush(acc)).thenReturn(acc);
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		assertDoesNotThrow(() -> accountService.changeNickname(acc, newNickname));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canChangeNicknameWithAuthenticatedAccount(Account acc)
	{
		String newNickname = "newMockAccNick";
		when(accDao.findByUsername(acc.getUsername()))
			.thenReturn(Optional.of(acc));
		when(accDao.save(acc)).thenReturn(acc);
		when(accDao.saveAndFlush(acc)).thenReturn(acc);
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		assertDoesNotThrow(() -> accountService.changeNickname(newNickname));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canChangePassword(Account acc)
	{
		String oldPassword = acc.getPassword();
		String newPassword = "qwerty123POI";
		
		when(passEnc.matches(oldPassword, oldPassword))
			.thenReturn(true);
		when(passEnc.encode(newPassword))
			.thenReturn(newPassword);
		when(accDao.findByUsername(acc.getUsername()))
			.thenReturn(Optional.of(acc));
		when(accDao.save(acc)).thenReturn(acc);
		when(accDao.saveAndFlush(acc)).thenReturn(acc);
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		assertDoesNotThrow(() -> accountService.changePassword(acc, oldPassword, newPassword));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canChangePasswordWithAuthenticatedAccount(Account acc)
	{
		String oldPassword = acc.getPassword();
		String newPassword = "qwerty123POI";
		
		when(passEnc.matches(oldPassword, oldPassword))
			.thenReturn(true);
		when(passEnc.encode(newPassword))
			.thenReturn(newPassword);
		when(accDao.findByUsername(acc.getUsername()))
			.thenReturn(Optional.of(acc));
		when(accDao.save(acc)).thenReturn(acc);
		when(accDao.saveAndFlush(acc)).thenReturn(acc);
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		assertDoesNotThrow(() -> accountService.changePassword(oldPassword, newPassword));
	}
	
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canRequestPasswordReset(Account acc)
	{
		String username = acc.getUsername();
		when(accDao.findByEmailOrUsername(username, username)).thenReturn(Optional.of(acc));
		when(prtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertDoesNotThrow(() -> accountService.requestPasswordReset(username));
	}
	@Test
	public void shouldNotifyWhenAccountNotPresent()
	{
		String username = "TestUsername";
		when(accDao.findByEmailOrUsername(username, username)).thenReturn(Optional.empty());
		when(prtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.requestPasswordReset(username));
	}
	@Test
	public void shouldNotifyWhenUsernameIsNull()
	{
		assertThrows(IllegalArgumentException.class,
				() -> accountService.requestPasswordReset(null));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void shouldNotifyAboutMailErrorOnPasswordResetRequest(Account acc)
	{
		String username = acc.getUsername();
		when(accDao.findByEmailOrUsername(username, username)).thenReturn(Optional.of(acc));
		when(prtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		doThrow(Mockito.mock(MailException.class))
			.when(emailServ).sendEmail(any(), any(), any());
		
		assertThrows(RuntimeException.class, () -> accountService.requestPasswordReset(username));
	}
	
	@Test
	public void canGetPasswordResetTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.of(
				new PasswordResetToken(tokenID, email, expiryDate)));
		
		assertEquals(accountService.getPasswordResetTokenStatus(token),
				TokenStatus.VALID);
	}
	@Test
	public void canGetExpiredPasswordResetTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date expiryDate = cal.getTime();
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.of(
				new PasswordResetToken(tokenID, email, expiryDate)));
		
		assertEquals(accountService.getPasswordResetTokenStatus(token),
				TokenStatus.EXPIRED);
	}
	@Test
	public void canGetInvalidPasswordResetTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.empty());
		
		assertEquals(accountService.getPasswordResetTokenStatus(token),
				TokenStatus.INVALID);
	}
	@Test
	public void returnsInvalidPasswordResetTokenStatusOnException()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(prtDao.findById(tokenID)).thenThrow(new RuntimeException());
		
		assertEquals(accountService.getPasswordResetTokenStatus(token),
				TokenStatus.INVALID);
	}
	@Test
	public void returnsInvalidPasswordResetTokenStatusWhenInvalid()
	{
		String token = "NotAuuidToken";
		
		assertEquals(accountService.getPasswordResetTokenStatus(token),
				TokenStatus.INVALID);
	}
	
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canResetPassword(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		CharSequence newPass = acc.getPassword() + "new";
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.of(
				new PasswordResetToken(tokenID, email, expiryDate)));
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertDoesNotThrow(() -> accountService.resetPassword(token, newPass));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotResetPasswordWithNullToken(Account acc)
	{
		String email = acc.getEmail();
		CharSequence newPass = acc.getPassword() + "new";
		
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.resetPassword(null, newPass));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotResetPasswordWithInvalidToken(Account acc)
	{
		String token = "NotAuuidToken";
		
		String email = acc.getEmail();
		CharSequence newPass = acc.getPassword() + "new";
		
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.resetPassword(token, newPass));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotResetPasswordWithUnknownToken(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		CharSequence newPass = acc.getPassword() + "new";
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.empty());
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.resetPassword(token, newPass));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotResetPasswordWithExpiredToken(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date expiryDate = cal.getTime();
		CharSequence newPass = acc.getPassword() + "new";
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.of(
				new PasswordResetToken(tokenID, email, expiryDate)));
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.resetPassword(token, newPass));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotResetPasswordWhenAccountNotPresent(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		CharSequence newPass = acc.getPassword() + "new";
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.of(
				new PasswordResetToken(tokenID, email, expiryDate)));
		when(accDao.findByEmail(email)).thenReturn(Optional.empty());
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.resetPassword(token, newPass));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotResetPasswordWithNullPassword(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.resetPassword(token, null));
	}
	
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canRequestEmailVerification(Account acc)
	{
		String username = acc.getUsername();
		when(accDao.findByEmailOrUsername(username, username)).thenReturn(Optional.of(acc));
		when(evtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertDoesNotThrow(() -> accountService.requestEmailVerification(acc));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canRequestEmailVerificationWithAuth(Account acc)
	{
		String username = acc.getUsername();
		when(accDao.findByEmailOrUsername(username, username)).thenReturn(Optional.of(acc));
		when(evtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		assertDoesNotThrow(() -> accountService.requestEmailVerification());
	}
	@Test
	public void shouldNotifyWhenNotAuthenticated()
	{
		String username = "TestUsername";
		when(accDao.findByEmailOrUsername(username, username)).thenReturn(Optional.empty());
		when(evtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.requestEmailVerification());
	}
	@Test
	public void shouldNotifyWhenAccountIsNull()
	{
		assertThrows(NullPointerException.class,
				() -> accountService.requestEmailVerification(null));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void shouldNotifyAboutMailErrorOnEmailVerificationRequest(Account acc)
	{
		String username = acc.getUsername();
		when(accDao.findByEmailOrUsername(username, username)).thenReturn(Optional.of(acc));
		when(evtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		doThrow(Mockito.mock(MailException.class))
			.when(emailServ).sendEmail(any(), any(), any());
		
		assertThrows(RuntimeException.class, () -> accountService.requestEmailVerification(acc));
	}
	
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canVerifyEmail(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(acc);
		
		assertDoesNotThrow(() -> accountService.verifyEmail(token));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotVerifyEmailWithNullToken(Account acc)
	{
		String email = acc.getEmail();
		
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.verifyEmail(null));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotVerifyEmailWithInvalidToken(Account acc)
	{
		String token = "NotAuuidToken";
		String email = acc.getEmail();
		
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.verifyEmail(token));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotVerifyEmailWithUnknownToken(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.empty());
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.verifyEmail(token));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotVerifyEmailWithExpiredToken(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.verifyEmail(token));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void cannotVerifyEmailWhenAccountNotPresent(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		when(accDao.findByEmail(email)).thenReturn(Optional.empty());
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertThrows(IllegalArgumentException.class,
				() -> accountService.verifyEmail(token));
	}
	@ParameterizedTest
	@MethodSource("mockAccount")
	public void canVerifyEmailWhenNotAuthenticated(Account acc)
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = acc.getEmail();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		when(accDao.findByEmail(email)).thenReturn(Optional.of(acc));
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		SecurityContext sc = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(secConConf.getContext()).thenReturn(sc);
		when(sc.getAuthentication()).thenReturn(auth);
		when(auth.isAuthenticated()).thenReturn(false);
		when(auth.getPrincipal()).thenReturn(null);
		
		assertDoesNotThrow(() -> accountService.verifyEmail(token));
	}
	
	@Test
	public void canGetEmailVerificationTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertEquals(accountService.getEmailVerificationTokenStatus(token),
				TokenStatus.VALID);
	}
	@Test
	public void canGetExpiredEmailVerificationTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertEquals(accountService.getEmailVerificationTokenStatus(token),
				TokenStatus.EXPIRED);
	}
	@Test
	public void canGetInvalidEmailVerificationTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.empty());
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertEquals(accountService.getEmailVerificationTokenStatus(token),
				TokenStatus.INVALID);
	}
	@Test
	public void returnsInvalidEmailVerificationTokenStatusOnException()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(evtDao.findById(tokenID)).thenThrow(new RuntimeException());
		when(accDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertEquals(accountService.getEmailVerificationTokenStatus(token),
				TokenStatus.INVALID);
	}
	
	@Test
	public void canDeleteExpiredTokens()
	{
		assertDoesNotThrow(() -> accountService.deleteExpiredTokens());
	}
	
	//---Sources---
	
	public static List<Arguments> mockRegistrationData()
	{
		return List.of(Arguments.of(new RegistrationDTO(
						"testplayer@test.pl",
						"TestPlayer",
						"QWERTYuiop123",
						true)),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						"TestLecturer",
						"QWERTYuiop123",
						false)));
	}
	public static List<Arguments> mockNullRegistrationDataAndAutoAuth()
	{
		return List.of(Arguments.of(null, false),
				Arguments.of(null, true),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTYuiop123",
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTYuiop123",
						true), false),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTYuiop123",
						false), true),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTYuiop123",
						true), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTYuiop123",
						false), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTYuiop123",
						true), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTYuiop123",
						false), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTYuiop123",
						true), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						"TestLecturer",
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						"TestLecturer",
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						"TestLecturer",
						null,
						false), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						"TestLecturer",
						null,
						true), true),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						"QWERTYuiop123",
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						"QWERTYuiop123",
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						"QWERTYuiop123",
						false), true),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						"QWERTYuiop123",
						true), true),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						null,
						false), true),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						null,
						true), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						null,
						false), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						null,
						true), true),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						null,
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						null,
						false), true),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						null,
						true), true));
	}
	public static List<Arguments> mockAccountAndUsername()
	{
		String username = "TestAccount";
		return List.of(
				Arguments.of(username, new Account.Builder()
						.withID(UUID.randomUUID())
						.withEmail("testAcc@test.pl")
						.withUsername(username)
						.withPassword("QWERTYuiop123")
						.build()));
	}
	public static List<Arguments> mockAccount()
	{
		return List.of(
				Arguments.of(new Account.Builder()
						.withID(UUID.randomUUID())
						.withEmail("testAcc@test.pl")
						.withUsername("TestAccount")
						.withPassword("QWERTYuiop123")
						.build()));
	}
	public static List<Arguments> mockUsername()
	{
		return List.of(Arguments.of("TestAccount"));
	}
	public static List<Arguments> mockIdAndEmptyOrFullAccountOptional()
	{
		UUID id = UUID.randomUUID();
		return List.of(
				Arguments.of(UUID.randomUUID(), Optional.empty()),
				Arguments.of(id, Optional.of(
					new Account.Builder()
						.withID(id)
						.withEmail("testAcc@test.pl")
						.withUsername("TestAccount")
						.withPassword("QWERTYuiop123")
						.build())));
	}
}
