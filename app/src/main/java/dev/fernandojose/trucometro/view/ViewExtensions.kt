package dev.fernandojose.trucometro.view

import android.view.View

// Animação de estufamento: expande para 115% do tamanho e volta a 100%
fun View.animarEstufamento() {
    animate()
        .scaleX(1.15f)
        .scaleY(1.15f)
        .setDuration(100)
        .withEndAction { animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start() }
        .start()
}
