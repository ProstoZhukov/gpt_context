@file:Suppress("NOTHING_TO_INLINE")

package ru.tensor.sbis.business.common.ui.utils

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.reactivex.disposables.Disposables
import ru.tensor.sbis.business.common.ui.base.ComparableObservable

/** @SelfDocumented */
inline val ObservableBoolean.isTrue
    get() = get()

/** @SelfDocumented */
inline val ObservableBoolean.isFalse
    get() = get().not()

/** @SelfDocumented */
inline val ObservableBoolean.toTrue
    get() = set(true)

/** @SelfDocumented */
inline val ObservableBoolean.toFalse
    get() = set(false)

/** @SelfDocumented */
inline val ObservableField<out Any?>.toNull
    get() = set(null)

@Suppress("UNCHECKED_CAST")
fun <T> ObservableField<out Any>.to(): T? = get() as? T

/** Изменение значения на противоположное */
inline fun ObservableBoolean.toggle() {
    val currentValue = get()
    set(!currentValue)
}

/** @SelfDocumented */
inline fun ObservableField<String>.getOrEmpty() = get().orEmpty()

/** @SelfDocumented */
inline fun ObservableField<CharSequence>.getStringOrEmpty() = get()?.toString().orEmpty()

/** @SelfDocumented */
inline val ObservableField<String>.length: Int
    get() = this.getOrEmpty().length

/** @SelfDocumented */
inline val ObservableField<Boolean>.isTrue
    get() = get() == true

/** @SelfDocumented */
inline val ObservableField<Boolean>.isFalse
    get() = get() != true

/**
 * Перегруженый оператор invoke для получения обернутого [T].
 */
operator fun <T> ObservableField<T>.invoke() = get()

/**
 * Сбросить состояние [ObservableField] (установить null).
 */
fun <T> ObservableField<T>.reset() {
    if (get() != null) {
        set(null)
    }
}

/**
 * Проверить оборачиваемый объект [T] в [ObservableField] на null
 */
fun <T> ObservableField<T>.isNull() = get() == null

/**
 * Проверить что оборачиваемый объект [T] в [ObservableField] НЕ null
 */
fun <T> ObservableField<T>.isNotNull() = !isNull()

/**
 * Проверить что оборачиваемый объект [CharSequence] в [ObservableField] НЕ null и не пуст
 */
fun ObservableField<out CharSequence>.isNotEmpty() = get().isNullOrEmpty().not()

/**
 * Указывает, НЕ равен ли какой-либо другой объект [other] обернутому объекту [T] в [ObservableField]
 */
fun <T> ObservableField<T>.isNotEqual(other: T?): Boolean = get() != other

/**
 * Безопасно проверить размерность обернутого списка [T] в [ObservableField]
 */
fun <T> ObservableField<List<T>>.size(): Int = get()?.size ?: 0

/**
 * Получить кол-во [predicate] элементов списка [T] в [ObservableField]
 */
fun <T> ObservableField<List<T>>.count(predicate: (T) -> Boolean): Int =
    get()?.filter(predicate)?.size ?: 0

/** Null-безопасное свойство для получения списка объектов [T] из [ObservableField] */
inline val <reified T> ObservableField<List<T>>.toList: List<T>
    get() = get() ?: emptyList()

/**
 * Проверить что обернутый список [T] в [ObservableField] НЕ содержит элементов класса [itemClass]
 */
inline fun <reified T> ObservableField<List<T>>.notContains(itemClass: Class<*>): Boolean =
    contains(itemClass).not()

/**
 * Проверить что обернутый список [T] в [ObservableField] содержит элемент(ы) класса [itemClass]
 */
inline fun <reified T> ObservableField<List<T>>.contains(itemClass: Class<*>): Boolean =
    toList.filterIsInstance(itemClass).isNotEmpty()

/**
 * Проверить что обернутый список [T] в [ObservableField] содержит хотя бы один элемент
 * типа [T] удовлетворяющий условию
 */
inline fun <reified T> ObservableField<List<BaseObservable>>.any(condition: (T) -> Boolean): Boolean =
    toList.any { it is T && condition(it) }

/**
 * Проверить что обернутый объект [T] в [ObservableField] отвечает условиям проверки [condition]
 */
inline fun <reified T> ObservableField<T>.check(condition: (T) -> Boolean): Boolean =
    get()?.let { condition(it) } == true

inline fun <reified T : BaseObservable> ObservableField<List<T>>.remove(item: T) {
    val modifiedList = toList.toMutableList().apply { remove(item) }
    set(modifiedList)
}

/**
 * Удаляет из обернутого в [ObservableField] списока [T] все вхождения класса [itemClass]
 */
inline fun <reified T : BaseObservable> ObservableField<List<T>>.removeAll(
    itemClass: Class<out T>,
    copy: Boolean = false,
): ObservableField<List<T>> {
    val filtered = toList.let {
        val overage = it.filterIsInstance(itemClass)
        it.minus(overage)
    }
    return if (copy) {
        ObservableField<List<T>>(filtered)
    } else {
        set(filtered)
        this
    }
}

/**
 * Проверить что обернутый список [T] в [ObservableField] null или пуст
 */
