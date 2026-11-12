package dev.fernandojose.trucometro.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.fernandojose.trucometro.databinding.ActivityNomesBinding

// Permite editar os nomes dos jogadores e devolve o resultado para a MainActivity.
class NomesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNomesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNomesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.nomesRoot) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val p = (MARGEM_BARRAS_DP * v.resources.displayMetrics.density).toInt()
            v.setPadding(bars.left + p, bars.top + p, bars.right + p, bars.bottom + p)
            insets
        }

        if (savedInstanceState == null) {
            binding.etNomeJ1.setText(intent.getStringExtra("NOME_J1"))
            binding.etNomeJ2.setText(intent.getStringExtra("NOME_J2"))
        }

        binding.btnSalvarNomes.setOnClickListener {
            val resultado =
                Intent().apply {
                    putExtra("NOVO_NOME_J1", binding.etNomeJ1.text.toString().trim())
                    putExtra("NOVO_NOME_J2", binding.etNomeJ2.text.toString().trim())
                }
            setResult(RESULT_OK, resultado)
            finish()
        }

        binding.btnCancelarNomes.setOnClickListener { finish() }
    }
}
