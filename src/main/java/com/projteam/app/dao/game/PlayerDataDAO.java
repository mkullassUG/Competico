package com.projteam.app.dao.game;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.PlayerData;

public interface PlayerDataDAO extends JpaRepository<PlayerData, UUID>
{
	public Optional<PlayerData> findByAccount_id(UUID id);
}
