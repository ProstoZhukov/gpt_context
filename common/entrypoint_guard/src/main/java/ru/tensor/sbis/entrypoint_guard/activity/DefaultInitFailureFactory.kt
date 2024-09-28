package ru.tensor.sbis.entrypoint_guard.activity

import androidx.fragment.app.Fragment
import ru.tensor.sbis.entrypoint_guard.activity.screen.ErrorFragment

/**
 * Дефолтная реализация [ActivityAssistant.InitFailureFactory].
 *
 * @author kv.martyshenko
 */
object DefaultInitFailureFactory : ActivityAssistant.InitFailureFactory {
    override fun createFragment(error: String): Fragment {
        return ErrorFragment.newInstance(error)
    }
}