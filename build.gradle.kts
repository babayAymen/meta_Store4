// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.21-1.0.15" apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
    id("io.realm.kotlin") version "1.11.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20" apply false
}