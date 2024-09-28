package ru.tensor.sbis.crud4.hierarchy_component

import android.os.Bundle
import android.os.Parcelable

/**
 * Предназначен для создания [ListComponentFragment] для заданной папки.
 *
 * @author ma.kolpakov
 */
interface ListComponentProvider<PATH_MODEL> : Parcelable {

    /** @SelfDocumented */
    fun create(bundle: Bundle?, folder: PATH_MODEL?): ListComponentFragment<PATH_MODEL>
}