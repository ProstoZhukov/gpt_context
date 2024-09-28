package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.di.CommonSingletonComponentProvider
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmRateBinding
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.di.DaggerRateComponent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.RateViewImpl
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.EmojiType
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.containerAs
import timber.log.Timber
import java.util.UUID

/**
 * Фрагмент оценки качества работы оператора.
 *
 * @author dv.baranov
 */
internal class RateFragment :
    BaseFragment(),
    AdjustResizeHelper.KeyboardEventListener,
    Content {

    /**
     * Реализация [ContentCreatorParcelable] для встраивания в dialog-контейнеры.
     *
     * @property messageUuid uuid сообщения, по которому была вызвана шторка оценки.
     * @property consultationRateType тип оценки (звезды/смайлы/пальцы).
     */
    @Parcelize
    class Creator(
        private val messageUuid: UUID,
        private val consultationRateType: ConsultationRateType,
        private val disableComment: Boolean
    ) : ContentCreatorParcelable {
        override fun createFragment(): Fragment = newInstance(messageUuid, consultationRateType, disableComment)
    }

    companion object {

        /**
         * Создать экземпляр фрагмента оценки качества работы оператора.
         *
         * @property messageUuid uuid сообщения, по которому была вызвана шторка оценки.
         * @property consultationRateType тип оценки (звезды/смайлы/пальцы).
         */
        fun newInstance(
            messageUuid: UUID,
            consultationRateType: ConsultationRateType,
            disableComment: Boolean
        ): Fragment =
            RateFragment().withArgs {
                putString(CRM_RATE_MESSAGE_UUID_KEY, messageUuid.toString())
                putParcelable(CRM_RATE_TYPE_KEY, consultationRateType)
                putBoolean(CRM_RATE_DISABLE_COMMENT_KEY, disableComment)
            }
    }

    private val fragmentLayout: Int
        get() = R.layout.communicator_crm_rate

    private var view: RateViewImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerRateComponent.factory().create(
            CommonSingletonComponentProvider.get(requireContext()),
        ) {
            val consultationRateType: ConsultationRateType? = arguments?.getParcelable(CRM_RATE_TYPE_KEY)
            val disableComment = arguments?.getBoolean(CRM_RATE_DISABLE_COMMENT_KEY) ?: false
            doIf(consultationRateType == null) { Timber.e("RateFragment - rateType is null") }
            RateViewImpl(
                CommunicatorCrmRateBinding.bind(requireView()),
                consultationRateType ?: EmojiType(),
                UUIDUtils.fromString(arguments?.getString(CRM_RATE_MESSAGE_UUID_KEY)) ?: UUIDUtils.NIL_UUID,
                disableComment
            ).also {
                view = it
            }
        }.also {
            it.injector().inject(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        view = null
        containerAs<Container.Closeable>()!!.closeContainer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflate(inflater, fragmentLayout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerAs<Container.Showable>()!!.showContent()
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean =
        if (!isTablet) {
            view?.onKeyboardOpenMeasure(keyboardHeight) ?: false
        } else {
            true
        }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean =
        if (!isTablet) {
            view?.onKeyboardCloseMeasure(keyboardHeight) ?: false
        } else {
            true
        }
}

private const val CRM_RATE_TYPE_KEY = "CRM_RATE_TYPE_KEY"
private const val CRM_RATE_MESSAGE_UUID_KEY = "CRM_RATE_MESSAGE_UUID_KEY"
private const val CRM_RATE_DISABLE_COMMENT_KEY = "CRM_RATE_DISABLE_COMMENT_KEY"
