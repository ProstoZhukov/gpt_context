package ru.tensor.sbis.common.util

import androidx.databinding.Observable
import androidx.databinding.ObservableField

/**@SelfDocumented */
open class ObservableString: ObservableField<String> {

    constructor(): super("")

    constructor(value: String): super(value)

    constructor(dependencies: Observable): super(dependencies)

    override fun get(): String {
        return super.get()!!
    }
}