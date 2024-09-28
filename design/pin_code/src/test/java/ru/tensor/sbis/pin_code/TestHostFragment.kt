package ru.tensor.sbis.pin_code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import ru.tensor.sbis.pin_code.decl.PinCodeConfiguration
import ru.tensor.sbis.pin_code.decl.PinCodeFeature
import ru.tensor.sbis.pin_code.decl.PinCodeRepository
import ru.tensor.sbis.pin_code.decl.PinCodeUseCase
import ru.tensor.sbis.pin_code.decl.createLazyPinCodeFeature
import ru.tensor.sbis.design.R as RDesign

/**
 * Тестовый хост фрагмент компонента ввода пин-кода.
 *
 * @author as.stafeev
 */
class TestHostFragment : Fragment() {

    lateinit var eventCatcher: TestEventCatcher

    private val pinCodeFeature: PinCodeFeature<String> by createLazyPinCodeFeature(this) {
        object : PinCodeRepository<String> {
            override fun onCodeEntered(digits: String) = eventCatcher.onCodeEntered()

            override fun onRetry() = eventCatcher.onRetry()

            override fun needCleanCode(error: Throwable) = eventCatcher.onNeedCleanCode()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return View(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pinCodeFeature.onRequestCheckCodeResult.observe(
            viewLifecycleOwner
        ) { result ->
            eventCatcher.onSuccess(result.data)
        }

        pinCodeFeature.onCanceled.observe(
            viewLifecycleOwner
        ) {
            eventCatcher.onCanceled()
        }
    }

    fun showPinCode(fragment: Fragment, useCase: PinCodeUseCase) {
        showPinCode(fragment, useCase.configuration)
    }

    fun showPinCode(fragment: Fragment, configuration: PinCodeConfiguration) {
        PinCodeFragment.create(fragment.requireActivity(), fragment.childFragmentManager, configuration)
    }
}

fun findPinCodeView(fragment: Fragment): View {
    fragment.childFragmentManager.executePendingTransactions()
    val pinCodeFragment = fragment.childFragmentManager.fragments.first()
        .childFragmentManager.fragments.first() as PinCodeFragment
    return pinCodeFragment.requireView()
}

/**
 * Запуск тестового фрагмента в контейнере с указанием темы.
 * Указываем тему чтобы избежать InflateException из-за наличия элементов из пакета com.google.android.material.
 */
fun launchTestFragmentFragmentInContainer() =
    launchFragmentInContainer(themeResId = RDesign.style.AppTheme) { TestHostFragment() }