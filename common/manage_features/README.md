### Модуль "Проверка доступности функционала"

Модуль содержит методы для проверки включен ли функционал (фичи, возможности) для пользователя,
так же содержит фрагмент для вывода служебной информации (фрагмент должен быть виден только в дебаге)

Реализован в рамках проекта [*Управление доступностью нового функционала*](https://online.sbis.ru/opendoc.html?guid=d5154684-80c8-4806-b8f1-e794b11c5ad4)
[*ТЗ проекта*](https://online.sbis.ru/shared/disk/1d8de204-bc33-42ac-998e-cd953847c673)

## Дополнительная информация
- [ответственный Степанов Роман Андреевич](https://online.sbis.ru/person/1aee1e1d-892b-480e-8131-b6386b5b7bc0)
- [ссылка на проект](https://online.sbis.ru/opendoc.html?guid=d92efa28-60d5-4965-8428-f8a5dc41195e)


### Подключение

Для добавления модуля "Проверка доступности функционала" в проект необходимо выполнить шаги ниже:

1. Подключение зависимостей
В файл settings.gradle проекта должны быть подключены следующие модули:

`include ':controller'`

`include ':base_components'`
`project(':base_components').projectDir = new File(settingsDir, "common/base_components")`

`include ':common'`
`project(':common').projectDir= new File(settingsDir, 'common/sbis-common')`

`include':design'`
`project(':design').projectDir = new File(settingsDir, 'design/design')`

`include ':declaration'`
`project(':declaration').projectDir = new File(settingsDir, 'declaration/declaration')`

2. Подключение к Application
Класс `Application` приложения должен реализовать интерфейс `ManageFeaturesComponentHolder` подобным образом:
`override val manageFeaturesComponent: ManageFeaturesComponent by lazy {
    ManageFeaturesComponentInitializer(dependencies).init(commonSingletonComponent)
}`


### Описание публичного API

1. Получить основной фрагмент модуля
@return основной фрагмент модуля [ManageFeaturesFragment]
fun getManageFeatureFragment(): Fragment

2. Проверить доступна ли пользователю проверка доступности функционала по переданным идентификаторам пользователя и клиента
По умолчанию используется идентификатор функционала test3xfeat
@param userID идентификатор пользователя
@param clientID идентификатор клиента (компании)
@return [true] если проверка доступна, иначе [false]
fun isManageFeaturesEnabled(userID: Int, clientID: Int): Boolean

3. Проверить доступен ли пользователю функционал с переданным идентификатором
@param featureName идентификатор функционала
@param userID идентификатор пользователя
@param clientID идентификатор клиента (компании)
@return [true] если функционал доступен, иначе [false]
fun isManageFeaturesEnabled(featureName: String, userID: Int, clientID: Int): Boolean


### Использование в приложениях

- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)