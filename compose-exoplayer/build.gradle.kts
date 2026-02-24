plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
}

val publishGroupId = project.findProperty("PUBLISH_GROUP_ID") as String? ?: "io.github.xxxape"
val publishArtifactId = project.findProperty("PUBLISH_ARTIFACT_ID") as String? ?: "compose-exoplayer"
val publishVersion = project.findProperty("PUBLISH_VERSION") as String? ?: "1.0.1"

android {
    namespace = "com.xxxape.exoplayer"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = publishVersion
                from(components["release"])
                pom {
                    name.set(publishArtifactId)
                    description.set("Compose ExoPlayer - Media3/ExoPlayer 的 Compose UI 封装")
                    url.set("https://github.com/xxxape/compose-exoplayer")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("xxxape")
                            name.set("xxxape")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/xxxape/compose-exoplayer.git")
                        developerConnection.set("scm:git:ssh://github.com/xxxape/compose-exoplayer.git")
                        url.set("https://github.com/xxxape/compose-exoplayer")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "local"
                url = uri(layout.buildDirectory.dir("repos/releases"))
            }
        }
    }
    signing {
        sign(publishing.publications["release"])
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.datasource)
    implementation(libs.media3.database)
    implementation(libs.media3.ui)
    implementation(libs.media3.ui.compose)
}
