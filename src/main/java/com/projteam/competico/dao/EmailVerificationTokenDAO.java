package com.projteam.competico.dao;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.EmailVerificationToken;

public interface EmailVerificationTokenDAO extends JpaRepository<EmailVerificationToken, UUID>
{
	public Optional<EmailVerificationToken> findByEmail(String email);
	public void deleteByEmail(String email);
	public void deleteByExpiryDateLessThan(Date expiryDate);
}
