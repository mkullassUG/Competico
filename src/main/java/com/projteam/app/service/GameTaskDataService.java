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
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.ChronologicalOrder;
import com.projteam.app.domain.game.tasks.ListChoiceWordFill;
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		
		List<String> wfText1 = List.of("I’m sorry to have to tell you that there has been some ",
				" in the project and we won’t be able to ",
				" our original ",
				" on July 30th for completing the ",
				" of the new software. Pedro’s absence for three weeks caused a bit of a ",
				", and there were more delays when we realised that there was still some",
				" in the databases that needed cleaning up. Still, "
						+ "I am confident that we can complete the project by the end of next month.");
		List<String> wfPossibleAnswers1 = List.of("bottleneck",
				"deadline", "dirty data",
				"migrate", "rollout", "slippage",
				"stick to", "within", "scope");
		List<WordFillElement.EmptySpace> wfEmptySpaces1 = List.of(
				new WordFillElement.EmptySpace("slippage"),
				new WordFillElement.EmptySpace("stick to"),
				new WordFillElement.EmptySpace("deadline"),
				new WordFillElement.EmptySpace("rollout"),
				new WordFillElement.EmptySpace("bottleneck"),
				new WordFillElement.EmptySpace("dirty data")
				);
		WordFill wf1 = new WordFill(UUID.randomUUID(),
				"Complete the text with the missing words:",
		new WordFillElement(UUID.randomUUID(),
				wfText1, wfEmptySpaces1, true,
				wfPossibleAnswers1), targetDifficulty);

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
		
//		List<String> leftWords3 = List.of("SMATTERING", "DESCEND", "INEVITABLE", "PROPENSITY", "APPROACH", "OVERESTIMATE", "INGRESS", "GLEAN", "DEBUNK", "SOUND", "WINDING", "IN, DEPTH", "EGRESS", "ITEM");
//		List<String> rightWords3 = List.of("bit, small amount", "go down, fall, drop", "bound to happen, predestined, unavoidable", "tendency, inclination", "attitude, method, way, manner", "overvalue, overstate, amplify", "entry, entrance", "obtain, gather", "invalidate, discredit", "healthy, toned, in good shape", "full of twists and turns, zigzagging", "thoroughly, extensively", "exit, way out", "thing, article, object");
		
		List<String> leftWords3 = List.of("SMATTERING", "DESCEND", "INEVITABLE", "PROPENSITY", "APPROACH", "OVERESTIMATE", "INGRESS");
		List<String> rightWords3 = List.of("bit, small amount", "go down, fall, drop", "bound to happen, predestined, unavoidable", "tendency, inclination", "attitude, method, way, manner", "overvalue, overstate, amplify", "entry, entrance");
		Map<Integer, Integer> correctMapping3 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6));
		
		WordConnect wc3 = new WordConnect(UUID.randomUUID(),
				"Match the words with their synonyms:",
				leftWords3, rightWords3, correctMapping3, targetDifficulty);
		
		List<String> leftWords4 = List.of("GLEAN", "DEBUNK", "SOUND", "WINDING", "IN, DEPTH", "EGRESS", "ITEM");
		List<String> rightWords4 = List.of("obtain, gather", "invalidate, discredit", "healthy, toned, in good shape", "full of twists and turns, zigzagging", "thoroughly, extensively", "exit, way out", "thing, article, object");
		Map<Integer, Integer> correctMapping4 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6));
		
		WordConnect wc4 = new WordConnect(UUID.randomUUID(),
				"Match the words with their synonyms:",
				leftWords4, rightWords4, correctMapping4, targetDifficulty);
		
		List<String> coText1 = List.of("Try to understand the problem and define the purpose of the program.",
				"Once you have analysed the problem, define the successive logical steps of the program.",
				"Write the instructions in a high-level language of your choice.",
				"Once the code is written, test it to detect bugs or errors.",
				"Debug and fix errors in your code.",
				"Finally, review the program’s documentation.");
		ChronologicalOrder co1 = new ChronologicalOrder(UUID.randomUUID(),
				"Put the phrases in order:",
				coText1, targetDifficulty);
		
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
		
		
		var lcwfRows1 = List.of(
				choiceWordFillElement(List.of("Would you mind waiting ", " Ms Bright gets back?"),
						List.of(wordChoice("until", "by")),
						true),
				choiceWordFillElement(List.of("By the way, could you remind everyone"
						+ " that our next meeting will be ", " Tuesday at 11:10?"),
						List.of(wordChoice("on", "in")),
						true),
				choiceWordFillElement(List.of("I need this report ", " 6:30 tomorrow at the latest."),
						List.of(wordChoice("by", "in")),
						true),
				choiceWordFillElement(List.of("What did you do ", " the weekend?"),
						List.of(wordChoice("at", "on")),
						true),
				choiceWordFillElement(List.of("Harry has ", " decided which university he wants to go."),
						List.of(wordChoice("already", "before")),
						true),
				choiceWordFillElement(List.of("Luckily, we landed exactly ", ", so we were able"
						+ " to catch our connecting flight."),
						List.of(wordChoice("on time", "in time")),
						true),
				choiceWordFillElement(List.of(" I got used to the new interface, it didn’t feel awkward anymore."),
						List.of(wordChoice("Once", "One day")),
						false),
				choiceWordFillElement(List.of("He’s been trying to solve this problem ", " three hours."),
						List.of(wordChoice("for", "since")),
						false));
		ListChoiceWordFill lcwf1 = new ListChoiceWordFill(UUID.randomUUID(), 
				"Choose the best word to complete the sentences:",
				lcwfRows1, targetDifficulty);
		
		if (wfeDao.count() == 0)
		{
			if (wfDao.count() == 0)
			{
				wfeDao.save(wf1.getContent());
				wfDao.save(wf1);
				log.info("WordFill tasks initialized");
			}
//			if (lwfDao.count() == 0)
//			{
//				wfeDao.saveAll(lwf1.getRows());
//				lwfDao.save(lwf1);
//				wfeDao.saveAll(lwf2.getRows());
//				lwfDao.save(lwf2);
//				log.info("ListWordFill tasks initialized");
//			}
//			if (lcwfDao.count() == 0)
//			{
//				lcwf1.getRows().forEach(cwfe ->
//				{
//					cwfewcDao.saveAll(cwfe.getWordChoices());
//					cwfeDao.save(cwfe);
//				});
//				lcwfDao.save(lcwf1);
//				log.info("ListChoiceWordFill tasks initialized");
//			}
		}
		if (wcDao.count() == 0)
		{
			wcDao.save(wc1);
			wcDao.save(wc2);
			wcDao.save(wc3);
			wcDao.save(wc4);
			log.info("WordChoice tasks initialized");
		}
		if (coDao.count() == 0)
		{
			coDao.save(co1);
			log.info("ChronologicalOrder tasks initialized");
		}
		
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
	private static ChoiceWordFillElement choiceWordFillElement(List<String> text,
			List<ChoiceWordFillElement.WordChoice> wordChoices,
			boolean startWithText)
	{
		return new ChoiceWordFillElement(UUID.randomUUID(),
				text, wordChoices, startWithText);
	}
	private static List<EmptySpace> emptySpaceList(String... list)
	{
		return Arrays.asList(list)
			.stream()
			.map(ans -> new EmptySpace(ans))
			.collect(Collectors.toList());
	}
	private static WordChoice wordChoice(String correctAnswer, String... incorrectAnswers)
	{
		return new WordChoice(UUID.randomUUID(),
					correctAnswer, new ArrayList<>(Arrays.asList(incorrectAnswers)));
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
