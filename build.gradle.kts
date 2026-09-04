plugins {
    application
    jacoco
    alias(libs.plugins.spotless)
    alias(libs.plugins.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.test.logger)
}

group = "io.hexlet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

application {
    mainClass = "io.hexlet.blog.App"
}

dependencies {
    implementation(libs.javalin)
    implementation(libs.javalinRenderingThymeleaf)
    implementation(libs.slf4jSimple)

    implementation(libs.thymeleaf)
    implementation(libs.thymeleafLayoutDialect)
    implementation(libs.thymeleafExtrasJava8time)
    implementation(libs.bootstrap)

    implementation(libs.h2)
    implementation(libs.postgresql)
    implementation(libs.hikariCp)

    testImplementation(platform(libs.junitBom))
    testRuntimeOnly(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.junitJupiterParams)
    testImplementation(libs.assertjCore)
    testImplementation(libs.unirestJava)
}

tasks.test {
    useJUnitPlatform()
}

testlogger {
    showStandardStreams = true
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

spotless {
    java {
        target("src/**/*.java")
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
        leadingTabsToSpaces(4)
    }
}

tasks.register("stage") {
    dependsOn("clean", "installDist")
}

tasks.installDist {
    mustRunAfter(tasks.clean)
}

// versionCatalogUpdate пишет свежие версии прямо в gradle/libs.versions.toml,
// поэтому руками их сверять не нужно. Ключи не сортируются: порядок в каталоге
// смысловой, по группам зависимостей.
versionCatalogUpdate {
    sortByKey = false
}
