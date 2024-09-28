package ru.tensor.sbis.swipeablelayout

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.swipeablelayout.util.SwipeHelper
import java.io.Serializable


/**
 * Вспомогательный класс для управления состоянием свайпа элементов [RecyclerView], представленных
 * [SwipeableLayout]
 *
 * @author us.bessonov
 */
@Deprecated("Больше не требуется. Будет удалён по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3")
class SwipeableViewBinderHelper<ID_TYPE : Serializable> {

    /**
     * Вызывается в [RecyclerView.Adapter.onBindViewHolder] для восстановления состояния свайпа для
     * элемента с заданным [id] и отслеживания изменения состояния, чтобы число элементов с открытым
     * меню не превышало 1
     *
     * @param swipeLayout view с поддержкой свайпа
     * @param id id элемента, связанного с view
     */
    @Deprecated(
        "Больше не требуется", ReplaceWith("Вместо этого, указывайте SwipeableLayout.itemUuid")
    )
    fun bind(swipeLayout: SwipeableLayout, id: ID_TYPE?) {
        swipeLayout.itemUuid = id?.toString().orEmpty()
    }

    /**
     * Сохраняет состояния известных элементов в [Bundle]
     *
     * @param outState контейнер для сохранения состояний
     * @param clientClass класс (обычно адаптера), использующий инструмент, требуемый чтобы различать состояния,
     * сохранённые разными [SwipeableViewBinderHelper]'ами
     */
    @JvmOverloads
    @Deprecated("Не требуется", ReplaceWith("-"))
    fun saveStates(outState: Bundle?, clientClass: Class<*>? = null) = Unit

    /**
     * Восстанавливает значения состояний элементов из [Bundle]
     *
     * @param inState контейнер с сохранёнными ранее состояниями
     * @param clientClass класс, указанный в [saveStates]
     */
    @JvmOverloads
    @Deprecated("Не требуется", ReplaceWith("-"))
    fun restoreStates(inState: Bundle?, clientClass: Class<*>? = null) = Unit

    /**
     * Закрывает меню, либо возвращает на исходную позицию элемент с заданным [id]
     *
     * @param id        id элемента, подлежащего закрытию
     * @param animated  Нужно ли закрыть с анимацией
     */
    @JvmOverloads
    @Deprecated(message = "Используйте SwipeHelper", replaceWith = ReplaceWith("SwipeHelper.closeAll()"))
    fun close(id: ID_TYPE, animated: Boolean = true) = SwipeHelper.closeAll(animated)

    /**
     * Закрывает, либо возвращает на исходную позицию, все открытые или открывающиеся элемены
     *
     * @param animated Нужно ли закрыть с анимацией
     */
    @JvmOverloads
    @Deprecated(message = "Используйте SwipeHelper", replaceWith = ReplaceWith("SwipeHelper.closeAll()"))
    fun closeAllOpenMenus(animated: Boolean = true) = SwipeHelper.closeAll(animated)

    /**
     * Закрывает, либо возвращает на исходную позицию, все элементы
     *
     * @param animated Нужно ли закрыть с анимацией
     * @param withDismissedWithTimeout Нужно ли закрыть предварительно удалённые элементы
     */
    @JvmOverloads
    @Deprecated(message = "Используйте SwipeHelper", replaceWith = ReplaceWith("SwipeHelper.resetAll()"))
    fun closeAll(animated: Boolean = true, withDismissedWithTimeout: Boolean = false) =
        SwipeHelper.resetAll(animated, withDismissedWithTimeout)

    /**
     * Удаляет сохраненное состояние элемента
     *
     * @param id идентификатор элемента, сохраненное состояние которого нужно сбросить
     */
    @Deprecated("Не требуется", ReplaceWith("-"))
    fun removeEntry(id: ID_TYPE) = Unit

    /**
     * Выполняет очитку ссылок на вьюхи, следует вызывать при отсоединении фрагмента
     */
    @Deprecated("Не требуется", ReplaceWith("-"))
    fun clearEntries() = Unit
}