package ru.tensor.sbis.entrypoint_guard

import android.content.Context

/**
 * Компонент, трансформирующий оригинальный [Context] к особенностям конкретного приложения.
 *
 * @author kv.martyshenko
 */
fun interface BaseContextPatcher : (Context?) -> Context?