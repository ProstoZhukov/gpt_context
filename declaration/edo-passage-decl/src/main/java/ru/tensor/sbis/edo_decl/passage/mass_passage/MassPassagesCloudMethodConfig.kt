package ru.tensor.sbis.edo_decl.passage.mass_passage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Параметры для вызова облачного метода для выполнения массовых переходов
 *
 * @property blObjectName   Имя объекта, от которого нужно вызывать метод
 * @property methodName     Имя метода
 * @property versionApi     Версия метода
 *
 * @author sa.nikitin
 */
@Parcelize
data class MassPassagesCloudMethodConfig(
    val blObjectName: String,
    val methodName: String,
    val versionApi: Int?
) : Parcelable