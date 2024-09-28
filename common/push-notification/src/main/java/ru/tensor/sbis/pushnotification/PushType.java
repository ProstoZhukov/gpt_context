package ru.tensor.sbis.pushnotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.info_decl.model.NotificationType;

/**
 * Тип push-уведомлений для регистрации контроллеров-обработчиков.
 * Является оберткой над реальным типом уведомления,
 * за исключением служебных внутренних типов {@link #UNDEFINED} и {@link #BUG}
 *
 * @author am.boldinov
 */
public enum PushType {

    WAITER_MESSAGE(NotificationType.WAITER_MESSAGE),
    NEW_MESSAGE(NotificationType.MESSAGE),
    NEW_CHAT_MESSAGE(NotificationType.CHAT_MESSAGE),
    NEW_INCOMING_CALL(NotificationType.NEW_INCOMING_CALL),
    NEW_VIDEO_CONFERENCE_CALL(NotificationType.NEW_VIDEO_CONFERENCE_CALL),
    NEWS(NotificationType.NEWS),
    TASK(NotificationType.TASK),
    DOCUMENT(NotificationType.DOCUMENT),
    DOCUMENT_REFUSED(NotificationType.REFUSE_DOCUMENT),
    DOCUMENT_APPROVED(NotificationType.APPROVED_DOCUMENT),
    DOCUMENT_ANNULATION_REQUESTED(NotificationType.DOCUMENT_ANNULATION_REQUEST),
    DOCUMENT_ANNULATION_ACCEPTED(NotificationType.ANNULATED_DOCUMENT),
    DOCUMENT_ANNULATION_REJECTED(NotificationType.NOT_ANNULATED_DOCUMENT),
    DELETED_BY_CONTRACTOR_DOCUMENT(NotificationType.DELETED_BY_CONTRACTOR_DOCUMENT),
    MEETING(NotificationType.MEETING),
    MEETING_RESULT(NotificationType.MEETING_RESULT),
    ACTIVITY(NotificationType.ACTIVITY),
    WEBINAR(NotificationType.WEBINAR),
    EVENT_CANCELED(NotificationType.EVENT_CANCELED),
    REQUIREMENT(NotificationType.REQUIREMENT),
    REPORT_ACCEPTED(NotificationType.REPORT_ACCEPTED),
    REPORT_REJECTED(NotificationType.REPORT_REJECTED),
    REPORT_LETTER(NotificationType.REPORT_LETTER),
    REPORT_ENCRYPTED(NotificationType.REPORT_ENCRYPTED),
    REPORT_ZERO(NotificationType.REPORT_ZERO),
    REPORT_NEED_TO_PASS(NotificationType.REPORT_NEED_TO_PASS),
    REPORT_VAT_RECONCILIATION(NotificationType.REPORT_VAT_RECONCILIATION),
    REPORT_BUDGET_RECONCILIATION(NotificationType.REPORT_BUDGET_RECONCILIATION),
    REPORT_AUTO_LOADING(NotificationType.REPORT_AUTO_LOADING),
    TENDER(NotificationType.TENDER),
    TENDER_CHANGE(NotificationType.TENDER_CHANGE),
    TENDER_RESULT(NotificationType.TENDER_RESULT),
    TRADING_FLOOR(NotificationType.TRADING_FLOOR),
    CONTRACTOR(NotificationType.CONTRACTROR),
    CONTRACTOR_EDO_INVITATION(NotificationType.CONTRACTOR_EDO_INVITATION),
    SIGNATURE_REQUEST(NotificationType.SIGNATURE_REQUEST),
    SIGNATURE_COPY(NotificationType.SIGNATURE_COPY),
    CRYPTO_OPERATION(NotificationType.CRYPTO_OPERATION),
    MOTION_DETECTED(NotificationType.MOTION_DETECTED),
    LOSS_CONNECTION(NotificationType.LOSS_CONNECTION),
    HANDLING(NotificationType.HANDLING),
    CONSULTATION(NotificationType.CONSULTATION),
    ONLINE_FORM(NotificationType.ONLINE_FORM),
    UNIVERSAL(NotificationType.UNIVERSAL),
    SMS_INFORMING(NotificationType.SMS_INFORMING),
    AUTH_ACCESS(NotificationType.AUTH_ACCESS),
    PROBLEM_EMPLOYEE(NotificationType.PROBLEM_EMPLOYEE),
    ORDER_I_AM_HERE(NotificationType.ORDER_I_AM_HERE),
    DELIVERY_REQUEST_I_AM_HERE(NotificationType.DELIVERY_REQUEST_I_AM_HERE),
    WAYBILL_I_AM_HERE(NotificationType.WAYBILL_I_AM_HERE),
    SALE(NotificationType.SALE),
    OFD(NotificationType.OFD),
    PAYMENT(NotificationType.PAYMENT),
    EARLY_SALARY_ADVANCE(NotificationType.EARLY_SALARY_ADVANCE),
    LICENSE_EXPIRES(NotificationType.LICENSE_EXPIRES),
    LICENSE_ACCRUAL(NotificationType.LICENSE_ACCRUAL),
    LICENSE_CONNECTED(NotificationType.LICENSE_CONNECTED),
    INTEGRATION_ERRORS(NotificationType.INTEGRATION_ERRORS),
    MARKETPLACES(NotificationType.MARKETPLACES),
    LEAD_NOTIFICATION(NotificationType.LEAD_NOTIFICATION),
    TAXES_AND_PENALTIES(NotificationType.TAXES_AND_PENALTIES),
    SALE_POINT_ORDER(NotificationType.SALE_POINT_ORDER),
    SALE_POINT_BOOKING(NotificationType.SALE_POINT_BOOKING),
    SALE_POINT_REGISTER_FOR_ME(NotificationType.SALE_POINT_REGISTER_FOR_ME),
    SALE_POINT_REGISTER(NotificationType.SALE_POINT_REGISTER),
    SALE_POINT_BIND_QR_CODE(NotificationType.SALE_POINT_BIND_QR_CODE),
    ADDING_WORK_SHIFT(NotificationType.ADDING_WORK_SHIFT),
    CANCELLING_WORK_SHIFT(NotificationType.CANCELLING_WORK_SHIFT),
    MY_WORK_SHIFT(NotificationType.MY_WORK_SHIFT),
    SCHEDULE_WORK_SHIFT(NotificationType.SCHEDULE_WORK_SHIFT),
    TRANSFERRED_WORK_SHIFT(NotificationType.TRANSFERRED_WORK_SHIFT),
    OPEN_BUFFER_DISK(NotificationType.OPEN_BUFFER_DISK),
    TELEPHONY_MISSED_CALL(NotificationType.MISSED_CALL),
    TELEPHONY_SERVICE_CALL(NotificationType.SERVICE_CALL),
    MOTIVATION(NotificationType.MOTIVATION),
    FUND(NotificationType.FUND),
    SECURITY(NotificationType.SECURITY),
    VIOLATION(NotificationType.VIOLATION),
    INSTRUCTION(NotificationType.INSTRUCTION),
    CALENDAR(NotificationType.CALENDAR),
    PLAN_VACATION(NotificationType.PLAN_VACATION),
    DIGEST(NotificationType.DIGEST),
    SETTINGS_CHANGED(NotificationType.SETTINGS_CHANGED),
    CLEAR_NOTIFICATION_CACHE(NotificationType.CLEAR_NOTIFICATION_CACHE),
    REVIEW(NotificationType.REVIEW),
    SABYGET_CHAT_MESSAGE(NotificationType.SABYGET_CHAT_MESSAGE),
    SABYGET_NEWS(NotificationType.SABYGET_NEWS),
    SABYGET_REVIEW(NotificationType.SABYGET_REVIEW),
    SABYGET_CREATE_REVIEW(NotificationType.SABYGET_CREATE_REVIEW),
    SABYGET_REFERRAL(NotificationType.SABYGET_REFERRAL),
    SABYGET_ON_DELIVERY(NotificationType.SABYGET_ON_DELIVERY),
    SABYGET_INCOMING_CALL(NotificationType.SABYGET_INCOMING_CALL),
    SABYGET_PURCHASE(NotificationType.SABYGET_PURCHASE),
    SABYGET_PURCHASE_REFUND(NotificationType.SABYGET_PURCHASE_REFUND),
    MEMBERSHIP_AND_CERTIFICATE(NotificationType.MEMBERSHIP_AND_CERTIFICATE),
    TIPS_NEW(NotificationType.TIPS_NEW),
    TIPS_ON(NotificationType.TIPS_ON),
    CLIENT_CALL(NotificationType.CLIENT_CALL),
    CLIENT_WRITE(NotificationType.CLIENT_WRITE),
    CLIENT_MEET(NotificationType.CLIENT_MEET),
    CLIENT_EVENT(NotificationType.CLIENT_EVENT),
    KNOWLEDGE(NotificationType.KNOWLEDGE),
    OPERATORS_CONSULTATION_MESSAGE(NotificationType.OPERATORS_CONSULTATION_MESSAGE),
    OPERATORS_RATE(NotificationType.OPERATORS_RATE),
    TRIGGER(NotificationType.TRIGGER),
    ACCOUNTING_NEED_TO_EXECUTE(NotificationType.ACCOUNTING_NEED_TO_EXECUTE),
    WAITER_SALE_PAID(NotificationType.WAITER_SALE_PAID),
    WAITER_DISH_COOKED(NotificationType.WAITER_DISH_COOKED),
    WAITER_CALL_BUTTONS(NotificationType.WAITER_CALL_BUTTONS),
    WAITER_DRAFT_SALE(NotificationType.WAITER_DRAFT_SALE),
    WAITER_CALL_TO_KITCHEN(NotificationType.WAITER_CALL_TO_KITCHEN),

