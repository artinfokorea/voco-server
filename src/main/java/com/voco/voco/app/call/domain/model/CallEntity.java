package com.voco.voco.app.call.domain.model;

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
@Table(name = "voco_call")
public class CallEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "scenario_id", nullable = false)
	private Long scenarioId;

	@Column(name = "room_name", nullable = false, length = 100)
	private String roomName;

	@Column(name = "analysis_id")
	private Long analysisId;

	private CallEntity(Long memberId, Long scenarioId, String roomName) {
		this.memberId = memberId;
		this.scenarioId = scenarioId;
		this.roomName = roomName;
	}

	public static CallEntity create(Long memberId, Long scenarioId, String roomName) {
		return new CallEntity(memberId, scenarioId, roomName);
	}

	public void updateAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
}
