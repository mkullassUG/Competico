package com.projteam.app.dao;

import java.util.UUID;
import com.projteam.app.domain.GameResults;

public interface GameResultDAO
{
	public void insertGameResults(GameResults gr);
	public void updateGameResults(GameResults gr);
	public void deleteGameResults(GameResults gr);
	public GameResults[] selectAllGameResults(); 
	public GameResults selectGameResults(UUID gameID);
}