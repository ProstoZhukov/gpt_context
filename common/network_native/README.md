#### Network
#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Розница](https://git.sbis.ru/mobileworkspace/apps/droid/retail)
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [Экран повара](https://git.sbis.ru/mobileworkspace/apps/droid/cookscreen)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [СБИС СМС](https://git.sbis.ru/mobileworkspace/apps/droid/sms)
- [Кладовщик](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)


#### Описание
Модуль содержит:
1. Нативную логику по работе с сетевыми запросами, построенную на библиотеке OkHttp.
Позволяет обращаться к API сервера, который реализует RPC стандарт без использования "контроллера"(платформа на C++).
2. Методы по обработке и установке необходимых cookies, headers и сертификатов.
3. Инструменты по работе с json-м, адаптеры и модели для обрабоки RecordSet-ов.(реализовано на библиотеке Gson)