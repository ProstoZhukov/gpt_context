package ru.tensor.sbis.common.feature

import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.res.AssetManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет общедоступные системные зависимости
 *
 * @author us.bessonov
 */
class AndroidSystem internal constructor(
    val contentResolver: ContentResolver,
    val assetManager: AssetManager,
    val sharedPreferences: SharedPreferences
) : Feature