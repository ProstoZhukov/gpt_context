package ru.tensor.sbis.verification_decl.login

import android.accounts.Account
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import io.reactivex.Completable
import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.verification_decl.lockscreen.AuthLaunchNavigator
import ru.tensor.sbis.verification_decl.login.event.HostEvent
import ru.tensor.sbis.verification_decl.login.event.NavigationEvent
import ru.tensor.sbis.verification_decl.auth.auth_code.AuthCodeLoginActivityProvider

/**
 * Интерфейс-контракт модуля авторизации.
 * ВНИМАНИЕ! Изменения в этом интерфейсе нужно поддержать в LoginInterfaceImpl и RetailLoginInterfaceImpl
 *
 * @author av.krymov
 */
interface LoginInterface : CurrentAccount, CurrentPersonalAccount, LoadCurrentPersonalAccount,
    PersonalAccountsExcludePrivate, PersonalAccounts, AuthEventsObservableProvider, AuthLaunchNavigator, Feature {
    /**
     * Запускает инициализацию модуля авторизации, включая инициализацию соцсетей, обновление сессии
     * и подписку на события контроллера
     *
     * @param application экземпляр Application
     */
    fun initialize(application: Application)

    /**
     * Создание фрагмента, используемого в качестве хоста в модуле авторизации
     *
     * @param isFromLockScreen открыт ли экран с экрана блокировки.
     * @param isAfterExit      открыт ли экран после разлогина на контроллере (был вызван метод exit).
     * @return хост фрагмент
     */
    fun getHostFragment(isFromLockScreen: Boolean, isAfterExit: Boolean): Fragment

    /**
     * Возвращает Observable, который отправляет событие каждый раз при обновлении текущего пользователя
     *
     * @return Observable с пользователем
     */
    val userAccountObservable: Observable<UserAccount>

    /**
     * Возвращает модель аккаунта для синхронизации
     *
     * @return пользователь
     */
    val currentAccountForSync: Account?
    //----------------------------------------------------------------------------------------------

    /**
     * Проверяет авторизован ли в МП локальный пользователь UI.
     */
    @get:WorkerThread
    val isAuthorized: Boolean

    /**
     * Проверяет корректность данных авторизации в данный момент времени (к примеру токен, логин).
     * Возвращает false только если пользователь был разлогинен по какой-то причине (закончилась сессия, ручной разлогин).
     * Если метод вернул false, то в момент входа в foreground режим пользователя перекинет на форму логина.
     *
     * @return true если данные авторизации корректны, false иначе
     */
    @Deprecated("Более не используется. Будет удалено.")
    val isAuthorizationDataCorrect: Boolean

    /**
     * Синхронизировать данные по пользователю (инфо, список аккантов).
     * Необходимо запускать в рабочем потоке.
     */
    @WorkerThread
    fun syncCurrentAccount()

    /**
     * Идентификатор персоны текущего пользователя.
     * В основном, для действий с микросервисом профилей.
     * Может вернуться пустым, если никогда не логинились.
     * Если вам нужен только ID, лучше брать отсюда, чем вызывать [getCurrentAccount].
     */
    @get:AnyThread
    val currentPersonId: String?

    /**
     * Основной идентификатор текущего пользователя.
     * Если вам нужен только ID, лучше брать отсюда, чем вызывать [getCurrentAccount].
     * По умолчанию = -1.
     */
    @get:AnyThread
    val currentUserId: Int?

    /**
     * Возвращает подписку на события связанные с хостом приложения
     */
    val hostEventObservable: Observable<HostEvent>

    /**
     * Подписка на события навигации
     */
    val navigationObservable: Observable<NavigationEvent?>?

    /**
     * Открывает экран с формой логина
     *
     * @param force true если необходимо запустить моментально, false если необходимо запустить только когда приложение находится или будет находиться в foreground режиме
     * @param isAuthActivityProviderExist true, если реализуется фича [AuthCodeLoginActivityProvider]. Необходимо для
     * правильного открытия Активности приложения в случаях, когда приложение реализует паттерн SingleActivity.
     */
    fun startLoginActivity(force: Boolean, isAuthActivityProviderExist: Boolean = false)

    /**
     * @return класс LoginActivity для дальнейшего создания [android.content.Intent]
     */
    val loginActivityClass: Class<out Activity?>

    /**
     * Возвращает токен авторизации текущего пользователя
     *
     * @return токен
     */
    val token: String?

    /**
     * Изменяет пароль текущего пользователя
     *
     * @param currentPassword старый пароль
     * @param newPassword     новый пароль
     * @return onComplete action если смена пароля произошла успешно, иначе onError consumer.
     * Результат в главном потоке.
     */
    fun changePassword(currentPassword: String, newPassword: String): Completable

    /**
     * Связать внешний токен (содержимое QR кода) с пользователем
     * Необходимо для авторизации по QR на онлайне
     *
     * @param token отсканированное содержимое QR кода с онлайна
     * @return onComplete если привязано, иначе onError с исключением.
     */
    fun linkExternalTokenWithUser(token: String): Completable

    /**
     * Логаут с открытием экрана логина
     */
    @WorkerThread
    fun logout()

    /** @see AuthLaunchNavigator.runLockScreenIfTime
     */
    override fun runLockScreenIfTime(context: Context) {}

    interface Provider : Feature {
        val loginInterface: LoginInterface
    }

    companion object {
        /**
         * Флаг-ключ, по которому можно понять была ли запущена startLoginActivity по причине окончания выхода на контроллере.
         */
        const val ARG_IS_AFTER_LOGIN = "ARG_IS_AFTER_LOGIN"
    }
}