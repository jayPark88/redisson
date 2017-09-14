package com.ecdn.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration

@ComponentScan(
        basePackages="com.ecdn",
        excludeFilters = {
                @Filter(type = FilterType.ANNOTATION, value = Controller.class),
                @Filter(type = FilterType.ANNOTATION, value = Service.class),
                @Filter(type = FilterType.ANNOTATION, value = ControllerAdvice.class) }
)
public class AppContext {
}
