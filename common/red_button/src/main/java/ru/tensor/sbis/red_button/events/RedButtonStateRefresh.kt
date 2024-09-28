package ru.tensor.sbis.red_button.events

import ru.tensor.sbis.red_button.data.RedButtonState

/**
 * Событие обновление состояния [RedButtonPreference]
 * @property redButtonState новое состояние красной кнопки
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonStateRefresh(val redButtonState: RedButtonState)