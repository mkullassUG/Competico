package com.projteam.competico.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultPersonalDTO implements GameResultDTO
{
	private double completion;
	private long timeTaken;
	private double difficulty;
}