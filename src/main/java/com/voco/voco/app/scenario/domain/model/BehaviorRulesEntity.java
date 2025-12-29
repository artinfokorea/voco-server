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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "voco_behavior_rules")
public class BehaviorRulesEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scenario_id", nullable = false)
	private ConversationScenarioEntity scenario;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "rules", nullable = false, columnDefinition = "jsonb")
	private List<String> rules;

	private BehaviorRulesEntity(List<String> rules) {
		this.rules = rules;
	}

	public static BehaviorRulesEntity create(List<String> rules) {
		return new BehaviorRulesEntity(rules);
	}

	public void setScenario(ConversationScenarioEntity scenario) {
		this.scenario = scenario;
	}
}
