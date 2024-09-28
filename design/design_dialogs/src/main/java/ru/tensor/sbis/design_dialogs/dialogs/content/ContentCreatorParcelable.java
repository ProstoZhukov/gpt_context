package ru.tensor.sbis.design_dialogs.dialogs.content;

import android.os.Parcelable;

/**
 * Наследник {@link BaseContentCreator} с объявлением наследования от {@link Parcelable}
 * Класс, реализующий данный интерфейс, должен быть неанонимным для того,
 * чтобы поддержать функционал {@link Parcelable}.
 *
 * @author sa.nikitin
 */
public interface ContentCreatorParcelable extends BaseContentCreator, Parcelable {
}
