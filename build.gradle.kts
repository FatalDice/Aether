import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.removeSuffixIfPresent
import proguard.gradle.ProGuardTask
import java.io.File

group = "uk.akane"
version = "1.0.0"

plugins {
    val kotlinVersion = "2.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("idea")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val proguardVersion = "7.6.0"

        classpath("com.guardsquare:proguard-gradle:$proguardVersion")
    }
}

mirai {
    noTestCore = true
    setupConsoleTestRuntime {
        classpath = classpath.filter {
            !it.nameWithoutExtension.startsWith("mirai-core-jvm")
        }
    }
}

repositories {
    if (System.getenv("CI")?.toBoolean() != true) {
        maven("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}

dependencies {
    val overflowVersion = "1.0.6"
    val coroutineVersion = "1.10.2"
    val ktorVersion = "3.2.0"

    implementation("io.ktor:ktor-server-netty:${ktorVersion}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    compileOnly("top.mrxiaom.mirai:overflow-core-api:$overflowVersion")
    testConsoleRuntime("top.mrxiaom.mirai:overflow-core:$overflowVersion")
}

open class BuildConfigExtension {
    private var _className: String = "BuildConfig"
    private var _packageName: String = ""
    var className: String
        set(value) { _className = value }
        get() = _className
    var packageName: String
        set(value) { _packageName = value }
        get() = _packageName
    fun BuildConfigExtension.className(className: String) { this.className = className }
    fun BuildConfigExtension.packageName(packageName: String) { this.packageName = packageName }

    val fields: MutableList<String> = mutableListOf()

    fun buildConfigField(type: String, name: String, value: String) {
        fields.add("    const val $name: $type = $value")
    }

    fun generateBuildConstantsFile(): String {
        val content = StringBuilder()
        content.appendLine("package $packageName")
        content.appendLine()
        content.appendLine("object $className {")
        fields.forEach { content.appendLine(it) }
        content.appendLine("}")
        return content.toString()
    }

    fun generateBuildConstants(outputDir: File) {
        val generatedClassContent = generateBuildConstantsFile()
        outputDir.mkdirs()
        val file = File(outputDir, "${className}.kt")
        file.writeText(generatedClassContent)
    }
}

fun Project.buildConfig(configure: BuildConfigExtension.() -> Unit) {
    val extension = extensions.create<BuildConfigExtension>("buildConfig")
    extension.configure()

    val outputDir = File("${buildDir}/generated/source")
    extension.generateBuildConstants(outputDir)
}

fun String.runCommand(
    workingDir: File = File(".")
): String = providers.exec {
    setWorkingDir(workingDir)
    commandLine(split(' '))
}.standardOutput.asText.get().removeSuffixIfPresent("\n")

buildConfig {
    className("BuildConstants")
    packageName("uk.akane.aether")

    buildConfigField("String", "AUTHOR", "\"AkaneTan\"")
    buildConfigField("String", "MAJOR_VERSION", "\"${project.version}\"")
    buildConfigField("String", "HASH_VERSION",  '\"' + "git rev-parse --short=7 HEAD".runCommand(workingDir = rootDir) + '\"')
    buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
}

tasks.withType<KotlinCompile> {
    source("${buildDir}/generated/source")
}

afterEvaluate {
    tasks.shadowJar {
        enabled = true
        archiveClassifier.set("debug")
    }
}

tasks.register<ProGuardTask>("proguard") {
    dependsOn("shadowJar")

    injars(file("${buildDir}/libs/${project.name}-${project.version}-debug.jar"))
    outjars(file("${buildDir}/libs/${project.name}-${project.version}-release.jar"))

    val javaHome = System.getProperty("java.home")
    if (System.getProperty("java.version").startsWith("1.")) {
        libraryjars("$javaHome/lib/rt.jar")
    } else {
        libraryjars(mapOf("filter" to "!module-info.class"), "$javaHome/jmods/java.base.jmod")
    }

    addLibraryJarsFromConfiguration(project, "compileClasspath") { jar ->
        jar.name.contains("annotations") || jar.name.contains("kotlin") ||
                jar.name.contains("mirai") || jar.name.contains("exposed")
    }

    configuration("proguard-rules.pro")

    printmapping(file("${buildDir}/proguard/mapping.txt"))
    allowaccessmodification()
    repackageclasses("")
}

tasks.register("assembleRelease") {
    group = "build"
    description = "Builds the release jar with shadowJar and obfuscation"

    dependsOn("shadowJar")
    dependsOn("proguard")
}

fun ProGuardTask.addLibraryJarsFromConfiguration(
    project: Project,
    configurationName: String,
    filter: (File) -> Boolean = { true }
) {
    val configuration = project.configurations.findByName(configurationName)
    configuration?.resolve()?.filter { it.name.endsWith(".jar") && filter(it) }?.forEach { jar ->
        libraryjars(jar)
    }
}