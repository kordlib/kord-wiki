plugins {
    kotlin("multiplatform") version "1.8.20"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
}

kotlin {
    jvmToolchain(17)
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation("dev.kord:kord-core:0.9.x-SNAPSHOT")
            }
        }

        named("jvmMain") {
            dependencies {
                implementation("dev.kord:kord-core-voice:0.9.x-SNAPSHOT")
                implementation("com.github.walkyst:lavaplayer-fork:1.4.0")
            }
        }
    }
}
