package ru.tensor.sbis.mvp.fragment.selection

import ru.tensor.sbis.mvp.presenter.BasePresenter

/**
 * Контракт для работы с окном выбора
 * Created by aa.mironychev on 10.05.2018.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface SelectionWindowContract {

    @Deprecated("Устаревший подход, переходим на mvi_extension")
    interface View {

        /**
         * Показать индикатор прогресса.
         */
        fun showProgress()

        /**
         * Показать окно выбора.
         */
        fun showAppearAnimation()

        /**
         * Закрыть окно выбора.
         */
        fun closeWindow()
    }

    @Deprecated("Устаревший подход, переходим на mvi_extension")
    interface Presenter<V : View> : BasePresenter<V> {

        /**
         * Обработать нажатие на кнопку "Закрыть".
         */
        fun onCloseClick()

        /**
         * Обработать нажатие на кнопку "Назад"
         *
         * @return true, если событие обработано, иначе false
         */
        @Deprecated("Unused for new bottom sheet impl")
        fun onBackPressed(): Boolean

        @Deprecated("Unused for new bottom sheet impl")
        fun onAppearAnimationStarted() {
        }

        @Deprecated("Unused for new bottom sheet impl")
        fun onAppearAnimationCompleted() {
        }
    }

    /**
     * Обработчик нажатия кнопки "Применить".
     * [Presenter] должен реализовывать этот интерфейс, если предполагается работа с плавающей
     * кнопкой в окне фильтра
     */
    @Deprecated("Устаревший подход, переходим на mvi_extension")
    interface OnApplyClickListener {

        /**
         * Обработать нажатие на кнопку "Применить"
         */
        fun onApplyClick()
    }

}