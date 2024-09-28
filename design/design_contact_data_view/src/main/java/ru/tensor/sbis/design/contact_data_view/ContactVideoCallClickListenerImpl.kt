package ru.tensor.sbis.design.contact_data_view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.communication_decl.videocall.ui.CallActivityProvider
import java.util.UUID

/**
 * Реализация обработчика клика по контакту.
 * Совершает видео/sip звонок через модуль видеозвонков и сервис видеосвязи если установлена соответвующий зависимость
 */
class ContactVideoCallClickListenerImpl(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val callActivityProvider: CallActivityProvider
) : ClickElementListener {

    constructor(
        fragment: Fragment,
        callActivityProvider: CallActivityProvider
    ) : this(
        fragment.requireContext(),
        fragment.childFragmentManager,
        callActivityProvider
    )

    override fun invoke(phoneNumber: String, id: UUID?) {
        callActivityProvider.performSipOrPhoneCall(
            context,
            fragmentManager,
            id?.toString(),
            isSIPTelephonyCanBeUsed = true,
            phoneNumber,
            isCompany = false,
            calledName = null,
            calledPhotoUrl = null
        )
    }
}