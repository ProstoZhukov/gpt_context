#### Декларативный модуль реестра контактов

#### Описание
Декларативный модуль реестра контактов, содержащий в себе модели и интерфейсы, которые использованы в этом реестре.

## Дополнительная информация

- [ответственный Жуков Д.А](https://online.sbis.ru/person/6148dfb3-2e78-4328-89f3-6cff9625ceae)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)
- [публичное API](https://online.sbis.ru/shared/disk/9668b93e-44dd-4fff-a9eb-d01c4eb5b4a9)

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

`include':declaration'`
`project(':declaration').projectDir = new File(settingsDir, 'declaration/declaration')`

...

## 2. Использование.
Должен использоваться только в подмодулях коммуникатора

## Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)