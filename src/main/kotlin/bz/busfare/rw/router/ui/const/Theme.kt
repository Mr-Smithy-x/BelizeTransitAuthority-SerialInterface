package bz.busfare.rw.router.ui.const

import androidx.compose.material.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import bz.busfare.rw.router.ui.BaseTheme

object Theme {

    val Base = BaseTheme(
        background = Color(0xFF2B2D42),
        border = Color(0xFF8D99AE),
        lightBorder = Color(0xFFEDF2FA),
        contentText = Color(0xFFFFFFFF),
        //button = Color(0xFFEF233C),
        //buttonPressed = Color(0xFFD90429)
        button = Color(0xFF003f88),
        buttonDisabled = Color(0xFF8D99AE),
        buttonPressed = Color(0xFF00296b)
    )

    data object Button: ButtonColors {
        @Composable
        override fun backgroundColor(enabled: Boolean): State<Color> {
            return mutableStateOf(when(enabled) {
                true -> Base.button
                else -> Base.buttonDisabled
            })
        }

        @Composable
        override fun contentColor(enabled: Boolean): State<Color> {
            return mutableStateOf(when(enabled) {
                true -> Base.contentText
                else -> Base.contentText
            })
        }
    }

}