package dev.fernandojose.trucometro.view

import android.view.View

private const val ESCALA_ESTUFAMENTO = 1.15f
private const val DURACAO_ESTUFAMENTO_MS = 100L

// Animação de estufamento: expande para 115% do tamanho e volta a 100%
fun View.animarEstufamento() {
    animate()
        .scaleX(ESCALA_ESTUFAMENTO)
        .scaleY(ESCALA_ESTUFAMENTO)
        .setDuration(DURACAO_ESTUFAMENTO_MS)
        .withEndAction {
            animate().scaleX(1.0f).scaleY(1.0f).setDuration(DURACAO_ESTUFAMENTO_MS).start()
        }
        .start()
}
