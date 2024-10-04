package ru.tensor.sbis.design.buttons.round.model

import ru.tensor.sbis.design.theme.global_variables.IconSize

/**
 * Модель для отступов счётчика. В зависимости от размера круглой кнопки, отступы должны быть различными.
 * @param outerOffset максимальный внешниё отступ от границы кнопки (сверху, справа, слева), за который не должны
 *                      заходить границы счётчика.
 * @param counterIconDepth вертикальный отступ (вниз) между верхней границей иконки и нижней границей счётчика.
 * @param horizontalCenterOffset горизонтальный отступ (вглубь) от правого (левого) края иконки до центра счётчика
 *                                (при условии, что счётчик не заступает за границы описанные параметром [outerOffset]).
 *
 * @author ra.geraskin
 */
internal enum class CounterOffsetsModel(
    val outerOffset: Int = 4,
    val counterIconDepth: Int,
    val horizontalCenterOffset: Int
) {
    S(
        counterIconDepth = 6,
        horizontalCenterOffset = 0
    ),
    M(
        counterIconDepth = 8,
        horizontalCenterOffset = 2
    ),
    L(
        counterIconDepth = 12,
        horizontalCenterOffset = 4
    );

    companion object {

        /**
         * Определить модель отступов в зависимости от размера круглой кнопки.
         */
        fun getOffsetBySize(buttonSize: SbisRoundButtonSize): CounterOffsetsModel =
            when (buttonSize.iconSize.globalVar) {
                IconSize.X2S -> S
                IconSize.XS -> S
                IconSize.S -> S
                IconSize.ST -> S
                IconSize.M -> S
                IconSize.L -> S
                IconSize.XL -> S
                IconSize.X2L -> S
                IconSize.X3L -> M
                IconSize.X4L -> M
                IconSize.X5L -> M
                IconSize.X6L -> L
                IconSize.X7L -> L
            }
    }
}