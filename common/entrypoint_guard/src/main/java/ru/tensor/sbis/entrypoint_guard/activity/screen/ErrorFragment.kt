package ru.tensor.sbis.entrypoint_guard.activity.screen

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.entrypoint_guard.R
import timber.log.Timber
import kotlin.system.exitProcess

/**
 * Реализация фрагмента, отображающего финальное состояние ошибки при инициализации приложения.
 *
 * @author kv.martyshenko
 */
internal class ErrorFragment : Fragment(R.layout.fragment_error) {
    private val errorMessage by lazy {
        val default = resources.getString(R.string.entrypoint_guard_unknown_error)
        requireArguments().getString(ARG_ERROR_MESSAGE, default) ?: default
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stubContent = createResourceImageStubContent()
        with(view) {
            findViewById<StubView>(R.id.entrypoint_guard_stub)?.setContent(stubContent)
            findViewById<View>(R.id.entrypoint_guard_close)?.setOnClickListener {
                Timber.e("Произошла ошибка инициализации контроллера '$errorMessage', нажата кнопка убийства процесса.")
                exitProcess(0)
            }
        }
    }

    private fun createResourceImageStubContent(): StubViewContent =
        ImageStubContent(
            imageType = StubViewImageType.ERROR,
            message = errorMessage,
            details = null
        )

    companion object {
        private const val ARG_ERROR_MESSAGE = "ERROR_MESSAGE"

        /**
         * Метод для создания экрана, отображающего финальное состояние ошибки при инициализации приложения.
         *
         * @param error информация об ошибке.
         */
        fun newInstance(error: String): Fragment {
            return ErrorFragment().apply {
                arguments = bundleOf(ARG_ERROR_MESSAGE to error)
            }
        }
    }
}