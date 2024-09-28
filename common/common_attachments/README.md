#### Common Attachments

|Модуль|Ответственные|Добавить|
|------|-------------|--------|
[Никитин Семен](https://online.sbis.ru/person/312e2356-e6c6-4cfa-8db0-a5b1daae1736)

#### Описание
Модуль содержит набор классов и утилит для работы со вложениями (Legacy - вынесено из модуля Common)
Модуль может быть переиспользован в нескольких приложениях.

# Подключение

Для добавления модуля в проект необходимо выполнить шаги ниже:

1. Зависимости
В файл settings.gradle проекта должны быть подключены следующие модули:

`include ':common'`  
`project(':common').projectDir= new File(settingsDir, 'common/sbis-common')`    

`include':design'`
`project(':design').projectDir = new File(settingsDir, 'design/design')`

`include':media_selection_pane'`
`project(':media_selection_pane').projectDir = new File(settingsDir, 'common/sbis-common')`

`include':mvp'`
`project(':mvp').projectDir = new File(settingsDir, 'common/mvp')`

## Использование в приложениях
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Коммуникутор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Сбис на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Витрина](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
