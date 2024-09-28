package ru.tensor.sbis.onboarding.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import ru.tensor.sbis.mvvm.ViewModelFactory
import ru.tensor.sbis.mvvm.withFactory
import ru.tensor.sbis.onboarding.BR
import javax.inject.Inject

/**
 * @author as.chadov
 */
internal abstract class OnboardingBaseFragment<
        VM : BaseViewModel,
        BINDING : ViewDataBinding,
        > :
    Fragment() {

    protected var binding: BINDING? = null

    @Inject
    internal lateinit var factory: ViewModelFactory<VM>

    @Suppress("LeakingThis")
    internal val viewModel: VM by lazy { withFactory(factory, vmClass) }

    protected abstract val vmClass: Class<VM>

    @get:LayoutRes
    protected abstract val layoutId: Int

    @StyleRes
    protected open val themeId: Int = ID_NULL

    init {
        retainInstance = true
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(applyStyle(inflater), layoutId, container, false)
        if (!binding!!.setVariable(BR.viewModel, viewModel)) {
            throw RuntimeException("Layout XML resource should contain data variable with name=\"viewModel\"")
        }
        return binding!!.root
    }

    private fun applyStyle(inflater: LayoutInflater): LayoutInflater {
        return if (themeId != ID_NULL) {
            val themeWrapper = ContextThemeWrapper(activity, themeId)
            inflater.cloneInContext(themeWrapper)
        } else inflater
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}