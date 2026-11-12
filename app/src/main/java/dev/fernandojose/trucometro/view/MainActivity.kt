package dev.fernandojose.trucometro.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import dev.fernandojose.trucometro.databinding.ActivityMainBinding
import dev.fernandojose.trucometro.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private val viewModel: MainViewModel by viewModels()

  // Recebe os nomes editados na NomesActivity (substitui o startActivityForResult)
  private val nomesLauncher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
          val nome1 = result.data?.getStringExtra("NOVO_NOME_J1") ?: ""
          val nome2 = result.data?.getStringExtra("NOVO_NOME_J2") ?: ""
          viewModel.atualizarNomes(nome1, nome2)
        }
      }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Infla o layout via View Binding
    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    // Ajusta as barras do sistema no ScrollView (android:id="@+id/main")
    ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    configurarObservadores()
    configurarListeners()
  }

  private fun configurarObservadores() {
    // Observa as alterações no ViewModel e atualiza os TextViews do XML
    viewModel.estadoJogo.observe(this) { jogo ->
      binding.tvLabelPlayer1.text = String.format("%s:", jogo.nomeJogador1)
      binding.tvScorePlayer1.text = jogo.pontosJogador1.toString()

      binding.tvLabelPlayer2.text = String.format("%s:", jogo.nomeJogador2)
      binding.tvScorePlayer2.text = jogo.pontosJogador2.toString()
    }

    // Exibe o alerta quando um jogador vence (>= 12 pontos)
    viewModel.eventoVitoria.observe(this) { mensagem ->
      mensagem?.let {
        AlertDialog.Builder(this)
            .setTitle("Fim de Partida!")
            .setMessage(it)
            .setPositiveButton("OK", null)
            .show()
        viewModel.limparEventoVitoria()
      }
    }

    // Exibe Snackbar com "Desfazer" após cada pontuação
    viewModel.mensagemSnackbar.observe(this) { mensagem ->
      mensagem?.let {
        Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
            .setAction("Desfazer") { viewModel.desfazerUltimaPontuacao() }
            .show()
        viewModel.limparMensagemSnackbar()
      }
    }

    // Exibe Toast quando o histórico for zerado
    viewModel.mensagemToast.observe(this) { mensagem ->
      mensagem?.let {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        viewModel.limparMensagemToast()
      }
    }
  }

  private fun configurarListeners() {
    // Botões de pontuação - Jogador 1 (IDs atualizados)
    binding.btnP1Add1.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(1, 1)
    }
    binding.btnP1Add3.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(1, 3)
    }
    binding.btnP1Add6.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(1, 6)
    }
    binding.btnP1Add9.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(1, 9)
    }
    binding.btnP1Add12.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(1, 12)
    }

    // Botões de pontuação - Jogador 2 (IDs atualizados)
    binding.btnP2Add1.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(2, 1)
    }
    binding.btnP2Add3.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(2, 3)
    }
    binding.btnP2Add6.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(2, 6)
    }
    binding.btnP2Add9.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(2, 9)
    }
    binding.btnP2Add12.setOnClickListener {
      it.animarEstufamento()
      viewModel.adicionarPontos(2, 12)
    }

    // Botões de Ações
    binding.btnHistory.setOnClickListener {
      val jogo = viewModel.estadoJogo.value ?: return@setOnClickListener
      val intent =
          Intent(this, HistoricoActivity::class.java).apply {
            putExtra("NOME_J1", jogo.nomeJogador1)
            putExtra("VITORIAS_J1", jogo.vitoriasJogador1)
            putExtra("NOME_J2", jogo.nomeJogador2)
            putExtra("VITORIAS_J2", jogo.vitoriasJogador2)
          }
      startActivity(intent)
    }

    // Pede confirmação antes de apagar vitórias e nomes
    binding.btnResetHistory.setOnClickListener {
      AlertDialog.Builder(this)
          .setTitle("Zerar Histórico")
          .setMessage("Isso apaga as vitórias, as pontuações e restaura os nomes. Continuar?")
          .setPositiveButton("Zerar") { _, _ -> viewModel.zerarHistorico() }
          .setNegativeButton("Cancelar", null)
          .show()
    }

    binding.btnEditNames.setOnClickListener {
      val jogo = viewModel.estadoJogo.value ?: return@setOnClickListener
      val intent =
          Intent(this, NomesActivity::class.java).apply {
            putExtra("NOME_J1", jogo.nomeJogador1)
            putExtra("NOME_J2", jogo.nomeJogador2)
          }
      nomesLauncher.launch(intent)
    }
  }
}
