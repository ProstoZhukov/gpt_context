#### Реестр контактов

#### Описание
Модуль содержит реализацию импортирования контактов с мобильного устройства

## Дополнительная информация

- [ответственный Жуков Д.А](https://online.sbis.ru/person/6148dfb3-2e78-4328-89f3-6cff9625ceae)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/26577907-852b-4c0a-92b2-c34f003a71ed)
- [ссылка на тех документацию](https://online.sbis.ru/shared/disk/df217e22-4927-4a1c-b74a-7a1c6d494b83)
- [публичное API](https://online.sbis.ru/shared/disk/9668b93e-44dd-4fff-a9eb-d01c4eb5b4a9)
- Зависимости модуля описаны в [ImportContactsDependency] (communicator_import_contacts/src/main/java/ru/tensor/sbis/communicator/communicator_import_contacts/contract/ImportContactsDependency.kt)

# Подключение.

Для добавления модуля импорта котактов в проект необходимо выполнить шаги ниже:

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

...
## 2. Указать название приложения, при подключении плагина

`ImportContactsPlugin.apply {
    customizationOptions.appName = "Saby Lite"
}`

## 3. Использование.
Для доступа к функционалу модуля извне следует использовать интерфейсы, указанные в `ImportContactsFeature`

## Использование в приложениях
- [SabyLite](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)