package ru.tensor.sbis.swipeablelayout;

import androidx.annotation.NonNull;

/**
 * Интерфейс сущности, содержащей {@link SwipeableLayout}.
 * Реализация остаётся на Java для обеспечения совместимости
 *
 * @author us.bessonov
 */
public interface SwipeableHolderInterface {

    /**
     * SelfDocumented
     */
    @NonNull
    SwipeableLayout getSwipeableLayout();

}
