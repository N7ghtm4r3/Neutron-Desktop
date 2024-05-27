package com.tecknobit.neutron.ui.screens

import androidx.compose.runtime.Composable

abstract class Screen {

    companion object {

        const val SPLASH_SCREEN = "splashscreen"

        const val HOME_SCREEN = "home"

    }

    @Composable
    abstract fun ShowScreen()

}