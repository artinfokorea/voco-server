package com.voco.voco.app.member.domain.model;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

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
@Table(name = "voco_member")
public class MemberEntity extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "korean_name", nullable = false)
	private String koreanName;

	@Column(name = "english_name", nullable = false)
	private String englishName;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	private MemberEntity(String koreanName, String englishName, String email, String password) {
		this.koreanName = koreanName;
		this.englishName = englishName;
		this.email = email;
		this.password = password;
	}

	public static MemberEntity create(String koreanName, String englishName, String email, String password) {
		return new MemberEntity(koreanName, englishName, email, password);
	}
}
