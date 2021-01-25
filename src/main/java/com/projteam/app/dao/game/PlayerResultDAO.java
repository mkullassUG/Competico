package com.projteam.app.dao.game;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.PlayerResult;

public interface PlayerResultDAO extends JpaRepository<PlayerResult, UUID>
{
	public Optional<PlayerResult> findByPlayerID(UUID id);
}