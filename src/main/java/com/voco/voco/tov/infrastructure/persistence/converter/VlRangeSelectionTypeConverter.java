package com.voco.voco.tov.infrastructure.persistence.converter;

import com.voco.voco.tov.domain.model.enums.VlRangeSelectionType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VlRangeSelectionTypeConverter implements AttributeConverter<VlRangeSelectionType, String> {

	@Override
	public String convertToDatabaseColumn(VlRangeSelectionType attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	@Override
	public VlRangeSelectionType convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return VlRangeSelectionType.fromValue(dbData);
	}
}
