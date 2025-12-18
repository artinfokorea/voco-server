package com.voco.voco.tov.domain.model.converter;

import com.voco.voco.tov.domain.model.enums.VlExamStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VlExamStatusConverter implements AttributeConverter<VlExamStatus, String> {

	@Override
	public String convertToDatabaseColumn(VlExamStatus attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	@Override
	public VlExamStatus convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return VlExamStatus.fromValue(dbData);
	}
}
