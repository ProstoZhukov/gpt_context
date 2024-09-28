#### Communicator base conversation

Модуль содержит реализацию интерфейсной части: 
1) Базовая реализация экрана переписки

## Дополнительная информация

- [ответственный Крохалев Р.В](https://online.sbis.ru/person/420fa93c-fd36-4081-b974-038c28749265)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/2198ca7a-02cc-4f4d-9e21-7a0714c5c4a7)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)
- [публичное API](https://online.sbis.ru/shared/disk/9668b93e-44dd-4fff-a9eb-d01c4eb5b4a9)
- Зависимости модуля описаны в [CommunicatorDependency] (сommunicator/src/main/java/ru/tensor/sbis/communicator/contract/CommunicatorDependency.kt)

#### Описание

Подмодуль с базовым функционалом, общим для Сбис и Сабигет. Напрямую извне не используется.

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

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)