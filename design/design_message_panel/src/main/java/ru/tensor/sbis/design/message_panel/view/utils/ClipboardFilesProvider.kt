package ru.tensor.sbis.design.message_panel.view.utils

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.MimeTypeMap
import android.widget.EditText
import androidx.core.net.toUri
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import com.facebook.common.util.UriUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Поставщик файлов из системного буфера обмена, которые выбирает пользователь для вставки.
 *
 * @author vv.chekurda
 */
class ClipboardFilesProvider(private val context: Context) : InputConnectionCompat.OnCommitContentListener {

    private data class ClipboardFileData(
        val contentUri: Uri,
        val mime: String = MimeTypeMap.getFileExtensionFromUrl(contentUri.toString())
            ?: DEFAULT_CLIPBOARD_FILE_MIME
    )

    private var lastInputContent: InputContentInfoCompat? = null
    private val clipboardPasteFileData = PublishSubject.create<ClipboardFileData>()

    /**
     * Для подписки на Uri файлов, которые пользователь пытается вставить из буфера обмена.
     */
    val clipboardFileUri: Observable<String>
        get() = clipboardPasteFileData.share()
            .flatMap(::prepareClipboardFileUri)

    /**
     * Создать входящее соединение.
     *
     * Метод необходимо проксировать из [EditText.onCreateInputConnection],
     * если необходимы файлы из списка системного буфера.
     * Выбранные файлы попадут в [clipboardFileUri].
     */
    fun createInputConnection(
        superConnection: InputConnection,
        outAttrs: EditorInfo,
        mimes: Array<String> = defaultSupportedMimes
    ): InputConnection {
        try {
            EditorInfoCompat.setContentMimeTypes(outAttrs, mimes)
            return InputConnectionCompat.createWrapper(superConnection, outAttrs, this)
        } catch (ex: Throwable) {
            Timber.e(ex)
        }
        return superConnection
    }

    /**
     * Обработать выбранный пункт контекстного меню.
     *
     * Метод необходимо проксировать из [EditText.onTextContextMenuItem],
     * если необходимы файлы из пункта "Вставить".
     * Выбранные файлы попадут в [clipboardFileUri].
     *
     * При возвращении методом null - вызывать super реализацию, иначе отдавать результат выше.
     */
    fun onTextContextMenuItem(id: Int): Boolean? =
        if (id == android.R.id.paste) {
            getPrimaryClipboardFileData()?.let { fileData ->
                postClipboardPasteFileData(fileData)
                true
            }
        } else null

    /**
     * Проверить вставку контента из системного буфера обмена.
     *
     * Метод необходимо вызывать из [EditText.onTextChanged],
     * если необходимы файлы из списка системного буфера,
     * которые на samsung вставляются в [EditText] как обычный текст.
     * Вставленный файл в виде текста придет в [clipboardFileUri].
     *
     * Этот текст никак иначе не перехватить (спасибо им за это),
     * поэтому при возвращении true необходимо вызывать следующий код из [EditText]:
     * setText(text.removeRange(lengthBefore, lengthAfter)).
     */
    fun checkClipboardPastedContent(
        text: CharSequence,
        lengthBefore: Int,
        lengthAfter: Int
    ): Boolean {
        val isPastingBigText = lengthAfter > lengthBefore && lengthAfter - lengthBefore >= 15
        if (!isPastingBigText) return false

        val addedText = text.substring(lengthBefore, lengthAfter)
        return if (addedText.contains(FULL_CONTENT_SCHEME)) {
            postClipboardPasteFileData(ClipboardFileData(contentUri = addedText.toUri()))
            true
        } else {
            false
        }
    }

    override fun onCommitContent(
        inputContentInfo: InputContentInfoCompat,
        flags: Int,
        opts: Bundle?
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 &&
            (flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0
        ) {
            try {
                lastInputContent = inputContentInfo
                inputContentInfo.requestPermission()
            } catch (ex: Exception) {
                Timber.e(ex)
                return false
            }
        }
        val fileData = ClipboardFileData(
            contentUri = inputContentInfo.contentUri,
            mime = inputContentInfo.description.getMimeType(0)
        )
        postClipboardPasteFileData(fileData)
        return true
    }

    private fun getPrimaryClipboardFileData(): ClipboardFileData? {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip
        return if (clipData != null &&
            clipData.itemCount == 1 &&
            clipData.description.hasMimeType(MIME_TYPE_ANY_IMAGE)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                lastInputContent = InputContentInfoCompat(
                    clipData.getItemAt(0).uri,
                    clipData.description,
                    null
                ).also {
                    it.requestPermission()
                }
            }
            ClipboardFileData(
                contentUri = clipData.getItemAt(0).uri,
                mime = clipData.description.getMimeType(0)
            )
        } else {
            null
        }
    }

    private fun prepareClipboardFileUri(fileData: ClipboardFileData): Observable<String> =
        Observable.fromCallable {
            val storeDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val dateFormatter = SimpleDateFormat(DateFormatTemplate.ONLY_DIGITS.template, Locale.getDefault())
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(fileData.mime) ?: DEFAULT_CLIPBOARD_FILE_MIME
            val fileName = "$TMP_FILE_NAME${dateFormatter.format(Date())}.$ext"
            val clipboardFileCopy = File(storeDir, fileName)
            try {
                FileUriUtil.writeToFile(context, clipboardFileCopy, fileData.contentUri).let { isSuccess ->
                    if (isSuccess) {
                        Uri.fromFile(clipboardFileCopy).toString()
                    } else {
                        throw RuntimeException()
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                StringUtils.EMPTY
            }
        }.filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())

    private fun postClipboardPasteFileData(fileData: ClipboardFileData) {
        clipboardPasteFileData.onNext(fileData)
    }
}

const val MIME_TYPE_GIF = "image/gif"
const val MIME_TYPE_ANY_IMAGE = "image/*"
const val MIME_TYPE_JGP = "image/jpg"
const val MIME_TYPE_PNG = "image/png"
const val MIME_TYPE_WEBP = "image/webp"

val defaultSupportedMimes = arrayOf(
    MIME_TYPE_GIF,
    MIME_TYPE_ANY_IMAGE,
    MIME_TYPE_JGP,
    MIME_TYPE_PNG,
    MIME_TYPE_WEBP
)

private const val DEFAULT_CLIPBOARD_FILE_MIME = "jpg"
private const val FULL_CONTENT_SCHEME = "${UriUtil.LOCAL_CONTENT_SCHEME}://"
private const val TMP_FILE_NAME = "tmp"