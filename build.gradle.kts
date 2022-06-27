plugins {
  val indraVersion = "2.1.1"
  id("net.kyori.indra") version indraVersion
  id("net.kyori.indra.checkstyle") version indraVersion
  id("net.kyori.indra.license-header") version indraVersion
  id("net.kyori.indra.publishing") version indraVersion
  id("net.kyori.indra.publishing.sonatype") version indraVersion
  id("net.ltgt.errorprone") version "2.0.2"
}

group = "com.kimorio"
description = "A registry framework"
version = "1.0.0-SNAPSHOT"

indra {
  github("kimorio", "registry") {
    ci(true)
  }

  mitLicense()

  javaVersions {
    target(17)
  }

  configurePublications {
    pom {
      developers {
        developer {
          id.set("kashike")
          name.set("Riley Park")
          timezone.set("America/Vancouver")
        }
      }
    }
  }
}

indraSonatype {
  useAlternateSonatypeOSSHost("s01")
}

repositories {
  mavenCentral()
}

dependencies {
  checkstyle("ca.stellardrift:stylecheck:0.1")
  errorprone("com.google.errorprone:error_prone_core:2.14.0")
  compileOnlyApi("org.jetbrains:annotations:23.0.0")
  testImplementation(platform("org.junit:junit-bom:5.8.2"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testImplementation("com.google.truth:truth:1.1.3")
}
