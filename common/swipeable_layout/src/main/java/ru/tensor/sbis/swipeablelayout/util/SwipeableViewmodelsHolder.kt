package ru.tensor.sbis.swipeablelayout.util

import ru.tensor.sbis.swipeablelayout.swipeablevm.MutableSwipeableVmHolder
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm

/**
 * Класс, предназначенный для сохранения прежних экземпляров [SwipeableVm] у элементов, реализующих
 * [MutableSwipeableVmHolder], при обновлении списка.
 * При этом, экземпляры [SwipeableVm] должны быть взаимозаменяемыми для одного и того же uuid.
 * Необходимо использовать в связке со [SwipeableVmHelper], если не гарантируется, что все вьюмодели, для которых
 * выполнится [SwipeableVmHelper.setupSwipeableViewModels], будут обновлены в databinding.
 *
 * @author us.bessonov
 */
class SwipeableViewmodelsHolder {

    private val latestSwipeableVms = mutableMapOf<String, SwipeableVm>()

    /**
     * Сохраняет [SwipeableVm] элементов [MutableSwipeableVmHolder] списка, либо восстанавливает [SwipeableVm] по uuid,
     * если они присутствовали в списке при вызове ранее
     */
    fun useRetainedSwipeableViewModels(list: List<MutableSwipeableVmHolder>) {
        list.forEach { item ->
            item.swipeableVm = latestSwipeableVms.getOrElse(item.swipeableVm.uuid) {
                item.swipeableVm.also { latestSwipeableVms[it.uuid] = it }
            }
        }
    }
}