package com.ffood.g1.converter;

import org.springframework.core.convert.converter.Converter;
import com.ffood.g1.enum_pay.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodConverter implements Converter<String, PaymentMethod> {

    @Override
    public PaymentMethod convert(String source) {
        try {
            return PaymentMethod.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // hoặc xử lý lỗi tùy theo yêu cầu của bạn
        }
    }
}