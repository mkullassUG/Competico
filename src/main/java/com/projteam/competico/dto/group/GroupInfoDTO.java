package com.projteam.competico.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfoDTO
{
	private String name;
	private String groupCode;
	private String creationDate;
	private int memberCount;
}
