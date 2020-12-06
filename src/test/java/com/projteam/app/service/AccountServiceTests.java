package com.projteam.app.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import com.projteam.app.dao.AccountDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;

public class AccountServiceTests
{
	private @Mock AccountDAO accDao;
	private @Mock PasswordEncoder passEnc;
	private @Mock AuthenticationManager authManager;
	
	private @InjectMocks AccountService accountService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads()
	{
		Assert.notNull(accountService, "Account Service initialization failed");
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldAuthenticateWhenRegisteringWithAutoAuthentication(RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		
		accountService.register(req, mockRegDto, true);
		
		verify(passEnc, times(1)).encode(mockRegDto.getPassword());
		verify(authManager, times(1)).authenticate(argThat(auth ->
			auth.getPrincipal().equals(mockRegDto.getEmail())
			&& auth.getCredentials().equals(mockRegDto.getPassword())));
		verifyNoMoreInteractions(passEnc);
		verifyNoMoreInteractions(authManager);
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldNotAuthenticateWhenRegisteringWithoutAutoAuthentication(RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		
		accountService.register(req, mockRegDto, false);
		
		verify(passEnc, times(1)).encode(mockRegDto.getPassword());
		verifyNoMoreInteractions(passEnc);
		verifyNoInteractions(authManager);
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldAuthenticateWithRightCredentialsWhenLoggingIn(RegistrationDTO mockRegDto)
	{
		HttpServletRequest req = new MockHttpServletRequest();
		LoginDTO mockLoginDto = new LoginDTO(mockRegDto.getEmail(), mockRegDto.getPassword());
		
		when(passEnc.encode(mockRegDto.getPassword()))
			.thenReturn(mockRegDto.getPassword().toString());
		when(passEnc.matches(mockRegDto.getPassword(),
				mockRegDto.getPassword().toString()))
			.thenReturn(true);
		when(accDao.selectAccount(mockRegDto.getEmail()))
			.thenReturn(new Account(
					mockRegDto.getEmail(),
					mockRegDto.getUsername(),
					mockRegDto.getPassword().toString()));
		
		accountService.register(req, mockRegDto, false);
		boolean loggedIn = accountService.login(req, mockLoginDto);
		
		Assert.isTrue(loggedIn, "Account not authenticated after login");
		verify(passEnc, times(1)).encode(mockRegDto.getPassword());
		verify(authManager, times(1)).authenticate(argThat(auth ->
				auth.getPrincipal().equals(mockRegDto.getEmail())
				&& auth.getCredentials().equals(mockRegDto.getPassword())));
		verify(passEnc, times(1)).matches(mockRegDto.getPassword(), mockRegDto.getPassword().toString());
		verifyNoMoreInteractions(passEnc);
		verifyNoMoreInteractions(authManager);
	}
	
	//---Sources---
	
	public static List<Arguments> mockRegistrationData()
	{
		List<Arguments> ret = new ArrayList<>();
		
		ret.add(Arguments.of(new RegistrationDTO(
				"testplayer@test.pl",
				"TestPlayer",
				"QWERTY",
				true)));
		ret.add(Arguments.of(new RegistrationDTO(
				"testlecturer@test.pl",
				"TestLecturer",
				"QWERTY",
				false)));
		
		return ret;
	}
}
