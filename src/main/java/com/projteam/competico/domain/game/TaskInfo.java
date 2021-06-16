package com.projteam.competico.domain.game;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TaskInfo")
@Access(AccessType.FIELD)
public class TaskInfo
{
	private @Id UUID taskID;
	private @Column(name = "creationDate", updatable = true)
		@Temporal(TemporalType.DATE) Date creationDate = new Date();
	
	public TaskInfo(UUID taskId)
	{
		taskID = taskId;
	}
}
