package com.projteam.competico.domain.game;

import java.util.Set;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.projteam.competico.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TaskSet", uniqueConstraints =
	@UniqueConstraint(columnNames = {"name", "lecturerID"}))
@Access(AccessType.FIELD)
public class TaskSet implements Initializable
{
	private @Id UUID id;
	private @Column(name = "name") String name;
	private @Column(name = "lecturerID") UUID lecturerID;
	
	@ElementCollection
	@Column(name = "taskInfos", updatable = true)
	private Set<TaskInfo> taskInfos;
	
	@Override
	public void initialize()
	{
		Initializable.initialize(taskInfos);
	}
}
