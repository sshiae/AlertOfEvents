pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        jcenter()
    }
}
dependencyResolutionManagement {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}

rootProject.name = "AlertOfEvents"
include(":app")