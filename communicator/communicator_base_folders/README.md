#### Communicator folders

#### Описание

Содержит базовый функционал папок, который используется в реестре диалогов и реестре контактов

## Дополнительная информация

- [ответственный Крохалев Р.В](https://online.sbis.ru/person/420fa93c-fd36-4081-b974-038c28749265)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)
- [публичное API](https://online.sbis.ru/shared/disk/9668b93e-44dd-4fff-a9eb-d01c4eb5b4a9)


## 1. Зависимости
В файле `settings.gradle` проекта должны быть подключены все модули, от которых они зависят:

`include ':controller'`

`include ':common'`
`project(':common').projectDir= new File(settingsDir, 'common/sbis-common')`

`include':design_folders'`
`project(':design').projectDir = new File(settingsDir, 'design/design_folders')`

...

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)