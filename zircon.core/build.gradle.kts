plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.71"
    id("maven-publish")
    id("signing")
}

kotlin {

    jvm {
        jvmTarget(JavaVersion.VERSION_1_8)
        //withJava()
    }

    js {
        browser {

        }
    }

    dependencies {
        with(Libs) {
            commonMainApi(kotlinStdLibCommon)

            commonMainApi(kotlinxCoroutinesCommon)
            commonMainApi(kotlinxCollectionsImmutable)
            commonMainApi(kotlinReflect)
            commonMainApi(kotlinxSerializationCommon)

            commonMainApi(cobaltCore)

            commonMainApi(korio)
            commonMainApi(korim)
        }

        with(TestLibs) {
            commonTestImplementation(kotlinTestCommon)
            commonTestImplementation(kotlinTestAnnotationsCommon)
        }

        with(Libs) {
            jvmMainApi(kotlinStdLibJdk8)
            jvmMainApi(kotlinReflect)

            jvmMainApi(kotlinxCoroutines)

            jvmMainApi(caffeine)
            jvmMainApi(snakeYaml)
            jvmMainApi(slf4jApi)

            jvmMainApi(kotlinxSerializationJvm)

            jvmMainApi(korioJvm)
            jvmMainApi(korimJvm)
        }

        with(TestLibs) {
            jvmTestApi(kotlinTestJunit)
            jvmTestApi(junit)
            jvmTestApi(mockitoAll)
            jvmTestApi(mockitoKotlin)
            jvmTestApi(assertJCore)
            jvmTestApi(logbackClassic)
            jvmTestApi(logbackCore)
        }

        with(Libs) {
            jvmMainApi(kotlinStdLibJs)
            jsMainApi(kotlinxSerializationJs)
            jsMainApi(kotlinxCoroutinesJs)
            jsMainApi(korioJs)
            jsMainApi(korimJs)
        }
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

publishing {
    publishWith(
            project = project,
            module = "zircon.core",
            desc = "Core component of Zircon."
    )
}

signing {
    isRequired = false
    sign(publishing.publications)
}

