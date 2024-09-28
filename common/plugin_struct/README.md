# Плагинная архитектура подключения прикладных модулей в приложения

## Какие проблемы решает?
_**Неудобства системы добавления прикладных модулей в приложения, построенной на holder-ах:**_
- Приходится вешать спец.интерфейсы (holder) на класс *Application* для каждого прикладного модуля, которому требуется обеспечить единственный экземпляр своего компонета;
- В каждом приложении для каждого прикладного модуля приходится вызывать явно инициализацию *dagger* графа (следствие из предыдущего пункта);
- Конфигурации прикладного модуля (подписка на пуши, deeplink и прочее) разбросана по разным местам на уровне приложения;
- В каждом приложении нужно реализовывать интерфейс зависимостей прикладного модуля *Dependency*, а это довольно много кода и в ряде случаев не все тривиально;

В итоге получается много однотипного кода на уровне приложения для встраивания прикладного модуля.

Предлагается всю логику по выполнению инициализации, настройки (подписке на события авторизации, пуши и прочее) унести в прикладной модуль. Тем самым мы обеспечим возможность с минимумом усилий встраивать прикладной модуль в любое приложение.

_**Преимущества организации структуры приложения на основе плагинного подхода:**_
- На уровне приложения не требуется "вешать" холдеры на *Application*;
- Все DI-компоненты могут быть легко скрыты внутри прикладных модулей, соответственно и их инициализация тоже;
- Не требуется в каждом приложении явно имплементировать *Dependency* для каждого модуля;
- Получаем четкий контракт модуля (в виде описания плагина), в котором видно: что он предоставляет наружу, от чего зависит (жестко или нет), какая возможность подстройки присутствует;
- Подход навязывает "ленивость" инициализации, а также на уровне описания пропагандирует опциональность зависимостей.

## Терминология

Раньше **фичей** мы называли публичный интерфейс модуля, но с точки зрения семантики это не совсем удобно. **Фича** - это скорее **минимально обособленный** функционал, то есть фактически любой прикладной модуль поставляет наружу больше одной фичи.
Например, NotificationActivityIntentProvider - это фича, предоставляющая интент для открытия экрана (Activity) уведомления. Или ProfileProvider - фича, помогающая получить объект профиля по UUID.

Сам же прикладной модуль будем воспринимать как **плагин**, который может поставлять любое количество обособленных **фич**. Для описания функционала прикладного модуля создаем класс с одноименным названием (например, ProfilePlugin), в рамках которого описываем:
- какие **фичи** поставляются наружу;
- что требуется извне (обязательные и опциональные зависимости);
- какие настройки можно задать у прикладного модуля для подстройки поведения под определенные нужды: как правило посредством выставления флагов, enum'ов и прочего (например, отключение обработки пуш-уведомлений);
- что происходит при инициализации;
- что происходит при конфигурировании.

Для удобства (а также из-за особенностей работы прикладных модулей) **Плагин** прикладного модуля существует в единственном экземпляре на приложение (то есть реализуется через **object** на котлине), что позволяет унести холдеры компонентов с уровня приложения в соответствующие плагины.

Особенностью подхода является предоставление наружу и получение из вне не объектов **Feature**, а провайдеров этих фич. Это позволит на уровне таких провайдеров спрятать проверку прав, а также сделать инициализацию фич максимально "ленивой".

## Применение
### Уровень модуля
Для интеграции модуля в плагинную архитектуру необходимо:
1. Отметить интерфейсом _Feature_ функционал, который необходимо поставлять наружу.
```kotlin
interface Feature
```
2. Для модуля создаем класс плагина на основе интерфейса _Plugin_
```kotlin
interface Plugin<C> {
    val api: Set<FeatureWrapper<out Feature>>
    val dependency: Dependency
    val customizationOptions: C
    fun initialize(application: Application) {}
    fun doAfterInitialize(application: Application) {}
}
```
- **api** - перечень публичного API предоставляемого модулем("фичи" предоставляемые модулем)
- **dependency** - зависимости, которые необходимо предоставить модулю для полноценной работы
- **customizationOptions** - объект содержащий, дополнительные настройки поведения модуля.
 Атрибут является опциональным. Указать Unit, если не требуется.
