package com.ffood.g1.converter;

import com.ffood.g1.enum_pay.PaymentStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<PaymentStatus, String> {
    @Override
    public String convertToDatabaseColumn(PaymentStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return PaymentStatus.fromCode(dbData);
    }
}
