package com.pdmcourse2026.basictemplate

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pdmcourse2026.basictemplate.screens.home.HomeScreen
import com.pdmcourse2026.basictemplate.screens.massvote.MassVoteScreen
import com.pdmcourse2026.basictemplate.screens.option.OptionsScreen
import com.pdmcourse2026.basictemplate.screens.question.QuestionScreen

@Composable
fun RankeUCA_App() {
  //val backStack = rememberNavBackStack(Routes.Home)
  val backStack = rememberNavBackStack(Routes.Home)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider {
      entry<Routes.Home> {
        HomeScreen(
          onAdminClick = { backStack.add(Routes.Questions) },
          onMassVoteClick = { backStack.add(Routes.MassVote) }
        )
      }
      entry<Routes.Questions> {
        QuestionScreen(
          onQuestionClick = { id -> backStack.add(Routes.Options(id)) },
          onBack = { backStack.removeLastOrNull() }
        )
      }
      entry<Routes.Options> { route ->
        OptionsScreen(
          questionId = route.questionId,
          onBack = { backStack.removeLastOrNull() }
        )
      }
      entry<Routes.MassVote> {
        MassVoteScreen(
          onBack = { backStack.removeLastOrNull() }
        )
      }
    },
  )
}
