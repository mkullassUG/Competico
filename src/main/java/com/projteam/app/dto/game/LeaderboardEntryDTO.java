package com.projteam.app.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO
{
	private String username;
	private String nickname;
	private int position;
	private int rating;
}
