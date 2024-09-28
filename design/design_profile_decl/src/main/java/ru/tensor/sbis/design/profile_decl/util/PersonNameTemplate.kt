package ru.tensor.sbis.design.profile_decl.util

/**@SelfDocumented */
enum class PersonNameTemplate {
    /**Пример: Иванов И.*/
    SURNAME_N,

    /**Пример: Иванов И. С.*/
    SURNAME_N_P,

    /**Пример: Иванов Иван*/
    SURNAME_NAME,

    /**Пример: Иванов Иван Сергеевич*/
    SURNAME_NAME_PATRONYMIC;

    private class Builder {
        private val list = mutableListOf<String>()

        fun initial(str: String?) = apply {
            if (!str.isNullOrBlank()) list.add(str.take(1).plus("."))
        }

        fun word(str: String?) = apply {
            if (!str.isNullOrBlank()) list.add(str)
        }

        fun build() = list.joinToString(separator = " ")
    }

    /**@SelfDocumented */
    fun format(surname: String?, name: String?, patronymic: String? = "") = Builder().run {
        when (this@PersonNameTemplate) {
            SURNAME_N -> word(surname).initial(name).build()
            SURNAME_N_P -> word(surname).initial(name).initial(patronymic).build()
            SURNAME_NAME -> word(surname).word(name).build()
            SURNAME_NAME_PATRONYMIC -> word(surname).word(name).word(patronymic).build()
        }
    }
}