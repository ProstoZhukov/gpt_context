package ru.tensor.sbis.design.gallery.impl

import android.content.ClipboardManager
import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.gallery.R
import ru.tensor.sbis.design.gallery.impl.di.NEED_ONLY_IMAGES_BOOL_NAME
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryAlbumItem
import ru.tensor.sbis.design.gallery.impl.store.primitives.GalleryItem
import ru.tensor.sbis.design.gallery.impl.utils.intColumn
import ru.tensor.sbis.design.gallery.impl.utils.longColumn
import ru.tensor.sbis.design.gallery.impl.utils.stringColumn
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

internal class GalleryRepository @Inject constructor(
    private val fileUriUtil: FileUriUtil,
    private val contentResolver: ContentResolver,
    private val resourcesProvider: ResourceProvider,
    private val clipboardManager: ClipboardManager? = null,
    @Named(NEED_ONLY_IMAGES_BOOL_NAME) private val needOnlyImages: Boolean,
) {

    companion object {
        internal const val ALL_MEDIA_ALBUM_ID = -1
        private const val ALL_IMAGES_ALBUM_ID = -2
        private const val ALL_VIDEOS_ALBUM_ID = -3
        private const val CLIPBOARD_IMAGE_ID = -1
        private const val MIME_TYPE_ANY_IMAGE = "image/*"
    }

    class MediaFields(cursor: Cursor) {
        val path: String? by cursor.stringColumn(MediaColumns.DATA)
        val bucketId: Int? by cursor.intColumn(MediaColumns.BUCKET_ID)
        val bucketName: String? by cursor.stringColumn(MediaColumns.BUCKET_DISPLAY_NAME)
        val dateTaken: Long? by cursor.longColumn(
            if (Build.VERSION.SDK_INT > 28) MediaColumns.DATE_MODIFIED
            else MediaColumns.DATE_TAKEN
        )
        val itemId: Int? by cursor.intColumn(MediaColumns._ID)
        val width: Int? by cursor.intColumn(MediaColumns.WIDTH)
        val height: Int? by cursor.intColumn(MediaColumns.HEIGHT)
        val size: Long? by cursor.longColumn(MediaColumns.SIZE)
        val duration: Long? by cursor.longColumn(MediaColumns.DURATION)
    }

    suspend fun getGalleryItems(): Flow<Map<Int, GalleryAlbumItem>> = callbackFlow {
        val observer: ContentObserver =
            object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean) {
                    trySend(selfChange)
                }
            }
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, observer)
        contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, observer)
        trySend(false)
        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }.map {
        withContext(Dispatchers.IO) {
            val mediaAlbum =
                GalleryAlbumItem(
                    ALL_MEDIA_ALBUM_ID, resourcesProvider.getString(R.string.design_gallery_album_all_media)
                )
            val imagesAlbum =
                GalleryAlbumItem(
                    ALL_IMAGES_ALBUM_ID, resourcesProvider.getString(R.string.design_gallery_album_all_images)
                )
            val videosAlbum =
                GalleryAlbumItem(
                    ALL_VIDEOS_ALBUM_ID, resourcesProvider.getString(R.string.design_gallery_album_all_videos)
                )
            val allAlbums: MutableMap<Int, GalleryAlbumItem> = mutableMapOf(
                mediaAlbum.id to mediaAlbum,
                imagesAlbum.id to imagesAlbum,
                videosAlbum.id to videosAlbum
            )

            val clipItemUri = getClipboardImageUri()
            if (clipItemUri != null) {
                val fileInfo: FileUriUtil.FileInfo? =
                    try {
                        fileUriUtil.getFileInfo(
                            clipItemUri,
                            requestName = true,
                            requestSize = true,
                            requestMimeType = true
                        )
                    } catch (ex: Exception) {
                        null
                    }
                if (fileInfo?.name != null) {
                    allAlbums[ALL_MEDIA_ALBUM_ID]?.addItem(
                        GalleryItem(
                            CLIPBOARD_IMAGE_ID,
                            ALL_MEDIA_ALBUM_ID,
                            Date().time,
                            clipItemUri
                        )
                    )
                }
            }

            readItems(isImages = true, allAlbums, mediaAlbum, imagesAlbum, videosAlbum)
            if (!needOnlyImages) {
                readItems(isImages = false, allAlbums, mediaAlbum, imagesAlbum, videosAlbum)
            }
            allAlbums.forEach { entry ->
                entry.value.sort()
            }
            allAlbums.removeEmptyAlbums()
        }
    }

    private fun readItems(
        isImages: Boolean,
        allAlbums: MutableMap<Int, GalleryAlbumItem>,
        mediaAlbum: GalleryAlbumItem,
        imagesAlbum: GalleryAlbumItem,
        videosAlbum: GalleryAlbumItem
    ) {
        val cursor: Cursor? =
            if (isImages) {
                contentResolver.getQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projectionImage
                )
            } else {
                contentResolver.getQuery(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projectionVideo,
                )
            }
        cursor?.use {
            val mediaFields = MediaFields(it)
            while (cursor.moveToNext()) {
                val bucketId = mediaFields.bucketId ?: ALL_MEDIA_ALBUM_ID
                val bucketName =
                    if (bucketId == ALL_MEDIA_ALBUM_ID) {
                        mediaAlbum.name
                    } else {
                        mediaFields.bucketName ?: mediaAlbum.name
                    }
                val path: String = mediaFields.path ?: continue
                val item = with(mediaFields) {
                    if (isImages) {
                        GalleryItem(
                            id = itemId,
                            bucketId = bucketId,
                            dateTaken = dateTaken,
                            uri = "file://$path",
                            isVideo = false,
                            duration = null,
                            width = width,
                            height = height,
                            size = size
                        )
                    } else {
                        GalleryItem(
                            id = itemId,
                            bucketId = bucketId,
                            dateTaken = dateTaken,
                            uri = "file://$path",
                            isVideo = true,
                            duration = duration,
                            width = width,
                            height = height,
                            size = size
                        )
                    }
                }
                allAlbums.getOrPut(bucketId) {
                    // сюда не может прийти bucketId созданных заранее альбомов (allMedia и др.)
                    // надо обсудить
                    // не можем обойтись без ручного добавления в "свои" альбомы
                    GalleryAlbumItem(bucketId, bucketName)
                    // else if (bucketId != ALL_MEDIA_ALBUM_ID)
                }.addItem(item)
                if (isImages) {
                    imagesAlbum.addItem(item)
                } else {
                    videosAlbum.addItem(item)
                }
                mediaAlbum.addItem(item)
            }
        }
    }

    private fun ContentResolver.getQuery(
        uri: Uri,
        projection: Array<String>,
    ): Cursor? {
        val order: String =
            if (Build.VERSION.SDK_INT > 28) {
                MediaColumns.DATE_MODIFIED
            } else {
                MediaColumns.DATE_TAKEN
            } + " DESC"
        return query(uri, projection, null, null, order)
    }

    private val projectionImage = arrayOf(
        MediaColumns._ID,
        MediaColumns.BUCKET_ID,
        MediaColumns.BUCKET_DISPLAY_NAME,
        MediaColumns.DATA,
        if (Build.VERSION.SDK_INT > 28) MediaColumns.DATE_MODIFIED else MediaColumns.DATE_TAKEN,
        MediaColumns.ORIENTATION,
        MediaColumns.WIDTH,
        MediaColumns.HEIGHT,
        MediaColumns.SIZE
    )

    private val projectionVideo = arrayOf(
        MediaColumns._ID,
        MediaColumns.BUCKET_ID,
        MediaColumns.BUCKET_DISPLAY_NAME,
        MediaColumns.DATA,
        if (Build.VERSION.SDK_INT > 28) MediaColumns.DATE_MODIFIED else MediaColumns.DATE_TAKEN,
        MediaColumns.DURATION,
        MediaColumns.WIDTH,
        MediaColumns.HEIGHT,
        MediaColumns.SIZE
    )

    private fun getClipboardImageUri(): String? {
        val clipData = clipboardManager?.primaryClip
        return if (clipData != null &&
            clipData.itemCount == 1 &&
            clipData.description.hasMimeType(MIME_TYPE_ANY_IMAGE)
        ) {
            clipData.getItemAt(0).uri.toString()
        } else {
            null
        }
    }

    private fun MutableMap<Int, GalleryAlbumItem>.removeEmptyAlbums(): MutableMap<Int, GalleryAlbumItem> =
        apply {
            if (get(ALL_IMAGES_ALBUM_ID)?.items?.isEmpty() == true) {
                remove(ALL_IMAGES_ALBUM_ID)
            }
            if (get(ALL_VIDEOS_ALBUM_ID)?.items?.isEmpty() == true) {
                remove(ALL_VIDEOS_ALBUM_ID)
            }
        }
}