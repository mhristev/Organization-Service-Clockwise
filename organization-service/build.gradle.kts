plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.clockwise"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Exclude default logging from Spring Boot starters
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc") {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
	implementation("org.springframework.boot:spring-boot-starter-webflux") {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
	implementation("org.springframework.boot:spring-boot-starter-actuator") {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
	
	// Add Spring Boot Log4j2 starter 
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.micrometer:micrometer-observation")
	implementation("org.springframework.kafka:spring-kafka") {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.flywaydb:flyway-database-postgresql")
	
	// Logging - keep only kotlin-logging
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	// For async logging
	implementation("com.lmax:disruptor:4.0.0")
	// For DisruptorBlockingQueue
	implementation("com.conversantmedia:disruptor:1.2.19")
	// JSON logging format for Elasticsearch
	implementation("org.apache.logging.log4j:log4j-layout-template-json:2.20.0")
	
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:r2dbc")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("com.h2database:h2")
	testRuntimeOnly("io.r2dbc:r2dbc-h2")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	implementation("org.flywaydb:flyway-core")
	// Add PostgreSQL JDBC driver for Flyway
	implementation("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("io.mockk:mockk:1.13.10")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
