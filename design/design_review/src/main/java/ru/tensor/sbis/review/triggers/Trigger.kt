package ru.tensor.sbis.review.triggers

import android.annotation.SuppressLint
import android.content.SharedPreferences
import ru.tensor.sbis.review.enumToString
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Интерфейс правила по которому вызывается сервис оценок
 *
 * @author ma.kolpakov
 */

sealed class Trigger(val eventKey: String) {
    /**
     * Метод проверяющий правило
     */
    abstract fun checkEvent(storage: SharedPreferences): Boolean

    /**
     * Сериализатор событий для конкретного триггера определяет каким образом событие будет сохранено.
     * Рекомендуется использовать [companion object]в реализации, так как сериализаторы уникальны между типами триггеров
     */
    abstract val serializer: EventSerializer
}

/**
 * Родительский триггер (Логическое "И") возвращает истину если все дочерние триггеры вернули истину
 */
class AndTrigger(vararg triggers: Trigger) : Trigger(ALL_EVENTS), TriggerParent {
    override val children: Array<out Trigger> = triggers
    override val serializer: EventSerializer = TriggerEventSerializer

    /**@SelfDocumented**/
    override fun checkEvent(storage: SharedPreferences) = children.all { it.checkEvent(storage) }

    /**@SelfDocumented**/
    private companion object TriggerEventSerializer : EventSerializer {
        override fun serialize(eventKey: String, storage: SharedPreferences) = Unit
    }

    /**@SelfDocumented**/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AndTrigger

        if (!children.contentEquals(other.children)) return false

        return true
    }

    /**@SelfDocumented**/
    override fun hashCode(): Int {
        return children.contentHashCode()
    }
}

/**
 * Родительский триггер (Логическое "ИЛИ") возвращает истину если хотя бы один дочерний триггер вернул истину
 */
class OrTrigger(vararg triggers: Trigger) : Trigger(ALL_EVENTS), TriggerParent {
    override val children: Array<out Trigger> = triggers
    override val serializer: EventSerializer = TriggerEventSerializer

    /**@SelfDocumented**/
    override fun checkEvent(storage: SharedPreferences) = children.any { it.checkEvent(storage) }

    /**@SelfDocumented**/
    private companion object TriggerEventSerializer : EventSerializer {
        override fun serialize(eventKey: String, storage: SharedPreferences) = Unit
    }

    /**@SelfDocumented**/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrTrigger

        if (!children.contentEquals(other.children)) return false

        return true
    }

    /**@SelfDocumented**/
    override fun hashCode(): Int {
        return children.contentHashCode()
    }
}

/**
 * Триггер счетчик возвращает истину если количество событий больше или равно пороговому значению
 * @param event - тип события на который реагирует триггер
 * @param threshold - пороговое значение
 */
class CountTrigger(event: Enum<*>, private val threshold: Int) : Trigger(enumToString(event)) {
    override val serializer: EventSerializer = TriggerEventSerializer

    /**@SelfDocumented**/
    override fun checkEvent(storage: SharedPreferences): Boolean {
        val currentValue = storage.getLong(eventKey, 0)
        return currentValue >= threshold
    }

    /**@SelfDocumented**/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CountTrigger

        if (eventKey != other.eventKey || threshold != other.threshold) return false

        return true
    }

    /**@SelfDocumented**/
    override fun hashCode(): Int {
        var result = threshold
        result = 31 * result + eventKey.hashCode()
        return result
    }

    /**@SelfDocumented**/
    private companion object TriggerEventSerializer : EventSerializer {
        /**@SelfDocumented**/
        override fun serialize(eventKey: String, storage: SharedPreferences) {
            var currentValue = storage.getLong(eventKey, 0)
            val editor = storage.edit()
            editor.putLong(eventKey, ++currentValue)
            editor.apply()
        }
    }

}

/**
 * Триггер счетчик возвращает истину если количество событий, за один день, больше или равно пороговому значению
 * @param event - тип события на который реагирует триггер
 * @param threshold - пороговое значение
 */
class DailyCountTrigger(event: Enum<*>, private val threshold: Int) : Trigger(enumToString(event)) {

    override val serializer: EventSerializer = TriggerEventSerializer

    /**@SelfDocumented**/
    override fun checkEvent(storage: SharedPreferences): Boolean {
        val currentValue = storage.getLong(eventKey + DAILY_COUNT_SUFFIX, 0)
        return currentValue >= threshold
    }

    /**@SelfDocumented**/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailyCountTrigger

        if (eventKey != other.eventKey || threshold != other.threshold) return false

        return true
    }

    /**@SelfDocumented**/
    override fun hashCode(): Int {
        var result = threshold
        result = 31 * result + eventKey.hashCode()
        return result
    }

    private companion object TriggerEventSerializer : EventSerializer {
        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("dd/M/yyyy")

        /**@SelfDocumented**/
        override fun serialize(eventKey: String, storage: SharedPreferences) {
            val currentDate = dateFormat.format(Date())

            val savedDate = storage.getString(eventKey + DAILY_COUNT_DATE, "")

            var currentValue = storage.getLong(eventKey + DAILY_COUNT_SUFFIX, 0)

            if (currentDate != savedDate) {
                currentValue = 0
            }

            val editor = storage.edit()
            editor.putLong(eventKey + DAILY_COUNT_SUFFIX, ++currentValue)
            editor.putString(eventKey + DAILY_COUNT_DATE, currentDate)
            editor.apply()
        }
    }
}

/**
 * Триггер счетчик возвращает истину если количество ДНЕЙ, в которые произошло хотя бы одно событие, больше или равно пороговому значению
 * @param event - тип события на который реагирует триггер
 * @param threshold - пороговое значение
 */
class DaysCountTrigger(
    event: Enum<*>,
    private val threshold: Int,
) : Trigger(enumToString(event)) {

    override val serializer: EventSerializer = TriggerEventSerializer

    /**@SelfDocumented**/
    override fun checkEvent(storage: SharedPreferences): Boolean {
        val currentValue = storage.getLong(eventKey + DAYS_COUNT_SUFFIX, 0)
        return currentValue >= threshold
    }

    /**@SelfDocumented**/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DaysCountTrigger

        if (eventKey != other.eventKey || threshold != other.threshold) return false

        return true
    }

    /**@SelfDocumented**/
    override fun hashCode(): Int {
        var result = threshold
        result = 31 * result + eventKey.hashCode()
        return result
    }

    internal companion object TriggerEventSerializer : EventSerializer {

        internal var currentDayProvider: () -> Date = { Date() }

        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("dd/M/yyyy")

        /**@SelfDocumented**/
        override fun serialize(eventKey: String, storage: SharedPreferences) {
            val currentDate = dateFormat.format(currentDayProvider())

            val savedDate = storage.getString(eventKey + DAYS_COUNT_DATE, "")

            if (currentDate != savedDate) {
                var currentValue = storage.getLong(eventKey + DAYS_COUNT_SUFFIX, 0)
                val editor = storage.edit()
                editor.putLong(eventKey + DAYS_COUNT_SUFFIX, ++currentValue)
                editor.putString(eventKey + DAYS_COUNT_DATE, currentDate)
                editor.apply()
            }
        }
    }
}

private const val ALL_EVENTS = "ALL"
internal const val DAILY_COUNT_SUFFIX = "|DAILY_COUNT"
internal const val DAILY_COUNT_DATE = "|DAILY_COUNT_DATE"

internal const val DAYS_COUNT_SUFFIX = "|DAYS_COUNT"
internal const val DAYS_COUNT_DATE = "|DAYS_COUNT_DATE"
