package com.projteam.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
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
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;

@Service
public class GameTaskDataService
{
	private List<JpaRepository<? extends Task, ?>> taskDAOlist;
	
	@Autowired
	public GameTaskDataService(ChoiceWordFillDAO cwfDao,
		ChoiceWordFillElementDAO cwfeDao,
		ChoiceWordFillElementWordChoice cwfewcDao,
		ChronologicalOrderDAO coDao,
		ListChoiceWordFillDAO lcwfDao,
		ListSentenceFormingDAO lsfDao,
		ListWordFillDAO lwfDao,
		MultipleChoiceDAO mcDao,
		MultipleChoiceElementDAO mceDao,
		SentenceFormingElementDAO sfeDao,
		SingleChoiceDAO scDao,
		WordConnectDAO wcDao,
		WordFillDAO wfDao,
		WordFillElementDAO wfeDao)
	{
		double targetDifficulty = 100;
		
		initTaskData(targetDifficulty,
				cwfDao, cwfeDao, cwfewcDao,
				coDao, lcwfDao, lsfDao,
				lwfDao, mcDao, mceDao,
				sfeDao, scDao, wcDao,
				wfDao, wfeDao);
	}
	
	@Transactional
	private void initTaskData(double targetDifficulty,
			ChoiceWordFillDAO cwfDao,
			ChoiceWordFillElementDAO cwfeDao,
			ChoiceWordFillElementWordChoice cwfewcDao,
			ChronologicalOrderDAO coDao,
			ListChoiceWordFillDAO lcwfDao,
			ListSentenceFormingDAO lsfDao,
			ListWordFillDAO lwfDao,
			MultipleChoiceDAO mcDao,
			MultipleChoiceElementDAO mceDao,
			SentenceFormingElementDAO sfeDao,
			SingleChoiceDAO scDao,
			WordConnectDAO wcDao,
			WordFillDAO wfDao,
			WordFillElementDAO wfeDao)
	{
//		List<String> text = List.of("Lorem ipsum dolor sit amet, consectetur ",
//				" elit. Quisque vestibulum, enim id fringilla sodales, libero   ipsum ",
//				" erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis ",
//				" dolor nec turpis. Quisque elementum ",
//				" accumsan. Lorem ipsum dolor ",
//				" amet, consectetur adipiscing elit. In nec ",
//				" nisi, et semper nisl. Cras placerat ",
//				" orci eget congue. Duis vitae gravida odio. Etiam elit turpis, ",
//				" ac nisi et, dapibus blandit nibh. Duis eleifend metus in iaculis tincidunt.");
//		List<String> possibleAnswers = List.of("slowo1", "slowo2",
//				"slowo3", "slowo4", "slowo5",
//				"slowo6", "slowo7", "slowo8");
//		List<WordFillElement.EmptySpace> emptySpaces = possibleAnswers.stream()
//				.map(ans -> new WordFillElement.EmptySpace(ans))
//				.collect(Collectors.toList());
//		
//		WordFill wf = new WordFill(UUID.randomUUID(),
//				new WordFillElement(UUID.randomUUID(),
//						text, emptySpaces, true,
//						possibleAnswers), targetDifficulty);

		List<String> leftWords1 = List.of("data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier");
		List<String> rightWords1 = List.of("eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna");
		Map<Integer, Integer> correctMapping1 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9));

		WordConnect wc1 = new WordConnect(UUID.randomUUID(),
				"Match the words with their translations:",
				leftWords1, rightWords1, correctMapping1, targetDifficulty);
		
		List<String> leftWords2 = List.of("keynote", "to convey (information)", "to unveil (a theme)", "consistent", "stiff", "a knack (for sth)", "a flair", "intricate", "dazzling", "to rehearse");
		List<String> rightWords2 = List.of("myśl przewodnia, główny motyw", "przekazywać/dostarczać (informacje)", "odkryć, ujawnić, odsłonić", "spójny, zgodny, konsekwentny", "sztywny, zdrętwiały", "talent, zręczność", "klasa, dar", "zawiły, misterny", "olśniewający", "próbować, przygotowywać się");
		Map<Integer, Integer> correctMapping2 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9));
		
		WordConnect wc2 = new WordConnect(UUID.randomUUID(),
				"Match the words with their translations:",
				leftWords2, rightWords2, correctMapping2, targetDifficulty);
		
		List<String> leftWords3 = List.of("SMATTERING", "DESCEND", "INEVITABLE", "PROPENSITY", "APPROACH", "OVERESTIMATE", "INGRESS", "GLEAN", "DEBUNK", "SOUND", "WINDING", "IN, DEPTH", "EGRESS", "ITEM");
		List<String> rightWords3 = List.of("bit, small amount", "go down, fall, drop", "bound to happen, predestined, unavoidable", "tendency, inclination", "attitude, method, way, manner", "overvalue, overstate, amplify", "entry, entrance", "obtain, gather", "invalidate, discredit", "healthy, toned, in good shape", "full of twists and turns, zigzagging", "thoroughly, extensively", "exit, way out", "thing, article, object");
		Map<Integer, Integer> correctMapping3 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9),
				Map.entry(10, 10),
				Map.entry(11, 11),
				Map.entry(12, 12),
				Map.entry(13, 13));
		
		WordConnect wc3 = new WordConnect(UUID.randomUUID(),
				"TODO: Add task instruction", //TODO add task instruction
				leftWords3, rightWords3, correctMapping3, targetDifficulty);
		
