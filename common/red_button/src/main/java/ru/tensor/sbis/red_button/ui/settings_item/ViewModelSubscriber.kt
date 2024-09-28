package ru.tensor.sbis.red_button.ui.settings_item

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.red_button.data.RedButtonOpenAction
import ru.tensor.sbis.red_button.utils.RedButtonOpenHelper
import ru.tensor.sbis.settings_screen_decl.Delegate

/**
 * Вспомогатльный класс для подписки/отписки в течении ж.ц. экрана на события вью модели для показа диалогов и тостов.
 *
 * @author du.bykov
 */
internal class ViewModelSubscriber(
    private val viewModel: RedButtonItemViewModel,
    private val openHelper: RedButtonOpenHelper
) : LifecycleObserver {

    private var delegate: Delegate? = null
    private val disposable = CompositeDisposable()

    /**
     * Сеттер делегата, выполняющего действия.
     * Делегат устанавливается через сеттер, т.к. [ViewModelSubscriber] создаётся и живёт ссылкой
     * внутри [RedButtonItem], в момент создания ссылки на делегат нет.
     * @see [RedButtonItem]
     * @param delegate делегат выполняющий действия
     */
    fun bindDelegate(delegate: Delegate) {
        this.delegate = delegate
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        with(disposable) {
            add(viewModel.errors.filter { it.isNotEmpty() }.subscribe { errorText -> delegate?.showToast(errorText) })
            add(
                viewModel.openAction.subscribe { action ->
                    delegate?.runCustomActionWithContext { context ->
                        if (action == RedButtonOpenAction.OPEN_FRAGMENT) {
                            delegate?.showFragment { openHelper.getRedButtonHost() }
                        } else {
                            delegate?.showDialog(openHelper.getDialog(action, context.resources))
                        }
                    }
                }
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        disposable.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        delegate = null
        viewModel.clear()
    }
}