package com.projteam.app.domain;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class EmailVerificationToken
{
	private @Id @Column(name = "id", unique = true) UUID id;
	private @Column(name = "email", unique = true) String email;
	private @Column(name = "expiryDate") @Temporal(TemporalType.TIMESTAMP) Date expiryDate;
}
