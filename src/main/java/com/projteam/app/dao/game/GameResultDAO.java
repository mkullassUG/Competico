package com.projteam.app.dao.game;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.GameResult;

public interface GameResultDAO extends JpaRepository<GameResult, UUID>
{
	Page<GameResult> findAllByResults_PlayerID(UUID playerID, Pageable pageable);
}