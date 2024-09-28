package ru.tensor.sbis.swipeablelayout.swipeablevm

import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.util.SwipeHelper

/**
 * Класс, координирующий состояние списка элементов [SwipeableVm], допускающий не более одного
 * элемента с открытым меню и восстанавливающий состояния свайпа при обновлении списка
 *
 * @author us.bessonov
 */
@Deprecated("Больше не требуется. Будет удалён по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3")
class SwipeableVmHelper {

    @JvmOverloads
    @Deprecated("Больше не требуется", ReplaceWith("Можно явно задать stateChangeListener для SwipeableVm"))
    fun <VM_HOLDER_TYPE : SwipeableVmHolder> setupSwipeableViewModels(
        items: List<VM_HOLDER_TYPE>, postAction: ((Int) -> Unit)? = null
    ): List<VM_HOLDER_TYPE> {

        items.forEach {
            it.swipeableVm.stateChangeListener = object : SwipeableLayout.StateChangeListener {
                override fun onStateChanged(state: Int) {
                    postAction?.invoke(state)
                }
            }
        }

        return items
    }

    /**
     * Закрывает все свайп меню
     *
     * @param items вьюмодели, у которых меню должно быть закрыто
     * @param considerClosedUntilReset считать ли элементы с заданным uuid закрытыми, независимо от обновлений состояния,
     * до очередного вызова [setupSwipeableViewModels]
     * @param forceClose true, если требуется принудительно закрыть свайп-меню, иначе false
     */
    @JvmOverloads
    @Deprecated(
        message = "Используйте SwipeHelper",
        replaceWith = ReplaceWith("SwipeHelper.resetAll() or SwipeHelper.closeAll()")
    )
    fun <VM_HOLDER_TYPE : SwipeableVmHolder> closeAll(
        items: List<VM_HOLDER_TYPE>, considerClosedUntilReset: Boolean = false, forceClose: Boolean = false
    ) = if (forceClose) SwipeHelper.resetAll() else SwipeHelper.closeAll()
}