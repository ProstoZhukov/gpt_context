#### Компонент для регистрации активности пользователя.

|Класс|Ответственные|Добавить|
|-----|-------------|--------|
|[UserActivityService](src/main/java/ru/tensor/sbis/user_activity_track_watcher/service/DefaultUserActivityService.kt)|[Мартышенко К.В.](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|
|[UserActivityTrackingWatcher](src/main/java/ru/tensor/sbis/user_activity_track_watcher/activity/UserActivityTrackingWatcher.kt)|[Мартышенко К.В.](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|

##### Описание
[DefaultUserActivityService](src/main/java/ru/tensor/sbis/user_activity_track_watcher/service/DefaultUserActivityService.kt) является реализацией *UserActivityService*.

Для автоматической регистрации активности пользователя на уровне приложения предназначен [UserActivityTrackingWatcher](src/main/java/ru/tensor/sbis/user_activity_track_watcher/activity/UserActivityTrackingWatcher.kt): достаточно на нужные нам `Activity` повесить интерфейс [UserActivityTrackable](src/main/java/ru/tensor/sbis/user_activity_track/activity/UserActivityTrackable.kt).

##### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [СБИС на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [СБИС касса](https://git.sbis.ru/mobileworkspace/apps/droid/retail)