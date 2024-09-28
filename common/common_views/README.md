#### Common Views

|Модуль|Ответственные|Добавить|
|------|-------------|--------|
|[document]|[Никитин Семен](https://online.sbis.ru/person/312e2356-e6c6-4cfa-8db0-a5b1daae1736)
|[motivation]|[Шпаковский Кирилл](https://online.sbis.ru/person/8e1549d9-2e39-484f-a9c4-124c0c16e771)
|[notification]|[Болдинов Алексей](https://online.sbis.ru/person/24f28dc0-4a33-4cb9-9c87-8be072ea0e0c)
|[sbisview]|[Бубенщиков Сергей](https://online.sbis.ru/person/1fb93b8c-350f-4785-8589-b0ff2edfbfa7)
|[scaletype]|[Никитин Семен](https://online.sbis.ru/person/312e2356-e6c6-4cfa-8db0-a5b1daae1736)

#### Описание
Модуль "Views утилиты для работы со вложениями" (Legacy - вынесено из модуля Common)

#### Подключение

Для добавления модуля в проект необходимо выполнить шаги ниже:

1. Зависимости
В файл settings.gradle проекта должны быть подключены следующие модули:

`include ':common'`  
`project(':common').projectDir= new File(settingsDir, 'common/sbis-common')`    

`include':design'`
`project(':design').projectDir = new File(settingsDir, 'design/design')`

#### Использование в приложениях
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Коммуникутор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Сбис на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Витрина](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter)
- [Розница](https://git.sbis.ru/mobileworkspace/apps/droid/retail)