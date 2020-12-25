package com.projteam.app.dao;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.GameResults;

public interface GameResultDAO extends JpaRepository<GameResults, UUID>
{}