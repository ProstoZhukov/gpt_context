package ru.tensor.sbis.list.view.utils

import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * Расположение индикатора прогресса постраничной подгрузки.
 */
enum class ProgressItemPlace : ComparableItem<ProgressItemPlace> {

    /** Сверху списка. */
    TOP {
        override fun areTheSame(otherItem: ProgressItemPlace) = otherItem == TOP

        override fun hasTheSameContent(otherItem: ProgressItemPlace): Boolean =
            areTheSame(otherItem)
    },

    /** Снизу списка. */
    BOTTOM {
        override fun areTheSame(otherItem: ProgressItemPlace) = otherItem == BOTTOM

        override fun hasTheSameContent(otherItem: ProgressItemPlace): Boolean =
            areTheSame(otherItem)
    },

    /** Слева от списка. */
    LEFT {
        override fun areTheSame(otherItem: ProgressItemPlace) = otherItem == LEFT

        override fun hasTheSameContent(otherItem: ProgressItemPlace): Boolean =
            areTheSame(otherItem)
    },

    /** Справа от списка. */
    RIGHT {
        override fun areTheSame(otherItem: ProgressItemPlace) = otherItem == RIGHT

        override fun hasTheSameContent(otherItem: ProgressItemPlace): Boolean =
            areTheSame(otherItem)
    }
}