package bz.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bz.ui.const.Theme

@Composable
fun SelectionButton(
    text: String,
    modifier: Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        Text(text)
    }
}

@Preview
@Composable
fun SelectionButtonPreview() {
    val colors = arrayOf(Theme.Base)

    /*colors = arrayOf(
        0xFF355070,
        0xFF6D597A,
        0xFFB56576,
        0xFFE56B6F,
        0xFFEAAC8B
    )
    colors = arrayOf(
        0xFFE63946,
        0xFFF1FAEE,
        0xFFA8DADC,
        0xFF457B9D,
        0xFF1D3557
    )

    colors = arrayOf(
        0xFF001427,
        0xFF708d81,
        0xFFf4d58d,
        0xFFbf0603,
        0xFF8d0801
    )
    colors = arrayOf(
        0xFF2B2D42,
        0xFF8D99AE,
        0xFFEDF2FA,
        0xFFEF233C,
        0xFFD90429
    )*/
    Column {
        repeat(colors.count()) {
            SelectionButton(
                text = "Test",
                modifier = Modifier.widthIn(min = 200.dp).heightIn(100.dp).padding(16.dp),
                colors = Theme.Button,
                onClick = {

                }
            )
        }

    }
}