    // Служебные/внутренние типы пушей
    UNDEFINED(Integer.MIN_VALUE), // любой неподдерживаемый тип пушей
    BUG(Integer.MAX_VALUE); // пуш об ошибке для истории уведомлений

    private final int intValue;

    /**
     * @param value числовое значение, соответствующее типу
     */
    PushType(int value) {
        this.intValue = value;
    }

    /**
     * @param notificationType реальный тип уведомления
     */
    PushType(NotificationType notificationType) {
        this.intValue = notificationType.getValue();
    }

    /**
     * Преобразование типа пуша к реальному типу уведомления
     *
     * @return реальный тип уведомления, по умолчанию {@link NotificationType#UNKNOWN}
     */
    @NonNull
    public NotificationType toNotificationType() {
        final NotificationType result = NotificationType.fromValue(intValue);
        return result != null ? result : NotificationType.UNKNOWN;
    }

    /**
     * @return числовое значение типа строкой
     */
    public String getValue() {
        return String.valueOf(intValue);
    }

    /**
     * @return числовое значение типа
     */
    public int getIntValue() {
        return intValue;
    }

    /**
     * Получение типа пуш уведомления по строковому значению
     *
     * @param value числовое значение типа строкой
     * @return соответствующий тип пуш уведомления, по умолчанию {@link #UNDEFINED}
     */
    @NonNull
    public static PushType fromValue(@Nullable String value) {
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return PushType.UNDEFINED;
        }
        final PushType[] values = values();
        for (PushType type : values) {
            if (type.intValue == intValue) {
                return type;
            }
        }
        return UNDEFINED;
    }

    /**
     * Получение типа пуш уведомления по реальному типу уведомления
     *
     * @param notificationType реальный тип уведомления
     * @return соответствующий тип пуш уведомления, по умолчанию {@link #UNDEFINED}
     */
    @NonNull
    public static PushType fromValue(@NonNull NotificationType notificationType) {
        final PushType[] values = values();
        for (PushType type : values) {
            if (notificationType.getValue() == type.intValue) {
                return type;
            }
        }
        return UNDEFINED;
    }
}