- **initialize** - этап инициализации модуля.
- **doAfterInitialize** - предназначен для выполнения доп.настроек модуля, оформления подписок на события и прочие действия,
которые должны осуществляться на старте модуля.
3. Расположение и именование.
- Класс плагина для модуля или компонента должен располагаться в корне пакета модуля\компонента.
- Имя класса плагина должно содержать уникальное именование относящееся к предметной области модуля и оканчиваться на **Plugin**.
Например для модуля с именем **Foo** создаем плагин **FooPlugin**.
- При интеграции view компонентов("вьюшек") в плагинную структуру приложения, если view не содержит в явном виде api и dependency,
необходимо создать "фантомный" интерфейс для плагина, который будет предоставляться API плагина.
При подключении view модуль в dependency укажет зависимость на данный "фантомный" интерфейс.
Это необходимо для формирования четкого контракта взаимодействия между модулями и компонентами.
При переходе на Jetpack Compose(https://developer.android.com/jetpack/compose) "фантомные" интерфейсы обретут смысловую нагрузку
и мы получим фабрику компонентов.

#### Примеры плагинов
##### Плагин без конфигурации и внешних зависимостей
```kotlin
object CommonUtilsPlugin : Plugin<Unit>, CommonSingletonComponentHolder {
    private lateinit var application: Application

    private val singletonComponent: CommonSingletonComponent by lazy {
        CommonSingletonComponentInitializer().init(application)
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommonSingletonComponent::class.java) { singletonComponent }
    )

    override val dependency: Dependency = Dependency.Builder().build()

    override val customizationOptions: Unit = Unit

    override fun initialize(application: Application) {
        this.application = application
    }

    override fun getCommonSingletonComponent(): CommonSingletonComponent {
        return singletonComponent
    }

}
```

Плагин наследуется от общего интерфейса **Plugin** и не предоставляет возможность конфигурации (указан generic параметр *Unit*). Является холдером **CommonSingletonComponent**, инициализация которого происходит по требованию. Зависимости у плагина отсутствуют, а во внешний мир поставляется сам компонент **CommonSingletonComponent**, для этого он помечен маркерным интерфейсом **Feature**. (Весь компонент отдается наружу - временное решение. Лучше гранулировано поставлять фичи, а сам компонент прятать внутри модуля.)
```kotlin
interface CommonSingletonComponent : Feature
```

На уровне прикладного модуля *common* практически ничего не меняется, кроме добавления файла плагина **CommonUtilsPlugin** и изменения функции получения **CommonSingletonComponent**, которую используют внутренние классы:
```kotlin
fun get(context: Context): CommonSingletonComponent {
    val application = context.applicationContext as Application

    return if(application is CommonSingletonComponentHolder) {
        application.commonSingletonComponent
    } else {
        CommonUtilsPlugin.commonSingletonComponent
    }
}
```
Условие временное, нужно для обеспечения работы модуля как в приложениях, построенных на "старом" подходе, так и в приложениях с плагинной архитектурой.

##### Плагин "со всем и сразу"
```kotlin
object AuthPlugin : Plugin<AuthPlugin.CustomizationOptions>, LoginSingletonComponentHolder {
    private lateinit var singletonComponent: LoginSingletonComponent

    private val sessionIdProvider: SessionIdProvider = object : SessionIdProvider {
        override fun getTokenId(): String? {
            return singletonComponent.loginInterface.token
        }
    }

    lateinit var commonSingletonComponent: FeatureProvider<CommonSingletonComponent>
    lateinit var routerInterface: FeatureProvider<RouterInterface.Provider>
    lateinit var mainActivityProvider: FeatureProvider<MainActivityProvider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ru.tensor.sbis.declaration.LoginInterface::class.java) { singletonComponent.loginInterface },
        FeatureWrapper(SessionIdProvider::class.java) { sessionIdProvider }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponent = it }
        .require(RouterInterface.Provider::class.java) { routerInterface = it }
        .require(MainActivityProvider::class.java) { mainActivityProvider = it }
        .build()

    override val customizationOptions: CustomizationOptions = CustomizationOptions

    override fun initialize(application: Application) {
        val dependency = object : AuthDependency {
            override fun getRouterInterface(): RouterInterface {
                return AuthPlugin.routerInterface.get().routerInterface
            }

            override fun getMainActivityIntent(): Intent {
                return mainActivityProvider.get().getMainActivityIntent()
            }

        }

        singletonComponent = LoginSingletonComponentInitializer(
            application,
            dependency,
            googleAuthEnable = customizationOptions.googleAuthEnable
        ).init(commonSingletonComponent.get())
    }

    override fun doAfterInitialize(application: Application) {
        singletonComponent.loginInterface.initialize(application)
    }

    override fun getLoginSingletonComponent(): LoginSingletonComponent {
        return singletonComponent
    }

    object CustomizationOptions {
        var googleAuthEnable: Boolean = false
    }
}
```
В данном плагине появляется возможность конфигурирования извне, указывая определенные значение флагов. Также появляется блок зависимостей: в данном случае все зависимости обязательные, но также доступна возможность указать опциональную зависимость:
```kotlin
var routerInterface: FeatureProvider<RouterInterface.Provider>? = null

override val dependency: Dependency = Dependency.Builder()
        .optional(RouterInterface.Provider::class.java) { routerInterface = it }
        .build()
```

Также есть возможность получать множество интерфейсов определенного типа (например, в наших приложениях *ViewerFacade*) с указанием опциональности:
```kotlin
private var viewers: Set<FeatureProvider<ViewerFacade>>? = null

override val dependency: Dependency = Dependency.Builder()
        .optionalSet(ViewerFacade::class.java) { viewers = it }
        .build()
```

##### Расширенные варианты настройки плагина
Возьмем к примеру плагин, у которого есть customizationOptions:
```kotlin
object MyPlugin : BasePlugin<MyPlugin.CustomizationOptions>() {

    private var sessionIdProvider: FeatureProvider<SessionIdProvider>? = null

    override val dependency: Dependency = Dependency.Builder()
        .optional(SessionIdProvider::class.java) { sessionIdProvider = it }
        .build()

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    class CustomizationOptions internal constructor() {
        var authRequired: Boolean = false
    }
}
```
В таком случае возникает противоречие, когда при добавлении плагина на уровне приложения указывают `customizationOptions.authRequired = true`,
при этом нужная зависимость `SessionIdProvider` никем не поставляется. Получается, что поведение плагина будет не корректным. Чтобы этого избежать,
нужно формировать `dependency` на основе `customizationOptions`. Для этого существуют специальные расширения `Dependency.Builder.requireIf()`, `Dependency.Builder.requireNotIf()`.
```kotlin
object MyPlugin : BasePlugin<MyPlugin.CustomizationOptions>() {

    private var sessionIdProvider: FeatureProvider<SessionIdProvider>? = null

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .requireIf(customizationOptions.authRequired, SessionIdProvider::class.java) { sessionIdProvider = it }
            .build()
    }

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    class CustomizationOptions internal constructor() {
        var authRequired: Boolean = false
    }
}
```
**!!!** Важный момент, что в этом случае `dependency` должна формироваться лениво (`by lazy`) из-за особенностей плагина (`object`, которые настраивается после создания).
Такой подход позволит сделать настраивание плагинов еще более гибким и расширяемым, при этом сохранив возможность на уровне плагина валидировать окружение в соответствии с желаемой конфигурацией.

### Уровень приложения
Организация кода с использованием плагинной структуры на уровне приложения выглядит следующим образом:

```kotlin
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val pluginManager = PluginManager()

        pluginManager.registerPlugins(
            CommonUtilsPlugin,
            PermissionPlugin,
            AuthPlugin.apply {
                customizationOptions.googleAuthEnable = false
            },
            ProfilePlugin
        )

        pluginManager.configure(this)
    }
}
```

1. Все необходимые плагины настраиваются нужным образом и добавляются в **PluginManager**.
```kotlin
val pluginManager = PluginManager()

pluginManager.registerPlugins(
            CommonUtilsPlugin,
            PermissionPlugin,
            AuthPlugin.apply {
                customizationOptions.googleAuthEnable = false
            },
            ProfilePlugin
        )
```
2. Запускается этап конфигурирования
```kotlin
pluginManager.configure(application)
```
, во время которого:
1) собирауются все фичи модулей по плагинам;
2) каждому плагину встраиваются необходимые зависимости (если отсутствуют обязательные, то кидается исключение);
3) выполняется последовательная инициализация плагинов;
4) выполняется настройка плагинов.

