package com.projteam.app.dao;

import java.util.ArrayList;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.projteam.app.domain.GameResults;

@Primary
@Repository("DatabaseGameResultDAO")
@Transactional
public class DatabaseGameResultDAO implements GameResultDAO
{
	@PersistenceUnit
	private EntityManagerFactory emf;
	
	public DatabaseGameResultDAO(EntityManagerFactory emf)
	{
		this.emf = emf;
	}
	
	@Override
	public void insertGameResults(GameResults gr)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(gr);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public void updateGameResults(GameResults gr)
	{
		EntityManager em = emf.createEntityManager();
		GameResults src = selectGameResults(gr.getGameID());
		em.getTransaction().begin();
		src.setResults(gr.getResults());
		em.getTransaction().commit();
	}

	@Override
	public void deleteGameResults(GameResults gr)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.remove(gr);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public GameResults[] selectAllGameResults()
	{
		EntityManager em = emf.createEntityManager();
		ArrayList<GameResults> ret = new ArrayList<>();
		for (Object o: 
			em.createQuery("SELECT gr FROM GameResults AS gr")
				.getResultList())
			ret.add((GameResults) o);
		em.close();
		return ret.toArray(l -> new GameResults[l]);
	}

	@Override
	public GameResults selectGameResults(UUID gameID)
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			GameResults ret = (GameResults) em
					.createQuery("SELECT gr FROM GameResults AS gr WHERE gr.gameID = :gameID")
					.setParameter("id", gameID)
					.getSingleResult();
			em.close();
			return ret;
		}
		catch (NoResultException e)
		{
			em.close();
			return null;
		}
	}
}