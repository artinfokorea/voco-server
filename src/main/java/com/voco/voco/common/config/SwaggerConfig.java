package com.voco.voco.common.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "bearerAuth";

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes(SECURITY_SCHEME_NAME,
					new SecurityScheme()
						.name(SECURITY_SCHEME_NAME)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
				))
			.info(new Info()
				.title("VOCO API")
				.version("1.0")
				.description("VOCO API DOC")
			);
	}

	@Bean
	public GroupedOpenApi api() {
		String[] paths = {"/api/v1/**"};
		String[] packagesToScan = {"com.voco.voco"};
		return GroupedOpenApi.builder().group("springdoc-openapi")
			.pathsToMatch(paths)
			.packagesToScan(packagesToScan).build();
	}
}
