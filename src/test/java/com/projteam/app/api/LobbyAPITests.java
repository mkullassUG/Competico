package com.projteam.app.api;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.domain.Account;
import com.projteam.app.dto.lobby.LobbyOptionsDTO;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.game.GameService;
import com.projteam.app.service.game.GameTaskDataService;
import com.projteam.app.service.game.LobbyService;

@SpringBootTest
@ContextConfiguration(name = "API-tests")
@AutoConfigureMockMvc(addFilters = false)
public class LobbyAPITests
{
	@Autowired
	private MockMvc mvc;
	
	private @MockBean AccountService accountService;
	private @MockBean LobbyService lobbyService;
	private @MockBean GameService gameService;
	private @MockBean GameTaskDataService gtdService;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@Test
	public void shouldCreateLobbySuccessfully() throws Exception
	{
		mvc.perform(post("/api/v1/lobby"))
			.andExpect(status().isCreated());
		
		verify(lobbyService, times(1)).createLobby();
		verifyNoMoreInteractions(lobbyService);
	}
	@Test
	public void createLobbyShouldReturnBadRequestWhenNotAuthenticated() throws Exception
	{
		when(lobbyService.createLobby()).thenThrow(new IllegalArgumentException("Not authenticated."));
		
		mvc.perform(post("/api/v1/lobby"))
			.andExpect(status().isBadRequest());
		
		verify(lobbyService, times(1)).createLobby();
		verifyNoMoreInteractions(lobbyService);
	}
	
