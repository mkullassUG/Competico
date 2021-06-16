package com.projteam.competico.domain.group;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import com.projteam.competico.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
@Table(name = "GroupJoinRequest", uniqueConstraints =
	@UniqueConstraint(columnNames = {"account", "lecturer_group"}))
public class GroupJoinRequest
{
	private @Id UUID id;
	private @JoinColumn(name = "account") @ManyToOne Account account;
	private @JoinColumn(name = "lecturer_group") @ManyToOne Group group;
	private @Column(name = "creationDate") @Temporal(TemporalType.TIMESTAMP) Date creationDate;
}
