
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation("com.github.N7ghtm4r3:APIManager:2.2.3")
            implementation("com.tecknobit.neutroncore:Neutron-core:1.0.0")
            api("moe.tlaster:precompose:1.6.0")
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha06")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-alpha06")
            implementation("com.godaddy.android.colorpicker:compose-color-picker-jvm:0.7.0")
            implementation("com.darkrockstudios:mpfilepicker:3.1.0")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(Deb, Pkg, Exe)
            packageName = "Neutron"
            packageVersion = "1.0.0"
            packageName = "Neutron"
            packageVersion = "1.0.0"
            version = "1.0.0"
            description = "Order and ticket revenue manager for the projects you are developing"
            copyright = "© 2024 Tecknobit"
            vendor = "Tecknobit"
            licenseFile.set(project.file("LICENSE"))
            macOS {
                bundleID = "com.tecknobit.neutron"
                iconFile.set(project.file("src/commonMain/resources/logo.icns"))
            }
            windows {
                iconFile.set(project.file("src/commonMain/resources/logo.ico"))
                upgradeUuid = UUID.randomUUID().toString()
            }
            linux {
                iconFile.set(project.file("src/commonMain/resources/logo.png"))
                packageName = "com-tecknobit-neutron"
                debMaintainer = "infotecknobitcompany@gmail.com"
                appRelease = "1.0.0"
                appCategory = "PERSONALIZATION"
                rpmLicenseType = "MIT"
            }
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
            obfuscate.set(true)
        }
    }
}
