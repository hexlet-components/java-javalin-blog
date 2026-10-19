plugins {
    application
    jacoco
    alias(libs.plugins.spotless)
    alias(libs.plugins.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.lombok)
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(27) }
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

application {
    mainClass = "org.example.hexlet.App"
}

dependencies {
    implementation(libs.javalin)
    implementation(libs.javalinRenderingJte)
    implementation(libs.slf4jSimple)

    implementation(libs.jte)

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


// Версия JaCoCo задана явно: встроенная в Gradle отстаёт от JDK, и на новой
// Java агент падает с IllegalClassFormatException.
jacoco { toolVersion = libs.versions.jacoco.get() }

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
