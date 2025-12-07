import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}


group = "bz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven ("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    maven ("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test"))
    implementation("com.fazecast:jSerialComm:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    //implementation("org.bouncycastle:bcpkix-jdk15to18:1.77")
    implementation("org.bytedeco:javacv-platform:1.5.9")

    implementation("io.github.davidepianca98:kmqtt-common-jvm:1.0.0")
    implementation("io.github.davidepianca98:kmqtt-client-jvm:1.0.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit-converters:2.8.1")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")


    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.9")

    // ZXing for QR and Barcode scanning
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")

    //odb interface
    implementation("com.github.eltonvs:kotlin-obd-api:1.3.0")
}

compose.desktop {
    application {
        mainClass = "bz.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "BzFare"
            packageVersion = "1.0.0"
            macOS {
                iconFile.set(project.file("belazon.icns"))
            }
            windows {
                iconFile.set(project.file("belazon.ico"))
            }
            linux {
                iconFile.set(project.file("belazon.png"))
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}