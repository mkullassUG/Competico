package com.projteam.app.service.game;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static com.projteam.app.utils.Initializable.init;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.PlayerDataDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.PlayerData;
import com.projteam.app.dto.game.LeaderboardEntryDTO;
import com.projteam.app.service.AccountService;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
		if (!pd.getAccount().hasRole(PLAYER_ROLE))
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
		
		System.out.println(position);
		System.out.println(start);
		
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
	
	@EqualsAndHashCode
	@ToString
	private static class OffsetBasedPageRequest implements Pageable
	{
		private int limit;
		private long offset;
		private final Sort sort;
		
		private OffsetBasedPageRequest(long offset, int limit, Sort sort)
		{
			if (offset < 0)
				throw new IllegalArgumentException("Offset index must not be less than zero!");
	
			if (limit < 1)
				throw new IllegalArgumentException("Limit must not be less than one!");
			
			this.limit = limit;
			this.offset = offset;
			this.sort = sort;
		}
		
		public static OffsetBasedPageRequest of(long offset, int limit, Sort sort)
		{
			return new OffsetBasedPageRequest(offset, limit, sort);
		}
		
		@Override
		public int getPageNumber()
		{
			return (int) (offset / limit);
		}
		@Override
		public int getPageSize()
		{
			return limit;
		}
		@Override
		public long getOffset()
		{
			return offset;
		}
		@Override
		public Sort getSort()
		{
			return sort;
		}
		@Override
		public Pageable next()
		{
			return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
		}
		public OffsetBasedPageRequest previous()
		{
			return hasPrevious() ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
		}
		@Override
		public Pageable previousOrFirst()
		{
			return hasPrevious() ? previous() : first();
		}
		@Override
		public Pageable first()
		{
			return new OffsetBasedPageRequest(0, getPageSize(), getSort());
		}
		@Override
		public boolean hasPrevious()
		{
			return offset > limit;
		}
	}
}
