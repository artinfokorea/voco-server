package com.voco.voco.tov.domain.model;

import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@SQLRestriction("deleted_at is NULL")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "vl_master_words")
public class VlWordEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "service_name", nullable = false, length = 50)
	private String serviceName;

	@Column(name = "english_word", nullable = false, length = 100)
	private String englishWord;

	private VlWordEntity(String serviceName, String englishWord) {
		this.serviceName = serviceName;
		this.englishWord = englishWord;
	}

	public static VlWordEntity create(String englishWord) {
		return new VlWordEntity("VOCA_LAB", englishWord);
	}

	public static VlWordEntity create(String serviceName, String englishWord) {
		return new VlWordEntity(serviceName, englishWord);
	}
}
