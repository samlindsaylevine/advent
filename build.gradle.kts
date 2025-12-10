plugins {
  kotlin("jvm") version "2.2.21"
  `java-library`
}

repositories {
  mavenCentral()
}

tasks {
  named<Test>("test") {
    useJUnitPlatform()
  }
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("commons-io:commons-io:2.14.0")
  implementation("com.google.guava:guava:32.0.1-jre")
  implementation("commons-codec:commons-codec:1.15")
  implementation("org.apache.commons:commons-lang3:3.18.0")
  implementation("com.google.code.gson:gson:2.10")
  implementation("org.reflections:reflections:0.10.2")

  // For "meta" actions - downloading input file & problem statement.
  implementation(files("lib/CookieMonster.jar"))
  implementation("org.jsoup:jsoup:1.15.3")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
  testImplementation("org.assertj:assertj-core:3.23.1")

  api("org.apache.commons:commons-math3:3.6.1")

  // Google's OR Tools for solving optimization problems in linear programming.
  implementation("com.google.ortools:ortools-java:9.12.4544")
}