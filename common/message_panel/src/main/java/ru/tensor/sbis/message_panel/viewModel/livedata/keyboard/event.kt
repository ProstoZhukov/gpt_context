package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import androidx.annotation.Px

/**
 * События, которые влияют на состояний клавиатуры
 *
 * @author vv.chekurda
 * @since 11/1/2019
 */
sealed class KeyboardEvent

/**
 * Автоматический подъём клавиатуры по запросу от поля ввода
 */
object OpenedByFocus : KeyboardEvent()
/**
 * Автоматическое опускание клавиатуры по запросу от поля ввода
 */
object ClosedByFocus : KeyboardEvent()

/**
 * Запрос подъёма клавиатуры извне. Клавиатура ещё не поднята
 */
object OpenedByRequest : KeyboardEvent()
/**
 * Запрос опускания клавиатуры извне. Клавиатура ещё не опущена
 */
object ClosedByRequest : KeyboardEvent()

/**
 * Подъём клавиатуры извне. Клавиатура уже поднята
 */
data class OpenedByAdjustHelper(@Px val height: Int) : KeyboardEvent()
/**
 * Опускание клавиатуры извне. Клавиатура уже опущена
 */
data class ClosedByAdjustHelper(@Px val height: Int) : KeyboardEvent()