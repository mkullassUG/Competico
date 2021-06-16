package com.projteam.competico.domain.game;

import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import com.projteam.competico.domain.Account;
import com.projteam.competico.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class PlayerData implements Initializable
{
	private @Id @Column(name = "id", unique = true) UUID id;
	private @OneToOne(optional = false) Account account;
	private int rating;
	
	@Override
	public void initialize()
	{
		Initializable.initialize(account);
	}
}
