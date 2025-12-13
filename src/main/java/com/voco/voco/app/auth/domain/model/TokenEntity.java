package com.voco.voco.app.auth.domain.model;

import java.time.LocalDateTime;

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
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "voco_token")
public class TokenEntity extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "access_token", nullable = false, length = 500)
	private String accessToken;

	@Column(name = "refresh_token", nullable = false, length = 500)
	private String refreshToken;

	@Column(name = "access_token_expired_at", nullable = false)
	private LocalDateTime accessTokenExpiredAt;

	@Column(name = "refresh_token_expired_at", nullable = false)
	private LocalDateTime refreshTokenExpiredAt;

	private TokenEntity(Long memberId, String accessToken, String refreshToken,
		LocalDateTime accessTokenExpiredAt, LocalDateTime refreshTokenExpiredAt) {
		this.memberId = memberId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessTokenExpiredAt = accessTokenExpiredAt;
		this.refreshTokenExpiredAt = refreshTokenExpiredAt;
	}

	public static TokenEntity create(Long memberId, String accessToken, String refreshToken,
		LocalDateTime accessTokenExpiredAt, LocalDateTime refreshTokenExpiredAt) {
		return new TokenEntity(memberId, accessToken, refreshToken, accessTokenExpiredAt, refreshTokenExpiredAt);
	}

	public void updateTokens(String accessToken, String refreshToken,
		LocalDateTime accessTokenExpiredAt, LocalDateTime refreshTokenExpiredAt) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessTokenExpiredAt = accessTokenExpiredAt;
		this.refreshTokenExpiredAt = refreshTokenExpiredAt;
	}

	public boolean isAccessTokenExpired() {
		return LocalDateTime.now().isAfter(accessTokenExpiredAt);
	}

	public boolean isRefreshTokenExpired() {
		return LocalDateTime.now().isAfter(refreshTokenExpiredAt);
	}
}