package ru.tensor.sbis.info_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Created by am.boldinov on 21.03.16.
 */
@Parcelize
enum class NotificationType(val value: Int) : Parcelable {

    UNKNOWN(-1), // -1;"Неизвестный тип уведомления"
    DOCUMENT(0), // 0;"Уведомление по документу"
    REFUSE_DOCUMENT(1), // 1;"Уведомление отказа по документу"
    TASK(2), // 2;"Уведомление по внутреннему ДО(Задача)"
    MESSAGE(3), // 3;"Уведомление по сообщению"
    CHAT_MESSAGE(77), // Сообщение из чата
    APPROVED_DOCUMENT(4), // 4;"Уведомление об утверждение по внешнему ДО"
    CALENDAR(5), // 5;"Уведомление о события в календаре"
    NEWS(6), // 6;"Уведомление о новости"
    START_VIDEO_CONF(7), // 7;"Уведомление о начале видеоконференции"
    TENDER(8), // 8;"Уведомление по тендерам"
    CONTRACTROR(9), //  9;"Уведомление по контрагентам"
    MEETING(10), // 10;"Уведомление о совещании"
    REQUIREMENT(11), // 11;"Уведомление о требованиях"
    UNALLOCATED(13), // 13;"Нераспределенные уведомления"
    EDIT_MESSAGE(14), // 14;"Уведомление по редактированию сообщению"
    HANDLING(15), // 15; Обращения Уведомление по обращению
    TAXES_AND_PENALTIES(16), // 16 Мои налоги и штрафы
    REPORT_ACCEPTED(17),  // 17 Отчет сдан Уведомление по отчетности
    REPORT_REJECTED(18), // 18 Отчет не принят Уведомление по отчетности
    SALE_POINT_BOOKING(19), // Бронь стола
    REPORT_LETTER(20), // Письма от госорганов Уведомление по отчетности
    AUTH_ACCESS(21), // 21 Подтверждение входа с нового устройства
    UNIVERSAL(22), // 22 Универсальный тип уведомлений Универсальный тип уведомлений
    SALE_POINT_ORDER(23), // Заказ на доставку
    REPORT_ENCRYPTED(24), // 24 Получен ответ Уведомление по зашифрованной отчетности
    INSTRUCTION(25), // Инструкция
    REPORT_ZERO(26), // Нулевая отчетность
    WEBINAR(28),// Новый вебинар
    PROBLEM_EMPLOYEE(29), // Сотрудники с проблемами
    NEW_INCOMING_CALL(118), // Уведомление о входящем вызове
    NEW_VIDEO_CONFERENCE_CALL(116), // Уведомление о входящем вызове от совещания
    MISSED_CALL(32), // Пропущенный вызов
    SERVICE_CALL(33), // Системное уведомение телефонии
    OFD(35), // Кассы ОФД
    ORDER_I_AM_HERE(36), // Новый наряд\заказ "Я тут",
    DELIVERY_REQUEST_I_AM_HERE(37), // изменения в заявки на доставку "Я тут",
    WAYBILL_I_AM_HERE(38), // изменения в путевом листе "Я тут",
    SALE(67), // Новый заказ
    MOTIVATION(39), // Поощрения и взыскания
    OPEN_BUFFER_DISK(40), // Открыть буфер диска
    TENDER_CHANGE(44), // Документация (по торгам) изменена
    TENDER_RESULT(45), // Итоги торгов
    SABYGET_CHAT_MESSAGE(48), // Уведомление по сообщению в чате sabyget
    REVIEW(49), // Уведомление по отзывам
    SABYGET_REVIEW(114), // Уведомление по ответам на отзывы/снятия отзыва с публикации
    VIOLATION(56), // Нарушения
    ACTIVITY(57), //Мероприятия
    MEETING_RESULT(58),
    SALE_POINT_REGISTER_FOR_ME(59), // Запись в салон красоты (мастеру)
    EVENT_CANCELED(61), // Событие отменено
    CONTRACTOR_EDO_INVITATION(64), // Приглашения контрагентов к документообороту
    CONSULTATION(65), // Консультации
    REPORT_NEED_TO_PASS(66), // Надо сдать (отчетность)
    MOTION_DETECTED(68), // обнаружено движение
    LOSS_CONNECTION(69), // потеря связи
    REPORT_VAT_RECONCILIATION(71), // сверка НДС
    SALE_POINT_REGISTER(72), // Запись в салон красоты (администратору)
    SALE_POINT_BIND_QR_CODE(482), // Привязка QR-кода к точке продаж
    TRADING_FLOOR(73), // Торговые площадки
    FUND(74), // Фонды
    SECURITY(75), // Безопасность
    PLAN_VACATION(76), // Плановые отпуска
    SCHEDULE_WORK_SHIFT(81), // График смен
    MY_WORK_SHIFT(82), // Моя смена
    CANCELLING_WORK_SHIFT(83), // Отмена смены
    TRANSFERRED_WORK_SHIFT(84), // Перенос смены
    ADDING_WORK_SHIFT(85), // Добавление смены
    ONLINE_FORM(86), // Онлайн формы
    DOCUMENT_ANNULATION_REQUEST(87), // Аннулирование документов
    ANNULATED_DOCUMENT(88), // Документы аннулированы
    NOT_ANNULATED_DOCUMENT(89), // В аннулировании документов отказано
    SABYGET_ON_DELIVERY(90), //Заказ доставляется
    SIGNATURE_REQUEST(92), // Заявка на ЭП
    SIGNATURE_COPY(137), // Копирование ключей
    CRYPTO_OPERATION(93), // Выполнение криптооперации
    SABYGET_NEWS(94), // Новость Saby Get
    DELETED_BY_CONTRACTOR_DOCUMENT(96), // Документ удален контрагентом
    SABYGET_CREATE_REVIEW(97), // Предложение оставить отзыв
    SABYGET_REFERRAL(98), //Приглашение присоединиться к реферальной системе компании
    SMS_INFORMING(99), // Уведомление об смс информировании
    WAITER_MESSAGE(101), // Сообщения официанта
    REPORT_BUDGET_RECONCILIATION(102), // Сверка с бюджетом
    TIPS_NEW(103), // Новые чаевые
    TIPS_ON(104), // Предложение подключиться к системе чаевых
    REPORT_AUTO_LOADING(105), // Загружен отчет с email
    MEMBERSHIP_AND_CERTIFICATE(108), // Уведомление о сертификате или абонементе
    SABYGET_INCOMING_CALL(109), // Уведомление о входящем звонке в Saby Get
    PAYMENT(110), // Уведомление об оплате
    EARLY_SALARY_ADVANCE(111), // Досрочный аванс
    LICENSE_EXPIRES(112), // заканчиваются лицензии
    LICENSE_ACCRUAL(113), // начисления по лицензиям
    INTEGRATION_ERRORS(117), // ошибки интеграции
    OPERATORS_CONSULTATION_MESSAGE(119), // Уведомления по сообщениям из консультаций только для оператора
    OPERATORS_RATE(313), // Оценка работы оператора, только для оператора
    SABYGET_PURCHASE(121), // Данные о покупке
    MARKETPLACES(123), // Маркетплейсы
    SABYGET_PURCHASE_REFUND(124), // Данные о возвратах покупок
    LEAD_NOTIFICATION(125), // Лиды
    WAITER_SALE_PAID(126), // Заказ оплачен
    WAITER_DISH_COOKED(127), // Блюдо приготовлено
    WAITER_CALL_BUTTONS(128), // Кнопки вызова официанта
    CLIENT_CALL(130), // Позвонить клиенту
    CLIENT_WRITE(131), // Написать клиенту
    CLIENT_MEET(132), // Встретиться с клиентом
    CLIENT_EVENT(133), // Событие с клиентом
    WAITER_DRAFT_SALE(135), // Черновик заказа из SabyGet
    KNOWLEDGE(136), // Уведомления по базе знаний
    LICENSE_CONNECTED(138), // Подключена лицензия
    TRIGGER(167), // Уведомления по триггерам
    WAITER_CALL_TO_KITCHEN(323), // Вызов официанта на кухню
    ACCOUNTING_NEED_TO_EXECUTE(355), // Уведомления по бухгалтерским событиям "Надо выполнить"

    // Служебные типы
    DIGEST(47), // уведомление-сводка
    SETTINGS_CHANGED(2048), // уведомление об изменении настроек
    CLEAR_NOTIFICATION_CACHE(3072); // уведомление об очистке локального кеша

    companion object {

        @JvmStatic
        fun fromValue(value: Int): NotificationType? {
            for (s in values()) {
                if (s.value == value) {
                    return s
                }
            }
            return null
        }
    }
}