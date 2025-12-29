package com.voco.voco.app.member.domain.model;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false)
	private Provider provider;

	@Column(name = "provider_id")
	private String providerId;

	@Column(name = "korean_name", nullable = false)
	private String koreanName;

	@Column(name = "english_name", nullable = false)
	private String englishName;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "password")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "level", nullable = false)
	private Level level;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_type", nullable = false)
	private UserType userType;

	private MemberEntity(Provider provider, String providerId, String koreanName, String englishName, String email,
		String password, Level level, UserType userType) {
		this.provider = provider;
		this.providerId = providerId;
		this.koreanName = koreanName;
		this.englishName = englishName;
		this.email = email;
		this.password = password;
		this.level = level;
		this.userType = userType;
	}

	public static MemberEntity create(String koreanName, String englishName, String email, String password,
		Level level) {
		return new MemberEntity(Provider.EMAIL, null, koreanName, englishName, email, password, level, UserType.USER);
	}

	public static MemberEntity createSocial(Provider provider, String providerId, String koreanName, String englishName,
		String email, Level level) {
		return new MemberEntity(provider, providerId, koreanName, englishName, email, null, level, UserType.USER);
	}

	public static MemberEntity createAdmin(String koreanName, String englishName, String email, String password,
		Level level) {
		return new MemberEntity(Provider.EMAIL, null, koreanName, englishName, email, password, level, UserType.ADMIN);
	}

	public boolean isAdmin() {
		return this.userType == UserType.ADMIN;
	}

	public void update(String englishName, Level level) {
		this.englishName = englishName;
		this.level = level;
	}
}
