package com.voco.voco.app.scenario.domain.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
@Table(name = "voco_conversation_slots")
public class ConversationSlotEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scenario_id", nullable = false)
	private ConversationScenarioEntity scenario;

	@Column(name = "slot_key", nullable = false, length = 50)
	private String slotKey;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "allowed_values", nullable = false, columnDefinition = "jsonb")
	private List<String> allowedValues;

	private ConversationSlotEntity(String slotKey, List<String> allowedValues) {
		this.slotKey = slotKey;
		this.allowedValues = allowedValues;
	}

	public static ConversationSlotEntity create(String slotKey, List<String> allowedValues) {
		return new ConversationSlotEntity(slotKey, allowedValues);
	}

	public void setScenario(ConversationScenarioEntity scenario) {
		this.scenario = scenario;
	}
}