#### Тонкая подстройка фич на уровне приложения
Существует возможность на уровне приложения подменить/скрыть какую-либо фичу для конкретного плагина или набора плагинов (иногда такое может потребоваться). Для этого создан **FeatureResolver**:
```kotlin
interface FeatureResolver {
    fun <F: Feature> resolveSingle(featureType: Class<out Feature>, caller: Plugin<*>, records: Set<PluginManager.Record<out F>>): FeatureProvider<F>?
    fun <F: Feature> resolveMulti(featureType: Class<out Feature>, caller: Plugin<*>, records: Set<PluginManager.Record<out F>>): Set<FeatureProvider<F>>?
}
```
, который можно передать в конструктор **PluginManager**. Например, так можно скрыть фичу от конкретного плагина:
```kotlin
val resolver = object : FeatureResolver {
    override fun <F : Feature> resolveSingle(
        featureType: Class<out Feature>,
        caller: Plugin<*>,
        records: Set<PluginManager.Record<out F>>
    ): FeatureProvider<F>? {
        return if(caller == IamherePlugin && featureType == PushCenter::class.java) {
            null
        } else if(records.size == 1) {
            records.first().feature as FeatureProvider<F>
        } else null
    }
    ...
}
```
В данном случае делаем *PushCenter* невидимым для плагина *IamherePlugin*.

