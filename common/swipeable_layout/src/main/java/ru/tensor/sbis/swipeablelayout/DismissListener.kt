package ru.tensor.sbis.swipeablelayout

/**
 * Слушатель события завершения смахивания элемента
 *
 * @author us.bessonov
 */
@Deprecated(
    "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
    ReplaceWith("SwipeableLayout.addEventListener()")
)
fun interface DismissListener {
    /**
     * Вызывается по завершении смахивания, если не предусмотрена отмена удаления, иначе вызывается по истечении
     * таймаута, если удаление к тому времени не будет отменено
     */
    fun onDismissed(uuid: String?)
}