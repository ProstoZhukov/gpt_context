package ru.tensor.sbis.verification_decl.auth

/**
 * Константы для автотестов
 *  Подробнее https://online.sbis.ru/shared/disk/1163c564-b767-474c-8492-0bf622b46c72 пункт 6.3
 *
 *  @author ar.leschev
 */
object AuthConsts {
    /** Константа автотестов: категория в интенте при которой запуск считается из автотестов. */
    const val SBIS_AUTOTEST_LAUNCH = "SBIS_AUTOTEST_LAUNCH"

    /** Константа логина. */
    const val SBIS_AUTOTEST_LOGIN = "SBIS_AUTOTEST_LOGIN"

    /** Константа пароля. */
    const val SBIS_AUTOTEST_PASSWORD = "SBIS_AUTOTEST_PASSWORD"

    /** Константа хоста. */
    const val SBIS_AUTOTEST_HOST = "SBIS_AUTOTEST_HOST"
}