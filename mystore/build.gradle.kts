import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.withType<BootJar> {
    mainClass.set("com.croquis.mystore.CatalogUxApplicationKt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
