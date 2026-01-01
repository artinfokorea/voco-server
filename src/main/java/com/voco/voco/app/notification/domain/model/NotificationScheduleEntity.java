package com.voco.voco.app.notification.domain.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@SQLRestriction("deleted_at is NULL")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "voco_notification_schedule",
	uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "day_of_week", "notification_time"})
)
public class NotificationScheduleEntity extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week", nullable = false)
	private DayOfWeek dayOfWeek;

	@Column(name = "notification_time", nullable = false)
	private LocalTime notificationTime;

	@Column(name = "scenario_id")
	private Long scenarioId;

	private NotificationScheduleEntity(Long memberId, DayOfWeek dayOfWeek, LocalTime notificationTime, Long scenarioId) {
		this.memberId = memberId;
		this.dayOfWeek = dayOfWeek;
		this.notificationTime = notificationTime;
		this.scenarioId = scenarioId;
	}

	public static NotificationScheduleEntity create(Long memberId, DayOfWeek dayOfWeek, LocalTime notificationTime, Long scenarioId) {
		return new NotificationScheduleEntity(memberId, dayOfWeek, notificationTime, scenarioId);
	}

	public void update(DayOfWeek dayOfWeek, LocalTime notificationTime, Long scenarioId) {
		this.dayOfWeek = dayOfWeek;
		this.notificationTime = notificationTime;
		this.scenarioId = scenarioId;
	}
}