package ru.tensor.sbis.design.link_share.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonHandler
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.link_share.LinkSharePlugin
import ru.tensor.sbis.design.link_share.databinding.LinkShareFragmentBinding
import ru.tensor.sbis.design.link_share.di.view.DaggerLinkShareViewComponent
import ru.tensor.sbis.design.link_share.di.view.LinkShareControllerInjector
import ru.tensor.sbis.design.link_share.di.view.LinkShareViewComponent
import ru.tensor.sbis.design.link_share.presentation.adapter.LinkShareAdapter
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams
import javax.inject.Inject

/**
 * Фрагмент экрана "поделиться ссылкой"
 *
 * @author ad.moskovchuk
 */
internal class LinkShareFragment : BaseFragment(), ConfirmationButtonHandler {

    companion object {
        private const val LINK_PARAMS = "LINK_PARAMS"

        /**@SelfDocumented*/
        @JvmStatic
        internal fun newInstance(params: SbisLinkShareParams) =
            LinkShareFragment().withArgs { putParcelable(LINK_PARAMS, params) }
    }

    /**@SelfDocumented*/
    @Inject
    lateinit var controllerInjector: LinkShareControllerInjector

    /**@SelfDocumented*/
    @Inject
    lateinit var shareAdapter: LinkShareAdapter

    private val params by lazy { requireArguments().getParcelableUniversally<SbisLinkShareParams>(LINK_PARAMS)!! }

    private var _binding: LinkShareFragmentBinding? = null
    private val binding: LinkShareFragmentBinding
        get() = _binding!!

    private val component: LinkShareViewComponent by lazy {
        DaggerLinkShareViewComponent.factory().create(
            LinkSharePlugin.singletonComponent,
            this,
            params
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        params.let {
            controllerInjector.inject(this@LinkShareFragment, it) {
                LinkShareView(binding, shareAdapter, params) { activity?.supportFragmentManager }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LinkShareFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onButtonClick(tag: String?, id: String, sbisContainer: SbisContainerImpl) {
        if (id == ConfirmationButtonId.OK.toString()) {
            sbisContainer.dismiss()
        }
    }
}