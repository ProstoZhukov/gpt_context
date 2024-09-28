package ru.tensor.sbis.swipeablelayout.util.swipestate

import androidx.recyclerview.widget.RecyclerView

/**
 * Идентификатор элемента списка со свайп-меню, используемый для восстановления состояния.
 * Набор идентификаторов позволяет обеспечить сохранение и восстановление состояния, вне зависимости от того,
 * предоставляет ли прикладной код идентификатор элементов, и представлен ли контейнер как [RecyclerView].
 *
 * @author us.bessonov
 */
internal sealed interface SwipeItemId

/** @SelfDocumented */
internal data class Uuid(val uuid: String) : SwipeItemId

/** @SelfDocumented */
internal data class AdapterPosition(val position: Int) : SwipeItemId

/** @SelfDocumented */
internal data class PositionInParent(val position: Int) : SwipeItemId

/** @SelfDocumented */
internal object NoId : SwipeItemId