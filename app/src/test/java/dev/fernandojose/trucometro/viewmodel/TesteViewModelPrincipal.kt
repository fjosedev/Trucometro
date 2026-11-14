package dev.fernandojose.trucometro.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class TesteViewModelPrincipal {
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun estadoInicial_deveTerJogadoresPadraoEPontuacoesZeradas() {
        val viewModel = MainViewModel()

        val state = viewModel.estadoJogo.value
        assertEquals("Jogador 1", state?.nomeJogador1)
        assertEquals("Jogador 2", state?.nomeJogador2)
        assertEquals(0, state?.pontosJogador1)
        assertEquals(0, state?.pontosJogador2)
        assertEquals(0, state?.vitoriasJogador1)
        assertEquals(0, state?.vitoriasJogador2)
    }

    @Test
    fun adicionarPontos_deveAtualizarJogadorSelecionadoEExibirMensagem() {
        val viewModel = MainViewModel()

        viewModel.adicionarPontos(jogador = 2, pontos = 3)

        assertEquals(0, viewModel.estadoJogo.value?.pontosJogador1)
        assertEquals(3, viewModel.estadoJogo.value?.pontosJogador2)
        assertEquals("+3 para Jogador 2", viewModel.mensagemSnackbar.value)
    }

    @Test
    fun botoesDePontuacao_deveAdicionarValoresCorrespondentes() {
        val pontosDosBotoes = listOf(1, 3, 6, 9, 12)

        pontosDosBotoes.forEach { pontos ->
            val viewModel = MainViewModel()

            viewModel.adicionarPontos(jogador = 1, pontos = pontos)

            assertEquals(pontos, viewModel.estadoJogo.value?.pontosJogador1)
            assertEquals(0, viewModel.estadoJogo.value?.pontosJogador2)
        }
    }

    @Test
    fun adicionarPontos_aoAtingirDozeDeveRegistrarVitoriaEEsperarConfirmacaoParaZerar() {
        val viewModel = MainViewModel()
        viewModel.atualizarNomes("Ana", "Bia")
        viewModel.adicionarPontos(jogador = 1, pontos = 9)

        viewModel.adicionarPontos(jogador = 1, pontos = 3)

        val state = viewModel.estadoJogo.value
        assertEquals(1, state?.vitoriasJogador1)
        assertEquals(12, state?.pontosJogador1)
        assertEquals(0, state?.pontosJogador2)
        assertEquals("Ana venceu a partida!", viewModel.eventoVitoria.value)

        viewModel.finalizarPartida()

        assertEquals(0, viewModel.estadoJogo.value?.pontosJogador1)
        assertEquals(0, viewModel.estadoJogo.value?.pontosJogador2)
        assertEquals(1, viewModel.estadoJogo.value?.vitoriasJogador1)
        assertEquals("Ana", viewModel.estadoJogo.value?.nomeJogador1)
    }

    @Test
    fun adicionarPontos_acimaDeDozeTambemDeveRegistrarVitoria() {
        val viewModel = MainViewModel()
        viewModel.adicionarPontos(jogador = 2, pontos = 9)

        viewModel.adicionarPontos(jogador = 2, pontos = 6)

        assertEquals(1, viewModel.estadoJogo.value?.vitoriasJogador2)
        assertEquals(15, viewModel.estadoJogo.value?.pontosJogador2)
        assertEquals("Jogador 2 venceu a partida!", viewModel.eventoVitoria.value)
    }

    @Test
    fun zerarPlacar_deveManterNomesEVitoriasEApagarSomentePontos() {
        val viewModel = MainViewModel()
        viewModel.atualizarNomes("Ana", "Bia")
        viewModel.adicionarPontos(jogador = 1, pontos = 12)
        viewModel.adicionarPontos(jogador = 2, pontos = 6)

        viewModel.zerarPlacar()

        val state = viewModel.estadoJogo.value
        assertEquals("Ana", state?.nomeJogador1)
        assertEquals("Bia", state?.nomeJogador2)
        assertEquals(1, state?.vitoriasJogador1)
        assertEquals(0, state?.vitoriasJogador2)
        assertEquals(0, state?.pontosJogador1)
        assertEquals(0, state?.pontosJogador2)

        viewModel.desfazerUltimaPontuacao()
        assertEquals(0, viewModel.estadoJogo.value?.pontosJogador1)
        assertEquals(0, viewModel.estadoJogo.value?.pontosJogador2)
    }

    @Test
    fun desfazerUltimaPontuacao_deveRestaurarEstadoAnteriorApenasUmaVez() {
        val viewModel = MainViewModel()
        viewModel.adicionarPontos(jogador = 1, pontos = 6)
        viewModel.adicionarPontos(jogador = 2, pontos = 3)

        viewModel.desfazerUltimaPontuacao()

        assertEquals(6, viewModel.estadoJogo.value?.pontosJogador1)
        assertEquals(0, viewModel.estadoJogo.value?.pontosJogador2)
        viewModel.desfazerUltimaPontuacao()
        assertEquals(6, viewModel.estadoJogo.value?.pontosJogador1)
    }

    @Test
    fun atualizarNomes_deveIgnorarNomesVazios() {
        val viewModel = MainViewModel()
        viewModel.atualizarNomes("Ana", "Bia")
        viewModel.adicionarPontos(jogador = 1, pontos = 6)
        viewModel.atualizarNomes(" ", "Carlos")

        assertEquals("Ana", viewModel.estadoJogo.value?.nomeJogador1)
        assertEquals("Carlos", viewModel.estadoJogo.value?.nomeJogador2)
        assertEquals(6, viewModel.estadoJogo.value?.pontosJogador1)
    }

    @Test
    fun zerarHistorico_deveApagarVitoriasEPontuacoesERestaurarNomesPadrao() {
        val viewModel = MainViewModel()
        viewModel.atualizarNomes("Ana", "Bia")
        viewModel.adicionarPontos(jogador = 1, pontos = 12)
        viewModel.adicionarPontos(jogador = 2, pontos = 6)

        viewModel.zerarHistorico()

        val state = viewModel.estadoJogo.value
        assertEquals("Jogador 1", state?.nomeJogador1)
        assertEquals("Jogador 2", state?.nomeJogador2)
        assertEquals(0, state?.pontosJogador1)
        assertEquals(0, state?.pontosJogador2)
        assertEquals(0, state?.vitoriasJogador1)
        assertEquals(0, state?.vitoriasJogador2)
        assertEquals("Histórico e pontuações foram zerados!", viewModel.mensagemToast.value)
    }

    @Test
    fun atualizarNomes_deveRemoverEspacosELimitarNomesADezesseisCaracteres() {
        val viewModel = MainViewModel()

        viewModel.atualizarNomes("  Ana  ", "  ABCDEFGHIJKLMNOPQRST  ")

        assertEquals("Ana", viewModel.estadoJogo.value?.nomeJogador1)
        assertEquals("ABCDEFGHIJKLMNOP", viewModel.estadoJogo.value?.nomeJogador2)
    }

    @Test
    fun limparEventos_deveRemoverMensagensDeSnackbarToastEVitoria() {
        val viewModel = MainViewModel()
        viewModel.adicionarPontos(jogador = 1, pontos = 12)
        viewModel.zerarHistorico()
        viewModel.limparMensagemSnackbar()
        viewModel.limparMensagemToast()
        viewModel.limparEventoVitoria()

        assertNull(viewModel.mensagemSnackbar.value)
        assertNull(viewModel.mensagemToast.value)
        assertNull(viewModel.eventoVitoria.value)
    }
}
