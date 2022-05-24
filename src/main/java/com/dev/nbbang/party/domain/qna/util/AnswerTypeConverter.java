package com.dev.nbbang.party.domain.qna.util;

import com.dev.nbbang.party.domain.qna.entity.AnswerType;
import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.Annotation;
import java.util.Locale;

public class AnswerTypeConverter implements Converter<String, AnswerType> {

    @Override
    public AnswerType convert(String source) {
        try {
            System.out.println(source);
            return AnswerType.valueOf(source.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
