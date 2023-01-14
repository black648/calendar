import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("kapt") version "1.7.22"
}

group = "com"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
val querydslVersion = "5.0.0"

repositories {
    mavenCentral()
}

apply(plugin = "kotlin-kapt")
apply(plugin = "kotlin-jpa")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    implementation("commons-collections:commons-collections:3.2.2")
    implementation("commons-lang:commons-lang:2.6")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.0.1")
    implementation("org.modelmapper:modelmapper:3.1.0")

    //DB
    implementation("org.postgresql:postgresql")
    implementation("org.mariadb.jdbc:mariadb-java-client")

    //JWT 0.11.5 버전..
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // QueryDSL 스프링 부트 3.0 이상
    implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// QueryDSL이 만들어주는 Qclass를 사용하기 위해 저 위치로 접근할 수 있도록 설정해주는 부분이다.
sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
