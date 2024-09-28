package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.di.CommonSingletonComponentProvider
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.di.DaggerQuickReplyComponent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.di.QuickReplyComponent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyView
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams

/**
 * Фрагмент списка быстрых ответов.
 *
 * @author dv.baranov
 */
internal class QuickReplyFragment :
    BaseFragment() {

    private val fragmentLayout: Int
        get() = R.layout.communicator_crm_quick_reply

    private val quickReplyParams: QuickReplyParams
        get() = arguments?.getSerializable(QUICK_REPLY_PARAMS_KEY) as QuickReplyParams

    private var quickReplyView: QuickReplyView? = null
    private var component: QuickReplyComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = DaggerQuickReplyComponent.factory().create(
            CommonSingletonComponentProvider.get(requireContext()),
            this,
            quickReplyParams,
            this.lifecycleScope,
        ).also {
            it.injector().inject(this, it.viewFactory)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setScrollListener(null)
        quickReplyView = null
        component = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflate(inflater, fragmentLayout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quickReplyView = component?.viewFactory?.let { it(view) }?.castTo()
    }

    /**
     * Сделать поисковый запрос по быстрым ответам.
     */
    internal fun setSearchQuery(query: String) {
        quickReplyView?.setSearchQuery(query)
    }

    /**
     * Обработать изменение высоты панели, содержащей фрагмент быстрых ответов.
     */
    internal fun handleHeightChanges(heightEqualZero: Boolean) {
        quickReplyView?.handleHeightChanges(heightEqualZero)
    }

    /**
     * Установить обработчик скролла списка быстрых ответов.
     */
    internal fun setScrollListener(listener: RecyclerView.OnScrollListener?) {
        quickReplyView?.setScrollListener(listener)
    }

    companion object {

        private const val QUICK_REPLY_PARAMS_KEY = "QUICK_REPLY_PARAMS_KEY"

        /**
         * Создать экземпляр фрагмента шторки быстрых ответов.
         */
        fun newInstance(
            quickReplyParams: QuickReplyParams,
        ): Fragment =
            QuickReplyFragment().withArgs {
                putSerializable(QUICK_REPLY_PARAMS_KEY, quickReplyParams)
            }
    }
}
