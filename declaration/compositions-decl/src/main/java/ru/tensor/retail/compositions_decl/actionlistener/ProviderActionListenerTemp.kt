package ru.tensor.retail.compositions_decl.actionlistener

/**
 * Базовый интерфейс для реализации его прикладными модулями с целью
 * определения своего Api и возможности установки данного слушателя.
 *
 * @param T - интерфейс ActionListener'a, содержащий набор Api,
 *            которые поддерживает прикладной модуль.
 *
 * @author da.pavlov1
 * */
@Deprecated(
    "Временное решение, нужно подумать https://online.sbis.ru/opendoc.html?guid=ca17819c-7c99-48e1-8cb3-d35e8a8adb5b&client=3"
)
interface ProviderActionListenerTemp<T : BaseActionListenerTemp> {
    fun setActionListener(listener: T)
}