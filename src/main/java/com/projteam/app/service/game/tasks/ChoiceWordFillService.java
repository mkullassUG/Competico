package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.ChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementWordChoiceDAO;
import com.projteam.app.domain.game.tasks.ChoiceWordFill;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.utils.Initializable;

@Service
public class ChoiceWordFillService implements TaskService
{
	private ChoiceWordFillDAO cwfDao;
	private ChoiceWordFillElementDAO cwfeDao;
	private ChoiceWordFillElementWordChoiceDAO cwfewcDao;
	
	public ChoiceWordFillService(ChoiceWordFillDAO cwfDao,
			ChoiceWordFillElementDAO cwfeDao,
			ChoiceWordFillElementWordChoiceDAO cwfewcDao)
	{
		this.cwfDao = cwfDao;
		this.cwfeDao = cwfeDao;
		this.cwfewcDao = cwfewcDao;
	}

	@Override
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return cwfDao.existsById(((ChoiceWordFill) task).getId());
	}
	@Override
	public long count()
	{
		return cwfDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return cwfDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return cwfDao.findAll()
				.stream()
				.map(t -> Initializable.init(t))
				.collect(Collectors.toList());
	}
	@Override
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		ChoiceWordFill cwf = (ChoiceWordFill) task;
		ChoiceWordFillElement cwfe = cwf.getContent();
		cwfewcDao.saveAll(cwfe.getWordChoices());
		cwfeDao.save(cwfe);
		cwfDao.save(cwf);
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ChoiceWordFill;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}