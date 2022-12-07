import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("com.netflix.dgs.codegen") version "5.4.0"
}

tasks.withType<BootJar> {
    mainClass.set("com.croquis.mystore.CatalogUxApplicationKt")
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    schemaPaths = mutableListOf("${projectDir}/src/main/resources/mystore-catalog.graphqls")
    generateClient = true
    packageName = "com.croquis.mystore.catalog.generated"
    generateDataTypes = true
    snakeCaseConstantNames = true
    language = "kotlin"
    typeMapping = mutableMapOf(
        "CrTimestamp" to "java.time.OffsetDateTime"
    )
}

tasks {
    val copyDatadog = register<Copy>("copyDatadog") {
        from(configurations.datadog).into("${project.buildDir}/libs")
        rename { "datadog.jar" }
    }

    getByName("jar").dependsOn(copyDatadog.name)
}

val logstashLogbackVersion: String? by ext
val sentryVersion: String? by ext

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:5.4.0"))
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    testImplementation("com.netflix.graphql.dgs:graphql-dgs-client:5.4.0")
    implementation("net.logstash.logback:logstash-logback-encoder:${logstashLogbackVersion}")
    implementation("io.sentry:sentry-logback:${sentryVersion}")
    implementation("io.sentry:sentry-spring-boot-starter:${sentryVersion}")
}

