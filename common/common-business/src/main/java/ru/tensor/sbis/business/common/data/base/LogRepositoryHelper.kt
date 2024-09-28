package ru.tensor.sbis.business.common.data.base

import ru.tensor.sbis.common.util.AppConfig
import timber.log.Timber
import java.util.Locale

/**
 * Хэлпер для логирования обращений к crud фасаду без кастомизации логируемых сообщений
 */
@Suppress("unused")
interface LogSimpleRepositoryHelper : LogRepositoryHelper<Any?, Any?, Any?, Any?>

/**
 * Хэлпер для логирования обращений к списочному crud фасаду для отладочной сборки
 */
interface LogRepositoryHelper<FILTER : Any?, ENTITY : Any?, LIST_FILTER : Any?, LIST : Any?> {

    val tag: String

    fun ENTITY.toReadableForm(): String = toString()

    fun LIST.toReadableListForm(): String = toString()

    fun logMessage(message: String) = log(message)

    fun ENTITY?.logCreate() = this?.also { log("CREATE received ${toReadableForm()}\n\n") }

    fun Long?.logCreateResult() = this?.also { log("CREATE ${this}\n\n") }

    fun ENTITY?.logRead(
        filter: Any?
    ) = also {
        if (it == null) {
            log("READ NULL with filter: $filter received NULL")
        } else {
            log("READ with filter: $filter received $it\n\n")
        }
    }

    fun ENTITY?.logRead(
        id: Int
    ) = also {
        if (it == null) {
            log("READ NULL for Id: $id received NULL")
        } else {
            log("READ for Id: $id received $it\n\n")
        }
    }

    fun ENTITY?.logReadWithRefresh(
        filter: FILTER
    ) = also {
        if (it == null) {
            log("FETCH NULL with filter:\n\t$filter\n\t received NULL")
        } else {
            log("FETCH with filter:\n\t$filter\n\t received ${it.toReadableForm()}\n\n")
        }
    }

    fun ENTITY?.logUpdate() =
        this?.also { log("UPDATE with entity:\n\t$this\n\t received ${toReadableForm()}\n\n") }

    fun ENTITY?.log(
        methodName: String,
        filter: Any?
    ) = also {
        val name = methodName.toUpperCase(Locale.getDefault())
        if (it == null) {
            log("$name NULL for Id: $filter) received NULL")
        } else {
            log("$name for Id: $filter) received $it\n\n")
        }
    }

    fun logDelete(
        uuid: FILTER,
        result: Boolean
    ) = log("DELETE with uuid:\n\t$uuid\n\treceived $result\n\n")

    fun logDelete(
        id: Long,
        result: Boolean
    ) = log("DELETE with id:\n\t$id\n\treceived $result\n\n")

    fun LIST?.logReadListWithRefresh(
        filter: LIST_FILTER
    ) = also {
        if (it == null) {
            log("LIST NULL with filter:\n\t$filter\n\t received NULL")
        } else {
            log("LIST with filter:\n\t$filter\n\t received ${it.toReadableListForm()}\n\n")
        }
    }

    fun LIST?.logSearchOnline(
        searchQuery: String,
        filter: LIST_FILTER?
    ) = also {
        if (it == null) {
            log("SEARCH_ONLINE NULL with query: \n\t$searchQuery\n\t, filter:\n\t$filter\n\t received NULL")
        } else {
            log("SEARCH_ONLINE with query: \n\t$searchQuery\n\t, filter:\n\t$filter\n\t received ${it.toReadableListForm()}\n\n")
        }
    }

    fun LIST?.logReadList(
        filter: LIST_FILTER
    ) = also {
        if (it == null) {
            log("REFRESH NULL with filter:\n\t$filter\n\t received NULL")
        } else {
            log("REFRESH with filter:\n\t$filter\n\t received ${it.toReadableListForm()}\n\n")
        }
    }


    fun logSetDataRefreshCallback() =
        log("setDataRefreshCallback has been set")

    private fun log(message: String) {
        // TODO https://online.sbis.ru/opendoc.html?guid=1c5e3d51-4daa-4c6d-81de-cbc20a2e063a&client=3 убрать ui логи если ошибка более не повторится
        if (true/*AppConfig.isDebug()*/) {
            Timber.tag(tag).w(message)
        }
    }
}
