# Trucometro

## Qualidade local

Requisitos: JDK 21 para o daemon Gradle e Android SDK com a plataforma 37. Para
formatar os arquivos Kotlin e Gradle:

```powershell
.\gradlew.bat spotlessApply
```

Para executar as mesmas verificações da CI:

```powershell
.\gradlew.bat spotlessCheck :app:detekt :app:lintDebug :app:testDebugUnitTest --continue
```

O Detekt analisa os arquivos Kotlin do módulo `app`; o lint e os testes unitários
usam a variante debug. Os testes instrumentados precisam de um dispositivo ou
emulador e não fazem parte dessa rotina. Os relatórios ficam em
`app/build/reports/`.

### Hooks locais

Instale os hooks explicitamente com `.\gradlew.bat installGitHooks`. A tarefa
copia os scripts de `.githooks` para `.git/hooks` sem substituir hooks existentes.
Se já houver algum hook, revise-o e integre manualmente o script correspondente.
Repositórios que usam `core.hooksPath` ou um arquivo `.git` (worktrees) precisam
instalar os scripts manualmente no diretório de hooks ativo.

O pre-commit verifica a formatação, sem modificar ou adicionar arquivos ao
índice; o commit-msg verifica a primeira linha da mensagem no padrão Conventional
Commits; e o pre-push executa as verificações de qualidade. As verificações
usam os arquivos atuais do diretório de trabalho, não apenas os arquivos no
índice ou nos commits sendo enviados. Execute `spotlessApply` e revise e adicione
as alterações desejadas antes de tentar novamente.

## CI

O GitHub Actions executa as mesmas verificações em pushes e pull requests e
disponibiliza os relatórios como artefato. A CI verifica o conteúdo do commit e
não depende de hooks locais.
