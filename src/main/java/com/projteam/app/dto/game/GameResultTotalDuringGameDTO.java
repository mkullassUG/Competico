package com.projteam.app.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultTotalDuringGameDTO implements GameResultDTO
{
	private String username;
	private String nickname;
	private long totalScore;
	private long totalTime;
	private boolean hasFinished;
	private boolean removedForInactivity;
}