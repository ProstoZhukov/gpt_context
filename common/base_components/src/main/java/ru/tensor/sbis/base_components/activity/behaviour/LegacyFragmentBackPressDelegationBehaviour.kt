package ru.tensor.sbis.base_components.activity.behaviour

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour

/**
 * Реализация поведения с поддержкой устаревшей обработки [FragmentBackPress]
 *
 * @author kv.martyshenko
 */
class LegacyFragmentBackPressDelegationBehaviour(
    private val fragmentTag: String
) : ActivityBehaviour<AppCompatActivity> {

    override fun onCreate(activity: AppCompatActivity) {
        super.onCreate(activity)

        val backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentBackPress = activity.supportFragmentManager.findFragmentByTag(fragmentTag) as? FragmentBackPress
                val backPressEventIntercepted = fragmentBackPress?.onBackPressed() ?: false
                if (!backPressEventIntercepted) {
                    isEnabled = false
                    activity.onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }

        }
        activity.onBackPressedDispatcher.addCallback(activity, backPressCallback)
    }
}