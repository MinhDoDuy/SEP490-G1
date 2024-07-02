package com.ffood.g1.converter;

import com.ffood.g1.enum_pay.OrderType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderTypeConverter implements Converter<String, OrderType> {

    @Override
    public OrderType convert(String source) {
        try {
            return OrderType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // hoặc xử lý lỗi tùy theo yêu cầu của bạn
        }
    }
}