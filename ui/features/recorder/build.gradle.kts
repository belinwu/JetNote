@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.cityzouitel.androidLibrary)
    alias(libs.plugins.cityzouitel.androidCompose)
}

android {
    namespace = "city.zouitel.recorder"
}

dependencies {
    //Modules.
    implementation(projects.domain)
    implementation(projects.ui.common.systemDesign)
    implementation(projects.core.datastore)

    //AndroidX.
    implementation(libs.constraintlayout)
}