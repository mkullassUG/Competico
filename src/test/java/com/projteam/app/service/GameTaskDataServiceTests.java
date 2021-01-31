package com.projteam.app.service;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.databind.JsonNode;
import com.projteam.app.service.game.tasks.ChoiceWordFillService;
import com.projteam.app.service.game.tasks.ChronologicalOrderService;
import com.projteam.app.service.game.tasks.ListChoiceWordFillService;
import com.projteam.app.service.game.tasks.ListSentenceFormingService;
import com.projteam.app.service.game.tasks.ListWordFillService;
import com.projteam.app.service.game.tasks.MultipleChoiceService;
import com.projteam.app.service.game.tasks.SingleChoiceService;
import com.projteam.app.service.game.tasks.WordConnectService;
import com.projteam.app.service.game.tasks.WordFillService;

class GameTaskDataServiceTests
{
	private @Mock ChoiceWordFillService cwfServ;
	private @Mock ChronologicalOrderService coServ;
	private @Mock ListChoiceWordFillService lcwfServ;
	private @Mock ListSentenceFormingService lsfServ;
	private @Mock ListWordFillService lwfServ;
	private @Mock MultipleChoiceService mcServ;
	private @Mock SingleChoiceService scServ;
	private @Mock WordConnectService wcServ;
	private @Mock WordFillService wfServ;
	
	private GameTaskDataService gtdServ;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		
		gtdServ = new GameTaskDataService(List.of(
					cwfServ, coServ, lcwfServ,
					lsfServ, lwfServ, mcServ,
					scServ, wcServ, wfServ));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 10, 50, 100, 125, 250, 9999})
	public void canCreateDefaultTask(int targetDifficulty)
	{
		assertNotNull(gtdServ.defaultTask(targetDifficulty));
	}
	@Test
	public void canGetAllTasksAsJson()
	{
		JsonNode res = gtdServ.getAllTasksAsJson();
		assertNotNull(res);
		assertTrue(res.isArray());
	}
}
