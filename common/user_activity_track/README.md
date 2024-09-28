#### Компонент для регистрации активности пользователя.

|Класс|Ответственные|Добавить|
|-----|-------------|--------|
|[UserActivityService](src/main/java/ru/tensor/sbis/user_activity_track/service/UserActivityService.kt)|[Мартышенко К.В.](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|

##### Описание
[UserActivityService](src/main/java/ru/tensor/sbis/user_activity_track/service/UserActivityService.kt) является ключевым компонентом, через который осуществяется взаимодействие. Метод *startPeriodicallyRegisterActivity()*/*stopPeriodicallyRegisterActivity()* позволяют активировать/деактивировать регистрацию активности на конкретном экране (как правило в Activity#onStart() и Activity#onStop()).
*UserActivityService#reset()* нужен для сброса текущего состояния и остановки трекинга.

Для автоматической регистрации активности пользователя на уровне приложения предназначен [UserActivityTrackingWatcher](src/main/java/ru/tensor/sbis/user_activity_track/activity/UserActivityTrackingWatcher.kt): достаточно на нужные нам `Activity` повесить интерфейс [UserActivityTrackable](src/main/java/ru/tensor/sbis/user_activity_track/activity/UserActivityTrackable.kt).

##### Использование в приложениях
Используется во всех приложениях, так как подключен к sbis-common