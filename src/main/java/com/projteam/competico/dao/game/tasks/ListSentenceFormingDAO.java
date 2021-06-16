package com.projteam.competico.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.tasks.ListSentenceForming;

public interface ListSentenceFormingDAO extends JpaRepository<ListSentenceForming, UUID>
{}
