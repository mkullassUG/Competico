package com.projteam.competico.dto.group;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupJoinRequestDTO
{
	private UUID id;
	private String username;
	private List<String> roles;
	private String creationDate;
}
