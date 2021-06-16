package com.projteam.competico.dao.game;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.projteam.competico.domain.game.PlayerData;

public interface PlayerDataDAO extends JpaRepository<PlayerData, UUID>
{
	public Optional<PlayerData> findByAccount_id(UUID id);
	@Query("select count(*) from PlayerData pd "
			+ "where pd.rating > :rating "
			+ "or (pd.rating = :rating and pd.account.username > :username)")
	public int getPositionOnLeaderboard(int rating, String username);
}