//		List<String> coText = List.of("Lorem ipsum dolor sit amet",
//				"consectetur adipiscing elit",
//				"sed do eiusmod tempor incididunt",
//				"ut labore et dolore magna aliqua",
//				"Ut enim ad minim veniam",
//				"quis nostrud exercitation",
//				"ullamco laboris nisi ut",
//				"aliquip ex ea commodo consequat");
//		
//		ChronologicalOrder co = new ChronologicalOrder(UUID.randomUUID(),
//				coText, targetDifficulty);
		
		List<WordFillElement> lwfeList1 = List.of(
				wordFillElement(List.of("I’m ", " you asked me that question."),
						emptySpaceList("GLAD"),
						true,
						List.of("GLAD", "SORRY", "REGRET", "INTERESTED")),
				wordFillElement(List.of("I’m afraid I can’t say it at the ", " of my head."),
						emptySpaceList("GLAD"),
						true,
						List.of("TIP", "END", "TOP", "BACK")),
				wordFillElement(List.of("As I’ve ", " before in my presentation, …"),
						emptySpaceList("MENTIONED"),
						true,
						List.of("SPOKEN", "MENTIONED", "SEEN", "TALKED")),
				wordFillElement(List.of("Do you mind if we deal ", " it later?"),
						emptySpaceList("WITH"),
						true,
						List.of("ON", "WITHOUT", "WITH", "FROM")),
				wordFillElement(List.of("In fact, it goes ", " to what I was saying earlier, …"),
						emptySpaceList("BACK"),
						true,
						List.of("BACK", "ON", "IN", "UP")),
				wordFillElement(List.of("I don’t want to go into too much ", " at this stage."),
						emptySpaceList("DETAIL"),
						true,
						List.of("DISTRUCTIONS", "DETAIL", "TIME", "DISCUSSIONS")));
		
		ListWordFill lwf1 = new ListWordFill(UUID.randomUUID(), "Complete the sentences with the best word:",
				lwfeList1, targetDifficulty);
		
		List<WordFillElement> lwfeList2 = List.of(
				wordFillElement(List.of("the act or way of leaving place: "),
						emptySpaceList("egress"),
						true,
						List.of("descend", "sound", "egress")),
				wordFillElement(List.of("a tendency to behave in a particular way: "),
						emptySpaceList("propensity"),
						true,
						List.of("smattering", "propensity", "glean")),
				wordFillElement(List.of("a very small amount or number: "),
						emptySpaceList("smattering"),
						true,
						List.of("glean", "ingress", "smattering")),
				wordFillElement(List.of("come down: "),
						emptySpaceList("descend"),
						true,
						List.of("descend", "in-depth", "winding")),
				wordFillElement(List.of("done carefully and in great detail: "),
						emptySpaceList("in-depth"),
						true,
						List.of("in-depth", "ingress", "debunk")),
				wordFillElement(List.of("healthy; in good condition: "),
						emptySpaceList("sound"),
						true,
						List.of("glean", "winding", "sound")),
				wordFillElement(List.of("a lot of something; big amount: "),
						emptySpaceList("sheer number"),
						true,
						List.of("propensity", "sheer number", "egress")),
				wordFillElement(List.of("repeatedly turns in different directions: "),
						emptySpaceList("winding"),
						true,
						List.of("debunk", "winding", "smattering")),
				wordFillElement(List.of("the act of entering something: "),
						emptySpaceList("ingress"),
						true,
						List.of("ingress", "egress", "propensity")),
				wordFillElement(List.of("to collect information in small amounts and often with difficulty: "),
						emptySpaceList("glean"),
						true,
						List.of("glean", "smattering", "debunk")),
				wordFillElement(List.of("to show that something is not true: "),
						emptySpaceList("debunk"),
						true,
						List.of("glean", "debunk", "in-depth")));
		
		ListWordFill lwf2 = new ListWordFill(UUID.randomUUID(), "Choose the word that fits the definitions:",
				lwfeList2, targetDifficulty);
		