fun <T> ObservableField<List<T>>?.isNullOrEmpty(): Boolean =
    this == null || size() == 0

/**
 * Проверить что обернутый список [T] в [ObservableField] НЕ null и НЕ пуст
 */
fun <T> ObservableField<List<T>>?.isNotNullOrEmpty(): Boolean =
    this != null && size() > 0

/**
 * Обновить список вью-моделей [BaseObservable]
 *
 * @param operator функция возвращающая обновленный [BaseObservable] вместо переданного [BaseObservable]
 */
inline fun <reified T : BaseObservable> List<T>.update(operator: (T: ComparableObservable) -> T?): MutableList<T> {
    val newList = if (this !is MutableList<*>) toMutableList() else this as MutableList<T>
    val iterator = newList.listIterator()
    while (iterator.hasNext()) {
        iterator.next().let { next ->
            if (next is ComparableObservable) {
                operator(next)?.let(iterator::set)
            }
        }
    }
    return newList
}

/**
 * Проверить что список пуст или содержит только элементы удовлетворяющие условию
 *
 * @param ignoreCondition условие игнорирования элемента в списке
 */
inline fun <reified T : BaseObservable> ObservableField<List<T>>.isEmptyOrHasOnly(
    ignoreCondition: (T) -> Boolean = { true },
) = toList.all { ignoreCondition(it) }

/**
 * Добавляет наблюдаемую [BaseObservable] вью-модель в список [BaseObservable]
 *
 * @param list список с вью-моделями
 * @param isRevertCondition реверт условия проверки [condition], по-умолчанию возвращает false
 * @param condition условие добавления вью-модели, по-умолчанию возвращает true
 */
inline fun <reified T : BaseObservable> ObservableField<T>?.addTo(
    list: MutableList<BaseObservable>,
    isRevertCondition: Boolean = false,
    condition: () -> Boolean = { true },
): MutableList<BaseObservable> {
    if (this != null) {
        get()?.takeIf {
            if (isRevertCondition) condition().not()
            else condition()
        }
            ?.let { item -> list.add(item) }
    }
    return list
}

/**
 * Добавляет наблюдаемую [BaseObservable] вью-модель в список [BaseObservable]
 *
 * @param list список с вью-моделями
 * @param isRevertCondition реверт условия проверки [condition], по-умолчанию возвращает false
 * @param condition условие добавления вью-модели, по-умолчанию возвращает true
 */
inline fun <reified T : List<BaseObservable>> ObservableField<T>?.addAllTo(
    list: MutableList<BaseObservable>,
    isRevertCondition: Boolean = false,
    condition: (T) -> Boolean = { true },
): MutableList<BaseObservable> {
    if (this != null) {
        get()?.takeIf { item ->
            if (isRevertCondition) condition(item).not()
            else condition(item)
        }?.let { item -> list.addAll(item) }
    }
    return list
}

//region ObservableField Dependencies
/**
 * Добавляет обратный вызов для прослушивания изменений в Observable объекте.
 *
 * @param replay уведомлять подписчика о текущем значении до изменений
 * @param callback обратный вызов
 * @return объект позволяющий прервать подписку
 */
inline fun <reified T : BaseObservable> T.addOnPropertyChangedCallback(
    replay: Boolean = false,
    crossinline callback: (T) -> Unit,
) = object : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(
        sender: Observable?,
        propertyId: Int,
    ) = callback(sender as T)
}.also {
    addOnPropertyChangedCallback(it)
    if (replay) {
        notifyChange()
    }
}.let { Disposables.fromAction { removeOnPropertyChangedCallback(it) } }

/**
 * Создает [ObservableField] который зависит от дочерних [Observable]
 *
 * @param dependencies зависимости
 * @param default значение по-умолчанию
 * @param mapper обратный вызов для обработки уведомлений об изменениях
 * @return созданный [ObservableField]
 */
inline fun <T> dependOfFields(
    vararg dependencies: Observable,
    default: T? = null,
    crossinline mapper: () -> T?,
) =
    object : ObservableField<T>(*dependencies) {
        override fun get(): T? = mapper()
    }.apply { set(default) }

inline fun dependOfBooleanFields(
    vararg dependencies: Observable,
    default: Boolean = false,
    crossinline mapper: () -> Boolean,
) =
    object : ObservableBoolean(*dependencies) {
        override fun get(): Boolean = mapper()
    }.apply { set(default) }

/**
 * Создает [ObservableBoolean] который зависит от дочерних [ObservableBoolean]
 * Оповещает слушателей при попытке установить ранее установленное значение
 *
 * @param dependencies зависимости
 * @param default значение по-умолчанию
 * @param mapper обратный вызов для обработки уведомлений об изменениях
 * @return созданный [ObservableField]
 */
inline fun dependOfFields(
    vararg dependencies: ObservableBoolean,
    default: Boolean = false,
    crossinline mapper: (List<ObservableBoolean>) -> Boolean,
) =
    object : ObservableBoolean(*dependencies) {
        override fun get(): Boolean = mapper(dependencies.toList())
        override fun set(value: Boolean) {
            if (get() == value) {
                notifyChange()
            } else {
                super.set(value)
            }
        }
    }.apply { set(default) }

//endregion ObservableField Dependencies



