package com.projteam.competico.dto.group;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessageDTO
{
	private UUID id;
	private String username;
	private String title;
	private String content;
	private String creationDate;
	private String editDate;
	private boolean read;
}
