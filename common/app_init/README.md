# Модуль механизмов асинхронной инициализации МП.
| Ответственность | Ответственные |
|-----------------|---------------|
| Участок работ | [Авторизация, регистрация и работа с пользователями Android](https://online.sbis.ru/area/00b5b020-c40d-422d-86b3-bf601b0800ce) |  


## Описание
Модуль поставляет базовые механизмы для асинхронной инициализации МП. Под асинхронной инициализацией понимается вынос инициализации платформы и опционально плагинной системы в фон, чтобы UI мог рисоваться. Определяется прикладниками и ответственными.


## Руководство по подключению и инициализации
Для начала работы с модулем необходимо подключение данных хранилищ в проекте.

| Репозиторий | модуль |
| ------ | ------ |
| https://git.sbis.ru/mobileworkspace/android-utils.git | common |
| https://git.sbis.ru/mobileworkspace/android-utils.git | mvvm |
| https://git.sbis.ru/mobileworkspace/android-utils.git | event_bus |
| https://git.sbis.ru/mobileworkspace/android-utils.git | common |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_progress |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_utils |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_stubview |
| https://git.sbis.ru/mobileworkspace/android-design.git | design_buttons |

Если ваше МП унаследовано от базового app-delegate [BaseSbisApplication](https://git.sbis.ru/mobileworkspace/android-utils/-/blob/878f6149e504e70621fce707d0360b3fbacb1950/app_systems/base_app_components/src/main/java/ru/tensor/sbis/base_app_components/BaseSbisApplication.kt), то необходимо:
-  пометить все ваши активности [AppInitAware](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/blob/9273026d7cd50979306fb3f6da89206c9592b8d2/toolbox-decl/src/main/java/ru/tensor/sbis/toolbox_decl/init/AppInitAware.kt)-интерфейсом.
-  в самой активности инициализировать [RestoreManager](app_init/src/main/java/ru/tensor/sbis/app_init/RestoreManager.kt).
-  передать в конструкторе экземпляр активности и id контейнтера. При инициализации и восстановлении в него будет встроен [LaunchFragment](app_init/src/main/java/ru/tensor/sbis/app_init/LaunchFragment.kt).
-  переопределить метод свойство из [AppInitAware](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/blob/9273026d7cd50979306fb3f6da89206c9592b8d2/toolbox-decl/src/main/java/ru/tensor/sbis/toolbox_decl/init/AppInitAware.kt) appNotInitializedYet. В нем совершить вызов restoreManager::subscribe
-  в методе onCreate перед вызовом super::onCreate вызывать установку фабрики инициализации фрагментов.
-  реализовывать дальнейшую "логику" активности в методе [appInitializationSucceeded](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/blob/9273026d7cd50979306fb3f6da89206c9592b8d2/toolbox-decl/src/main/java/ru/tensor/sbis/toolbox_decl/init/AppInitAware.kt#L15). С подробным поведением можно ознакомиться в самом интерфейсе.
-  может потребоваться пересмотр работы с корневой транзацией и backstack, проводимой при добавлении контента без сохраненного состояния.

На выходе получится что-то подобное:
```
    
private val restoreManager = RestoreManager()

init {
    restoreManager.init(this, containerId)
}

override val appNotInitializedYet: (() -> Unit) = {
    restoreManager.subscribe()
}

override fun onCreate(savedInstanceState: Bundle?) {
    restoreManager.setFactory()
    super.onCreate(savedInstanceState)
}

override fun appInitializationSucceeded(savedInstanceState: Bundle?) {
    if (savedInstanceState == null) {
        val fragment = HostFragment()
        supportFragmentManager.beginTransaction()
            .run {
                val tag = HostFragment::class.java.canonicalName
                replace(containerId, fragment, tag)
                addToBackStack(tag)
                commitAllowingStateLoss()
            }
    }
}

```
Если ваше МП не заинтересованно в асинхронной инициализации или **не наследуется от базового app-delegate**, то необходимо:
- расширить свой app-delegate InitToolsHolder.
- добавить колбек-заглушку в вашу реализацию app-delegat'а, чтобы уже помеченные интерфейсом активности не отвалились.
- После инициализации платформы и плагинной системы добавить отправку события об успешной инициализации и установку нового значения.

```
class MyAppDelegate: Application(), InitToolsHolder by InitToolsDelegate() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(InitCallbackStub()) <---

        PlatformInitializer.init(this)
        PluginSystem.initialize(...)
        setInitState(Initialized) <---
    }
}
```
Если ваше МП заинтересованно в асинхронной инициализации и по каким-то причинам еще не унаследовано от базового app-delegate, то необходимо провести самостоятельное подключение:
- унаследовать ваш делегат от [InitToolsHolder](https://git.sbis.ru/mobileworkspace/android-serviceapi/-/blob/9273026d7cd50979306fb3f6da89206c9592b8d2/toolbox-decl/src/main/java/ru/tensor/sbis/toolbox_decl/init/InitToolsHolder.kt)
- поддержать асинхронную инициализацию с вызовом соответствующего метода из интерфейса по завершению необходимых этапов. Для примера смотри базовый делегат [BaseSbisApplication](https://git.sbis.ru/mobileworkspace/android-utils/-/blob/878f6149e504e70621fce707d0360b3fbacb1950/app_systems/base_app_components/src/main/java/ru/tensor/sbis/base_app_components/BaseSbisApplication.kt).
```
class AppDelegate : Application(), InitToolsHolder by InitToolsDelegate()
```
Примеры в активностях:
- [Корневая активность МП Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator/-/blob/726b7133336b7ea026195418f58af5fc85376ffd/app/src/main/java/ru/tensor/sbis/droid/saby/MainActivity.kt)
- [Активность преветственного экрана](https://git.sbis.ru/mobileworkspace/android-utils/-/blob/878f6149e504e70621fce707d0360b3fbacb1950/onboarding/src/main/java/ru/tensor/sbis/onboarding/ui/OnboardingActivity.kt)


## Темизация
В случае ошибки инициализации может быть показан экран с ошибкой. Описание его атрибутов можно найти в [ресурсах модуля](app_init/src/main/res/values/attr.xml)