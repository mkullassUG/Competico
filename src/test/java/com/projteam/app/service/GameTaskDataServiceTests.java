package com.projteam.app.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.projteam.app.dao.game.tasks.ChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementWordChoice;
import com.projteam.app.dao.game.tasks.ChronologicalOrderDAO;
import com.projteam.app.dao.game.tasks.ListChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ListSentenceFormingDAO;
import com.projteam.app.dao.game.tasks.ListWordFillDAO;
import com.projteam.app.dao.game.tasks.MultipleChoiceDAO;
import com.projteam.app.dao.game.tasks.MultipleChoiceElementDAO;
import com.projteam.app.dao.game.tasks.SentenceFormingElementDAO;
import com.projteam.app.dao.game.tasks.SingleChoiceDAO;
import com.projteam.app.dao.game.tasks.WordConnectDAO;
import com.projteam.app.dao.game.tasks.WordFillDAO;
import com.projteam.app.dao.game.tasks.WordFillElementDAO;

class GameTaskDataServiceTests
{
	private @Mock ChoiceWordFillDAO cwfDao;
	private @Mock ChoiceWordFillElementDAO cwfeDao;
	private @Mock ChoiceWordFillElementWordChoice cwfewcDao;
	private @Mock ChronologicalOrderDAO coDao;
	private @Mock ListChoiceWordFillDAO lcwfDao;
	private @Mock ListSentenceFormingDAO lsfDao;
	private @Mock ListWordFillDAO lwfDao;
	private @Mock MultipleChoiceDAO mcDao;
	private @Mock MultipleChoiceElementDAO mceDao;
	private @Mock SentenceFormingElementDAO sfeDao;
	private @Mock SingleChoiceDAO scDao;
	private @Mock WordConnectDAO wcDao;
	private @Mock WordFillDAO wfDao;
	private @Mock WordFillElementDAO wfeDao;
	
	private @InjectMocks GameTaskDataService gtdServ;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 10, 50, 100, 125, 250, 9999})
	public void canCreateDefaultTask(int targetDifficulty)
	{
		assertNotNull(gtdServ.defaultTask(targetDifficulty));
	}
}
