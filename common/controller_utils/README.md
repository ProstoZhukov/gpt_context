# Общие утилиты для работы с контроллером

Модуль содержит набор утилит для работы с контроллером.

# Подключение

Для добавления модуля в проект необходимо выполнить шаги ниже:

1. Зависимости
В файл settings.gradle проекта должны быть подключены следующие модули:

`include ':controller'`

`include':network_native'`
`project(':network_native').projectDir = new File(settingsDir, 'common/network_native')`

## Использование в приложениях
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Коммуникутор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Сбис на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Витрина](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [Saby AppMarket](https://git.sbis.ru/mobileworkspace/apps/droid/appmarket)
- [СМС](https://git.sbis.ru/mobileworkspace/apps/droid/sms)
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter)
- [Экран повара](https://git.sbis.ru/mobileworkspace/apps/droid/cookscreen)
- [Розница](https://git.sbis.ru/mobileworkspace/apps/droid/retail)
