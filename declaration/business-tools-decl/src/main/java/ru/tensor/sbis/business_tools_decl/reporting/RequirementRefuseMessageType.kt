package ru.tensor.sbis.business_tools_decl.reporting

import androidx.annotation.StringRes
import ru.tensor.sbis.business_tools_decl.R

/**
 * Тип причины отказа по требованию
 *
 * @author ev.grigoreva
 */
enum class RequirementRefuseMessageType constructor(val value: Int) {
    FILE_FORMAT_ERROR(0), RATEPAYER_ERROR(1), CERTIFICATE_ERROR(2);

    val code: String
        get() {
            return when (this) {
                FILE_FORMAT_ERROR -> "0300000000" //"Ошибки, выявляемые при форматном контроле"
                RATEPAYER_ERROR -> "0400100000" //"Не идентифицирован налогоплательщик"
                CERTIFICATE_ERROR -> "0100100000" //"Отсутствие, неправильное указание ЭП"
            }
        }

    val userMessageRes: Int @StringRes
    get() {
        return when (this) {
            FILE_FORMAT_ERROR -> R.string.business_tools_decl_requirement_refuse_message_file_format
            RATEPAYER_ERROR -> R.string.business_tools_decl_requirement_refuse_message_ratepayer
            CERTIFICATE_ERROR -> R.string.business_tools_decl_requirement_refuse_message_certificate
        }
    }

    companion object {

        /**
         * @param value значение
         * @return состояние
         */
        @JvmStatic
        fun fromValue(value: Int): RequirementRefuseMessageType? {
            for (type in values()) {
                if (type.value == value) {
                    return type
                }
            }
            return null
        }

        /**
         * @param code код
         * @return состояние
         */
        @JvmStatic
        fun fromCode(code: String?): RequirementRefuseMessageType? {
            if (code == null) {
                return null
            }
            for (type in values()) {
                if (type.code == code) {
                    return type
                }
            }
            return null
        }
    }
}