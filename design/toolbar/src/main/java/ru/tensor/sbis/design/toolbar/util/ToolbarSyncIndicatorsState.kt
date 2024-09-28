package ru.tensor.sbis.design.toolbar.util

import ru.tensor.sbis.design.toolbar.Toolbar

/**
 * State индикаторов синхронизации.
 *
 * @see Toolbar.changeSyncIndicatorsState
 *
 * @author ps.smirnyh
 */
enum class ToolbarSyncIndicatorsState {

    /** Синхронизация не запущена, индикаторы скрыты. */
    NOT_RUNNING,

    /** Запущена синхронизация, отображается индикатор загрузки. */
    RUNNING,

    /** Нет интернета, отображается иконка отсутствия сети. */
    NO_INTERNET
}