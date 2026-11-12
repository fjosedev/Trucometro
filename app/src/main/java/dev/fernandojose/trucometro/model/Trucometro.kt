package dev.fernandojose.trucometro.model

// Classe de dados que guarda o estado atual da partida e gerencia a lógica das pontuações e
// histórico.
data class Trucometro(
    var nomeJogador1: String = "Jogador 1",
    var nomeJogador2: String = "Jogador 2",
    var pontosJogador1: Int = 0,
    var pontosJogador2: Int = 0,
    var vitoriasJogador1: Int = 0,
    var vitoriasJogador2: Int = 0,
)
