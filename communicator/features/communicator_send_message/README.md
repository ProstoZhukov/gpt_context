#### Механизм отправки сообщений в фоне

#### Описание
Модуль отправки сообщений в фоне, который содержит в себе реализацию интерфейса SendMessageManager
и его вспомогательных элементов.

## Дополнительная информация

- [ответственный Баранов Д.В.](https://online.sbis.ru/person/9ec1d410-7a2c-40f0-bf00-7d1db5d1c30f)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/256ffd2c-970b-44ac-9e65-a56386775520)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/fa32755a-bf24-49a4-b604-6c8d106386e7)
- Зависимости модуля описаны в [SendMessageDependency] (features/communicator_send_message/src/main/java/ru/tensor/sbis/communicator/send_message/contract/SendMessageDependency.kt)

# Подключение.

Для добавления модуля коммуникатор в проект необходимо выполнить шаги ниже:

## 1. Зависимости
В файле `settings.gradle` проекта должны быть подключены модули коммуникатора из 
файла `$communicator_dir/settings.gradle` а так же все модули, от которых они зависят:

``` groovy
ext.communicator_dir = 'communicator'
apply from: "$communicator_dir/settings.gradle"
```

`include ':controller'`

`include ':common'`
`project(':common').projectDir= new File(settingsDir, 'common/sbis-common')`

`include':design'`
`project(':design').projectDir = new File(settingsDir, 'design/design')`

...

## 2. Использование.
Для доступа к функционалу модуля извне следует использовать интерфейс `SendMessageManager`

Для доступа к реализации (`SendMessageManager`) интерфейса модуля следует определить их как зависимости в вызывающем модуле, а на уровне приложения иметь экземпляр такой реализации.

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)