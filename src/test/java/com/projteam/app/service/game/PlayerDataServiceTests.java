package com.projteam.app.service.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.projteam.app.dao.game.PlayerDataDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.PlayerData;
import com.projteam.app.service.AccountService;

class PlayerDataServiceTests
{
	private @Mock PlayerDataDAO playerDataDao;
	private @Mock AccountService accountService;
	
	private @InjectMocks PlayerDataService pdService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void getPlayerDataWithNullAccountReturnsEmpty()
	{
		assertTrue(pdService.getPlayerData(null).isEmpty());
	}
	@Test
	public void getPlayerDataWithNonPlayerAccountReturnsEmpty()
	{
		assertTrue(pdService.getPlayerData(mockAccount()).isEmpty());
	}
	@Test
	public void canGetPlayerDataWhenInDatabase()
	{
		Account acc = mockAccount(Account.PLAYER_ROLE);
		PlayerData pd = new PlayerData(UUID.randomUUID(), acc, 1000);
		
		when(playerDataDao.findByAccount_id(acc.getId()))
			.thenReturn(Optional.of(pd));
		
		var res = pdService.getPlayerData(acc);
		assertTrue(res.isPresent());
		assertEquals(res.get().getId(), pd.getId());
		assertEquals(res.get().getAccount(), acc);
		assertEquals(res.get().getRating(), pd.getRating());
	}
	@Test
	public void canGetPlayerDataWhenNotInDatabase()
	{
		Account acc = mockAccount(Account.PLAYER_ROLE);
		
		when(playerDataDao.findByAccount_id(acc.getId()))
			.thenReturn(Optional.empty());
		when(playerDataDao.save(any())).thenAnswer(args -> args.getArgument(0));
		
		var res = pdService.getPlayerData(acc);
		assertTrue(res.isPresent());
		assertEquals(res.get().getAccount(), acc);
	}
	
	@Test
	public void savePlayerDataWithNullReturnsNull()
	{
		assertNull(pdService.savePlayerData(null));
	}
	@Test
	public void savePlayerDataWithNullAccountReturnsNull()
	{
		assertNull(pdService.savePlayerData(new PlayerData(UUID.randomUUID(), null, 1234)));
	}
	@Test
	public void savePlayerDataWithNonPlayerAccountReturnsNull()
	{
		assertNull(pdService.savePlayerData(new PlayerData(UUID.randomUUID(), mockAccount(), 1234)));
	}
	@Test
	public void canSaveCorrectPlayerData()
	{
		PlayerData pd = new PlayerData(
				UUID.randomUUID(), mockAccount(Account.PLAYER_ROLE), 1234);
		
		when(playerDataDao.save(pd)).thenReturn(pd);
		
		assertNotNull(pdService.savePlayerData(pd));
	}
	
	@Test
	public void canGetTopLeaderboard()
	{
		Page<PlayerData> page = new PageImpl<>(List.of(
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user2", Account.PLAYER_ROLE),
						1200),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user1", Account.PLAYER_ROLE),
						1170),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user3", Account.PLAYER_ROLE),
						11500),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user5", Account.PLAYER_ROLE),
						1000),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user6", Account.PLAYER_ROLE),
						865)
				));
		
		when(playerDataDao.findAll((Pageable) any()))
			.thenReturn(page);
		
		var res = pdService.getTopLeaderboard();
		
		assertNotNull(res);
		assertNotEquals(res.size(), 0);
	}
	@Test
	public void canGetRelativeLeaderboard()
	{
		Account currAcc = mockAccountWithName("user3", Account.PLAYER_ROLE);
		PlayerData currPd = new PlayerData(UUID.randomUUID(), currAcc, 11500);
		Page<PlayerData> page = new PageImpl<>(List.of(
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user2", Account.PLAYER_ROLE),
						1200),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user1", Account.PLAYER_ROLE),
						1170),
				currPd,
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user5", Account.PLAYER_ROLE),
						1000),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user6", Account.PLAYER_ROLE),
						865)
				));
		
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(currAcc));
		when(playerDataDao.findByAccount_id(currAcc.getId()))
			.thenReturn(Optional.of(currPd));
		when(playerDataDao.getPositionOnLeaderboard(
				currPd.getRating(), currAcc.getUsername()))
			.thenReturn(7);
		when(playerDataDao.findAll((Pageable) any()))
			.thenReturn(page);
		
		var res = pdService.getRelativeLeaderboard();
		
		assertNotNull(res);
		assertEquals(res.size(), page.getContent().size());
	}
	@Test
	public void canGetRelativeLeaderboardWithAccount()
	{
		Account currAcc = mockAccountWithName("user3", Account.PLAYER_ROLE);
		PlayerData currPd = new PlayerData(UUID.randomUUID(), currAcc, 11500);
		Page<PlayerData> page = new PageImpl<>(List.of(
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user2", Account.PLAYER_ROLE),
						1200),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user1", Account.PLAYER_ROLE),
						1170),
				currPd,
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user5", Account.PLAYER_ROLE),
						1000),
				new PlayerData(UUID.randomUUID(),
						mockAccountWithName("user6", Account.PLAYER_ROLE),
						865)
				));
		
		when(playerDataDao.findByAccount_id(currAcc.getId()))
			.thenReturn(Optional.of(currPd));
		when(playerDataDao.getPositionOnLeaderboard(
				currPd.getRating(), currAcc.getUsername()))
			.thenReturn(7);
		when(playerDataDao.findAll((Pageable) any()))
			.thenReturn(page);
		
		var res = pdService.getRelativeLeaderboard(currAcc);
		
		assertNotNull(res);
		assertEquals(res.size(), page.getContent().size());
	}
	
	//---Helpers---
	
	private static Account mockAccount(String... roles)
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withUsername("username")
				.withNickname("nickname")
				.withEmail("user@email.pl")
				.withPassword("pass")
				.withRoles(List.of(roles))
				.build();
	}
	private static Account mockAccountWithName(String name, String... roles)
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withUsername(name)
				.withNickname(name)
				.withEmail("user@email.pl")
				.withPassword("pass")
				.withRoles(List.of(roles))
				.build();
	}
}
