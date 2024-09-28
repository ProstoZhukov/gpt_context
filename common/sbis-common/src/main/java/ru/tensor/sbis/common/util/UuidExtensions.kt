package ru.tensor.sbis.common.util

import java.util.UUID

/**@SelfDocumented*/
fun UUID?.isNullOrNil(): Boolean = this == null || this == UUIDUtils.NIL_UUID

/**@SelfDocumented*/
fun UUID.isNotNil(): Boolean = this != UUIDUtils.NIL_UUID

/**@SelfDocumented*/
fun UUID.isNil(): Boolean = this == UUIDUtils.NIL_UUID

/**@SelfDocumented*/
fun String?.toUuid(): UUID = UUID.fromString(this)