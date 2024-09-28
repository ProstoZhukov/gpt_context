#### WIP Общие реализации для записи аудио и видео сообщений в панели ввода сообщений

|Модуль|Ответственные|
|------|-------------|
|[design_message_panel_recorder_common]|[Чекурда Владимир](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877)|

## Дополнительная информация
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)

# Подключение.

Для добавления модуля design_message_panel_recorder_common в проект необходимо выполнить шаги ниже:

## 1. Зависимости
В файле `settings.gradle` проекта должны быть а так же все модули, от которых он зависит:

`include ':design'`
`project(':design').projectDir= new File(settingsDir, 'design/design')`

`include':design_utils'`
`project(':design_utils').projectDir = new File(settingsDir, 'design/design_utils')`

...

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)