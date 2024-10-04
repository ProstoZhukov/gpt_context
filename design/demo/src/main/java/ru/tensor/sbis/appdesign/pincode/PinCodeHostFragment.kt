package ru.tensor.sbis.appdesign.pincode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.FragmentPinCodeHostBinding
import ru.tensor.sbis.appdesign.extensions.showToast
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.Anchor
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.AnchorGravity
import ru.tensor.sbis.pin_code.decl.PinCodeFeature
import ru.tensor.sbis.pin_code.decl.PinCodeRepository
import ru.tensor.sbis.pin_code.decl.PinCodeUseCase
import ru.tensor.sbis.pin_code.decl.createLazyPinCodeFeature

class PinCodeHostFragment : Fragment() {

    private val pinCodeFeature: PinCodeFeature<String> by createLazyPinCodeFeature(this) { RepositoryImpl() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //слушаем результат выполнения от RepositoryImpl.onCodeEntered
        pinCodeFeature.onRequestCheckCodeResult.observe(viewLifecycleOwner, { result ->
            showToast(result.data)
            //получили конкретные данные, в данном примере это "123456789"
            //компонент был автоматически закрыт
        })

        pinCodeFeature.onCanceled.observe(viewLifecycleOwner, {
            //пользователь самостоятельно закрыл окно ввода пин-кода
        })

        //создаем фрагмент ввода пин-кода
        if (savedInstanceState == null) pinCodeFeature.show(
            this,
            PinCodeUseCase.ConfirmSignature(
                description = "Для подтверждения входа введите последние 4 цифры входящего звонка."
            ),
            popoverAnchor = Anchor.createTopWithOverlayAnchor(R.id.pin_code_tablet_test_view, AnchorGravity.START)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return FragmentPinCodeHostBinding.inflate(inflater).root
    }
}

class RepositoryImpl : PinCodeRepository<String> {
    override fun onCodeEntered(digits: String): String {
        //запрос в облако или какое-то локальное действие
        Thread.sleep(1000)
        //именно этот результат ожидаем в pinCodeFeature.onRequestCheckCodeResult.observe
        return "123456789"
    }

    override fun onRetry() {
        //запрос в облако
        throw Exception("Спец ошибочка")
    }

    override fun needCleanCode(error: Throwable) = true
}