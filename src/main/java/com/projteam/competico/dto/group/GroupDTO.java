package com.projteam.competico.dto.group;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO
{
	private String name;
	private String groupCode;
	private String creationDate;
	private List<String> gameCodes;
	private List<String> lecturers;
	private List<String> players;
}