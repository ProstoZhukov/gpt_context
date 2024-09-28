package ru.tensor.sbis.design.view.input.mask.ip.api

import android.util.AttributeSet
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.mask.ip.IpInputKeyListener
import ru.tensor.sbis.design.view.input.mask.ip.IpInputViewFilter
import ru.tensor.sbis.design.view.input.mask.ip.IpInputViewTextWatcher
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi
import ru.tensor.sbis.design.view.input.utils.addFirstListenerBeforeSecondListener

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода с ip-адресом.
 * Поддерживается только ipv4 адрес без порта. Автоматически выставляются точки при наборе трех подряд идущих
 * цифр (октетов), автоматически ограничивается длина адреса, если точка поставлена вручную (длина октета меньше 3)
 * и др. - запрещает вписывать неправильные ip-адреса
 *
 * @author ia.nikitin
 */
internal class IpInputViewController(
    private val singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : SingleLineInputViewControllerApi by singleLineInputViewController {

    private val ipTextWatcher: IpInputViewTextWatcher by lazy(LazyThreadSafetyMode.NONE) {
        IpInputViewTextWatcher(baseInputView)
    }

    override fun attach(baseInputView: BaseInputView, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        singleLineInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)

        actualKeyListener = IpInputKeyListener
        inputView.filters = arrayOf(IpInputViewFilter())
        inputView.addFirstListenerBeforeSecondListener(ipTextWatcher, valueChangedWatcher)
    }
}