	@Test
	public void shouldJoinLobbySuccessfully() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.addPlayer(gameCode)).thenReturn(true);
		
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
			.andExpect(status().isOk());
		
		verify(lobbyService, times(1)).addPlayer(gameCode);
		verifyNoMoreInteractions(lobbyService);
	}
	@Test
	public void joinLobbyShouldReturnBadRequestWhenAlreadyIn() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.addPlayer(gameCode)).thenReturn(false);
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
			.andExpect(status().isBadRequest());
		
		verify(lobbyService, times(1)).addPlayer(gameCode);
		verify(lobbyService, times(1)).lobbyExists(gameCode);
		verifyNoMoreInteractions(lobbyService);
	}
	@Test
	public void joinLobbyShouldReturnBadRequestWhenLobbyDoesNotExist() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.addPlayer(gameCode)).thenReturn(false);
		when(lobbyService.lobbyExists(gameCode)).thenReturn(false);
		
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
			.andExpect(status().isBadRequest());
		
		verify(lobbyService, times(1)).addPlayer(gameCode);
		verify(lobbyService, times(1)).lobbyExists(gameCode);
		verifyNoMoreInteractions(lobbyService);
	}
	
	@Test
	public void shouldGetLobbyStatus() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.isLobbyFull(gameCode)).thenReturn(false);
		when(lobbyService.getMaximumPlayerCount(gameCode)).thenReturn(20);
		when(lobbyService.getMaximumPlayerCount(gameCode)).thenReturn(20);
		Account host = mockHost(PLAYER_ROLE);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.getPlayers(gameCode)).thenReturn(List.of(host));
		
		mvc.perform(get("/api/v1/lobby/" + gameCode))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exists", is(true)));
	}
	@Test
	public void whenLobbyDoesNotExistShouldInformAboutIt() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.lobbyExists(gameCode)).thenReturn(false);
		
		mvc.perform(get("/api/v1/lobby/" + gameCode))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exists", is(false)));
	}
	
	@Test
	public void shouldGetPlayerInfo() throws Exception
	{
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.getLobbyForAccount(host)).thenReturn(Optional.of(gameCode));
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		
		mvc.perform(get("/api/v1/playerinfo"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username", is(host.getUsername())))
			.andExpect(jsonPath("$.nickname", is(host.getNickname())))
			.andExpect(jsonPath("$.isHost", is(true)))
			.andExpect(jsonPath("$.gameStarted", is(false)))
			.andExpect(jsonPath("$.gameCode", is(gameCode)));
	}
	@Test
	public void shouldGetPlayerInfoWhenInGame() throws Exception
	{
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.getLobbyForAccount(host)).thenReturn(Optional.empty());
		when(gameService.getGameForAccount(host)).thenReturn(Optional.of(gameCode));
		when(gameService.getGameID(gameCode)).thenReturn(UUID.randomUUID());
		
		mvc.perform(get("/api/v1/playerinfo"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username", is(host.getUsername())))
			.andExpect(jsonPath("$.nickname", is(host.getNickname())))
			.andExpect(jsonPath("$.gameStarted", is(true)))
			.andExpect(jsonPath("$.gameCode", is(gameCode)));
	}
	@Test
	public void shouldGetPlayerInfoWhenNotInLobbyOrInGame() throws Exception
	{
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.getLobbyForAccount(host)).thenReturn(Optional.empty());
		when(gameService.getGameForAccount(host)).thenReturn(Optional.empty());
		
		mvc.perform(get("/api/v1/playerinfo"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username", is(host.getUsername())))
			.andExpect(jsonPath("$.nickname", is(host.getNickname())));
	}
	@Test
	public void playerInfoShouldReturnBadRequestWhenNotAuthenticated() throws Exception
	{
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.empty());
		
		mvc.perform(get("/api/v1/playerinfo"))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void shouldSeeWhetherLobbyStatusChanged() throws Exception
	{
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.hasAnythingChanged(gameCode, host)).thenReturn(Optional.empty());
		when(gameService.gameExists(gameCode)).thenReturn(false);
		
		mvc.perform(get("/api/v1/lobby/" + gameCode + "/changes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lobbyExists", is(false)));
		
		verify(lobbyService, times(1)).hasAnythingChanged(gameCode, host);
		verifyNoMoreInteractions(lobbyService);
	}
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void shouldSeeWhetherLobbyStatusChanged(boolean lobbyStatusChanged) throws Exception
	{
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.hasAnythingChanged(gameCode, host)).thenReturn(Optional.of(lobbyStatusChanged));
		when(gameService.gameExists(gameCode)).thenReturn(false);
		
		mvc.perform(get("/api/v1/lobby/" + gameCode + "/changes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lobbyContentChanged", is(lobbyStatusChanged)))
			.andExpect(jsonPath("$.gameStarted", is(false)));
		
		verify(lobbyService, times(1)).hasAnythingChanged(gameCode, host);
		verifyNoMoreInteractions(lobbyService);
	}
	@Test
	public void shouldSeeWhenGameStarted() throws Exception
	{
		UUID gameID = UUID.randomUUID();
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.hasAnythingChanged(gameCode, host)).thenReturn(Optional.of(false));
		when(gameService.gameExists(gameCode)).thenReturn(true);
		when(gameService.getGameID(gameCode)).thenReturn(gameID);
		
		mvc.perform(get("/api/v1/lobby/" + gameCode + "/changes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lobbyContentChanged", is(false)))
			.andExpect(jsonPath("$.gameStarted", is(true)))
			.andExpect(jsonPath("$.gameID", is(gameID.toString())));
		
		verify(lobbyService, times(1)).hasAnythingChanged(gameCode, host);
		verifyNoMoreInteractions(lobbyService);
	}
	
	@Test
	public void shouldRemovePlayerSuccessfully() throws Exception
	{
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		Account player = mockPlayer("player");
		
		when(accountService.findByUsername(player.getUsername())).thenReturn(Optional.of(player));
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		
		mvc.perform(delete("/api/v1/lobby/" + gameCode + "/players")
				.contentType(APPLICATION_JSON_UTF8)
				.content(player.getUsername()))
			.andExpect(status().isOk());
		
		verify(lobbyService, times(1)).removePlayer(gameCode, host, player);
	}
	
	@ParameterizedTest
	@CsvSource(value = 
	{
			"gameCode,10,false", "gameCode,10,true",
			"gameCode,15,false", "gameCode,15,true",
			"gameCode,20,false", "gameCode,20,true",
			"gameCode,25,false", "gameCode,25,true",
			"gameCode,50,false", "gameCode,50,true"
	})
	public void shouldUpdateLobbyOptionsSuccessfully(
			String gameCode,
			int maxPlayerSize,
			boolean allowsRandomPlayers) throws Exception
	{
		LobbyOptionsDTO loDto = new LobbyOptionsDTO(maxPlayerSize, allowsRandomPlayers);
		
		when(lobbyService.updateOptions(gameCode, loDto)).thenReturn(true);
		
		mvc.perform(put("/api/v1/lobby/" + gameCode)
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(loDto)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
		
		verify(lobbyService, times(1)).updateOptions(gameCode, loDto);
		verifyNoMoreInteractions(lobbyService);
	}
	@Test
	public void shouldFindRandomLobby() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.getRandomLobby()).thenReturn(gameCode);
		
		mvc.perform(get("/api/v1/lobby/random"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldNotFindRandomLobbyWhenNoneIsAvailable() throws Exception
	{
		when(lobbyService.getRandomLobby()).thenReturn(null);
		mvc.perform(get("/api/v1/lobby/random"))
			.andExpect(status().isNotFound());
	}
	@Test
	public void shouldDeleteLobbyWhenHostLeaves() throws Exception
	{
		String gameCode = "gameCode";
		
		when(lobbyService.isHost(gameCode)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode)).thenReturn(true);
		
		mvc.perform(post("/api/v1/lobby/" + gameCode + "/leave"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
		
		verify(lobbyService, times(1)).isHost(gameCode);
		verify(lobbyService, times(1)).deleteLobby(gameCode);
		verifyNoMoreInteractions(lobbyService);
	}
	@Test
	public void shouldRemovePlayerWhenPlayerLeaves() throws Exception
	{
		String gameCode = "gameCode";
		
		when(lobbyService.isHost(gameCode)).thenReturn(false);
		when(lobbyService.removePlayer(gameCode)).thenReturn(true);
		
		mvc.perform(post("/api/v1/lobby/" + gameCode + "/leave"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
		
		verify(lobbyService, times(1)).isHost(gameCode);
		verify(lobbyService, times(1)).removePlayer(gameCode);
		verifyNoMoreInteractions(lobbyService);
	}
	
	//---Sources---
	
	private static Account mockHost(String role)
	{
		return new Account.Builder()
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withNickname("TestHost")
				.withPassword("QWERTY")
				.withRoles(List.of(role))
				.build();
	}
	private static Account mockPlayer(String name)
	{
		return new Account.Builder()
				.withEmail("test" + name + "@test.pl")
				.withUsername("Test" + name)
				.withPassword("QWERTY" + name)
				.withRoles(List.of(PLAYER_ROLE))
				.build();
	}
	
	//---Helpers---
	
	public String toJson(Object o) throws JsonProcessingException
	{
		return mapper.writeValueAsString(o);
	}
}
