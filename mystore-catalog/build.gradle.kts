import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.withType<BootJar> {
    mainClass.set("com.croquis.mystore.CatalogUxApplicationKt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}

val logstashLogbackVersion: String? by ext
val sentryVersion: String? by ext


dependencies {
    implementation("net.logstash.logback:logstash-logback-encoder:${logstashLogbackVersion}")
    implementation("io.sentry:sentry-logback:${sentryVersion}")
    implementation("io.sentry:sentry-spring-boot-starter:${sentryVersion}")
}
