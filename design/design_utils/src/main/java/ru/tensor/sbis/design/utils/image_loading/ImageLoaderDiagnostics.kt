package ru.tensor.sbis.design.utils.image_loading

import android.annotation.SuppressLint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date

private var ENABLE_IMAGE_LOADER_DIAGNOSTICS = false

private const val MAX_ENTRIES = 100

/**
 * Предназначен для упрощения диагностики проблем при загрузке изображений.
 * Данные публикуются только при активном флаге [ENABLE_IMAGE_LOADER_DIAGNOSTICS].
 *
 * @author us.bessonov
 */
object ImageLoaderDiagnostics {

    private val entries = Collections.synchronizedMap(LinkedHashMap<Int, MutableList<String>>())

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("HH:mm:ss.SSS")

    /**
     * Логгирует диагностическое сообщение.
     *
     * @param id идентификатор набора сущностей, обеспечивающих работу конкретной view изображения
     */
    fun log(id: Int? = null, message: String) {
        if (!ENABLE_IMAGE_LOADER_DIAGNOSTICS) return
        Timber.d("#$id: $message")
        synchronized(this) {
            val list = entries[id ?: return]
                ?: mutableListOf<String>()
                    .also {
                        if (entries.size >= MAX_ENTRIES) entries.remove(entries.keys.first())
                        entries[id] = it
                    }
            list.add("${sdf.format(Date())} | #$id: $message")
        }
    }

    /**
     * Логгирует ошибку, сопровождая диагностической информацией по view изображения
     */
    fun error(id: Int?, message: String) = Timber.e("Image #$id: $message\n${getDiagnosticDataById(id)}")

    private fun getDiagnosticDataById(id: Int?) = entries[id ?: -1]?.joinToString("\n").orEmpty()
}