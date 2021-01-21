package com.projteam.app.dao;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.Account;

public interface AccountDAO extends JpaRepository<Account, UUID>
{
	public Optional<Account> findByUsername(String username);
	public Optional<Account> findByEmailOrUsername(String email, String username);
	public boolean existsByUsername(String username);
}