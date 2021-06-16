package com.projteam.competico.service.game;

import static com.projteam.competico.domain.Account.PLAYER_ROLE;
import static com.projteam.competico.utils.Initializable.init;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.PlayerDataDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.game.PlayerData;
import com.projteam.competico.dto.game.LeaderboardEntryDTO;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.utils.OffsetBasedPageRequest;

@Service
public class PlayerDataService
{
	private PlayerDataDAO playerDataDao;
	private AccountService accountService;
	
	public static final int DEFAULT_RATING = 1000;
	public static final int LEADERBOARD_SNIPPET_SIZE = 5;
	
	@Autowired
	public PlayerDataService(PlayerDataDAO playerDataDao,
			AccountService accountService)
	{
		this.playerDataDao = playerDataDao;
		this.accountService = accountService;
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
		Account acc = pd.getAccount();
		if ((acc == null) || !acc.hasRole(PLAYER_ROLE))
			return null;
		
		return playerDataDao.save(pd);
	}
	
	@Transactional
	public List<LeaderboardEntryDTO> getTopLeaderboard()
	{
		Page<PlayerData> page = playerDataDao.findAll(
				PageRequest.of(0, LEADERBOARD_SNIPPET_SIZE,
				Sort.by(Order.desc("rating"), Order.desc("account.username"))));
		
		List<LeaderboardEntryDTO> ret = new ArrayList<>();
		
		int pos = 1;
		for (PlayerData entry: page)
		{
			Account acc = entry.getAccount();
			ret.add(new LeaderboardEntryDTO(
					acc.getUsername(),
					acc.getNickname(),
					pos, entry.getRating()));
			pos++;
		}
		return ret;
	}
	@Transactional
	public List<LeaderboardEntryDTO> getRelativeLeaderboard()
	{
		return getRelativeLeaderboard(getAccount());
	}
	@Transactional
	public List<LeaderboardEntryDTO> getRelativeLeaderboard(Account acc)
	{
		if (acc == null)
			return List.of();
		PlayerData currentPD = playerDataDao.findByAccount_id(acc.getId())
				.orElse(null);
		if (currentPD == null)
			return List.of();
		
		int position = playerDataDao.getPositionOnLeaderboard(
				currentPD.getRating(), acc.getUsername());
		int start = Math.max(position - (LEADERBOARD_SNIPPET_SIZE / 2), 0);
		
		Page<PlayerData> page = playerDataDao.findAll(
				OffsetBasedPageRequest.of(start, LEADERBOARD_SNIPPET_SIZE,
				Sort.by(Order.desc("rating"), Order.desc("account.username"))));
		
		List<LeaderboardEntryDTO> ret = new ArrayList<>();
		
		int curr = start + 1;
		for (PlayerData entry: page)
		{
			Account currAcc = entry.getAccount();
			ret.add(new LeaderboardEntryDTO(
					currAcc.getUsername(),
					currAcc.getNickname(),
					curr, entry.getRating()));
			curr++;
		}
		
		return ret;
	}
	
	private Account getAccount()
	{
		return accountService.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}
}
