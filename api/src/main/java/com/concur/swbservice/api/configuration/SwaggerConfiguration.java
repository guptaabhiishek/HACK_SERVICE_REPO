package com.concur.swbservice.api.configuration;

import com.concur.swbservice.api.resource.v1_0.SwbserviceResource;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import rx.Observable;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spring.web.plugins.DefaultConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;

/**
 * Created by mtalbot on 11/08/2015.
 */
@Configuration
@EnableSwagger2
@EnableAutoConfiguration(exclude = {HypermediaAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
public class SwaggerConfiguration {
    @Bean
    public Docket swaggerConfig() {
        TypeResolver resolver = new TypeResolver();

        Docket dock = new Docket(DocumentationType.SWAGGER_2).
                forCodeGeneration(true).
                apiInfo(
                        new ApiInfo(
                                "swbservice",
                                "Simple demo project for muServices",
                                "v1",
                                "",
                                "agileteam@concur.com",
                                "Concur Internal Use Only",
                                "")
                ).
                alternateTypeRules(
                        AlternateTypeRules.newRule(resolveType(of(Observable.class, Collection.class, WildcardType.class), resolver), resolveType(of(List.class, WildcardType.class), resolver)),
                        AlternateTypeRules.newRule(resolveType(of(Observable.class, ResponseEntity.class, WildcardType.class), resolver), WildcardType.class),
                        AlternateTypeRules.newRule(resolveType(of(Observable.class, WildcardType.class), resolver), WildcardType.class),
                        AlternateTypeRules.newRule(resolveType(of(ResponseEntity.class, WildcardType.class), resolver), WildcardType.class),
                        AlternateTypeRules.newRule(resolveType(of(Collection.class, WildcardType.class), resolver), resolveType(of(List.class, WildcardType.class), resolver)),
                        AlternateTypeRules.newRule(UUID.class, String.class),
                        AlternateTypeRules.newRule(LocalDate.class, String.class),
                        AlternateTypeRules.newRule(DateTime.class, String.class)
                );

        return dock;
    }

    private ResolvedType resolveType(Stream<Class<? extends Object>> types, TypeResolver resolver) {
        return resolveType(types.iterator(), resolver);
    }

    private ResolvedType resolveType(Iterator<Class<? extends Object>> types, TypeResolver resolver) {
        Class<? extends Object> next = types.next();

        if (next == WildcardType.class) {
            return resolver.resolve(next);
        } else {
            return resolver.resolve(next, resolveType(types, resolver));
        }
    }
}
