package dev.fernandojose.trucometro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.fernandojose.trucometro.model.PONTOS_VITORIA
import dev.fernandojose.trucometro.model.Trucometro

// Atua como o cérebro de regras de negócio da aplicação. Ela não sabe nada sobre elementos visuais
// (como botões, cores ou XMLs), mas contém toda a lógica para manipular as pontuações e estados do
// jogo.
class MainViewModel : ViewModel() {
    companion object {
        private const val MAX_CARACTERES_NOME = 16
    }

    // Variáveis de estado

    // 1. Estado principal que contém todos os dados do Trucometro (Nomes, Pontos e Vitórias)
    private val _estadoJogo = MutableLiveData(Trucometro())
    val estadoJogo: LiveData<Trucometro>
        get() = _estadoJogo

    // 2. Evento disparado apenas quando um jogador atinge 12 ou mais pontos
    private val _eventoVitoria = MutableLiveData<String?>()
    val eventoVitoria: LiveData<String?>
        get() = _eventoVitoria

    // 3. Mensagem para exibir avisos flutuantes na tela (Toast)
    private val _mensagemToast = MutableLiveData<String?>()
    val mensagemToast: LiveData<String?>
        get() = _mensagemToast

    // 4. Mensagem exibida em um Snackbar com a ação "Desfazer"
    private val _mensagemSnackbar = MutableLiveData<String?>()
    val mensagemSnackbar: LiveData<String?>
        get() = _mensagemSnackbar

    // Estado imediatamente anterior à última pontuação, usado pelo "Desfazer"
    private var estadoAnterior: Trucometro? = null

    /**
     * Incrementa os pontos (+1, +3, +6, +9 ou +12) do jogador selecionado. Ao atingir a pontuação
     * de vitória, registra a vitória e aguarda a confirmação da mensagem antes de zerar a rodada.
     */
    fun adicionarPontos(jogador: Int, pontos: Int) {
        if (_eventoVitoria.value != null) return

        val jogoAtual = _estadoJogo.value ?: Trucometro()
        estadoAnterior = jogoAtual.copy()
        val nome = if (jogador == 1) jogoAtual.nomeJogador1 else jogoAtual.nomeJogador2
        _mensagemSnackbar.value = "+$pontos para $nome"

        if (jogador == 1) {
            jogoAtual.pontosJogador1 += pontos
            if (jogoAtual.pontosJogador1 >= PONTOS_VITORIA) {
                jogoAtual.vitoriasJogador1++
                _eventoVitoria.value = "${jogoAtual.nomeJogador1} venceu a partida!"
            }
        } else if (jogador == 2) {
            jogoAtual.pontosJogador2 += pontos
            if (jogoAtual.pontosJogador2 >= PONTOS_VITORIA) {
                jogoAtual.vitoriasJogador2++
                _eventoVitoria.value = "${jogoAtual.nomeJogador2} venceu a partida!"
            }
        }

        // Notifica a MainActivity que o estado foi alterado
        _estadoJogo.value = jogoAtual
    }

    /** Zera os pontos da rodada após o usuário confirmar a mensagem de vitória. */
    fun finalizarPartida() {
        val jogoAtual = _estadoJogo.value ?: return
        estadoAnterior = null
        resetarPontosPartida(jogoAtual)
        _estadoJogo.value = jogoAtual
    }

    /** Restaura o estado anterior à última pontuação adicionada. */
    fun desfazerUltimaPontuacao() {
        estadoAnterior?.let { _estadoJogo.value = it }
        estadoAnterior = null
    }

    fun limparMensagemSnackbar() {
        _mensagemSnackbar.value = null
    }

    /** Zera os pontos da rodada sem alterar nomes ou vitórias acumuladas. */
    fun zerarPlacar() {
        val jogoAtual = _estadoJogo.value ?: Trucometro()
        estadoAnterior = null
        resetarPontosPartida(jogoAtual)
        _estadoJogo.value = jogoAtual
    }

    /** Reseta a pontuação da rodada atual sem alterar o número de vitórias acumuladas. */
    private fun resetarPontosPartida(jogo: Trucometro) {
        jogo.pontosJogador1 = 0
        jogo.pontosJogador2 = 0
    }

    /** Atualiza os nomes das equipes com os dados recebidos da NomesActivity. */
    fun atualizarNomes(nome1: String, nome2: String) {
        val jogoAtual = _estadoJogo.value ?: Trucometro()
        if (nome1.isNotBlank()) {
            jogoAtual.nomeJogador1 = nome1.trim().take(MAX_CARACTERES_NOME)
        }
        if (nome2.isNotBlank()) {
            jogoAtual.nomeJogador2 = nome2.trim().take(MAX_CARACTERES_NOME)
        }
        estadoAnterior = null
        _estadoJogo.value = jogoAtual
    }

    /** Zera o histórico de vitórias, pontuações e restaura os nomes originais. */
    fun zerarHistorico() {
        estadoAnterior = null
        _estadoJogo.value = Trucometro()
        _mensagemToast.value = "Histórico e pontuações foram zerados!"
    }

    // Métodos para limpar eventos de exibição única e evitar repetições ao girar a tela
    fun limparMensagemToast() {
        _mensagemToast.value = null
    }

    fun limparEventoVitoria() {
        _eventoVitoria.value = null
    }
}
