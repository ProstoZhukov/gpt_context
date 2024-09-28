package ru.tensor.sbis.communication_decl.communicator

import androidx.fragment.app.Fragment

/**
 * Интерфейс Master-Detail фрагмента
 * для делегирования выполнения транзакций с текущим details контейнером.
 *
 * @author vv.chekurda
 */
interface CommunicatorMasterDetailFragment {

    /**
     * Отобразить фрагмент в details контейнере
     * @param fragment - фрагмент, который требуется открыть
     */
    fun showDetailFragment(fragment: Fragment)

    /**
     * Удалить текущий details фрагмент
     */
    fun removeDetailFragment()

    /**
     * Текст заглушки в Detail фрагменте. Если поле == null, тогда используется строка по умолчанию.
     */
    var detailsStubViewText: String?

}