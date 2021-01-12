package com.projteam.app.dao.game;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.GameResults;

public interface GameResultsDAO extends JpaRepository<GameResults, UUID>
{}