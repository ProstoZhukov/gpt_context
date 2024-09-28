package ru.tensor.sbis.version_checker_decl

import android.content.Intent

/**
 * Маркерный интерфейс. Сообщает что компонент может быть инициирован системой для установки другого МП семейства Сбис.
 * Вешается на `Activity`, а обработка производится диспетчером версионирования [VersioningDispatcher]
 * Должен применяться к `Activity` обрабатывающей ссылки на авторизацию/установку МП по qr-коду.
 * Пример qr-ссылки: https://online.sbis.ru/auth/qrcode/sbis/?token=token_value.
 *
 * По умолчанию таким `Activity` является обработчик назначенный для компонента открытия ссылок.
 * Смотреть gradle проекта:
 * ```
 * sabyLinks.setup { set ->
 *  // активити для маркировки
 *  set.component = "ru.tensor.sbis.business.LaunchActivity"
 *  // отключено открытие ссылки qr-кода с токеном авторизации в МП
 *  set.enableQrAuthAndInstallApp = false
 * }
 * ```
 *
 * @author as.chadov
 */
interface InstallationComponent {

    /**
     * Возвращает true если компонент открыт для Открытия/установки другого МП и его дальнейшая стандартная работа должна быть прервана.
     * По умолчанию при реализации установить false.
     * Побочные эффекты:
     * 1. если экран был поднят из Истории cм. [Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY] не пытаемся перейти к установке МП из qr-ссылки.
     * Считаем что ожидаемое поведение для пользователя стандартный сценарий использования МП.
     * 2. если экран был поднят из Истории (см. п.1) и данные в намерении `Intent.data` содержат токен авторизации в другом МП, то такие данные
     * будут удалены. Необходимо для предотвращения прокидывания "чужого" токена на экран авторизации, т.е. предотвратить вход под пользователем
     * предназначенным для авторизации в др. МП.
     *
     * Пример:
     * ```
     *  override fun onCreate(savedInstanceState: Bundle?) {
     *      super.onCreate(savedInstanceState)
     *      ...
     *      if (launchedForInstallation) {
     *          finish()
     *          return
     *      }
     *      ...
     *  }
     * ```
     */
    var launchedForInstallation: Boolean
}