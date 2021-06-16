package com.projteam.competico.domain.group;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.projteam.competico.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GroupMessage")
@Access(AccessType.FIELD)
public class GroupMessage
{
	private @Id UUID id;
	private @JoinColumn(name = "account") @ManyToOne Account account;
	private @JoinColumn(name = "lecturer_group") @ManyToOne Group group;
	private @Column(name = "title", length = 500) String title;
	private @Column(name = "content", length = 5000) String content;
	private @Column(name = "creationDate") @Temporal(TemporalType.TIMESTAMP) Date creationDate;
	private @Column(name = "editDate", nullable = true) @Temporal(TemporalType.TIMESTAMP) Date editDate;
	private @ElementCollection List<UUID> readBy;
}
