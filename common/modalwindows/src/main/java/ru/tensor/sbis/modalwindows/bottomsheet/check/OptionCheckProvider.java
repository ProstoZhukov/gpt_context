package ru.tensor.sbis.modalwindows.bottomsheet.check;

/**
 * Интерфейс "выбираемой" опции.
 *
 * @author sr.golovkin
 */
public interface OptionCheckProvider {

    /**
     * Выбрана ли опция.
     * @return true - если опция выбрана, false - иначе
     */
    boolean isChecked();

}
