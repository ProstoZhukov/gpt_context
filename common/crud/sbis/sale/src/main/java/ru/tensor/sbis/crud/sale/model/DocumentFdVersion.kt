package ru.tensor.sbis.crud.sale.model

/**@SelfDocumented */
enum class DocumentFdVersion(val code: Int, val versionName: String) {

    /**@SelfDocumented */
    FD_VERSION_1_0(1, "1.0"),

    /**@SelfDocumented */
    FD_VERSION_1_05(2, "1.05"),

    /**@SelfDocumented */
    FD_VERSION_1_1(3, "1.1"),

    /**@SelfDocumented */
    FD_VERSION_1_2(4, "1.2");

    companion object {

        /**@SelfDocumented */
        fun getDocumentFdVersionByCode(code: Int?) = values().firstOrNull { it.code == code }

        /**@SelfDocumented */
    }

    fun isFfdOneOneOrAbove() = code >= 3
}