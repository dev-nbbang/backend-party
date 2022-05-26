package com.dev.nbbang.party.global.config;

import com.dev.nbbang.party.domain.qna.util.AnswerTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new AnswerTypeConverter());
    }
}
