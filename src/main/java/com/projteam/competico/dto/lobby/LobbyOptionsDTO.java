package com.projteam.competico.dto.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyOptionsDTO
{
	private int maxPlayers;
	private boolean allowsRandomPlayers;
	
	public boolean allowsRandomPlayers()
	{
		return allowsRandomPlayers;
	}
}
