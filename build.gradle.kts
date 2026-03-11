plugins {
	`maven-publish`
	signing
	java
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
	withJavadocJar()
	withSourcesJar()
}

group = "moe.nea"
version = "1.4.0"

allprojects {
	apply(plugin = "java")
	tasks.withType<JavaCompile>().configureEach {
		options.release.set(21)
	}
	repositories {
		mavenCentral()
	}
}

project(":updater") {
	tasks.jar {
		archiveFileName.set("updater.jar")
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "moe.nea.libautoupdate.postexit.PostExitMain"
				)
			)
		}
	}
}

dependencies {
	@Suppress("VulnerableLibrariesLocal")
	implementation("com.google.code.gson:gson:2.13.2")

	// Use a modern JUnit version (5.11.4 is current for 2026)
	val junitVersion = "5.11.4"
	testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

	// MANDATORY for Gradle 9+: Add the JUnit Platform Launcher
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
	withJavadocJar()
	withSourcesJar()
}

tasks.withType(Test::class) {
	useJUnitPlatform()
}

tasks.javadoc {
	isFailOnError = false
}

tasks.processResources {
	val updateJar = tasks.getByPath(":updater:jar")
	from(updateJar.outputs)
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
			pom {
				licenses {
					license {
						name.set("BSD-2-Clause")
					}
				}
				developers {
					developer {
						name.set("Linnea Gräf")
					}
				}
				scm {
					url.set("https://git.nea.moe/nea/libautoupdate")
				}
			}
		}
	}
}
