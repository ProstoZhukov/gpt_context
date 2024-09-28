package ru.tensor.sbis.communicator.sbis_conversation.preview

import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * Класс `ConversationPreviewOnTouchListener` реализует интерфейс `View.OnTouchListener`
 * и используется для обработки событий касания на представлениях (views) с целью различения
 * обычного клика (с минимальным перемещением пальца).
 *
 * Логика работы:
 * - При касании (`MotionEvent.ACTION_DOWN`) сохраняет начальные координаты касания (X и Y).
 * - При отпускании пальца (`MotionEvent.ACTION_UP`) проверяет, насколько сильно сдвинулись координаты.
 *   Если смещение по обеим осям (X и Y) меньше порогового значения (`clickThreshold`),
 *   событие трактуется как обычный клик, и вызывается переданная в конструктор функция `actionOnClick`.
 *
 * Конструктор:
 * - `actionOnClick`: Функция, которая вызывается при распознавании обычного клика.
 *
 * Поля:
 * - `downX`: Хранит координату X начального касания.
 * - `downY`: Хранит координату Y начального касания.
 * - `clickThreshold`: Пороговое значение, определяющее максимальное смещение по осям X и Y,
 *   при котором событие считается кликом (в пикселях). Значение по умолчанию — 10.
 *
 * Методы:
 * - `onTouch(v: View, event: MotionEvent): Boolean`: Метод, реализующий обработку событий касания.
 *   Возвращает `false`, чтобы позволить передать дальнейшие события другим обработчикам.
 *
 * Примечание:
 * - Если пользователь совершает касание с небольшим перемещением пальца (меньше `clickThreshold`),
 *   оно будет считаться кликом, и вызывается функция `actionOnClick`.
 * - Если перемещение пальца превышает пороговое значение, касание не будет считаться кликом.
 *
 * Пример использования:
 * ```
 * val view = findViewById<View>(R.id.view)
 * view.setOnTouchListener(ConversationPreviewOnTouchListener {
 *     // Действие при клике
 *     Toast.makeText(context, "Клик!", Toast.LENGTH_SHORT).show()
 * })
 * ```
 * @author da.zhukov
 */
internal class ConversationPreviewOnTouchListener(val actionOnClick: () -> Unit) : View.OnTouchListener {
    private var downX = 0f
    private var downY = 0f
    private val clickThreshold = 10  // Максимальное допустимое перемещение для клика

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Сохраняем координаты нажатия
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_UP -> {
                // Сравниваем координаты нажатия и отпускания
                val upX = event.x
                val upY = event.y
                if (abs(upX - downX) < clickThreshold && abs(upY - downY) < clickThreshold) {
                    // Это обычный клик, если перемещение было меньше порога
                    actionOnClick()
                }
            }
            else -> Unit
        }
        return false
    }
}