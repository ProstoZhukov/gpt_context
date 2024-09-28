package ru.tensor.retail.compositions_decl.actionlistener.extensions

import androidx.fragment.app.Fragment
import ru.tensor.retail.compositions_decl.actionlistener.BaseActionListenerTemp
import ru.tensor.retail.compositions_decl.actionlistener.ProviderActionListenerTemp

/**
 * Функция осуществляет проверку реализации объектом [Fragment] интерфейса [ProviderActionListenerTemp]
 * и если это так, то происходит установка слушателя.
 *
 * @author da.pavlov1
 * */
@Deprecated(
    "Временное решение, нужно подумать https://online.sbis.ru/opendoc.html?guid=ca17819c-7c99-48e1-8cb3-d35e8a8adb5b&client=3"
)
fun Fragment.setActionListenerIfHisProvidedTemp(listener: BaseActionListenerTemp) =
    tryCast<ProviderActionListenerTemp<BaseActionListenerTemp>> { setActionListener(listener) }

private inline fun <reified T : ProviderActionListenerTemp<*>> Fragment.tryCast(crossinline block: T.() -> Unit) {
    if (this is T) block(this) else Unit
}