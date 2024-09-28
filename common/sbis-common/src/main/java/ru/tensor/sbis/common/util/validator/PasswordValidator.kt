package ru.tensor.sbis.common.util.validator

import android.content.Context

import ru.tensor.sbis.common.R
import kotlin.math.roundToLong

private const val MIN_PASSWORD_LENGTH = 6
private const val MIN_COMPLEXITY_LEVEL = 27

/**
 * Валидатор пароля
 */
class PasswordValidator : Validator<String> {

    /**
     * Валидация пароля с сообщением об ошибке
     *
     * @param context - контекст приложения
     * @param password - валидируемый пароль
     * @return - null если пароль валидный, иначе сообщение об ошибке-подсказка
     */
    fun validateWithMessage(context: Context?, password: String): String? {
        if (password.length < MIN_PASSWORD_LENGTH) {
            return context?.getString(R.string.common_password_is_too_short).orEmpty()
        }

        val upperCaseCount = password.count { it.isUpperCase() }
        val lowerCaseCount = password.count { it.isLowerCase() }
        val digitCount = password.count { it.isDigit() }
        val miscCount = password.count { !it.isLetterOrDigit() && !it.isWhitespace() }

        val complexityLevel = (upperCaseCount + 1) * (lowerCaseCount + 1) * (digitCount + 1) * (1 + miscCount * 1.2)

        return if (complexityLevel.roundToLong() < MIN_COMPLEXITY_LEVEL) {
            context?.getString(R.string.common_low_password_complexity).orEmpty()
        } else {
            null
        }
    }

    override fun validate(password: String): Boolean {
        return validateWithMessage(null, password) == null
    }

    fun isEmpty(password: String): Boolean {
        return password.trim { it <= ' ' }.isEmpty()
    }

    fun isEquals(passwordFirst: String, passwordSecond: String): Boolean {
        return passwordFirst == passwordSecond
    }

    fun validateInputFieldsNonEmpty(vararg args: String): Boolean {
        for (arg in args) {
            if (isEmpty(arg)) {
                return false
            }
        }

        return true
    }
}
