package ru.tensor.sbis.entrypoint_guard.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

/**
 * Реализация [FragmentFactory], возвращающая базовый фрагмент [Fragment] на любой вызов.
 *
 * @author kv.martyshenko
 */
internal object StubFragmentFactory : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String
    ): Fragment {
        return Fragment()
    }
}