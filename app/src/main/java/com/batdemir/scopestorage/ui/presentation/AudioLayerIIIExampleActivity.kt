package com.batdemir.scopestorage.ui.presentation

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import com.batdemir.scopestorage.core.BaseActivity
import com.batdemir.scopestorage.utils.StorageManager
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalFoundationApi::class)
class AudioLayerIIIExampleActivity : BaseActivity() {
    override val showBackPress: Boolean = true
    override val title: String = "MP 3 Example"
    private var items: MutableStateFlow<List<Pair<Uri, Bitmap?>>> = MutableStateFlow(emptyList())
    private lateinit var downloadManager: DownloadManager
    private val onCompleted = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            updateItems()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        updateItems()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(onCompleted, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onStop() {
        unregisterReceiver(onCompleted)
        super.onStop()
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
                            .size(128.dp)
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
        Button(onClick = { getUri()?.let { downloadMp3(it) } }) {
            Text(text = "Capture MP 3")
        }
    }

    private fun downloadMp3(uri: Uri) {
        val request =
            DownloadManager.Request(Uri.parse("https://github.com/batdemir/android-scope-storage/blob/master/audios/test.mp3"))
        request.setTitle("Downloading")
        request.setMimeType("audio/mp3")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationUri(uri)
        try {
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
            updateItems()
        }
    }

    private fun updateItems() {
        items.value = StorageManager.getAudios(this)
    }

    private fun getUri(): Uri? {
        val fileName = "${System.currentTimeMillis()}.mp3"
        val contentValues =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                }
            } else {
                val directory = this@AudioLayerIIIExampleActivity.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                ContentValues().apply {
                    put(MediaStore.Audio.AudioColumns.DATA, "${directory}$fileName")
                }
            }
        return contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
    }
}
