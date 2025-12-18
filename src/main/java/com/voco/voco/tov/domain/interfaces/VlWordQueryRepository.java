package com.voco.voco.tov.domain.interfaces;

import java.util.List;
import java.util.UUID;

import com.voco.voco.tov.domain.interfaces.dto.WordWithDetailsDto;

public interface VlWordQueryRepository {
	List<WordWithDetailsDto> findWordsWithDetailsByMasterWordIds(List<UUID> masterWordIds);
}