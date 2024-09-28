package ru.tensor.sbis.verification_decl.auth.auth_device

/**
 * Роутер для управления переходами на экране Безопасность.
 *
 * @author ar.leschev
 */
interface DeviceListHostRouter {

    /**
     * Метод для показа экрана настроек.
     * */
    fun showLoginSettingsFragment()

    /**
     * Метод для показа экрана смены пароля.
     * */
    fun showChangePasswordFragment()

    /**
     * Метод для закрытия экрана.
     */
    fun close()

    /**
     * Показать экран настройки пин-кода
     */
    fun showLockScreen()

    /**
     * Показать сканнер QR.
     */
    fun showQrScannerFragment()
}