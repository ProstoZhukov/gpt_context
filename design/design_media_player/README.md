#### Компонент плеера для проигрывания аудио и видео сообщений

| Модуль|Ответственные|
|-------|-------------|
|[media_player]|[Жуков Дмитрий](https://online.sbis.ru/person/6148dfb3-2e78-4328-89f3-6cff9625ceae)

## Дополнительная информация
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)

# Подключение.

Для добавления модуля media_player в проект необходимо выполнить шаги ниже:

## 1. Зависимости
В файле `settings.gradle` проекта должны быть а так же все модули, от которых он зависит:

`include ':network_native'`
`project(':network_native').projectDir= new File(settingsDir, 'common/network_native')`

`include':mediaplayer'`
`project(':mediaplayer').projectDir = new File(settingsDir, 'common/mediaplayer')`

...

## 2. Использование.
Для доступа к функционалу модуля извне следует использовать интерфейс `MediaPlayerFeature`

Для доступа к реализации (`MediaPlayerFeatureImpl`) интерфейса модуля следует определить его как зависимости в вызывающем модуле, а на уровне приложения иметь экземпляр такой реализации.

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
