package com.projteam.app.integration.game;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.config.SecurityContextConfig;
import com.projteam.app.dao.AccountDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.dto.game.tasks.show.ChoiceWordFillElementDTO;
import com.projteam.app.dto.game.tasks.show.ChronologicalOrderDTO;
import com.projteam.app.dto.game.tasks.show.ListChoiceWordFillDTO;
import com.projteam.app.dto.game.tasks.show.ListWordFillDTO;
import com.projteam.app.dto.game.tasks.show.MultipleChoiceElementDTO;
import com.projteam.app.dto.game.tasks.show.SingleChoiceDTO;
import com.projteam.app.dto.game.tasks.show.WordConnectDTO;
import com.projteam.app.dto.game.tasks.show.WordFillElementDTO;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.game.GameService;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc(addFilters = false)
public class GameTestsWithDatabase
{
	@Autowired
	private MockMvc mvc;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private @MockBean SecurityContextConfig secConf;
	
	private @Autowired AccountDAO accDao;
	private @Autowired AccountService accServ;
	private @Autowired GameService gameServ;
	
	private Account host;
	private Account player;
	private int playerCount;
	
	private String currentUsername;
	
	private static final MediaType APPLICATION_JSON_UTF8 =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));
	
	@BeforeAll
	public void initAccounts()
	{
		host = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testHost@test.pl")
				.withUsername("TestHostAccount")
				.withNickname("TestHostAccount")
				.withPassword("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		player = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testPlayer@test.pl")
				.withUsername("TestPlayerAccount")
				.withNickname("TestPlayerAccount")
				.withPassword("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		playerCount = 2;
		
		currentUsername = host.getUsername();
		
		host = accDao.save(host);
		player = accDao.save(player);
	}
	
	@RepeatedTest(value = 15)
	public void canCompleteFullGameWhileGettingResults() throws Exception
	{	
		SecurityContext sec = mock(SecurityContext.class);
		when(secConf.getContext()).thenReturn(sec);
		
		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenAnswer(arg ->
			accServ.loadUserByUsername(currentUsername));
		
		when(sec.getAuthentication()).thenReturn(auth);
		
		String hostUsername = host.getUsername();
		String playerUsername = player.getUsername();
		
		currentUsername = hostUsername;
		String gameCode = mvc.perform(post("/api/v1/lobby"))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertNotNull(gameCode);
		
		currentUsername = playerUsername;
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
				.andExpect(status().isOk());
		
		currentUsername = hostUsername;
		mvc.perform(post("/api/v1/lobby/" + gameCode + "/start"))
				.andExpect(status().isCreated());
		
		String gameID = mapper.readTree(mvc.perform(get("/api/v1/playerinfo"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString())
				.get("gameID")
				.asText();
		
		int taskCount = gameServ.getTaskCount(gameCode);
		for (int i = 0; i < taskCount; i++)
		{
			currentUsername = hostUsername;
			JsonNode ti1 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
			mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
					.contentType(APPLICATION_JSON_UTF8)
					.content(sampleAnswer(ti1)))
				.andExpect(status().isOk());
			
			currentUsername = playerUsername;
			JsonNode ti2 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
				mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
						.contentType(APPLICATION_JSON_UTF8)
						.content(sampleAnswer(ti2)))
					.andExpect(status().isOk());
				
			assertCanGetResults(gameID, playerCount, i + 1, hostUsername, playerUsername, sec);
		}
		
		assertFalse(gameServ.gameExists(gameCode));
		
		currentUsername = hostUsername;
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		currentUsername = playerUsername;
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		
		assertCanGetResults(gameID, playerCount, taskCount, hostUsername, playerUsername, sec);
	}
	@RepeatedTest(value = 15)
	public void canCompleteFullGameWithoutGettingResultsMidGame() throws Exception
	{
		SecurityContext sec = mock(SecurityContext.class);
		when(secConf.getContext()).thenReturn(sec);
		
		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenAnswer(arg ->
			accServ.loadUserByUsername(currentUsername));
		
		when(sec.getAuthentication()).thenReturn(auth);
		
		Authentication hostAuth = mock(Authentication.class);
		Authentication playerAuth = mock(Authentication.class);
		when(hostAuth.getPrincipal()).thenReturn(host);
		when(hostAuth.isAuthenticated()).thenReturn(true);
		when(playerAuth.getPrincipal()).thenReturn(player);
		when(playerAuth.isAuthenticated()).thenReturn(true);
		
		String hostUsername = host.getUsername();
		String playerUsername = player.getUsername();
		
		currentUsername = hostUsername;
		String gameCode = mvc.perform(post("/api/v1/lobby"))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertNotNull(gameCode);
		
		currentUsername = playerUsername;
		mvc.perform(post("/api/v1/lobby/join/" + gameCode))
				.andExpect(status().isOk());
		
		currentUsername = hostUsername;
		mvc.perform(post("/api/v1/lobby/" + gameCode + "/start"))
				.andExpect(status().isCreated());
		
		String gameID = mapper.readTree(mvc.perform(get("/api/v1/playerinfo"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString())
				.get("gameID")
				.asText();
		
		int taskCount = gameServ.getTaskCount(gameCode);
		for (int i = 0; i < taskCount; i++)
		{
			currentUsername = hostUsername;
			JsonNode ti1 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
			mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
					.contentType(APPLICATION_JSON_UTF8)
					.content(sampleAnswer(ti1)))
				.andExpect(status().isOk());
			
			currentUsername = playerUsername;
			JsonNode ti2 = mapper.readTree(
				mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.currentTaskNumber", is(i)))
					.andExpect(jsonPath("$.taskCount", is(taskCount)))
					.andReturn()
					.getResponse()
					.getContentAsString());
			mvc.perform(post("/api/v1/game/" + gameCode + "/tasks/answer")
					.contentType(APPLICATION_JSON_UTF8)
					.content(sampleAnswer(ti2)))
				.andExpect(status().isOk());
		}
		
		assertFalse(gameServ.gameExists(gameCode));
		
		currentUsername = hostUsername;
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		currentUsername = playerUsername;
		mvc.perform(get("/api/v1/game/" + gameCode + "/tasks/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.hasGameFinished", is(true)));
		
		assertCanGetResults(gameID, playerCount, taskCount, hostUsername, playerUsername, sec);
	}
	
	private void assertCanGetResults(String gameID,
			int playerCount,
			int taskCount,
			String hostUsername,
			String playerUsername,
			SecurityContext sec) throws Exception
	{
		currentUsername = hostUsername;
		mvc.perform(get("/api/v1/scores/" + gameID + "/total"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(playerCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/personal"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(taskCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/total/changes"))
			.andExpect(status().isOk());
		
		currentUsername = playerUsername;
		mvc.perform(get("/api/v1/scores/" + gameID + "/total"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(playerCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/personal"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(taskCount)));
		mvc.perform(get("/api/v1/scores/" + gameID + "/total/changes"))
			.andExpect(status().isOk());
	}

	//---Helpers---
	
	private String sampleAnswer(JsonNode taskInfo) throws JsonMappingException, JsonProcessingException
	{
		switch (taskInfo.get("taskName").asText())
		{
			case "WordFill":
				WordFillElementDTO wfeDto = mapper.treeToValue(taskInfo.get("task"),
						WordFillElementDTO.class);
				return mapper.valueToTree(
						Map.of("answers",
							wfeDto.getPossibleAnswers()
							.stream()
							.limit(wfeDto.getEmptySpaceCount())
							.collect(Collectors.toList())))
						.toString();
			case "ListWordFill":
				ListWordFillDTO lwf = mapper.treeToValue(taskInfo.get("task"),
						ListWordFillDTO.class);
				List<List<String>> possibleAnswers = lwf.getPossibleAnswers();
				List<Integer> emptySpaceCounts = lwf.getEmptySpaceCount();
				List<List<String>> lwfRet = new ArrayList<>();
				int l = possibleAnswers.size();
				for (int i = 0; i < l; i++)
				{
					List<String> list = possibleAnswers.get(i);
					lwfRet.add(list.stream()
							.limit(emptySpaceCounts.get(i))
							.collect(Collectors.toList()));
				}
				return mapper.valueToTree(Map.of("answers", lwfRet)).toString();
			case "ChoiceWordFill":
				ChoiceWordFillElementDTO cwfeDto = mapper.treeToValue(taskInfo.get("task"),
						ChoiceWordFillElementDTO.class);
				return mapper.valueToTree(
						Map.of("answers",
							cwfeDto.getWordChoices()
							.stream()
							.map(list -> list.stream()
									.findFirst()
									.orElse(null))
							.collect(Collectors.toList())))
						.toString();
			case "ListChoiceWordFill":
				ListChoiceWordFillDTO lcwf = mapper.treeToValue(taskInfo.get("task"),
						ListChoiceWordFillDTO.class);
				return mapper.valueToTree(Map.of("answers", lcwf.getWordChoices()
						.stream()
						.map(list -> list.stream()
								.map(li -> li.stream()
										.findFirst()
										.orElse(null))
								.collect(Collectors.toList()))
						.collect(Collectors.toList()))).toString();
			case "SingleChoice":
				SingleChoiceDTO sc = mapper.treeToValue(taskInfo.get("task"),
						SingleChoiceDTO.class);
				return mapper.valueToTree(Map.of("answer", sc.getAnswers()
						.stream()
						.findFirst()
						.orElse(null))).toString();
			case "MultipleChoice":
				MultipleChoiceElementDTO mc = mapper.treeToValue(taskInfo.get("task"),
						MultipleChoiceElementDTO.class);
				return mapper.valueToTree(Map.of("answers", mc.getAnswers())).toString();
			case "WordConnect":
				WordConnectDTO wcDto = mapper.treeToValue(taskInfo.get("task"),
						WordConnectDTO.class);
				int leftSize = wcDto.getLeftWords().size();
				int rightSize = wcDto.getLeftWords().size();
				List<Integer> rightList = IntStream.range(0, rightSize)
						.mapToObj(i -> i)
						.collect(Collectors.toList());
				return mapper.valueToTree(
						Map.of("answerMapping", IntStream.range(0, leftSize)
								.mapToObj(i -> i)
								.collect(Collectors.toMap(i -> i, i -> rightList.get(i)))))
							.toString();
			case "ChronologicalOrder":
				ChronologicalOrderDTO coDto = mapper.treeToValue(taskInfo.get("task"),
						ChronologicalOrderDTO.class);
				return mapper.valueToTree(
						Map.of("answers", coDto.getSentences()))
							.toString();
			default:
				return "[]";
		}
	}
}
