package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementWordChoiceDAO;
import com.projteam.app.dao.game.tasks.ListChoiceWordFillDAO;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.app.domain.game.tasks.ListChoiceWordFill;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.utils.Initializable;

@Service
public class ListChoiceWordFillService implements TaskService
{
	private ListChoiceWordFillDAO lcwfDao;
	private ChoiceWordFillElementDAO cwfeDao;
	private ChoiceWordFillElementWordChoiceDAO cwfewcDao;
	
	public ListChoiceWordFillService(ListChoiceWordFillDAO lcwfDao,
			ChoiceWordFillElementDAO cwfeDao,
			ChoiceWordFillElementWordChoiceDAO cwfewcDao)
	{
		this.lcwfDao = lcwfDao;
		this.cwfeDao = cwfeDao;
		this.cwfewcDao = cwfewcDao;
	}
	
	@Override
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return lcwfDao.existsById(((ListChoiceWordFill) task).getId());
	}
	@Override
	public long count()
	{
		return lcwfDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return lcwfDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return lcwfDao.findAll()
				.stream()
				.map(t -> Initializable.init(t))
				.collect(Collectors.toList());
	}
	@Override
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		ListChoiceWordFill lcwf = (ListChoiceWordFill) task;
		for (ChoiceWordFillElement cwfe: lcwf.getRows())
		{
			cwfewcDao.saveAll(cwfe.getWordChoices());
			cwfeDao.save(cwfe);
		}
		lcwfDao.save(lcwf);
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ListChoiceWordFill;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}