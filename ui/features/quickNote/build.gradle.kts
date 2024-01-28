@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.cityzouitel.androidLibrary)
    alias(libs.plugins.cityzouitel.androidCompose)
}

android {
    namespace = "city.zouitel.quicknote"
}

dependencies {
    //Modules.
    implementation(projects.domain)
    implementation(projects.ui.common.systemDesign)
    implementation(projects.core.datastore)
    implementation(projects.ui.features.note)

    //AndroidX.
    implementation(libs.constraintlayout)

    //LiveData.
    implementation(libs.lifecycle.livedata)

    //Accompanist.
    implementation(libs.accompanist.systemuicontroller)
}