plugins {
  id("org.jetbrains.kotlin.jvm") version "1.5.31"
  `java-library`
}

repositories {
  mavenCentral()
}

tasks {
  named<Test>("test") {
    useJUnitPlatform()
  }

  withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
    }
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("commons-io:commons-io:2.7")
  implementation("com.google.guava:guava:30.0-jre")
  implementation("commons-codec:commons-codec:1.10")
  implementation("org.apache.commons:commons-lang3:3.4")
  implementation("com.google.code.gson:gson:2.10")
  implementation("org.reflections:reflections:0.9.8")

  // For "meta" actions - downloading input file & problem statement.
  implementation(files("lib/CookieMonster.jar"))
  implementation("org.jsoup:jsoup:1.14.3")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.1.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
  testImplementation("org.assertj:assertj-core:3.8.0")

  api("org.apache.commons:commons-math3:3.6.1")
}