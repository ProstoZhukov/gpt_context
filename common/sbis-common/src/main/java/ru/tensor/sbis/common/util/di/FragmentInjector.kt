package ru.tensor.sbis.common.util.di

import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment

/**
 * Класс для осуществления инъекции зависимостей во фрагмент, учитываю ж.ц.
 * Инъекции должны осуществляется в методе onAttach фрагмента до вызова super.onAttach, это позволяет использовать AAC
 * для инъекции ViewModel классов, которые могут быть извлечени из хранилища, только после этого события.
 * Сам метод, которым может происходит инъекция, может быть переопределен для целей тестирования.
 * @property fragment Fragment объект инъекции
 * @property _injection Function0<Unit> как будет происходить инъекция
 * @constructor
 */
class FragmentInjector(val fragment: Fragment, injection: () -> Unit) {

    private var _injection: (() -> Unit)? = injection

    fun inject() {
        _injection?.invoke()
        _injection = null
    }

    @VisibleForTesting
    fun setInjection(injection: () -> Unit) {
        _injection = injection
    }
}

/**
 * Лаконичное использование класса
 * @receiver Fragment объект инъекции
 * @param injection () -> Unit как будет происходить инъекция
 * @return FragmentInjector создаваемый инжектор
 */
fun Fragment.withInjection(injection: () -> Unit): FragmentInjector {
    @Suppress("UNCHECKED_CAST")
    return FragmentInjector(this, injection)
}