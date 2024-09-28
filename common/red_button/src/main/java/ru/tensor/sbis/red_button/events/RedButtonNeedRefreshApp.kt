package ru.tensor.sbis.red_button.events

import ru.tensor.sbis.red_button.data.RedButtonStubType

/**
 * Событие сигнализирующее о том, что требуется перезапуск приложения.
 * Будет выброшено, когда от контроллера придёт сигнал о том, что требуется отобразить заглушку.
 * @see [RedButtonReRunAppInteractor.subscribeOnAppReRun]
 * @property stubType тип заглушки, которую требуется отобразить
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonNeedRefreshApp(val stubType: RedButtonStubType)