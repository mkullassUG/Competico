package com.projteam.competico.domain.group;

import java.util.List;
import java.util.UUID;
import com.projteam.competico.domain.game.TaskSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupLobbySettings
{
	private UUID groupId;
	private String groupCode;
	private List<TaskSet> selectedTasksets;
}
