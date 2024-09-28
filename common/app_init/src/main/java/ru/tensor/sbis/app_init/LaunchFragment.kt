package ru.tensor.sbis.app_init

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.controller_utils.loading.StartupExposer
import ru.tensor.sbis.design_splash.SplashLoadingIndicator
import ru.tensor.sbis.mvvm.argument

import ru.tensor.sbis.design.R as RDesign

/**
 * Фрагмент с крутилкой и статусами инициализации платформы.
 * Показывается в момент асинхронной инициализации.
 *
 * Запрещается использовать тут контроллер и плагинную систему.
 *
 * @author ar.leschev
 */
class LaunchFragment : Fragment(R.layout.fragment_launch) {

    private val forceMessage by argument(ARG_FORCE_MESSAGE, "")
    private val withUiStatus by argument(ARG_UI_STATUS_ON, true)
    private val forceBackground by argument(ARG_FORCE_BACKGROUND, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainContainer = view.findViewById<FrameLayout>(R.id.app_init_splash)
        val splashLoadingIndicator = view.findViewById<SplashLoadingIndicator>(R.id.app_init_splash_view)
        splashLoadingIndicator.isVisible = withUiStatus

        if (forceBackground) {
            mainContainer.background = ResourcesCompat.getDrawable(resources, RDesign.drawable.design_splash, requireContext().theme)
        }

        if (forceMessage.isEmpty()) {
            splashLoadingIndicator.setStatusFlow(StartupExposer.event)
        } else {
            splashLoadingIndicator.setStatusText(forceMessage)
        }
    }

    companion object {
        private const val ARG_FORCE_MESSAGE = "ARG_FORCE_MESSAGE"
        private const val ARG_UI_STATUS_ON = "ARG_UI_STATUS_ON"
        private const val ARG_FORCE_BACKGROUND = "ARG_FORCE_BACKGROUND"

        /** Создать экземпляр фрагмента с предустановленным сообщением [forceMessage]. */
        fun newInstance(forceMessage: String = "", withUiStatus: Boolean = true, forceBackground: Boolean = false) =
            LaunchFragment().withArgs {
                putString(ARG_FORCE_MESSAGE, forceMessage)
                putBoolean(ARG_UI_STATUS_ON, withUiStatus)
                putBoolean(ARG_FORCE_BACKGROUND, forceBackground)
            }
    }
}