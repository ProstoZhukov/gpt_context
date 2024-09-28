# Пуш уведомления

Модуль содержит набор классов, необходимых для настройки получения и отображения пуш уведомлений.

## Дополнительная информация

- [ответственный Болдинов А.М.](https://online.sbis.ru/person/24f28dc0-4a33-4cb9-9c87-8be072ea0e0c)
- [ссылка на тех. документацию](https://wi.sbis.ru/doc/notification_service/push-notifications/)

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [СМС](https://git.sbis.ru/mobileworkspace/apps/droid/sms)
- [Салон](https://git.sbis.ru/mobileworkspace/apps/droid/salon)
- [Витрина](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [На складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Задачи](https://git.sbis.ru/mobileworkspace/apps/droid/saby-tasks/)
- [Saby Get](https://git.sbis.ru/mobileworkspace/apps/droid/sabyget/)

## Темизация

Пуш уведомления SbisPushNotification поддерживают темизацию.
По умолчанию используется базовая тема `PushNotificationTheme`.

Для применения собственной стилизации необходимо добавить в тему приложения атрибут `pushNotificationTheme`.
В качестве значения атрибута нужно указать ссылку на перегруженную тему `PushNotificationTheme` из модуля `push-notification-utils`.

Доступные атрибуты:

`pushNotificationSmallIcon` - маленькая иконка в уведомлении
`pushNotificationColor` - акцентный цвет, который будет применяться стандартными шаблонами стилей
                          при придставлении уведомления (цвет иконки, также может использоваться
                          в качестве цвета заголовка, кнопок в пуше - в зависимости от версии android)

## Счётчик уведомлений в приложении

Ключевые компоненты:
[NotificationBadge](/push_notification/src/main/java/ru/tensor/sbis/pushnotification/util/counters/NotificationBadge.java),
[AppIconCounterUpdater](/push_notification/src/main/java/ru/tensor/sbis/pushnotification/util/counters/AppIconCounterUpdater.kt).

`NotificationBadge` - класс для установки значения счётчика (бейджа) на иконке приложения.
Поддерживает лаунчеры большинства популярных устройств:
- asus
- huawei
- htc
- oppo
- samsung
- sony
- lenovo (ZUK Mobile)
- xiaomi
- vivo

А также кастомные лаунчеры:
- adw
- apex
- nova

В `DefaultBadger` описаны лаунчеры по умолчанию.

Метод `applyCount(int)` распознает тип текущего используемого лаунчера и в соответствии с его api обновляет счётчик.

`AppIconCounterUpdater` - класс для обновления значения счётчика уведомлений у иконки приложения.
Позволяет увеличивать счётчик вызовом `incrementCounter()` либо сбросывать его вызовом `removeCounter()`.
Отслеживает состояние приложения (background/foreground) путем подписки на события от `AppLifecycleTracker`
При переходе приложения на передний план автоматически сбрасывает счетчик уведомлений, считая их прочитанными.
Также счетчик сбрасывается при разлогине пользователя - вызов `removeCounter()` из `PushServiceSubscriptionManager`.
Метод `incrementCounter()` вызывается из `PushCenter` при получении нового пуш уведомления в `handleMessage(Map<String, String>)`.

Для отображения и изменения счетчика на иконке приложения достаточно подключить плагин `PushNotificationPlugin`.