#### Проверка плагинов и их зависимостей на уровне приложения
Поскольку в текущем варианте плагинная система построена по принципу сервис локатора, проверку работоспособности плагинов в приложении НЕ удастся проверять на этапе компиляции. (кодогенерация на данном этапе отсутствует, ждем релиза kotlin 1.5 и [KSP](https://github.com/google/ksp)). В связи с этим предлагается проверку проводить с помощью запуска одного unit-теста. 
Для этого создается тестовое приложение:
```kotlin
class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        RxJavaPlugins.setErrorHandler {  }
        AppConfig.init(this)
        FirebaseApp.initializeApp(this)

        val pluginManager = AppPluginInitializer.buildPluginManager(this)
        pluginManager.configure(this)
    }
}
```
И сам unit-тест с ипользованием Robolectic:
```kotlin
@Config(
    sdk = [ Build.VERSION_CODES.P ],
    application = TestApplication::class
)
@RunWith(RobolectricTestRunner::class)
class AppPluginInitializerTest {

    @Test
    fun testPlugins() {
        val app: Application = ApplicationProvider.getApplicationContext()
    }

}
```
Этого достаточно, чтобы провалидировать, что у всех плагинов есть обязательные зависимости, каждый плагин успешно инициализируется и конфигурируется.
Поскольку в нашей системе unit-тесты запускаются при добросках, сломать зависимости будет крайне сложно.
