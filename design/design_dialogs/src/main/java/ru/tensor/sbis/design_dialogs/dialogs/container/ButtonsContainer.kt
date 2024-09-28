package ru.tensor.sbis.design_dialogs.dialogs.container

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton

/**
 * Интерфейс контейнера, содержащего кнопки плавающей панели.
 * Для добавления кнопок в ваш контейнер, переопределите функцию [addButton].
 * Требуется, если кнопки находятся на разных уровнях, но встроиться должны в единую панель
 *
 * @author ia.nikitin
 */
interface ButtonsContainer {

    /** Добавить кнопку в контейнер */
    fun addButton(button: Button)

    /**
     * Удалить кнопку из контейнера
     *
     * @param buttonId Идентификатор кнопки [Button.Params.id]
     */
    fun removeButton(buttonId: String)

    /**
     * Связать контейнер с прокручиваемой view. Требуется для определения функциональности скрытия кнопок при прокрутке
     * При реализации данной функции есть возможность выбора:
     * 1) Установить [CoordinatorLayout.Behavior] на плавающую панель, [view] в этом случае не понадобится
     * 2) Использовать [view] для отслеживания событий прокрутки и реализовать логику скрытия кнопок вручную
     */
    fun bindScrollingView(view: ScrollingView)

    /**
     * Отвязать контейнер от прокручиваемой view.
     * Если [view] была сохранена в свойство класса, требуется его обнулить
     */
    fun unbindScrollingView(view: ScrollingView)

    /**
     * Переключить отображение панели кнопок.
     *
     * @param isVisible Видимость панели
     */
    @Deprecated(
        "Небходимо избавиться после выполнения " +
            "https://dev.saby.ru/opendoc.html?guid=c82c27b3-b149-42c6-9fe8-53e2d0323f11&client=3"
    )
    fun changeButtonsPanelVisibility(isVisible: Boolean)

    /**
     * Добавляемая кнопка
     *
     * @property params Параметры кнопки
     * @property value Кнопка
     */
    data class Button(val params: Params, val value: AbstractSbisButton<*, *>) {

        /**
         * Параметры кнопки
         *
         * @property id     Идентификатор, который служит для определения кнопки и ее изменения по требованию
         * @property order  Порядок кнопки, служит для определения места расположения кнопки в панели.
         *                  Перед добавлением следует отсортировать все пришедшие кнопки по этому параметру.
         *                  Чтобы разместить свою кнопку в зависимости от пришедших, нужно знать их order и
         *                  неявно завязывать ордера разных кнопок между собой.
         *                  Например, чтобы расположить кнопку между "Посмотреть весь" и кнопкой оглавления Sabydoc,
         *                  нужно задать order между значениями order этих кнопок см. [AttachmentButtonParams]
         */
        data class Params(val id: String, val order: Int)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Button
            if (params.id != other.params.id) return false
            return true
        }

        override fun hashCode() = params.id.hashCode()
    }

    sealed interface ScrollingView {

        class Nested(val value: NestedScrollView) : ScrollingView

        class Recycler(val value: RecyclerView) : ScrollingView
    }
}
