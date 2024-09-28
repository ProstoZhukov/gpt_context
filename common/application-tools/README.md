# <Название модуля>
| Ответственность | Ответственные |
|-----------------|---------------|
| Разработка | [Быков Д.>](https://online.sbis.ru/person/1aee1e1d-892b-480e-8131-b6386b5b7bc0) |

## Документация
По большей части код выносился в модуль без проектов для устранения дублирования.
Для пакета [ru.tensor.sbis.application_tools.logsender](https://online.sbis.ru/shared/disk/7d462100-50c1-46a1-89c4-d688cdb60b83)

## Описание
Задача модуля - объединение отладочного кода приложений, инициализации отладочных утилит, типа LeakCanary, и скрыть это за единым интерфейсом. Так мы не только устраняем дублирования кода,
но и снимаем с ответственных за приложение разработчиков необходимость поддерживать в актуальном состоянии набор отладочных средств.  

## Руководство по подключению и инициализации
Какие зависимости требуются, как подключить модуль, и прочие действия, необходимые перед началом использования функционала.
| Репозиторий | модуль |
|-----------------|---------------|
|https://git.sbis.ru/mobileworkspace/android-design.git|design|
|https://git.sbis.ru/mobileworkspace/android-design.git|design_dialogs|
|https://git.sbis.ru/mobileworkspace/android-design.git|design_utils|
|https://git.sbis.ru/mobileworkspace/android-utils.git|common|
|https://git.sbis.ru/mobileworkspace/android-utils.git|base_components|
|https://git.sbis.ru/mobileworkspace/android-utils.git|fresco_utils|
|https://git.sbis.ru/mobileworkspace/android-utils.git|settings-screen-decl|
|https://git.sbis.ru/mobileworkspace/android-utils.git|toolbar|
|https://git.sbis.ru/mobileworkspace/android-utils.git|push-notification-utils|
|https://git.sbis.ru/mobileworkspace/android-utils.git|testing|
|https://git.sbis.ru/mobileworkspace/android-login.git|events_tracker|
|https://git.sbis.ru/mobileworkspace/android-login.git|settings-screen-decl|

## Описание публичного API
Необходимо создать объект DebugTools и инициализировать его в Application
```kotlin
class App : Application() {
private val debugTools = DebugTools(
    this,
    BuildConfig.DEBUG,
    BuildConfig.DEBUG_RX,
    BuildConfig.APPLICATION_ID,
    BuildConfig.VERSION_NAME,
    "",
    BuildConfig.VERSION_CODE,
    BuildConfig.CI_BUILD,
    BuildConfig.PUBLIC_BUILD
)

override fun onCreate() {
    super.onCreate()

    debugTools.init()
```

При запуске приложения для автотестов, необходимо вызвать метод DebugTools.updateIsAutoTestLaunch в главной активити приложения.
```kotlin
class MainActivity() : TrackingActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
    DebugTools.updateIsAutoTestLaunch(intent)
```