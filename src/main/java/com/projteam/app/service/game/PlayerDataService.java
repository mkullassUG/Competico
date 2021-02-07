package com.projteam.app.service.game;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static com.projteam.app.utils.Initializable.init;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.PlayerDataDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.PlayerData;

@Service
public class PlayerDataService
{
	private PlayerDataDAO playerDataDao;
	
	public static final int DEFAULT_RATING = 1000;
	
	@Autowired
	public PlayerDataService(PlayerDataDAO playerDataDao)
	{
		this.playerDataDao = playerDataDao;
	}
	
	@Transactional
	public Optional<PlayerData> getPlayerData(Account acc)
	{
		if (acc == null)
			return Optional.empty();
		if (!acc.hasRole(PLAYER_ROLE))
			return Optional.empty();
		
		Optional<PlayerData> ret = playerDataDao.findByAccount_id(acc.getId())
				.map(pd -> init(pd));
		if (ret.isPresent())
			return ret;
		
		PlayerData pd = new PlayerData(UUID.randomUUID(), acc, DEFAULT_RATING);
		pd = init(playerDataDao.save(pd));
		
		return Optional.of(pd);
	}
	@Transactional
	public PlayerData savePlayerData(PlayerData pd)
	{
		if (pd == null)
			return null;
		if (!pd.getAccount().hasRole(PLAYER_ROLE))
			return null;
		
		return playerDataDao.save(pd);
	}
}
