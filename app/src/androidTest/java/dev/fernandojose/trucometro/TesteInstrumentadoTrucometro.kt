package dev.fernandojose.trucometro

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.fernandojose.trucometro.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TesteInstrumentadoTrucometro {
    @get:Rule val regraCenario = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun aoAbrirAplicativo_deveExibirPlacarZerado() {
        onView(withId(R.id.tvScorePlayer1)).check(matches(withText("0")))
        onView(withId(R.id.tvScorePlayer2)).check(matches(withText("0")))
    }

    @Test
    fun aoAdicionarPontos_deveAtualizarSomenteJogadorSelecionado() {
        onView(withId(R.id.btnP1Add3)).perform(click())

        onView(withId(R.id.tvScorePlayer1)).check(matches(withText("3")))
        onView(withId(R.id.tvScorePlayer2)).check(matches(withText("0")))

        onView(withId(R.id.btnP2Add6)).perform(click())

        onView(withId(R.id.tvScorePlayer1)).check(matches(withText("3")))
        onView(withId(R.id.tvScorePlayer2)).check(matches(withText("6")))
    }

    @Test
    fun aoAtingirDozePontos_deveExibirVitoriaEZerarPlacar() {
        onView(withId(R.id.btnP1Add12)).perform(click())

        onView(withText("Fim de Partida!")).check(matches(withText("Fim de Partida!")))
        onView(withText("Jogador 1 venceu a partida!"))
            .check(matches(withText("Jogador 1 venceu a partida!")))
        onView(withText("OK")).perform(click())
        onView(withId(R.id.tvScorePlayer1)).check(matches(withText("0")))
        onView(withId(R.id.tvScorePlayer2)).check(matches(withText("0")))
    }

    @Test
    fun aoDesfazerPontuacao_deveRestaurarPlacarAnterior() {
        onView(withId(R.id.btnP1Add3)).perform(click())
        onView(withText("Desfazer")).perform(click())

        onView(withId(R.id.tvScorePlayer1)).check(matches(withText("0")))
        onView(withId(R.id.tvScorePlayer2)).check(matches(withText("0")))
    }

    @Test
    fun comMensagemDePontuacaoVisivel_deveAbrirConfirmacaoDeZerarHistorico() {
        onView(withId(R.id.btnP2Add3)).perform(click())
        onView(withText("+3 para Jogador 2")).check(matches(isDisplayed()))

        onView(withId(R.id.btnZerarHistorico)).perform(click())
        onView(withId(android.R.id.button1)).check(matches(withText("Zerar")))
    }

    @Test
    fun aoZerarHistorico_deveApagarVitoriasEPontuacoesERestaurarNomesPadrao() {
        onView(withId(R.id.btnInformarNomes)).perform(click())
        onView(withId(R.id.etNomeJ1)).perform(replaceText("Ana"))
        onView(withId(R.id.etNomeJ2)).perform(replaceText("Bia"), closeSoftKeyboard())
        onView(withId(R.id.btnSalvarNomes)).perform(click())

        onView(withId(R.id.btnP1Add12)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.btnP2Add3)).perform(click())
        onView(withId(R.id.btnZerarHistorico)).perform(click())
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.tvLabelPlayer1)).check(matches(withText("Jogador 1:")))
        onView(withId(R.id.tvLabelPlayer2)).check(matches(withText("Jogador 2:")))
        onView(withId(R.id.tvScorePlayer1)).check(matches(withText("0")))
        onView(withId(R.id.tvScorePlayer2)).check(matches(withText("0")))

        onView(withId(R.id.btnHistorico)).perform(click())
        onView(withId(R.id.tvHistNomeJ1)).check(matches(withText("Jogador 1")))
        onView(withId(R.id.tvHistVitoriasJ1)).check(matches(withText("0")))
    }
}
