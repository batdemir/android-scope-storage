package com.batdemir.scopestorage.ui.presentation

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.batdemir.scopestorage.core.BaseActivity
import com.batdemir.scopestorage.utils.StorageManager
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalFoundationApi::class)
class ImageExampleActivity : BaseActivity() {
    override val showBackPress: Boolean = true
    override val title: String = "Image Example"
    private var items: MutableStateFlow<List<Pair<Uri, Bitmap?>>> = MutableStateFlow(emptyList())
    private lateinit var capture: ActivityResultLauncher<Uri>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        capture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            updateItems()
        }
        updateItems()
    }

    override fun composeScreen(): @Composable ColumnScope.() -> Unit = {
        val items by items.collectAsState()
        LazyVerticalGrid(
            modifier = Modifier.weight(1f),
            cells = GridCells.Adaptive(128.dp)
        ) {
            itemsIndexed(items) { _, item ->
                item.second?.let {
                    Image(
                        modifier = Modifier
                            .size(72.dp)
                            .padding(8.dp)
                            .clickable {
                                contentResolver.delete(item.first, null, null)
                                updateItems()
                            },
                        bitmap = it.asImageBitmap(),
                        contentDescription = null
                    )
                }
            }
        }
        Button(onClick = { getUri()?.let { capture.launch(it) } }) {
            Text(text = "Capture Image")
        }
    }

    private fun updateItems() {
        lifecycleScope.launchWhenResumed {
            items.value = StorageManager.getImages(this@ImageExampleActivity)
        }
    }

    private fun getUri(): Uri? {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }
}
