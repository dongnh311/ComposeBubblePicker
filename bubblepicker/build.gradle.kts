import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.dongnh.bubblepicker"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        named("main") {
            java.srcDirs("src/main/kotlin")
        }
        named("test") {
            java.srcDirs("src/test/kotlin")
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = false
    }

    lint {
        disable += "UnrememberedMutableState"
        abortOnError = false
        checkReleaseBuilds = false
    }
}

// AGP 8.7 + Compose BOM 2024.12 lint classloader crash
// (NoClassDefFoundError: ComposableBodyVisitor). Skip lint analysis until tool fix.
tasks.matching { it.name.startsWith("lint") }.configureEach { enabled = false }

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    testImplementation("junit:junit:4.13.2")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = "io.github.dongnh311",
        artifactId = "compose-bubble-picker",
        version = project.findProperty("releaseVersion") as String? ?: "1.0.0",
    )

    configure(AndroidSingleVariantLibrary(variant = "release", sourcesJar = true, publishJavadocJar = true))

    pom {
        name.set("ComposeBubblePicker")
        description.set("Compose-first bubble picker for Android with pure-Kotlin PBD physics")
        inceptionYear.set("2026")
        url.set("https://github.com/dongnh311/ComposeBubblePicker")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("dongnh311")
                name.set("DongNH")
                email.set("hoaidongit5@gmail.com")
                url.set("https://github.com/dongnh311")
            }
        }
        scm {
            url.set("https://github.com/dongnh311/ComposeBubblePicker")
            connection.set("scm:git:git://github.com/dongnh311/ComposeBubblePicker.git")
            developerConnection.set("scm:git:ssh://git@github.com/dongnh311/ComposeBubblePicker.git")
        }
    }
}
