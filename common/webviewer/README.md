#### WebViewer

## Дополнительная информация

- [ответственный: Бубенщиков С.В.](https://online.sbis.ru/person/1fb93b8c-350f-4785-8589-b0ff2edfbfa7)

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Розница](https://git.sbis.ru/mobileworkspace/apps/droid/retail)
- [Монитор для клиентов](https://git.sbis.ru/mobileworkspace/apps/droid/hallscreen)
- [Экран повара](https://git.sbis.ru/mobileworkspace/apps/droid/cookscreen)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [СБИС СМС](https://git.sbis.ru/mobileworkspace/apps/droid/sms)
- [Кладовщик](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Мобильные задачи](https://git.sbis.ru/mobileworkspace/apps/droid/saby-tasks)


#### Описание
Модуль предоставляет API для открытия документа через WebView. 
WebView в составе модуля содержит необходимые cookies, headers, certificates и др.настройки, 
которые необходимы для работы в инфраструктуре компании.

Важная особеность! 
Если в проекте используется модуль [Richtext](https://git.sbis.ru/mobileworkspace/android-utils/tree/rc-20.7100/richtext),
то к проекту обязательно дополнительно подключается WebViewer, 
т.к. Richtext требует его в качестве обязательной зависимости для своей работы 