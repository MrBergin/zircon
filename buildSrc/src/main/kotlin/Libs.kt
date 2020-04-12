import Versions.cobaltVersion
import Versions.filtersVersion
import Versions.gdxVersion
import Versions.kotlinVersion
import Versions.kotlinxCollectionsImmutableVersion
import Versions.kotlinxCoroutinesVersion
import Versions.logbackVersion
import Versions.korlibsVersion

object Libs {

    const val kotlinStdLibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion"
    const val kotlinStdLibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    const val kotlinStdLibJs = "org.jetbrains.kotlin:kotlin-stdlib-js"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
    const val kotlinxCoroutinesCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$kotlinxCoroutinesVersion"
    const val kotlinxSerializationCommon = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.kotlinxSerializationVersion}"
    const val kotlinxSerializationJvm = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinxSerializationVersion}"
    const val kotlinxSerializationJs = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinxSerializationVersion}"
    const val kotlinxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion"
    const val kotlinxCoroutinesJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$kotlinxCoroutinesVersion"
    const val kotlinxCollectionsImmutable = "org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinxCollectionsImmutableVersion"

    const val cobaltCore = "org.hexworks.cobalt:cobalt.core:$cobaltVersion"

    const val korio = "com.soywiz.korlibs.korio:korio:$korlibsVersion"
    const val korioJvm = "com.soywiz.korlibs.korio:korio-jvm:$korlibsVersion"
    const val korioJs = "com.soywiz.korlibs.korio:korio-js:$korlibsVersion"

    const val korim = "com.soywiz.korlibs.korim:korim:$korlibsVersion"
    const val korimJvm = "com.soywiz.korlibs.korim:korim-jvm:$korlibsVersion"
    const val korimJs = "com.soywiz.korlibs.korim:korim-js:$korlibsVersion"

    const val caffeine = "com.github.ben-manes.caffeine:caffeine:${Versions.caffeineVersion}"
    const val snakeYaml = "org.yaml:snakeyaml:${Versions.snakeyamlVersion}"
    const val slf4jApi = "org.slf4j:slf4j-api:${Versions.slf4jVersion}"
    const val gdx = "com.badlogicgames.gdx:gdx:$gdxVersion"
    const val gdxFreetype = "com.badlogicgames.gdx:gdx-freetype:$gdxVersion"
    const val gdxFreetypePlatform = "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion"
    const val gdxBox2D = "com.badlogicgames.gdx:gdx-box2d:$gdxVersion"
    const val gdxBackendLwjgl = "com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion"
    const val gdxPlatform = "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
    const val gdxBox2DPlatform = "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop"
    const val logbackClassic = "ch.qos.logback:logback-classic:$logbackVersion"
    const val filters = "com.jhlabs:filters:$filtersVersion"
}