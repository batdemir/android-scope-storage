package com.batdemir.scopestorage.utils

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.batdemir.scopestorage.core.BaseActivity

object StorageManager {
    fun getAudios(activity: BaseActivity): MutableList<Pair<Uri, Bitmap?>> {
        val values: MutableList<Pair<Uri, Bitmap?>> = mutableListOf()
        val valueUrls: MutableList<Pair<Long, Uri>> = mutableListOf()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        activity.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                valueUrls.add(Pair(id, ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)))
            }
        }
        valueUrls.forEach {
            try {
                val bitmap = BitmapFactory.decodeResource(
                    activity.resources,
                    android.R.drawable.presence_audio_online
                )
                values.add(Pair(it.second, bitmap))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return values
    }

    fun getImages(activity: BaseActivity): MutableList<Pair<Uri, Bitmap?>> {
        val values: MutableList<Pair<Uri, Bitmap?>> = mutableListOf()
        val valueUrls: MutableList<Pair<Long, Uri>> = mutableListOf()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        activity.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                valueUrls.add(Pair(id, ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)))
            }
        }
        valueUrls.forEach {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val source = ImageDecoder.createSource(activity.contentResolver, it.second)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(activity.contentResolver, it.second)
                }
                values.add(Pair(it.second, bitmap))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return values
    }

    fun getVideos(activity: BaseActivity): MutableList<Pair<Uri, Bitmap?>> {
        val values: MutableList<Pair<Uri, Bitmap?>> = mutableListOf()
        val valueUrls: MutableList<Pair<Long, Uri>> = mutableListOf()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Video.Media._ID)
        activity.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                valueUrls.add(Pair(id, ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)))
            }
        }
        valueUrls.forEach {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activity.contentResolver.loadThumbnail(it.second, Size(128, 128), null)
                } else {
                    MediaStore.Video.Thumbnails.getThumbnail(
                        activity.contentResolver,
                        it.first,
                        MediaStore.Video.Thumbnails.MICRO_KIND,
                        null
                    )
                }
                values.add(Pair(it.second, bitmap))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return values
    }
}
