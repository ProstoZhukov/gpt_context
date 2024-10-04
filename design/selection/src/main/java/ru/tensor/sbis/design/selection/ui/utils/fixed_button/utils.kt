/**
 * Инструменты для обеспечения функциональности "Фиксированных кнопок"
 *
 * @author ma.kolpakov
 */

package ru.tensor.sbis.design.selection.ui.utils.fixed_button

import android.view.ViewStub
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.design.selection.BR
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel

/**
 * Функция инициализации и вставки "Фиксированной кнопки"
 */
internal fun ViewStub.inflateFixedButton(
    type: FixedButtonType,
    vm: FixedButtonViewModel<*>,
    lifecycleOwner: LifecycleOwner
) {
    val binding: ViewDataBinding = findViewById<ViewStub>(R.id.fixedButton)
        .apply { layoutResource = type.buttonLayout }
        .inflate()
        .run(DataBindingUtil::bind)!!
    binding.setVariable(BR.vm, vm)
    binding.lifecycleOwner = lifecycleOwner
}
