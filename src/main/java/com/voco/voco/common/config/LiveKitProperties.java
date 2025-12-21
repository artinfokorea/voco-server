package com.voco.voco.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "livekit")
public record LiveKitProperties(
	String apiKey,
	String apiSecret
) {
}
