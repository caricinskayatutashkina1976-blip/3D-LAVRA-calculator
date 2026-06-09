package com.lavka.calculator.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lavka.calculator.ui.screens.AboutScreen
import com.lavka.calculator.ui.screens.AgreementScreen
import com.lavka.calculator.ui.screens.CalculatorScreen
import com.lavka.calculator.ui.screens.SettingsScreen
import com.lavka.calculator.ui.theme.LavkaCalculatorTheme
import com.lavka.calculator.ui.theme.OrangePrimary
import com.lavka.calculator.viewmodel.AppViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Calculator : Screen("calculator", "Калькулятор", Icons.Default.Calculate)
    data object Settings : Screen("settings", "Настройки", Icons.Default.Settings)
    data object About : Screen("about", "О приложении", Icons.Default.Info)
}

private val bottomNavItems = listOf(
    Screen.Calculator,
    Screen.Settings,
    Screen.About
)

@Composable
fun LavkaCalculatorApp(viewModel: AppViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsState()
    val input by viewModel.input.collectAsState()
    val result by viewModel.result.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showAgreement by viewModel.showAgreement.collectAsState()

    LavkaCalculatorTheme(themeMode = settings.themeMode) {
        if (showAgreement) {
            AgreementScreen(
                isFirstLaunch = !settings.agreementAccepted,
                onAccept = { viewModel.acceptAgreement() },
                onDecline = { viewModel.declineAgreement() },
                onClose = { viewModel.dismissAgreementView() }
            )
        } else {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(screen.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = OrangePrimary,
                                    selectedTextColor = OrangePrimary,
                                    indicatorColor = OrangePrimary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Calculator.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Calculator.route) {
                        CalculatorScreen(
                            input = input,
                            result = result,
                            errorMessage = errorMessage,
                            settings = settings,
                            onInputChange = { newInput -> viewModel.updateInput { newInput } },
                            onCalculate = { viewModel.calculate() },
                            onReset = { viewModel.resetInputToDefaults() }
                        )
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            settings = settings,
                            onSettingsChange = { viewModel.updateSettings(it) },
                            onResetAll = { viewModel.resetAllSettings() }
                        )
                    }
                    composable(Screen.About.route) {
                        AboutScreen(
                            onShowAgreement = { viewModel.showAgreementScreen() }
                        )
                    }
                }
            }
        }
    }
}
