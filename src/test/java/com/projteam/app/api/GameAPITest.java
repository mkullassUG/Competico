package com.projteam.app.api;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.projteam.app.domain.Account;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.LobbyService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class GameAPITest
{
	@Autowired
	private MockMvc mvc;
	
	private @MockBean LobbyService lobbyService;
	private @MockBean AccountService accountService;
	
	private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldCreateLobbySuccessfully() throws Exception
	{
		mvc.perform(post("/api/v1/lobby"))
			.andExpect(status().isCreated());
		
		verify(lobbyService).createLobby();
		verifyNoMoreInteractions(lobbyService);
	}
	@Test
	public void createLobbyShouldReturnBadRequestWhenNotAuthenticated() throws Exception
	{
		when(lobbyService.createLobby()).thenThrow(new IllegalArgumentException("Not authenticated."));
		
		mvc.perform(post("/api/v1/lobby"))
			.andExpect(status().isBadRequest());
		
		verify(lobbyService).createLobby();
		verifyNoMoreInteractions(lobbyService);
	}
	
	@Test
	public void shouldJoinLobbySuccessfully() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.addPlayer(gameCode)).thenReturn(true);
		
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
			.andExpect(status().isOk());
		
		verify(lobbyService).addPlayer(gameCode);
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
		
		verify(lobbyService).addPlayer(gameCode);
		verify(lobbyService).lobbyExists(gameCode);
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
		
		verify(lobbyService).addPlayer(gameCode);
		verify(lobbyService).lobbyExists(gameCode);
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
			.andExpect(status().isOk());
	}
	@Test
	public void whenLobbyDoesNotExistShouldInformAboutIt() throws Exception
	{
		String gameCode = "gameCode";
		when(lobbyService.lobbyExists(gameCode)).thenReturn(false);
		
		mvc.perform(get("/api/v1/lobby/" + gameCode))
			.andExpect(status().isOk());
	}
	
	@Test
	public void shouldGetPlayerInfo() throws Exception
	{
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.getLobbyForPlayer(host)).thenReturn(gameCode);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		
		mvc.perform(get("/api/v1/playerinfo"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldGetPlayerInfoWhenNotInLobby() throws Exception
	{
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.getLobbyForPlayer(host)).thenReturn(null);
		
		mvc.perform(get("/api/v1/playerinfo"))
			.andExpect(status().isOk());
	}
	@Test
	public void playerInfoShouldReturnBadRequestWhenNotAuthenticated() throws Exception
	{
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.empty());
		
		mvc.perform(get("/api/v1/playerinfo"))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void shouldGetWhetherLobbStatusChanged() throws Exception
	{
		String gameCode = "gameCode";
		Account host = mockHost(PLAYER_ROLE);
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		
		mvc.perform(get("/api/v1/lobby/" + gameCode + "/changes"))
			.andExpect(status().isOk());
		
		verify(lobbyService).hasAnthingChanged(gameCode, host);
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
		
		verify(lobbyService).removePlayer(gameCode, host, player);
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
	
	//---Sources---
	
	private static Account mockHost(String role)
	{
		return new Account.Builder()
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withNickname("TestHost")
				.withPasswordHash("QWERTY")
				.withRoles(List.of(role))
				.build();
	}
	private static Account mockPlayer(String name)
	{
		return new Account.Builder()
				.withEmail("test" + name + "@test.pl")
				.withUsername("Test" + name)
				.withPasswordHash("QWERTY" + name)
				.withRoles(List.of(PLAYER_ROLE))
				.build();
	}
}
