package ru.tensor.sbis.plugin_struct.utils

import android.content.Context
import android.content.MutableContextWrapper

/**
 * Маркерный класс, которым помечается темизированный глобальными переменными контекст.
 *
 * @param базовый контекст данного враппера.
 *
 * @author da.zolotarev
 */
class SbisThemedContext(base: Context) : MutableContextWrapper(base)