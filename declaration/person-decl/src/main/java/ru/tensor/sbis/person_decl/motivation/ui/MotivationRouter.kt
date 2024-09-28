package ru.tensor.sbis.person_decl.motivation.ui

import java.util.Date

/**
 * Интерфейс для роутера хост-фрагмента мотивации
 */
interface MotivationRouter {

    /**
     * Показ первого экрана модуля Мотивация.
     *
     * @param needShowPrePaymentBanner - нужно ли показывать баннер ближайшей выплаты
     * и досрочного аванса.
     */
    fun showMainScreen(needShowPrePaymentBanner: Boolean)

    /**
     * Открывает экран Выплаты.
     */
    fun showPayoutsDetails()

    /**
     * Открывает экран Стимулы.
     */
    fun showStimulusScreen()

    /**
     * Открывает экран Расшифровка месяца/года.
     *
     * @param periodFrom - [Date] дата начала периода
     * @param periodTo - [Date] дата конца периода
     */
    fun showSalaryDetailScreen(periodFrom: Date, periodTo: Date)

    /**
     * Обработка нажатия "назад"
     * @return true - если обработали
     */
    fun onBackPressed(): Boolean

    /**
     * @SelfDocumented
     */
    interface HasMotivationRouter {
        val motivationRouter: MotivationRouter
    }
}
