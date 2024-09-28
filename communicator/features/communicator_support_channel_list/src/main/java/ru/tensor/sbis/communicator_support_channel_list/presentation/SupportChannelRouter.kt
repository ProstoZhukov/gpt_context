package ru.tensor.sbis.communicator_support_channel_list.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCreationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator_support_channel_list.di.SupportChannelListPlugin.companyDetailsFragmentProvider
import ru.tensor.sbis.communicator_support_channel_list.di.SupportChannelListPlugin.containerFrameIdProvider
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import ru.tensor.sbis.design.R
import ru.tensor.sbis.viper.arch.router.FragmentTransactionCustomAnimations
import java.util.UUID

/**
 * Фабрика для SupportChannelRouter
 */
@AssistedFactory
internal interface SupportChannelRouterFactory {
    fun create(
        @Assisted fragmentManager: FragmentManager,
        @Assisted overlayFragmentHolder: OverlayFragmentHolder?,
        @Assisted containerId: Int,
        @Assisted isTab: Boolean,
        @Assisted config: SupportComponentConfig
    ): SupportChannelRouter
}

/**
 * Роутер для рестра консультаций
 * @param fragmentManager FragmentManager
 * @param containerId containerId для FragmentManager
 * @param crmConversationFeature Фабрика фрагмента чата CRM.
 * @param config Конфиг для отображения.
 */
internal class SupportChannelRouter @AssistedInject constructor(
    @Assisted private val fragmentManager: FragmentManager,
    @Assisted private val overlayFragmentHolder: OverlayFragmentHolder?,
    @Assisted private val containerId: Int,
    @Assisted private val isTab: Boolean,
    @Assisted private val config: SupportComponentConfig,
    private val crmConversationFeature: CRMConversationFragmentFactory,
) {

    /**
     * Список фрагментов, для которых требуется показать ActionButton
     */
    val destinationsWithCreateButton = listOf(CONSULTATIONS_FRAGMENT_TAG)

    private var supportFragmentManager: FragmentManager? = null

    fun initSupportFragmentManager(supportFragmentManager: FragmentManager) {
        this.supportFragmentManager = supportFragmentManager
    }

    /**
     * Открыть список консультаций
     */
    fun openConsultations(fragmentFactory: () -> Fragment?) {
        fragmentFactory.invoke()?.let {
            showChildFragment(it, CONSULTATIONS_FRAGMENT_TAG)
        }
    }

    /**
     * Создать консультацию
     */
    fun createConsultation(
        sourceId: UUID,
        needBackButton: Boolean = true,
        isSabyget: Boolean = false,
        isBrand: Boolean = false,
        hasAccordion: Boolean = false,
        useOverlay: Boolean = false
    ) {
        val fragment = crmConversationFeature.createCRMConversationFragment(
            CRMConsultationCreationParams(
                crmConsultationCase = config.toChatType(sourceId, isSabyget, isBrand),
                needBackButton = needBackButton,
                hasAccordion = hasAccordion,
                isSwipeBackEnabled = !isSabyget && !isBrand
            )
        )
        if (useOverlay && overlayFragmentHolder != null) {
            overlayFragmentHolder.setFragment(fragment, false)
        } else {
            showChildFragment(fragment, CONVERSATION_FRAGMENT_TAG)
        }
    }

    /**
     *  Открыть переписку по косультации
     */
    fun openConversation(
        id: UUID,
        needBackButton: Boolean = true,
        needOpenKeyboard: Boolean = false,
        isSabyget: Boolean = false,
        isBrand: Boolean = false,
        useOverlay: Boolean = false,
        hasAccordion: Boolean = false,
        isMultyChannel: Boolean = false
    ) {
        val fragment = crmConversationFeature.createCRMConversationFragment(
            CRMConsultationOpenParams(
                needBackButton = needBackButton,
                isSwipeBackEnabled = !isSabyget && !isBrand && isMultyChannel,
                hasAccordion = hasAccordion,
                crmConsultationCase = config.toChatType(id, isSabyget, isBrand),
                needOpenKeyboard = needOpenKeyboard,
            )
        )
        if (useOverlay && overlayFragmentHolder != null) {
            overlayFragmentHolder.setFragment(fragment, false)
        } else {
            showChildFragment(fragment, CONVERSATION_FRAGMENT_TAG)
        }
    }

    fun openCompanyDetails(companyId: UUID) {
        val fragment = companyDetailsFragmentProvider
            ?.get()?.getCompanyDetailsFragment(companyId) ?: return
        val containerId = containerFrameIdProvider?.get()?.containerFrameId ?: return
        val supportFragmentManager = this.supportFragmentManager ?: return
        val animations = FragmentTransactionCustomAnimations.getRightInRightOutAnimations()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(animations.enter, animations.exit, animations.popEnter, animations.popExit)
            .add(containerId, fragment, fragment::class.java.simpleName)
            .addToBackStack(fragment::class.java.simpleName)
            .commit()
    }

    /**
     * Показать фрагмент
     * @param fragment фрагмент
     * @param tag тег для addToBackStack
     */
    private fun showChildFragment(fragment: Fragment, tag: String) {
        val enterAnimationId = R.anim.right_in
        val exitAnimationId = R.anim.right_out

        with(fragmentManager.beginTransaction()) {
            if (!isTab)
                setCustomAnimations(enterAnimationId, R.anim.nothing, R.anim.nothing, exitAnimationId)
            add(containerId, fragment)
            addToBackStack(tag)
            commit()
        }
    }

    private fun SupportComponentConfig.toChatType(
        originUuid: UUID,
        isSabyget: Boolean,
        isBrand: Boolean
    ): CRMConsultationCase =
        if (this is SupportComponentConfig.SabyGet) {
            CRMConsultationCase.SalePoint(originUuid, isSabyget, isBrand)
        } else {
            CRMConsultationCase.Client(originUuid)
        }

    companion object {

        /**
         * Тег для фрагмента консультаций
         */
        private const val CONSULTATIONS_FRAGMENT_TAG = "CONSULTATIONS_FRAGMENT_TAG"

        /**
         * Тег для фрагмента переписки
         */
        private const val CONVERSATION_FRAGMENT_TAG = "CONVERSATION_FRAGMENT_TAG"
    }

}