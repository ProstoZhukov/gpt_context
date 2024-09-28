package ru.tensor.sbis.modalwindows.bottomsheet;

/**
 * Интерфейс слушателя кликов на элементы списка опций.
 *
 * @author sr.golovkin
 */
public interface OnOptionClickListener {

    /**
     * Обработать нажатие на опцию на указанной позиции.
     * @param position - позиция нажатой опции
     */
    void onOptionClick(int position);

}
