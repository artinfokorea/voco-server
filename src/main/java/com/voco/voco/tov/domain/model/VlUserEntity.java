package com.voco.voco.tov.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.tov.domain.model.enums.VlUserType;

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
@Table(name = "common_users")
public class VlUserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "login_id", nullable = false, length = 50)
	private String loginId;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "service_name", nullable = false, length = 50)
	private String serviceName;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 20)
	private VlUserType type;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	private VlUserEntity(String name, String loginId, String passwordHash, String serviceName, VlUserType type) {
		this.name = name;
		this.loginId = loginId;
		this.passwordHash = passwordHash;
		this.serviceName = serviceName;
		this.type = type;
	}

	public static VlUserEntity create(String name, String loginId, String passwordHash) {
		return new VlUserEntity(name, loginId, passwordHash, "VOCA_LAB", VlUserType.STUDENT);
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}