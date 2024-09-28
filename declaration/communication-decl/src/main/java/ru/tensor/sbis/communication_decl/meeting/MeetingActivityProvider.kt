package ru.tensor.sbis.communication_decl.meeting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * @author is.mosin
 *
 * Интерфейс, описывающий методы для формирования намерения на запуск активности совещаний, вебинаров и событий
 */
interface MeetingActivityProvider : Feature {

    /**
     * Предоставить намерение на запуск активности карточки совещания(видеосовещания), вебинара
     *
     * @param context     - Вызывающий контекст
     * @param cardType    - Тип действия
     */
    fun getEventCardActivityIntent(context: Context, cardType: EventCardType): Intent

    /**
     * Возвращает Fragment для открытия карточки события
     */
    fun getEventCardHostFragment(bundle: Bundle?): Fragment


    /**
     * Возвращает Fragment для открытия карточки события по заданному [cardType]
     */
    fun getEventCardHostFragment(cardType: EventCardType): Fragment
}