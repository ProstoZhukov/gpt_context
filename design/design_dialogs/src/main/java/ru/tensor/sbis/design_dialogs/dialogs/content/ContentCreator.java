package ru.tensor.sbis.design_dialogs.dialogs.content;

import java.io.Serializable;

/**
 * Наследник {@link BaseContentCreator} с объявлением наследования от {@link Serializable}
 * Класс, реализующий данный интерфейс, должен быть неанонимным для того,
 * чтобы поддержать функционал {@link Serializable}.
 *
 * @author sa.nikitin
 */
public interface ContentCreator extends BaseContentCreator, Serializable {
}
