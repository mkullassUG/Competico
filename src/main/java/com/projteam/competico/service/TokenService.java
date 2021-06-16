package com.projteam.competico.service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.projteam.competico.dao.EmailVerificationTokenDAO;
import com.projteam.competico.dao.PasswordResetTokenDAO;
import com.projteam.competico.domain.EmailVerificationToken;
import com.projteam.competico.domain.PasswordResetToken;
import com.projteam.competico.domain.TokenStatus;

@Service
public class TokenService
{
	private PasswordResetTokenDAO prtDao;
	private EmailVerificationTokenDAO evtDao;
	
	private static final int TOKEN_VALIDITY_TIME = 7;
	private static final int TOKEN_REMOVAL_WAITING_TIME = 7;
	
	public TokenService(PasswordResetTokenDAO prtDao,
			EmailVerificationTokenDAO evtDao)
	{
		this.prtDao = prtDao;
		this.evtDao = evtDao;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public PasswordResetToken generatePasswordResetToken(String email)
	{
		prtDao.deleteByEmail(email);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, TOKEN_VALIDITY_TIME);
		Date expiryDate = cal.getTime();
		prtDao.flush();
		
		return prtDao.saveAndFlush(new PasswordResetToken(UUID.randomUUID(), email, expiryDate));
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public EmailVerificationToken generateEmailVerificationToken(String email)
	{
		evtDao.deleteByEmail(email);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, TOKEN_VALIDITY_TIME);
		Date expiryDate = cal.getTime();
		evtDao.flush();
		
		return evtDao.saveAndFlush(new EmailVerificationToken(UUID.randomUUID(), email, expiryDate));
	}
	
	@Transactional
	public TokenStatus getPasswordResetTokenStatus(String token)
	{
		try
		{
			return prtDao.findById(UUID.fromString(token))
					.map(tok -> tok.getExpiryDate().before(new Date())?
							TokenStatus.EXPIRED:TokenStatus.VALID)
					.orElse(TokenStatus.INVALID);
		}
		catch (Exception e)
		{
			return TokenStatus.INVALID;
		}
	}
	public TokenStatus getEmailVerificationTokenStatus(String token)
	{
		try
		{
			return evtDao.findById(UUID.fromString(token))
					.map(tok -> tok.getExpiryDate().before(new Date())?
							TokenStatus.EXPIRED:TokenStatus.VALID)
					.orElse(TokenStatus.INVALID);
		}
		catch (Exception e)
		{
			return TokenStatus.INVALID;
		}
	}
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public PasswordResetToken consumePasswordResetToken(String token)
	{
		UUID tokenId = null;
		try
		{
			tokenId = UUID.fromString(token);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("INVALID_TOKEN");
		}
		var ret = prtDao.findById(tokenId)
			.orElseThrow(() -> new IllegalArgumentException("INVALID_TOKEN"));
		if (ret.getExpiryDate().before(new Date()))
			throw new IllegalArgumentException("EXPIRED_TOKEN");
		prtDao.deleteById(tokenId);
		prtDao.flush();
		
		return ret;
	}
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public EmailVerificationToken consumeEmailVerificationToken(String token)
	{
		UUID tokenId = null;
		try
		{
			tokenId = UUID.fromString(token);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("INVALID_TOKEN");
		}
		EmailVerificationToken evt = evtDao.findById(tokenId)
				.orElseThrow(() -> new IllegalArgumentException("INVALID_TOKEN"));
		if (evt.getExpiryDate().before(new Date()))
			throw new IllegalArgumentException("EXPIRED_TOKEN");
		evtDao.deleteById(tokenId);
		evtDao.flush();
		
		return evt;
	}
	
	@Scheduled(cron = "0 0 3 * * *")
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void deleteExpiredTokens()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -TOKEN_REMOVAL_WAITING_TIME);
		Date date = cal.getTime();
		
		prtDao.deleteByExpiryDateLessThan(date);
		prtDao.flush();
		evtDao.deleteByExpiryDateLessThan(date);
		evtDao.flush();
	}
}
