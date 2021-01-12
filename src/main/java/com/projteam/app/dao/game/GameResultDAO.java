package com.projteam.app.dao.game;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.GameResult;

public interface GameResultDAO extends JpaRepository<GameResult, UUID>
{
	public Optional<GameResult> findByPlayerID(UUID id);
}