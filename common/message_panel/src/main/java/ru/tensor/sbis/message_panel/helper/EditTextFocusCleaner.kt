package ru.tensor.sbis.message_panel.helper

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.ClosedByAdjustHelper
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.ClosedByRequest
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEvent
import timber.log.Timber

/**
 * Класс для очистки фокуса поля ввода, в зависимости от состояния клавиатуры
 *
 * @author us.bessonov
 */
internal class EditTextFocusCleaner(
    private val observerOn: Scheduler = TensorSchedulers.androidUiScheduler
) {

    private var ignoreAdjustHelperEvents: Boolean = false

    /**
     * Должно ли игнорироваться событие [ClosedByAdjustHelper] при определении необходимости очистки фокуса
     */
    fun setIgnoreAdjustHelperEvents(shouldIgnoreAdjustHelperEvents: Boolean) {
        ignoreAdjustHelperEvents = shouldIgnoreAdjustHelperEvents
    }

    /**
     * Отслеживает изменения состояния клавиатуры и выполняет очистку фокуса [editText] по установленным правилам
     */
    @CheckResult
    fun subscribeOnFocusClearing(editText: EditText,
        keyboardState: Observable<KeyboardEvent>
    ): Disposable {
        // при опускании клавиатуры теряем фокус
        return keyboardState
            .filter {
                it is ClosedByRequest ||
                        it is ClosedByAdjustHelper && !ignoreAdjustHelperEvents && editText.isFocusedButNotActiveInput()
            }
            .observeOn(observerOn)
            .subscribe({ editText.clearFocus() }, Timber::e)
    }

    private fun EditText.isFocusedButNotActiveInput(): Boolean {
        return hasFocus() && !(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).isActive(this)
    }
}