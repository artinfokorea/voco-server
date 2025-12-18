package com.voco.voco.tov.infrastructure.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlWordQueryRepository;
import com.voco.voco.tov.domain.interfaces.dto.WordWithDetailsDto;
import com.voco.voco.tov.domain.model.VlWordAntonymEntity;
import com.voco.voco.tov.domain.model.VlWordEntity;
import com.voco.voco.tov.domain.model.VlWordExampleSentenceEntity;
import com.voco.voco.tov.domain.model.VlWordMeaningEntity;
import com.voco.voco.tov.domain.model.VlWordSynonymEntity;
import com.voco.voco.tov.infrastructure.persistence.VlWordAntonymJpaRepository;
import com.voco.voco.tov.infrastructure.persistence.VlWordExampleSentenceJpaRepository;
import com.voco.voco.tov.infrastructure.persistence.VlWordJpaRepository;
import com.voco.voco.tov.infrastructure.persistence.VlWordMeaningJpaRepository;
import com.voco.voco.tov.infrastructure.persistence.VlWordSynonymJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlWordQueryRepositoryImpl implements VlWordQueryRepository {

	private final VlWordJpaRepository vlWordJpaRepository;
	private final VlWordMeaningJpaRepository vlWordMeaningJpaRepository;
	private final VlWordSynonymJpaRepository vlWordSynonymJpaRepository;
	private final VlWordAntonymJpaRepository vlWordAntonymJpaRepository;
	private final VlWordExampleSentenceJpaRepository vlWordExampleSentenceJpaRepository;

	@Override
	public List<WordWithDetailsDto> findWordsWithDetailsByMasterWordIds(List<UUID> masterWordIds) {
		if (masterWordIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<VlWordEntity> words = vlWordJpaRepository.findByIdIn(masterWordIds);
		List<VlWordMeaningEntity> meanings = vlWordMeaningJpaRepository.findByMasterWordIdIn(masterWordIds);
		List<VlWordSynonymEntity> synonyms = vlWordSynonymJpaRepository.findByMasterWordIdIn(masterWordIds);
		List<VlWordAntonymEntity> antonyms = vlWordAntonymJpaRepository.findByMasterWordIdIn(masterWordIds);
		List<VlWordExampleSentenceEntity> exampleSentences = vlWordExampleSentenceJpaRepository.findByMasterWordIdIn(masterWordIds);

		Map<UUID, List<String>> meaningsByWordId = meanings.stream()
			.collect(Collectors.groupingBy(
				VlWordMeaningEntity::getMasterWordId,
				Collectors.mapping(VlWordMeaningEntity::getKoreanMeaning, Collectors.toList())
			));

		Map<UUID, List<String>> synonymsByWordId = synonyms.stream()
			.collect(Collectors.groupingBy(
				VlWordSynonymEntity::getMasterWordId,
				Collectors.mapping(VlWordSynonymEntity::getSynonym, Collectors.toList())
			));

		Map<UUID, List<String>> antonymsByWordId = antonyms.stream()
			.collect(Collectors.groupingBy(
				VlWordAntonymEntity::getMasterWordId,
				Collectors.mapping(VlWordAntonymEntity::getAntonym, Collectors.toList())
			));

		Map<UUID, List<WordWithDetailsDto.ExampleSentenceDto>> exampleSentencesByWordId = exampleSentences.stream()
			.collect(Collectors.groupingBy(
				VlWordExampleSentenceEntity::getMasterWordId,
				Collectors.mapping(
					e -> new WordWithDetailsDto.ExampleSentenceDto(e.getSentenceWithBlank(), e.getBlankAnswer()),
					Collectors.toList()
				)
			));

		return words.stream()
			.map(word -> new WordWithDetailsDto(
				word.getId(),
				word.getEnglishWord(),
				meaningsByWordId.getOrDefault(word.getId(), Collections.emptyList()),
				synonymsByWordId.getOrDefault(word.getId(), Collections.emptyList()),
				antonymsByWordId.getOrDefault(word.getId(), Collections.emptyList()),
				exampleSentencesByWordId.getOrDefault(word.getId(), Collections.emptyList())
			))
			.toList();
	}
}