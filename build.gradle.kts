// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt) apply false
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktfmt().kotlinlangStyle()
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**/*.kts")
        ktfmt().kotlinlangStyle()
    }
}

abstract class InstallGitHooksTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val hooksSourceDir: DirectoryProperty

    @get:Internal abstract val gitHooksDir: DirectoryProperty

    @TaskAction
    fun install() {
        val hooksFolder = gitHooksDir.get().asFile
        check(hooksFolder.isDirectory) {
            "Diretório .git/hooks não encontrado. Instale os hooks manualmente neste repositório."
        }
        listOf("pre-commit", "commit-msg", "pre-push").forEach { name ->
            val target = hooksFolder.resolve(name)
            if (target.exists()) {
                logger.warn("Hook existente preservado: {}", target)
            } else {
                hooksSourceDir.file(name).get().asFile.copyTo(target)
                check(target.setExecutable(true) || target.canExecute()) {
                    "Não foi possível tornar executável o hook $target"
                }
                logger.lifecycle("Hook instalado: {}", target)
            }
        }
    }
}

tasks.register<InstallGitHooksTask>("installGitHooks") {
    group = "verification"
    description = "Instala hooks Git sem substituir hooks existentes."
    hooksSourceDir.set(layout.projectDirectory.dir(".githooks"))
    gitHooksDir.set(layout.projectDirectory.dir(".git/hooks"))
}
