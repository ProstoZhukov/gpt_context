# Модуль "Красная Кнопка"

Модуль содержит комплекс компонентов для работы с микросервисом красной кнопки


## Дополнительная информация

- [ответственный Быков Дмитрий Юрьевич](https://online.sbis.ru/person/1aee1e1d-892b-480e-8131-b6386b5b7bc0)
- [ссылка на проект](https://online.sbis.ru/opendoc.html?guid=21c3f62e-fce9-48ea-9d82-23a9fdfad4a0)


# Подключение

Для добавления модуля красной кнопки в проект необходимо выполнить шаги ниже:

1. Зависимости
В файл settings.gradle проекта должны быть подключены следующие модули:

`include ':controller'`

`include ':common'`
`project(':common').projectDir= new File(settingsDir, 'common/sbis-common')`

`include':design'`
`project(':design').projectDir = new File(settingsDir, 'design/design')`

`include ':settings-screen-decl'`
`project(':settings-screen-decl').projectDir = new File(settingsDir, 'auth/settings-screen-decl')`

`include ':modalwindows'`
`project(':modalwindows').projectDir = new File(settingsDir, 'common/modalwindows')`

`include ':base_components'`
`project(':base_components').projectDir = new File(settingsDir, 'common/base_components')`

`include ':mvp'`
`project(':mvp').projectDir = new File(settingsDir, 'common/mvp')`

2. Наблюдение за событиями контроллера.
Требуется подписаться на события от контроллера посредством вызова метода RedButtonFeature.subscribeOnRedButtonControllerCallback().
Подписку требуется осуществлять в методе onCreate() у класса-наследника от Application.
Пример подписки:
`DaggerRedButtonComponent.builder()
            .commonSingletonComponent(commonSingletonComponent)
            .build()
            .redButtonFeature
            .subscribeOnRedButtonControllerCallback()`

3. Открытие заглушки из основной активности.
На втором шаге вы уже подписались на события от контроллера,но модуль не знает когда и где требуется
отображать заглушку о необходимости перезагрузить приложение. Поэтому требутеся вызвать из основной
активности (MainActivity, RootActivity и т.д.) метод RedButtonFeature.openStubIfNeeded.
Метод принимает 2 аргумента: контекст и лямбда, которая будет выполнена, если не требутся показать заглушку.

Если приложение умеет показывать онбординга через модуль onboarding, то в лямбду нужно положить метод открытия онбординга.
Это позволит открыть онбординг только если не требуется показать заглушку.
Обычно достаточно вызвать RedButtonFeature.openStubIfNeeded в методе onCreate у активности.

Пример вызова из MainActivity:
`RedButtonFeature.openStubIfNeeded(this, () -> { /** Я буду вызвана, если заглушка не требутся */ })`

Если приложение умеет обрабатывать диплинки через модуль deeplink, то требуется у активити в методе
onNewIntent вызывать метод RedButtonFeature.openStubIfNeeded с лямбдой, содержащей вызов
RedButtonFeature.isLockedUi() и если метод в Observable вернёт false, то можно вызывать метод обработки диплинки.

Пример вызова из MainActivity:
`RedButtonFeature.openStubIfNeeded(this, () -> {
    RedButtonFeature.isLockedUi().subscribe(locked -> {
      if (!locked) {
          //обработка диплинка
      }
    })
})`

4. Добавление преференса на экран настроек.
Требуется добавить реализацию интерфейса ru.tensor.sbis.settings_screen.content.CreateContent.
При добавлении всех пунктов настроек просто нужно добавить элемент RedButtonItem(context: Context) в список создаваемых элементов

### Описание публичного API

1. Подписаться на событие контроллера о том, что нужно показать заглушку.
fun subscribeOnRedButtonControllerCallback()

2. Проверить нужно ли показать заглушку.
@return [Single], излучающий true, если нужно, иначе false.
fun isLockedUi(): Single<Boolean>

3. Проверка не нажата ли "Красная кнопка"
@return [Single] излучающий true, если кнопка не нажата, иначе false
fun isRedButtonActivated(): Single<Boolean>

4. Открыть заглушку, если требуется иначе, запустить делегат.
@param activity родительская активность.
@param noStubHandler делегат для обработки случая если заглушка не требуется.
fun openStubIfNeeded(activity: ComponentActivity, noStubHandler: () -> Unit)

## Использование в приложениях
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
