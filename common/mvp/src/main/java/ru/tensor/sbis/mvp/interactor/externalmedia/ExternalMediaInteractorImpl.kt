package ru.tensor.sbis.mvp.interactor.externalmedia

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import io.reactivex.Observable
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import java.io.File
import java.util.TreeMap

/**
 * Реализация [ExternalMediaInteractor].
 * @property contentResolver Поставщик доступа к контенту.
 *
 * @author vv.malyhin
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ExternalMediaInteractorImpl(
    private var contentResolver: ContentResolver
) : BaseInteractor(), ExternalMediaInteractor {

    @SuppressLint("Recycle")
    override fun getExternalMedia(
        positionToStartFrom: Int,
        maxCount: Int,
        supportedTypes: Array<String>?
    ): Observable<PagedListResult<File>> =
        Observable.fromCallable {
            val filesByDate: TreeMap<Long, MutableList<File>> = TreeMap()
            val types: Set<String>? = supportedTypes?.map { it.substringAfter('/') }?.toSet()
            val categories = supportedTypes?.map { it.substringBefore('/') }?.toSet()
            MediaTypes
                .values()
                .filter { type -> categories == null || type.name.lowercase() in categories }
                .mapNotNull { type -> contentResolver.getMediaCursor(type.contentUri, maxCount) }
                .forEach { cursor -> filesByDate += cursor.readFiles(maxCount, types) }
            return@fromCallable PagedListResult(filesByDate.getLastFiles(maxCount), false)
        }
            .compose(getObservableBackgroundSchedulers())
}

private fun Cursor.readFiles(maxCount: Int, types: Set<String>?): TreeMap<Long, MutableList<File>> {
    var count = 0
    val filesByDate: TreeMap<Long, MutableList<File>> = TreeMap()
    while (moveToNext() && count < maxCount) {
        val absolutePath = getString(0) ?: continue
        val type = FileUtil.getFileExtension(absolutePath).substringAfter('.')
        if (types != null && type !in types) continue
        val file = File(absolutePath)
        if (file.exists()) {
            filesByDate
                .getOrPut(file.lastModified()) { mutableListOf() }
                .add(File(absolutePath))
            count++
        }
    }
    close()
    return filesByDate
}

private fun ContentResolver.getMediaCursor(uri: Uri, maxCount: Int): Cursor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // В Android 11 больше не поддерживается 'LIMIT', необходимо использовать ContentResolver.QUERY_ARG_LIMIT
        // Используемый метод доступен начиная с 26 API
        // https://developer.android.com/reference/kotlin/android/content/ContentResolver#query_2
        // p.s. ContentResolverCompat из 'core-1.5.0' не поддерживает использование 26 API
        val selectionArgs = Bundle().apply {
            putInt(ContentResolver.QUERY_ARG_LIMIT, maxCount)
            putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.MediaColumns.DATE_MODIFIED)
            )
            putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
        }
        query(
            uri,
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE),
            selectionArgs,
            null
        )
    } else {
        val sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC" + " LIMIT " + maxCount + " OFFSET " + 0
        query(
            uri,
            arrayOf(MediaStore.MediaColumns.DATA),
            null,
            null,
            sortOrder
        )
    }
}

private fun TreeMap<Long, out List<File>>.getLastFiles(maxCount: Int) = values.flatten().reversed().take(maxCount)

/**
 * Категории медиафайлов, которые предоставляет галерея устройства.
 * @property contentUri URI для обнаружения файлов конкретной категории.
 *
 * @author vv.malyhin
 */
private enum class MediaTypes(val contentUri: Uri) {
    IMAGE(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
    VIDEO(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
}