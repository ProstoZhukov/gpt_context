package ru.tensor.sbis.loyalty_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Эвент от экрана "Программы лояльности". */
sealed interface LoyaltyProgramsFragmentEvent : Parcelable {

    /** Пользователь нажал кнопку "Закрыть". */
    @Parcelize
    object CloseBtnClicked : LoyaltyProgramsFragmentEvent

    /**
     * В процессе работы экрана была пересчитана продажа.
     * Сама продажа в эвенте не передаётся, т.к на текущий момент модель продажи слишком тяжела
     * для представления в Parcelable. Продажа шлётся эвентом по шине.
     * */
    @Parcelize
    object SaleUpdated : LoyaltyProgramsFragmentEvent

    /** Пользователь нажал на применённую продажу. */
    @Parcelize
    class AssignedLoyaltyProgramClicked(val loyaltyProgram: LoyaltyProgramBaseInfo) : LoyaltyProgramsFragmentEvent

    /** В процессе работы экрана произошла не критическая ошибка. */
    @Parcelize
    class NonCriticalError(val errorText: String) : LoyaltyProgramsFragmentEvent

    /** В процессе работы экрана произошла критическая ошибка. */
    @Parcelize
    class CriticalError(val errorText: String) : LoyaltyProgramsFragmentEvent
}
