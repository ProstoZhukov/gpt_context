package ru.tensor.sbis.hallscheme.v2

import ru.tensor.sbis.hallscheme.v2.business.model.HallSchemeModel
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi

/**
 * Контрактный интерфейс, определяющий взаимодействие View и Presenter.
 * @author aa.gulevskiy
 */
internal interface HallSchemeV2Contract {
    /**@SelfDocumented*/
    interface View {
        /**
         * Отображает элементы схемы зала.
         */
        fun drawItems(items: List<HallSchemeItemUi>)

        /**
         * Масштабирует схему зала.
         */
        fun applyScale(scale: Float)

        /**
         * Инициализирует вью.
         */
        fun initHallSchemeView()

        /**
         * Задаёт размер вью для отображения элеменов схемы зала.
         */
        fun setItemsLayoutSize(top: Int, left: Int, bottom: Int, right: Int)

        /**
         * Очищает схему зала (удаляет все дочерние вью).
         */
        fun clearScheme()

        // Background section
        /**
         * Устанавливает пустой фон.
         */
        fun showEmptyBackground()

        /**
         * Устанавливает фон и задаёт ему масштаб по границам элементов.
         */
        fun showBackgroundIfTablesPinned(
            isRemoteUrl: Boolean,
            url: String,
            left: Int,
            top: Int,
            translate: Int,
            backgroundZoom: Float
        )

        /**
         * Устанавливает фон плиткой.
         */
        fun showRepeatedBackground(isRemoteUrl: Boolean, url: String)

        /**
         * Устанавливает растянутый фон.
         */
        fun showStretchedBackground(isRemoteUrl: Boolean, url: String)

        /**
         * Устанавливает фон сверху по центру.
         */
        fun showCenterTopBackground(isRemoteUrl: Boolean, url: String)

        /**
         * Устанавливает фон слева сверху.
         */
        fun showLeftTopBackground(isRemoteUrl: Boolean, url: String)

        /**
         * Устанавливает фон справа сверху.
         */
        fun showRightTopBackground(isRemoteUrl: Boolean, url: String)

        /**
         * Устанавливает границы фона.
         */
        fun setBackgroundViewSize(imageWidth: Int, imageHeight: Int)

        /**
         * Устанавливает границы фона по размерам родительского вью.
         */
        fun setBackgroundViewSizeMatchParent()
    }

    /**@SelfDocumented*/
    interface Presenter {
        /**
         * Устанавливает модель схемы зала.
         */
        fun setHallSchemeModel(hallSchemeModel: HallSchemeModel)

        /**
         * Устанавливает масштаб схемы зала.
         */
        fun setScale(scale: Float)

        /**
         * Рисует элементы схемы зала и сбрасывает масштаб.
         */
        fun drawItemsAndResetScale()

        /**
         * Уведомляет об успешной загрузке фонового изображения.
         */
        fun imageLoadingSuccess(width: Int?, height: Int?)

        /**
         * Уведомляет о неуспешной загрузке фонового изображения.
         */
        fun imageLoadingFailure()

        /**
         * Перерисовывает элементы схемы зала.
         */
        fun redrawItems(changedItems: List<HallSchemeItemUi>)

        /**
         * Полностью перерисовывает схему при условии, что она первоначально была отрисована методом show().
         */
        fun relayout()
    }
}