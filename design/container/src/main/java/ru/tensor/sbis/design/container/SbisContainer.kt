package ru.tensor.sbis.design.container

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.design.container.DimType.CUTOUT
import ru.tensor.sbis.design.container.DimType.NONE
import ru.tensor.sbis.design.container.DimType.SHADOW
import ru.tensor.sbis.design.container.DimType.SOLID
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.VerticalLocator

/**
 * Класс представляющий контейнер для отображения контента.
 * @author ma.kolpakov
 */
interface SbisContainer {
    /**
     * Тип затенения под контейнером
     */
    var dimType: DimType

    /**
     * Флаг необходимости анимировать появления и исчезновения контейнера
     */
    var isAnimated: Boolean

    /**
     * Будет ли происходить закрытие при нажатии на область вне контейнера
     */
    var isCloseOnTouchOutside: Boolean

    /**
     * Возможно ли закрытие контейнера, например по нажатию на кнопку назад
     */
    var isDialogCancelable: Boolean

    /**
     * Необходимость передавать тачи в родительское активити
     */
    var isTranslateTouchToParent: Boolean

    /**
     * Метод отображающий контейнер по заданным правилам, горизонтальное и вертикальное выравнивание задается независимо
     * @param fragmentManager - необходим так как контейнер представляет собой фрагмент
     * @param horizontalLocator - правило горизонтального выравнивания
     * @param verticalLocator - правило вертикального выравнивания
     * @param isSync - небходим ли синхронный показ (см. [DialogFragment.showNow])
     */
    fun show(
        fragmentManager: FragmentManager,
        horizontalLocator: HorizontalLocator,
        verticalLocator: VerticalLocator,
        isSync: Boolean = false
    )

    /**
     * Получить вьюмодель контейнера для взаимодействия прикладного кода с ним.
     *
     * На данный момент может приводить к вылету при вызове до показа
     * или после скрытия контейнера.
     */
    fun getViewModel(): ContainerViewModel

    /**
     * Дает возможность установить листенер закрытия в любой момент
     * существования контейнера, в отличие от подписки на
     * getViewModel().onDismissContainer.
     *
     * В каких случаях сработает листенер - см. [ContainerViewModel.onDismissContainer].
     *
     * Установка листенера через этот метод никак не влияет на подписки
     * на [ContainerViewModel.onDismissContainer], как и они не влияют
     * на этот листенер. Чтобы очистить ранее установленный через этот
     * метод листенер, надо передать нулевой [listener].
     */
    fun setOnDismissListener(listener: (() -> Unit)?)
}

/**
 * Тип затенения
 * @see NONE - без затенения
 * @see SOLID - сплошное затенение
 * @see CUTOUT - затенение с вырезом для вызывающего элемента работает только для AnchorLocator
 * @see SHADOW - без затенения всего экрана, отображаетс только локальная тень от контейнера
 */
enum class DimType {
    NONE,
    SOLID,
    CUTOUT,
    SHADOW
}
