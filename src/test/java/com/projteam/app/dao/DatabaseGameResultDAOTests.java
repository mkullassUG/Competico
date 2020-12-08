package com.projteam.app.dao;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;
import com.projteam.app.domain.GameResult;
import com.projteam.app.domain.GameResults;

public class DatabaseGameResultDAOTests
{
	private @Mock EntityManagerFactory emf;
	
	private @InjectMocks DatabaseGameResultDAO dao;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads()
	{
		Assert.notNull(emf, "Game result Data Access Object initialization failed");
	}
	
	@ParameterizedTest
	@MethodSource("sampleGameResults")
	public void shouldPersistGameResults(GameResults gr)
	{
		EntityManager em = Mockito.mock(EntityManager.class);
		EntityTransaction et = Mockito.mock(EntityTransaction.class);
		
		when(emf.createEntityManager()).thenReturn(em);
		when(em.getTransaction()).thenReturn(et);
		
		dao.insertGameResults(gr);
		
		verify(emf, times(1)).createEntityManager();
		verify(em, times(2)).getTransaction();
		verify(em, times(1)).persist(gr);
		verify(et, times(1)).begin();
		verify(et, times(1)).commit();
		verify(em, times(1)).close();
		
		verifyNoMoreInteractions(emf, em, et);
	}
	
	//---Sources---
	
	public static List<Arguments> sampleGameResults()
	{
		List<Arguments> ret = new ArrayList<>();
		
		GameResults gr = new GameResults(UUID.randomUUID());
		gr.addResult(new GameResult(UUID.randomUUID(), Map.of(
				"Task1", 100l,
				"Task2", 250l,
				"Task3", 50l)));
		ret.add(Arguments.of(gr));
		
		return ret;
	}
}
