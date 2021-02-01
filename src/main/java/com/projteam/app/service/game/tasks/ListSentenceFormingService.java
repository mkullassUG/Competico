package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.ListSentenceFormingDAO;
import com.projteam.app.dao.game.tasks.SentenceFormingElementDAO;
import com.projteam.app.domain.game.tasks.ListSentenceForming;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.utils.Initializable;

@Service
public class ListSentenceFormingService implements TaskService
{
	private ListSentenceFormingDAO lsfDao;
	private SentenceFormingElementDAO sfeDao;
	
	public ListSentenceFormingService(ListSentenceFormingDAO lsfDao,
			SentenceFormingElementDAO sfeDao)
	{
		this.lsfDao = lsfDao;
		this.sfeDao = sfeDao;
	}
	
	@Override
	@Transactional
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return lsfDao.existsById(((ListSentenceForming) task).getId());
	}
	@Override
	@Transactional
	public long count()
	{
		return lsfDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return lsfDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return lsfDao.findAll()
				.stream()
				.map(t -> Initializable.init(t))
				.collect(Collectors.toList());
	}
	@Override
	@Transactional
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		ListSentenceForming lsf = (ListSentenceForming) task;
		sfeDao.saveAll(lsf.getRows());
		lsfDao.save(lsf);
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ListSentenceForming;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}