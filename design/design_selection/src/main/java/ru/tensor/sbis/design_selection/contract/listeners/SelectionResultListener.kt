package ru.tensor.sbis.design_selection.contract.listeners

import androidx.fragment.app.FragmentActivity
import io.reactivex.internal.disposables.DisposableContainer
import java.io.Serializable

/**
 * Слушатель результата компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionResultListener<in ITEM, in ACTIVITY : FragmentActivity> : Serializable {

    /**
     * Обработать успешное подтверждение выбора.
     *
     * @param activity активити, на которой открыт фрагмент компонента выбора.
     * @param result результат выбора.
     * @param requestKey ключ запроса, с которым открывался компонент выбора.
     * @param disposable контейнер вью-модели, куда можно сложить подписки по асинхронным обработкам результата.
     */
    fun onComplete(
        activity: ACTIVITY,
        result: SelectionComponentResult<ITEM>,
        requestKey: String,
        disposable: DisposableContainer
    )

    /**
     * Обработать закрытие компонента выбора.
     * Вызывается при явной инициативе пользователя покинуть компонент выбора путем
     * нажатия на стрелку назад или на системную кнопку назад в корневой папке.
     *
     * @param activity активити, на которой открыт фрагмент компонента выбора.
     * @param requestKey ключ запроса, с которым открывался компонент выбора.
     */
    fun onCancel(activity: ACTIVITY, requestKey: String)

    /**
     * Результат компонента выбора.
     */
    data class SelectionComponentResult<out ITEM>(
        val items: List<ITEM>,
        val appended: Boolean = false
    )
}