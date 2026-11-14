package dev.fernandojose.trucometro.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.fernandojose.trucometro.R
import dev.fernandojose.trucometro.databinding.ActivityHistoricoBinding

// Exibe o total de vitórias de cada jogador, recebido da MainActivity via Intent.
class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewById<android.widget.TextView>(R.id.toolbarStatus).setText(R.string.status_historico)

        ViewCompat.setOnApplyWindowInsetsListener(binding.historicoRoot) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.tvHistNomeJ1.text = intent.getStringExtra("NOME_J1")
        binding.tvHistVitoriasJ1.text = intent.getIntExtra("VITORIAS_J1", 0).toString()
        binding.tvHistNomeJ2.text = intent.getStringExtra("NOME_J2")
        binding.tvHistVitoriasJ2.text = intent.getIntExtra("VITORIAS_J2", 0).toString()

        binding.btnVoltar.setOnClickListener { finish() }
    }
}
