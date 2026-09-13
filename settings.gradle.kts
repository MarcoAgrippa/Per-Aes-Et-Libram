pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Tesseract4Android (ćirilični OCR) se distribuira preko JitPack-a, nije na Maven Central.
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "PerAesEtLibram"
include(":app")
include(":baselineprofile")
