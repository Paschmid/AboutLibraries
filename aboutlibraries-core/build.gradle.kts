import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    // kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.mikepenz.aboutlibraries.core"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    lint {
        abortOnError = false
    }
}

kotlin {
    targetHierarchy.default()

    jvm()
    js(IR) {
        nodejs {}
        browser {}
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap = true
                sourceMapEmbedSources = null
            }
        }
    }
    androidTarget {
        publishAllLibraryVariants()
    }
    // wasm()

    // tier 1
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // tier 2
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // tier 3
    // androidNativeArm32()
    // androidNativeArm64()
    // androidNativeX86()
    // androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    // common sets
    ios()
    tvos()
    watchos()

    /*
    cocoapods {
        summary = "AboutLibraries automatically detects all dependencies of a project and collects their information including the license."
        homepage = "https://github.com/mikepenz/AboutLibraries"
        authors = "Mike Penz"
        license = "Apache 2.0"
        framework {
            baseName = "AboutLibrariesFramework"
            isStatic = false // Dynamic framework support
        }
    }
     */

    sourceSets {
        val commonMain by getting

        val multiplatformMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(multiplatformMain)
        }
        val nativeMain by getting {
            dependsOn(multiplatformMain)
        }
        val jsMain by getting {
            dependsOn(multiplatformMain)
        }
        val androidMain by getting {
            dependsOn(commonMain)
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}

dependencies {
    // kotlinx Serialize
    "commonMainImplementation"(libs.kotlinx.serialization)
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach {
            noAndroidSdkLink.set(false)
        }
    }
}

if (project.hasProperty("pushall") || project.hasProperty("library_core_only")) {
    mavenPublishing {
        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
}