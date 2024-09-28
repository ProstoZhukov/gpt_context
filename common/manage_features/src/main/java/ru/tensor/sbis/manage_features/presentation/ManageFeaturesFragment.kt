package ru.tensor.sbis.manage_features.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.di.withInjection
import ru.tensor.sbis.manage_features.data.di.ManageFeaturesComponent
import ru.tensor.sbis.manage_features.databinding.ManageFeaturesFragmentBinding
import ru.tensor.sbis.manage_features.presentation.di.DaggerManageFeaturesFragmentComponent
import javax.inject.Inject

/**
 * Основной фрагмент модуля
 */
internal class ManageFeaturesFragment : BaseFragment() {

    private val disposable = SerialDisposable()
    private val injector = withInjection {
        DaggerManageFeaturesFragmentComponent.factory()
            .create(this, ManageFeaturesComponent.get(requireActivity()))
            .inject(this)
    }

    /** @SelfDocumented */
    @Inject
    lateinit var viewModel: ManageFeaturesViewModel

    /** @SelfDocumented */
    override fun onAttach(context: Context) {
        injector.inject()
        super.onAttach(context)
    }

    /**
     * Требует у системы сохранять фрагмент при откреплении от активности и подписывается на ошибки излучаемые вью моделью
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        disposable.set(viewModel.errors.subscribe { showToast(it) })
    }

    /**
     * Создание макета и биндинг вью модели вместе со слушателем
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        ManageFeaturesFragmentBinding.inflate(inflater).also {
            it.toolbar.rightIcon1.setOnClickListener(viewModel.checkClickListener)
            it.viewModel = viewModel
        }.root

    /**
     * Очистка ресурсов
     */
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}