package ru.tensor.sbis.application_tools.debuginfo.model

import androidx.annotation.StringRes

import java.io.Serializable

/**
 * @author du.bykov
 *
 * Базовая сущность порции отладочной записи.
 */
open class BaseDebugInfo protected constructor() : Serializable {

    var type: Type

    @StringRes
    var title: Int = 0

    var description: String? = null

    init {
        type = Type.INFO
    }

    constructor(
        title: Int,
        description: String
    ) : this() {
        this.title = title
        this.description = description
    }

    enum class Type {
        INFO,
        CRASH,
        LOG
    }

}
