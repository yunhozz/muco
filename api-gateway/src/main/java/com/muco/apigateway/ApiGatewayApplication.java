package com.muco.apigateway;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
public class ApiGatewayApplication {

	private final RouteDefinitionLocator locator;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public List<GroupedOpenApi> apis() {
		List<GroupedOpenApi> groups = new ArrayList<>();
		List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
		assert definitions != null;

		definitions.stream()
				.filter(definition -> definition.getId().matches(".*-service") || definition.getId().matches(".*-server"))
				.forEach(definition -> {
					String name = definition.getId()
							.replaceAll("-service", "")
							.replaceAll("-server", "");

					GroupedOpenApi group = GroupedOpenApi.builder()
							.pathsToMatch("/" + name + "/**")
							.group(name)
							.build();

					groups.add(group);
		});

		return groups;
	}
}