plugins {
    kotlin("js")
}

base.archivesBaseName = "zircon.js.examples-js"

kotlin {
    target {
        browser()
    }
}

dependencies {
    with(Projects) {
        implementation(zirconCore)
        implementation(zirconJsCanvas)
    }
}