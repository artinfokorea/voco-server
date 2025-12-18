package com.voco.voco.tov.infrastructure.persistence.converter;

import com.voco.voco.tov.domain.model.enums.VlQuestionType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VlQuestionTypeConverter implements AttributeConverter<VlQuestionType, String> {

	@Override
	public String convertToDatabaseColumn(VlQuestionType attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	@Override
	public VlQuestionType convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return VlQuestionType.fromValue(dbData);
	}
}
