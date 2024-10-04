#### Компонент плеера для проигрывания аудио и видео сообщений

|Модуль|Ответственные|
|-------|-------------|
|[video_player_view]|[Жуков Дмитрий](https://online.sbis.ru/person/6148dfb3-2e78-4328-89f3-6cff9625ceae)|

## Дополнительная информация
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)

# Подключение.

Для добавления модуля video_player_view в проект необходимо выполнить шаги ниже:

## 1. Зависимости
В файле `settings.gradle` проекта должны быть а так же все модули, от которых он зависит:

`include ':controller'`

`include ':design'`
`project(':design').projectDir= new File(settingsDir, 'design/design')`

`include':common'`
`project(':common').projectDir = new File(settingsDir, 'common/common')`

...

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)