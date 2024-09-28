#### Communicator share

#### Описание
Модуль содержит экран мульти-выбора диалогов и получателей для отправки shared файлов. Может использоваться там,
где доступна функция "поделиться" в новый или существующий диалог.

## Дополнительная информация

- [ответственный Чекурда В.В](https://online.sbis.ru/person/0fe3e077-6d50-431c-9353-f630fc789877)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)
- [публичное API](https://online.sbis.ru/shared/disk/9668b93e-44dd-4fff-a9eb-d01c4eb5b4a9)

## Зависимости
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

#### Использование
Для доступа к функционалу модуля извне следует использовать интерфейсы, указанные в `DialogSelectionFeature`

Для доступа к реализации (`DialogSelectionFeatureImpl`) интерфейсов модуля следует определить их как зависимости в вызывающем модуле, а на уровне приложения иметь экземпляр такой реализации.

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator) 