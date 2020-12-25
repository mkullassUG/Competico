package com.projteam.app.dao;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.Account;

public interface AccountDAO extends JpaRepository<Account, UUID>
{}