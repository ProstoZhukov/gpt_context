package ru.tensor.sbis.calendar_decl.clients

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Провайдер главного экрана журнала записи */
interface BookingJournalHostFragmentProvider: Feature {

    /** Получить фрагмент главного экрана журнала записи */
    fun createBookingJournalHostFragment(): Fragment
}