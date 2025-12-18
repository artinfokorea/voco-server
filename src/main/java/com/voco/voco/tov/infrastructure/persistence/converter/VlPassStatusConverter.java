package com.voco.voco.tov.infrastructure.persistence.converter;

import com.voco.voco.tov.domain.model.enums.VlPassStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VlPassStatusConverter implements AttributeConverter<VlPassStatus, String> {

	@Override
	public String convertToDatabaseColumn(VlPassStatus attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	@Override
	public VlPassStatus convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return VlPassStatus.fromValue(dbData);
	}
}
