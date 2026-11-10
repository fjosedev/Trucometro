package dev.fernandojose.trucometro

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
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
    onView(withText("Jogador 1 venceu a partida!")).check(matches(withText("Jogador 1 venceu a partida!")))
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
}
