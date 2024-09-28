package ru.tensor.sbis.communicator.crm.conversation.router

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.getParentAs
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmAnotherOperatorFragmentFactoryProvider
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmChannelsFragmentFactoryProvider
import ru.tensor.sbis.communicator.crm.conversation.contract.CRMConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.RateFragment
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.RateContainerFactory
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentFragment
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentFragment.Companion.CRM_ANOTHER_OPERATOR_PARAMS
import ru.tensor.sbis.communicator.declaration.crm.CRMHostRouter
import ru.tensor.sbis.communicator.declaration.crm.contract.CRMConversationContract
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.consultations.generated.ConsultationChannel
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import java.util.UUID
import javax.inject.Inject

/**
 * Интерфейс роутера экрана CRM.
 *
 * @author da.zhukov
 */
internal interface CRMConversationRouter {
    /**
     * Показать просмотрщик с вложением.
     * @param args аргументы с информацией о вложениях для запуска компонента просмотрщика.
     */
    fun showViewerSlider(args: ViewerSliderArgs)

    /**
     * Открыть новую консультацию.
     */
    fun openNewConsultation(params: CRMConsultationParams)

    /**
     * Открыть следующую консультацию.
     */
    fun openNextConsultation(params: CRMConsultationParams)

    /**
     * Открыть экран выбора группы для переназначения чата.
     */
    fun openReassignToGroupScreen(conversationUuid: UUID)

    /**
     * Показать снизу компонент ввода комментария для переназначения чата на оператора.
     */
    fun openReassignCommentToOperator(conversationUuid: UUID, operatorId: UUID?, consultationChannel: ConsultationChannel)

    /**
     * Показать снизу компонент для переназначения чата на оператора.
     */
    fun openReassignToOperator(params: Bundle)

    /**
     * Открыть экран выбора клиента, с которым нужно начать чат.
     */
    fun openWriteToClientScreen(originId: UUID, consultationAuthorId: UUID)

    /**
     * Инициализация роутера и его зависимостей.
     * @param fragment фрагмент, в котором будет осуществляться навигация.
     */
    fun init(fragment: Fragment)

    /**
     * Отсоединение роутера, в этом месте произойдет очистка зависимостей и ссылок.
     */
    fun detachRouter()

    /**
     * Открыть экран оценки качества работы оператора.
     */
    fun openRateScreen(
        messageUuid: UUID,
        consultationRateType: ConsultationRateType,
        disableComment: Boolean
    )

    /**
     * Открыть карточку компании в sabyget/brand.
     */
    fun openCompanyDetails(companyId: UUID)

    /**
     * Открыть консультируемого чатах оператора.
     */
    fun openPersonCard(personId: UUID)
}

/**
 * Реализация роутера экрана CRM.
 */
