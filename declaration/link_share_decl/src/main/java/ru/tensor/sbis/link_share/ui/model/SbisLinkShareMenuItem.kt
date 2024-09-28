package ru.tensor.sbis.link_share.ui.model

/** Пункты меню компонента "поделиться ссылкой" */
enum class SbisLinkShareMenuItem {
    /** Скопировать ссылку */
    COPY,

    /** Открыть ссылку в браузере */
    OPEN_IN_BROWSER,

    /** QR-код ссылки */
    QR,

    /** Отправить (поделиться) ссылкой*/
    SEND,

    /** Пользовательский пункт меню */
    CUSTOM
}