//		if (wfeDao.count() == 0)
//		{
//			if (wfDao.count() == 0)
//			{
//				wfeDao.save(wf.getContent());
//				wfDao.save(wf);
//			}
//			if (lwfDao.count() == 0)
//			{
//				lwf1.getRows().forEach(wfeDao::save);
//				lwfDao.save(lwf1);
//				lwf2.getRows().forEach(wfeDao::save);
//				lwfDao.save(lwf2);
//			}
//		}
		if (wcDao.count() == 0)
		{
			wcDao.save(wc1);
			wcDao.save(wc2);
			wcDao.save(wc3);
		}
//		if (coDao.count() == 0)
//			coDao.save(co);
		
		taskDAOlist = List.of(
				cwfDao, coDao, lcwfDao,
				lsfDao, lwfDao, mcDao,
				scDao, wcDao, wfDao);
	}
	
	private static WordFillElement wordFillElement(List<String> text,
			List<EmptySpace> emptySpaces,
			boolean startWithText,
			List<String> possibleAnswers)
	{
		return new WordFillElement(UUID.randomUUID(),
				text, emptySpaces, startWithText, possibleAnswers);
	}
	private static List<EmptySpace> emptySpaceList(String... list)
	{
		return Arrays.asList(list)
			.stream()
			.map(ans -> new EmptySpace(ans))
			.collect(Collectors.toList());
	}
	
	@Transactional
	public Task generateRandomTask(double targetDifficulty)
	{
		ArrayList<JpaRepository<? extends Task, ?>> taskDAOs =
				new ArrayList<>(taskDAOlist);
		Collections.shuffle(taskDAOs);
		
		for (JpaRepository<? extends Task, ?> taskDAO: taskDAOs)
		{
			long count = taskDAO.count();
			if (count < 1)
				continue;
			
			int pos = (int) (Math.random() * count);
			
			Task ret = taskDAO.findAll(PageRequest.of(pos, 1))
					.stream()
					.findFirst()
					.orElse(null);
			if (ret != null)
			{
				ret.initialize();
				return ret;
			}
		}
		
		return defaultTask(targetDifficulty);
	}
	
	public Task defaultTask(double targetDifficulty)
	{
		List<String> leftWords1 = List.of("data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier");
		List<String> rightWords1 = List.of("eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna");
		Map<Integer, Integer> correctMapping1 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9));

		return new WordConnect(UUID.randomUUID(), "Match the words with their translations:",
				leftWords1, rightWords1, correctMapping1, targetDifficulty);
	}
}
