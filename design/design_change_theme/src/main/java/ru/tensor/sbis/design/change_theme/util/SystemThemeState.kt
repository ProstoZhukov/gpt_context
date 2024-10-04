package ru.tensor.sbis.design.change_theme.util

import android.content.res.Configuration

/**
 * Состояние системной темы.
 *
 * @author ra.geraskin
 */
enum class SystemThemeState {

    /**
     * Установлена дневная тема. [Configuration.UI_MODE_NIGHT_NO]
     */
    DAY,

    /**
     * Установлена ночная тема.[Configuration.UI_MODE_NIGHT_YES]
     */
    NIGHT,

    /**
     * Устройством не поддерживается системная темизация.
     */
    NOT_SUPPORTED
}