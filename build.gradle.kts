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

// Tarefa compatível com Configuration Cache para instalar Git Hooks
abstract class InstallGitHooksTask : DefaultTask() {

    @get:OutputDirectory abstract val gitHooksDir: DirectoryProperty

    @TaskAction
    fun install() {
        val hooksFolder = gitHooksDir.get().asFile
        if (!hooksFolder.exists()) {
            hooksFolder.mkdirs()
        }

        // 1. Hook de Pre-Commit (Formatação automática com Spotless)
        val preCommitHook = hooksFolder.resolve("pre-commit")
        preCommitHook.writeText(
            """
            #!/bin/bash
            unset ANDROID_PREFS_ROOT
            echo "🔍 [Git Hook] Formatando código com Spotless..."
            ./gradlew spotlessApply --daemon
            git diff --name-only --cached --diff-filter=ACMR | xargs git add
            """
                .trimIndent() + "\n",
        )
        preCommitHook.setExecutable(true)

        // 2. Hook de Commit-Msg (Validação de Conventional Commits)
        val commitMsgHook = hooksFolder.resolve("commit-msg")
        commitMsgHook.writeText(
            """
            #!/bin/bash
            commit_msg_file=${'$'}1
            commit_msg=$(cat "${'$'}commit_msg_file")

            pattern="^(revert: )?(feat|fix|docs|style|refactor|perf|test|build|ci|chore|wip)(\([a-zA-Z0-9_-]+\))?!?: .{1,100}${'$'}"

            if ! [[ "${'$'}commit_msg" =~ ${'$'}pattern ]]; then
                echo "❌ [Git Hook] MENSAGEM DE COMMIT INVÁLIDA!"
                echo "Sua mensagem: '${'$'}commit_msg'"
                echo ""
                echo "A mensagem deve seguir o padrão de Conventional Commits, ex:"
                echo "  feat: adiciona botão de limpar"
                echo "  fix: corrige formatação do resultado"
                echo "  refactor(view): move MainActivity para o pacote view"
                echo "  test: adiciona testes para ImcViewModel"
                echo ""
                echo "Tipos aceitos: feat, fix, docs, style, refactor, perf, test, build, ci, chore, wip"
                exit 1
            fi
            """
                .trimIndent() + "\n",
        )
        commitMsgHook.setExecutable(true)

        // 3. Hook de Pre-Push (Execução de testes unitários antes do push)
        val prePushHook = hooksFolder.resolve("pre-push")
        prePushHook.writeText(
            """
            #!/bin/bash
            unset ANDROID_PREFS_ROOT
            echo "🧪 [Git Hook] Executando testes unitários antes do push..."
            ./gradlew test --daemon
            if [ ${'$'}? -ne 0 ]; then
                echo "❌ [Git Hook] Testes unitários falharam. Push cancelado!"
                exit 1
            fi
            """
                .trimIndent() + "\n",
        )
        prePushHook.setExecutable(true)

        println("✅ [Git Hooks] Hooks instalados/atualizados com sucesso em .git/hooks/")
    }
}

val installGitHooksTask =
    tasks.register<InstallGitHooksTask>("installGitHooks") {
        group = "verification"
        description =
            "Instala automaticamente os Git Hooks (Spotless, Conventional Commits e Pre-Push) no projeto."
        gitHooksDir.set(layout.projectDirectory.dir(".git/hooks"))
    }

// Garante que os hooks sejam instalados na inicialização do build/sync
tasks
    .matching { it.name == "preBuild" }
    .configureEach {
        dependsOn(installGitHooksTask)
    }
