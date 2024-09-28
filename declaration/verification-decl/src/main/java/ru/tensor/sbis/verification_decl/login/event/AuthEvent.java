package ru.tensor.sbis.verification_decl.login.event;

/**
 * Событие авторизации для подписки на UI.
 *
 * @author ar.leschev
 */
public class AuthEvent {

    /**
     * События атворизации.
     */
    public enum EventType {
        /**
         * Пользователь только что совершил вход в приложение(например введя логин и пароль).
         * Или произошло успешное переключение на другой аккаунт.
         */
        LOGIN,
        /**
         * Успешно произошел разлогин пользователя.
         * Пользователь сам нажал на "выход", сессионный токен по каким-то причинам протух.
         * Или пользователь собирается переключится на другой аккаунт.
         */
        LOGOUT,
        /**
         * Приложение было только что запущено с уже авторизованным пользователем.
         * Вызовется единожды при запуске приложения.
         */
        AUTHORIZED
    }

    public EventType eventType;

    /**
     * Данный флаг нужно проверять только по событию EventType.LOGIN.
     * false если залогинился последний вышедший пользователь.
     * true если пользователь успешно переключился на другой аккаунт в настройках
     * или авторизовался новый пользователь(идентификатор последнего разлогиненного не равен текущему залогиненному).
     */
    public boolean isNewAccount;

    /**
     * Данный флаг нужно проверять только по событию EventType.LOGIN.
     * В некоторых кейсах, мы уже авторизовались на контроллере, но переходить с экрана авторизации нам ещё не нужно.
     * Всего может быть 2 события LOGIN:
     * - авторизованы на контроллере, значение флага - false.
     * - UI авторизации сделал необходимые операции, можно переходить в МП - true.
     */
    public boolean isAuthUiFinished;

    /**
     * Данный флаг нужно проверять только по событию EventType.LOGOUT.
     * true если произошел полноценный логаут юзера, иначе означает что будет произведена смена аккаунта на другой \ b
     */
    public boolean isSessionClosed;

    public AuthEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public AuthEvent(EventType eventType, boolean isNewAccount) {
        this(eventType);
        this.isNewAccount = isNewAccount;
    }

    public AuthEvent(boolean isSessionClosed, EventType eventType) {
        this(eventType);
        this.isSessionClosed = isSessionClosed;
    }
}
