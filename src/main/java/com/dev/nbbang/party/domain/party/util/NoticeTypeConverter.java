package com.dev.nbbang.party.domain.party.util;

import com.dev.nbbang.party.domain.party.entity.NoticeType;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class NoticeTypeConverter implements Converter<String, NoticeType> {

    @Override
    public NoticeType convert(String source) {
        try {
            System.out.println(source);
            return NoticeType.valueOf(source.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
