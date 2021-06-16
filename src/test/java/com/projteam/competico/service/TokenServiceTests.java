package com.projteam.competico.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.projteam.competico.dao.EmailVerificationTokenDAO;
import com.projteam.competico.dao.PasswordResetTokenDAO;
import com.projteam.competico.domain.EmailVerificationToken;
import com.projteam.competico.domain.PasswordResetToken;
import com.projteam.competico.domain.TokenStatus;

class TokenServiceTests
{
	private @Mock PasswordResetTokenDAO prtDao;
	private @Mock EmailVerificationTokenDAO evtDao;
	
	private @InjectMocks TokenService tokenService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads()
	{
		assertNotNull(tokenService);
	}

	@Test
	public void canGeneratePasswordResetToken()
	{
		String email = "testmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		
		when(prtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(prtDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertNotNull(tokenService.generatePasswordResetToken(email));
		
		verify(prtDao, times(1)).deleteByEmail(email);
		verify(prtDao, times(1)).saveAndFlush(any());
		
		ArgumentCaptor<PasswordResetToken> captor =
				ArgumentCaptor.forClass(PasswordResetToken.class);
		verify(prtDao, atLeast(0)).save(captor.capture());
		verify(prtDao, atLeast(0)).saveAndFlush(captor.capture());

		assertEquals(captor.getAllValues().size(), 1);
	}
	@Test
	public void canGenerateEmailVerificationToken()
	{
		String email = "testmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		
		when(evtDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(evtDao.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		
		assertNotNull(tokenService.generateEmailVerificationToken(email));
		
		verify(evtDao, times(1)).deleteByEmail(email);
		
		ArgumentCaptor<EmailVerificationToken> captor =
				ArgumentCaptor.forClass(EmailVerificationToken.class);
		verify(evtDao, atLeast(0)).save(captor.capture());
		verify(evtDao, atLeast(0)).saveAndFlush(captor.capture());

		assertEquals(captor.getAllValues().size(), 1);
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
		
		assertEquals(tokenService.getPasswordResetTokenStatus(token),
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
		
		assertEquals(tokenService.getPasswordResetTokenStatus(token),
				TokenStatus.EXPIRED);
	}
	@Test
	public void canGetInvalidPasswordResetTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.empty());
		
		assertEquals(tokenService.getPasswordResetTokenStatus(token),
				TokenStatus.INVALID);
	}
	@Test
	public void returnsInvalidPasswordResetTokenStatusOnException()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(prtDao.findById(tokenID)).thenThrow(new RuntimeException());
		
		assertEquals(tokenService.getPasswordResetTokenStatus(token),
				TokenStatus.INVALID);
	}
	@Test
	public void returnsInvalidPasswordResetTokenStatusWhenInvalid()
	{
		String token = "NotAuuidToken";
		
		assertEquals(tokenService.getPasswordResetTokenStatus(token),
				TokenStatus.INVALID);
	}
	
	@Test
	public void canConsumePasswordResetToken()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testEmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.of(
				new PasswordResetToken(tokenID, email, expiryDate)));
		
		assertDoesNotThrow(() -> tokenService.consumePasswordResetToken(token));
	}
	@Test
	public void cannotResetPasswordWithUnknownToken()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.empty());
		
		assertThrows(IllegalArgumentException.class,
				() -> tokenService.consumePasswordResetToken(token));
	}
	@Test
	public void cannotResetPasswordWithExpiredToken()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testEmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date expiryDate = cal.getTime();
		
		when(prtDao.findById(tokenID)).thenReturn(Optional.of(
				new PasswordResetToken(tokenID, email, expiryDate)));
		
		assertThrows(IllegalArgumentException.class,
				() -> tokenService.consumePasswordResetToken(token));
	}
	
	@Test
	public void canVerifyEmail()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testEmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		
		assertDoesNotThrow(() -> tokenService.consumeEmailVerificationToken(token));
	}
	@Test
	public void cannotVerifyEmailWithUnknownToken()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.empty());
		
		assertThrows(IllegalArgumentException.class,
				() -> tokenService.consumeEmailVerificationToken(token));
	}
	@Test
	public void cannotVerifyEmailWithExpiredToken()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		String email = "testEmail@mock.com";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date expiryDate = cal.getTime();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.of(
				new EmailVerificationToken(tokenID, email, expiryDate)));
		
		assertThrows(IllegalArgumentException.class,
				() -> tokenService.consumeEmailVerificationToken(token));
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
		
		assertEquals(tokenService.getEmailVerificationTokenStatus(token),
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
		
		assertEquals(tokenService.getEmailVerificationTokenStatus(token),
				TokenStatus.EXPIRED);
	}
	@Test
	public void canGetInvalidEmailVerificationTokenStatus()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(evtDao.findById(tokenID)).thenReturn(Optional.empty());
		
		assertEquals(tokenService.getEmailVerificationTokenStatus(token),
				TokenStatus.INVALID);
	}
	@Test
	public void returnsInvalidEmailVerificationTokenStatusOnException()
	{
		UUID tokenID = UUID.randomUUID();
		String token = tokenID.toString();
		
		when(evtDao.findById(tokenID)).thenThrow(new RuntimeException());
		
		assertEquals(tokenService.getEmailVerificationTokenStatus(token),
				TokenStatus.INVALID);
	}
	
	@Test
	public void canDeleteExpiredTokens()
	{
		assertDoesNotThrow(() -> tokenService.deleteExpiredTokens());
	}
}
