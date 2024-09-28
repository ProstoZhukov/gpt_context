package ru.tensor.sbis.catalog_decl.catalog

/** Класс, описывающий алкогольные свойства номенклатуры. */
data class NomenclatureAlcoInfo(val isMarked: Boolean, val code: String?, val capacity: Double?, val volume: Double?)