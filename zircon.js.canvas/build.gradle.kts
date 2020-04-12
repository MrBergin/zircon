plugins {
    kotlin("js")
}

kotlin {
    target {
        browser()
    }
}

dependencies {
    with(Projects) {
        implementation(zirconCore)
    }
}