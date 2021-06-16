package com.projteam.competico.dao;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.Account;

public interface AccountDAO extends JpaRepository<Account, UUID>
{
	public Optional<Account> findByEmail(String email);
	public Optional<Account> findByUsername(String username);
	public Optional<Account> findByEmailOrUsername(String email, String username);
	public boolean existsByUsername(String username);
	public boolean existsByUsernameOrEmail(String username, String email);
}