package com.batdemir.scopestorage

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.batdemir.scopestorage.core.BaseActivity
import com.batdemir.scopestorage.extensions.move
import com.batdemir.scopestorage.ui.presentation.AudioLayerIIIExampleActivity
import com.batdemir.scopestorage.ui.presentation.ImageExampleActivity
import com.batdemir.scopestorage.ui.presentation.VideoExampleActivity

class MainActivity : BaseActivity() {
    override val showBackPress: Boolean = false
    override val title: String = "Main"
    override fun composeScreen(): @Composable ColumnScope.() -> Unit = {
        Button(onClick = { move(AudioLayerIIIExampleActivity::class.java, true) }) {
            Text(text = "MP 3 Example")
        }
        Button(onClick = { move(ImageExampleActivity::class.java, true) }) {
            Text(text = "Image Example")
        }
        Button(onClick = { move(VideoExampleActivity::class.java, true) }) {
            Text(text = "Video Example")
        }
    }
}
