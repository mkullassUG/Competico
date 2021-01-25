package com.projteam.app.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.mockito.MockitoAnnotations;
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
import com.projteam.app.domain.Account;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;

public class AccountServiceTests
{
	private @Mock AccountDAO accDao;
	private @Mock PasswordEncoder passEnc;
	private @Mock AuthenticationManager authManager;
	private @Mock SecurityContextConfig secConConf;
	
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
		
		accountService.register(req, mockRegDto, true);
		
		verify(passEnc, times(1)).encode(mockRegDto.getPassword());
		verify(authManager, times(1)).authenticate(argThat(auth ->
			auth.getPrincipal().equals(mockRegDto.getEmail())
			&& auth.getCredentials().equals(mockRegDto.getPassword())));
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
		
		assertThrows(NullPointerException.class,
				() -> accountService.register(req, mockRegDto, autoAuthenticate));
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
					.withEmail(mockRegDto.getEmail())
					.withUsername(mockRegDto.getUsername())
					.withPassword(mockRegDto.getPassword().toString())
					.build()));
		
		boolean loggedIn = accountService.login(req, mockLoginDto);
		
		assertTrue(loggedIn);
		ArgumentCaptor<Authentication> authCap = ArgumentCaptor.forClass(Authentication.class);
		verify(authManager, times(1)).authenticate(authCap.capture());
		Authentication auth = authCap.getValue();
		assertEquals(auth.getPrincipal(), mockRegDto.getEmail());
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
		when(accDao.findByEmailOrUsername(acc.getEmail(), acc.getEmail())).thenReturn(Optional.of(acc));
		when(accDao.findByEmailOrUsername(acc.getUsername(), acc.getUsername())).thenReturn(Optional.of(acc));
		
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
	
	//---Sources---
	
	public static List<Arguments> mockRegistrationData()
	{
		return List.of(Arguments.of(new RegistrationDTO(
						"testplayer@test.pl",
						"TestPlayer",
						"QWERTY",
						true)),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						"TestLecturer",
						"QWERTY",
						false)));
	}
	public static List<Arguments> mockNullRegistrationDataAndAutoAuth()
	{
		return List.of(Arguments.of(null, false),
				Arguments.of(null, true),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTY",
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTY",
						true), false),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTY",
						false), true),
				Arguments.of(new RegistrationDTO(
						null,
						"TestLecturer",
						"QWERTY",
						true), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTY",
						false), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTY",
						true), false),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTY",
						false), true),
				Arguments.of(new RegistrationDTO(
						"testlecturer@test.pl",
						null,
						"QWERTY",
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
						"QWERTY",
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						"QWERTY",
						false), false),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						"QWERTY",
						false), true),
				Arguments.of(new RegistrationDTO(
						null,
						null,
						"QWERTY",
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
						.withEmail("testAcc@test.pl")
						.withUsername(username)
						.withPassword("QWERTY")
						.build()));
	}
	public static List<Arguments> mockAccount()
	{
		return List.of(
				Arguments.of(new Account.Builder()
						.withEmail("testAcc@test.pl")
						.withUsername("TestAccount")
						.withPassword("QWERTY")
						.build()));
	}
	public static List<Arguments> mockUsername()
	{
		return List.of(Arguments.of("TestAccount"));
	}
	public static List<Arguments> mockIdAndEmptyOrFullAccountOptional()
	{
		return List.of(
				Arguments.of(UUID.randomUUID(), Optional.empty()),
				Arguments.of(UUID.randomUUID(), Optional.of(
					new Account.Builder()
						.withEmail("testAcc@test.pl")
						.withUsername("TestAccount")
						.withPassword("QWERTY")
						.build())));
	}
}
