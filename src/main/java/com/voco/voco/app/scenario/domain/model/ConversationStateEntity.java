package com.voco.voco.app.scenario.domain.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "voco_conversation_states")
public class ConversationStateEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scenario_id", nullable = false)
	private ConversationScenarioEntity scenario;

	@Column(name = "state_order", nullable = false)
	private Integer stateOrder;

	@Column(name = "state_name", nullable = false, length = 50)
	private String stateName;

	private ConversationStateEntity(Integer stateOrder, String stateName) {
		this.stateOrder = stateOrder;
		this.stateName = stateName;
	}

	public static ConversationStateEntity create(Integer stateOrder, String stateName) {
		return new ConversationStateEntity(stateOrder, stateName);
	}

	public void setScenario(ConversationScenarioEntity scenario) {
		this.scenario = scenario;
	}
}