internal class CRMConversationRouterImpl @Inject constructor(
    private val context: Context,
    private val dependency: CRMConversationDependency,
) : CRMConversationRouter {

    private var crmHostRouter: CRMHostRouter? = null
    private var crmConversationFragment: Fragment? = null

    override fun showViewerSlider(args: ViewerSliderArgs) {
        val viewerIntent = dependency.createViewerSliderIntent(context, args)
        context.startActivity(viewerIntent)
    }

    override fun openNewConsultation(params: CRMConsultationParams) {
        val crmConversationContract: CRMConversationContract? =
            crmConversationFragment?.getParentAs<CRMConversationContract>()
                ?: crmConversationFragment?.activity?.supportFragmentManager?.fragments?.find {
                    it is CRMConversationContract
                } as? CRMConversationContract
                ?: findCRMShowNewConversationContract()

        crmConversationContract?.showNewConversation(params)
    }

    /**
     * Поиск обработчика создания новой консультации в обычных каналах.
     */
    private fun findCRMShowNewConversationContract(): CRMConversationContract? {
        val activityFragments = crmConversationFragment?.activity?.supportFragmentManager?.fragments
        val result = activityFragments?.find {
            it is CRMConversationContract
        } as? CRMConversationContract ?: activityFragments?.map {
            it.childFragmentManager.fragments.find { fragment ->
                fragment is CRMConversationContract
            }
        }?.first() as? CRMConversationContract

        return result
    }

    override fun openNextConsultation(params: CRMConsultationParams) {
        crmHostRouter?.openNextConsultation(params)
    }

    override fun openReassignToGroupScreen(conversationUuid: UUID) {
        crmChannelsFragmentFactoryProvider?.let {
            val content = it.get().createCrmChannelListFragment(CrmChannelListCase.CrmChannelReassignCase(conversationUuid))
            crmHostRouter?.openContentScreen(content, CHANNELS_FRAGMENT_TAG)
        }
    }

    override fun  openReassignCommentToOperator(
        conversationUuid: UUID,
        operatorId: UUID?,
        consultationChannel: ConsultationChannel
    ) {
        crmConversationFragment
        CrmReassignCommentFragment.newInstance(
            CRMAnotherOperatorParams(
                consultationId = conversationUuid,
                operatorId = operatorId,
                channelId = consultationChannel.id,
                channelName = consultationChannel.name
            ),
            crmConversationFragment!!.requireContext().getThemeColorInt(R.attr.unaccentedAdaptiveBackgroundColor)
        ).show(crmConversationFragment!!.childFragmentManager, OPERATOR_REASSIGN_COMMENT_FRAGMENT_TAG)
    }

    override fun openReassignToOperator(params: Bundle) {
        val operatorParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            params.getParcelable(CRM_ANOTHER_OPERATOR_PARAMS, CRMAnotherOperatorParams::class.java)
        } else {
            params.getParcelable(CRM_ANOTHER_OPERATOR_PARAMS) as? CRMAnotherOperatorParams
        } ?: return

        crmAnotherOperatorFragmentFactoryProvider?.let {
            val content = it.get().createCRMAnotherOperatorFragment(operatorParams)
            crmHostRouter?.openContentScreen(content, OPERATOR_REASSIGN_FRAGMENT_TAG)
        }
    }

    override fun openPersonCard(personId: UUID) {
        val context = crmConversationFragment?.requireContext() ?: return
        val intent = CRMConversationPlugin.personCardProvider?.get()?.createPersonCardIntent(context, personId)
        intent?.let { crmHostRouter?.openContentScreenInActivity(it)}
    }

    override fun openWriteToClientScreen(originId: UUID, consultationAuthorId: UUID) {
        crmChannelsFragmentFactoryProvider?.let {
            val content = it.get().createCrmChannelListFragment(CrmChannelListCase.CrmChannelConsultationCase(originId, consultationAuthorId))
            crmHostRouter?.openContentScreen(content, CHANNELS_FRAGMENT_TAG)
        }
    }

    override fun init(fragment: Fragment) {
        crmConversationFragment = fragment
        dependency.crmHostRouterFeatureProvider?.getCRMHostRouter()?.let {
            crmHostRouter = it
            crmHostRouter!!.initRouter(fragment)
        }
    }

    override fun detachRouter() {
        crmConversationFragment = null
        crmHostRouter = null
    }

    override fun openRateScreen(
        messageUuid: UUID,
        consultationRateType: ConsultationRateType,
        disableComment: Boolean
    ) {
        val rateFragmentCreator = RateFragment.Creator(messageUuid, consultationRateType, disableComment)
        val rateFragment = RateContainerFactory.createContainer(context, rateFragmentCreator)
        crmConversationFragment?.childFragmentManager?.let {
            rateFragment.show(it, RATE_FRAGMENT_TAG)
        }
    }

    override fun openCompanyDetails(companyId: UUID) {
        val crmConversationContract: CRMConversationContract? =
            crmConversationFragment?.getParentAs<CRMConversationContract>()
                ?: crmConversationFragment?.activity?.supportFragmentManager?.fragments?.find {
                    it is CRMConversationContract
                } as? CRMConversationContract
                ?: findCRMShowNewConversationContract()
        crmConversationContract?.openSalePointDetailCard(companyId)
    }
}

private const val RATE_FRAGMENT_TAG = "RATE_FRAGMENT_TAG"
private const val CHANNELS_FRAGMENT_TAG = "CHANNELS_FRAGMENT_TAG"
private const val OPERATOR_REASSIGN_FRAGMENT_TAG = "OPERATOR_REASSIGN_FRAGMENT_TAG"
private const val OPERATOR_REASSIGN_COMMENT_FRAGMENT_TAG = "OPERATOR_REASSIGN_COMMENT_FRAGMENT_TAG"
