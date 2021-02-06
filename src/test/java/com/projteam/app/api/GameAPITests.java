package com.projteam.app.api;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.domain.game.tasks.answers.WordFillAnswer;
import com.projteam.app.dto.game.GameResultPersonalDTO;
import com.projteam.app.dto.game.GameResultTotalDTO;
import com.projteam.app.dto.game.GameResultTotalDuringGameDTO;
import com.projteam.app.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.game.GameService;
import com.projteam.app.service.game.GameTaskDataService;
import com.projteam.app.service.game.LobbyService;

@SpringBootTest
@ContextConfiguration(name = "API-tests")
@AutoConfigureMockMvc(addFilters = false)
public class GameAPITests
{
	@Autowired
	private MockMvc mvc;
	
	private @MockBean AccountService accountService;
	private @MockBean LobbyService lobbyService;
	private @MockBean GameService gameService;
	private @MockBean GameTaskDataService gtdService;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private static final MediaType APPLICATION_JSON_UTF8 =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));
	
	@Test
	public void shouldCreateGameSuccessfully() throws Exception
	{
		String gameCode = "gameCode";
		
		when(gameService.createGameFromLobby(gameCode)).thenReturn(true);
		
		mvc.perform(post("/api/v1/lobby/" + gameCode + "/start"))
			.andExpect(status().isCreated())
			.andExpect(content().string("" + true));
		
		verify(gameService, times(1)).createGameFromLobby(gameCode);
		verifyNoMoreInteractions(gameService);
	}
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void shouldGetGameStatus(boolean gameExists) throws Exception
	{
		String gameCode = "gameCode";
		
		when(gameService.gameExists(gameCode)).thenReturn(gameExists);
		
		mvc.perform(get("/api/v1/game/" + gameCode))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exists", is(gameExists)));
		
		verify(gameService, times(1)).gameExists(gameCode);
		verifyNoMoreInteractions(gameService);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 10, 15, 20})
	public void shouldGetCurrentTask(int taskNumber) throws Exception
	{
		String gameCode = "gameCode";
		List<String> text = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		List<EmptySpace> emptySpaces = answers
				.stream()
				.map(ans -> new EmptySpace(ans))
				.collect(Collectors.toList());
		List<String> possibleAnswers = List.of("abc", "def", "ghi", "jkl", "mno", "pqr");
		
		Task task = new WordFill(UUID.randomUUID(), "Test instruction", List.of(),
				new WordFillElement(UUID.randomUUID(), text, emptySpaces, false, possibleAnswers), 100);
		TaskInfoDTO taskInfo = task.prepareTaskInfo(taskNumber, taskNumber * 2);
		
		when(gameService.hasGameFinished(gameCode)).thenReturn(false);
		when(gameService.getCurrentTaskInfo(gameCode)).thenReturn(taskInfo);
		
		String res = mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		
		assertEquals(mapper.readTree(res), mapper.valueToTree(taskInfo));
		
		verify(gameService, times(1)).hasGameFinished(gameCode);
		verify(gameService, times(1)).getCurrentTaskInfo(gameCode);
		verifyNoMoreInteractions(gameService);
	}
	@Test
	public void shouldNotGetCurrentTaskWhenGameFinished() throws Exception
	{
		String gameCode = "gameCode";
		
		when(gameService.hasGameFinished(gameCode)).thenReturn(true);
		
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		
		verify(gameService, times(1)).hasGameFinished(gameCode);
		verifyNoMoreInteractions(gameService);
	}
	@Test
	public void shouldAcceptAnswer() throws Exception
	{
		String gameCode = "gameCode";
		
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		TaskAnswer answer = new WordFillAnswer(answers);
		
		mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.valueToTree(answer)
						.toString()))
			.andExpect(status().isOk());
		
		verify(gameService, times(1)).acceptAnswer(eq(gameCode), 
				(JsonNode) eq(mapper.valueToTree(answer)));
		verifyNoMoreInteractions(gameService);
	}
	@Test
	public void shouldDiscardMaformedAnswer() throws Exception
	{
		String gameCode = "gameCode";
		
		Mockito.doThrow(JsonProcessingException.class)
			.when(gameService)
			.acceptAnswer(eq(gameCode), (JsonNode) any());
		
		mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
				.contentType(APPLICATION_JSON_UTF8)
				.content("{}"))
			.andExpect(status().isBadRequest());
		
		verify(gameService, times(1)).acceptAnswer(eq(gameCode), (JsonNode) any());
		verifyNoMoreInteractions(gameService);
	}
	@Test
	public void shouldGetResultsDuringGame() throws Exception
	{
		UUID gameID = UUID.randomUUID();
		
		GameResultTotalDuringGameDTO grt1 = new GameResultTotalDuringGameDTO(
				"mockUsername1", "mockNickname1", 1250, 8000, true, false);
		GameResultTotalDuringGameDTO grt2 = new GameResultTotalDuringGameDTO(
				"mockUsername2", "mockNickname2", 1000, 7658, false, false);
		Class<GameResultTotalDuringGameDTO> grClass = GameResultTotalDuringGameDTO.class;
		
		when(gameService.getCurrentResults(gameID)).thenReturn(Optional.of(List.of(grt1, grt2)));
		
		String res = mvc.perform(get("/api/v1/scores/" + gameID + "/total"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		
		JsonNode json = mapper.readTree(res);
		assertTrue(json.isArray());
		assertEquals(mapper.treeToValue(json.get(0), grClass), grt1);
		assertEquals(mapper.treeToValue(json.get(1), grClass), grt2);
	}
	@Test
	public void shouldGetResultsAfterGame() throws Exception
	{
		UUID gameID = UUID.randomUUID();
		
		GameResultTotalDTO grt1 = new GameResultTotalDTO(
				"mockUsername1", "mockNickname1", 1250, 10000);
		GameResultTotalDTO grt2 = new GameResultTotalDTO(
				"mockUsername2", "mockNickname2", 1075, 9000);
		Class<GameResultTotalDTO> grClass = GameResultTotalDTO.class;
		
		when(gameService.getCurrentResults(gameID)).thenReturn(Optional.empty());
		when(gameService.getResults(gameID)).thenReturn(Optional.of(List.of(grt1, grt2)));
		
		String res = mvc.perform(get("/api/v1/scores/" + gameID + "/total"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		
		JsonNode json = mapper.readTree(res);
		assertTrue(json.isArray());
		assertEquals(mapper.treeToValue(json.get(0), grClass), grt1);
		assertEquals(mapper.treeToValue(json.get(1), grClass), grt2);
	}
	@Test
	public void shouldGetPersonalResults() throws Exception
	{
		UUID gameID = UUID.randomUUID();
		
		GameResultPersonalDTO grp1 = new GameResultPersonalDTO(1, 10000, 100);
		GameResultPersonalDTO grp2 = new GameResultPersonalDTO(0.75, 22123, 175);
		Class<GameResultPersonalDTO> grClass = GameResultPersonalDTO.class;
		
		when(gameService.getPersonalResults(gameID)).thenReturn(Optional.of(List.of(grp1, grp2)));
		
		String res = mvc.perform(get("/api/v1/scores/" + gameID + "/personal"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		
		JsonNode json = mapper.readTree(res);
		assertTrue(json.isArray());
		assertEquals(mapper.treeToValue(json.get(0), grClass), grp1);
		assertEquals(mapper.treeToValue(json.get(1), grClass), grp2);
	}
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void shouldTellWhetherResultsChanged(boolean resultsChanged) throws Exception
	{
		UUID gameID = UUID.randomUUID();
		
		when(gameService.haveResultsChanged(gameID)).thenReturn(Optional.of(resultsChanged));
		
		mvc.perform(get("/api/v1/scores/" + gameID + "/total/changes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.haveResultsChanged", is(resultsChanged)));
		
		verify(gameService, times(1)).haveResultsChanged(gameID);
		verifyNoMoreInteractions(gameService);
	}
	@Test
	public void shouldReturnEmptyIfGameNotExists() throws Exception
	{
		UUID gameID = UUID.randomUUID();
		
		when(gameService.haveResultsChanged(gameID)).thenReturn(Optional.empty());
		
		mvc.perform(get("/api/v1/scores/" + gameID + "/total/changes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.gameExists", is(false)));
		
		verify(gameService, times(1)).haveResultsChanged(gameID);
		verifyNoMoreInteractions(gameService);
	}
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void shouldReturnBadRequestWhenCannotAccessRequests(boolean resultsChanged) throws Exception
	{
		UUID gameID = UUID.randomUUID();
		String msg = "Insufficient permissions to view this game";
		
		when(gameService.haveResultsChanged(gameID))
			.thenThrow(new IllegalArgumentException(
					msg));
		
		mvc.perform(get("/api/v1/scores/" + gameID + "/total/changes"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(msg));
		
		verify(gameService, times(1)).haveResultsChanged(gameID);
		verifyNoMoreInteractions(gameService);
	}
	
	@Test
	public void shouldNoteInteractionSuccessfully() throws Exception
	{
		String gameCode = "gameCode";
		
		mvc.perform(post("/api/v1/game/" + gameCode + "/ping"))
			.andExpect(status().isOk());
		
		verify(gameService).noteInteraction(gameCode);
	}
	@ParameterizedTest
	@ValueSource(ints = {-5, -1, 0, 1, 2, 5, 25, 100, 9999})
	public void shouldGetGameHistorySuccessfully(int page) throws Exception
	{
		when(gameService.getHistory(anyInt()))
			.thenReturn(new PageImpl<>(List.of()));
		if (page < 1)
		{
			mvc.perform(get("/api/v1/game/history/" + page))
				.andExpect(redirectedUrl("api/v1/game/history/1"));
		}
		else
		{
			mvc.perform(get("/api/v1/game/history/" + page))
				.andExpect(status().isOk());
			
			verify(gameService).getHistory(page - 1);
		}
	}
	
	@Test
	public void shouldGetGameResultsView() throws Exception
	{
		UUID gameID = UUID.randomUUID();
		mvc.perform(get("/game/results/" + gameID))
			.andExpect(status().isOk());
	}
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 5, 25, 100, 9999})
	public void shouldGetGameHistoryView(int page) throws Exception
	{
		mvc.perform(get("/game/history/" + page))
			.andExpect(status().isOk());
	}
}
