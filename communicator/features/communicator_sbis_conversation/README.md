#### Communicator sbis conversation

#### Описание
Модуль содержит реализацию интерфейсной части:
1) Экран переписки
2) Экран информации о сообщении
3) Отображения меню выбора фильтра списка статусов прочитанности сообщения

## Дополнительная информация

- [ответственный Крохалев Р.В](https://online.sbis.ru/person/420fa93c-fd36-4081-b974-038c28749265)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/2198ca7a-02cc-4f4d-9e21-7a0714c5c4a7)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)
- [публичное API](https://online.sbis.ru/shared/disk/9668b93e-44dd-4fff-a9eb-d01c4eb5b4a9)
- Зависимости модуля описаны в [CommunicatorSbisConversationDependency] (сommunicator_sbis_conversation/src/main/java/ru/tensor/sbis/communicator/sbis_conversation/contract/CommunicatorCommonDependency.kt)

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
Для доступа к функционалу модуля извне следует использовать интерфейсы, указанные в `CommunicatorSbisConversationFeature`

Для доступа к реализации (`CommunicatorSbisConversationFeatureImpl`) интерфейсов модуля следует определить их как зависимости в вызывающем модуле, а на уровне приложения иметь экземпляр такой реализации.

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [СабиТаск](https://git.sbis.ru/mobileworkspace/apps/droid/saby-tasks)