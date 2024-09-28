#### Принудительное и рекомендуемое обновление
Модуль содержит функционал, позволяющий проинформировать пользователя о доступности новой или 
критически несовместимой версии используемого приложения.

| Ответственность | Ответственные | 
|-----------------|---------------|
| Разработка      | [Чадов А.С.](https://online.sbis.ru/person/d38b3a75-f889-4725-8f12-69cb8bffca79) |
| Тестирование    | [Аминов Д.Е.](https://online.sbis.ru/person/888c673e-66a2-4f3d-8d49-a148c6abf113) | 

#### Использование в приложениях
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Брендовые приложения](https://git.sbis.ru/mobileworkspace/apps/droid/brand)
- [Доки](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Демо коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/demo-communicator)
- [Касса/Престо](https://git.sbis.ru/mobileworkspace/apps/droid/retail)
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Легкий коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/sabylite)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [Официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [СМС](https://git.sbis.ru/mobileworkspace/apps/droid/sms)

#### Внешний вид и техническая документация
[ТД Принудительное обновление мобильного приложения у клиентов](https://online.sbis.ru/shared/disk/c78b957a-8c01-4ce9-a969-ed03e3a27cd1) <br/>
[ТД Модернизация окна обновления МП](https://online.sbis.ru/shared/disk/96a081b2-dd2e-43e7-be42-ea63dca81cc8)
[Макет_и_спецификация](http://axure.tensor.ru/MobileAPP/#p=%D0%BE%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5_%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F_ver2&g=1)
[Макет_и_спецификация_v2](http://axure.tensor.ru/MobileAPP/%D0%BE%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5_%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F_ver2.html)
[Новый_макет](https://www.figma.com/proto/T8LtomTKC4GKnwrp4Thb9i/%D0%9E%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F?page-id=1%3A2&type=design&node-id=2222-19249&t=xlvGEUrkUZ8AWTvY-0&scaling=min-zoom)
[Новая_спецификация](https://n.sbis.ru/article/Update)

#### Содержание
- [Описание](#Описание)
- [Концептуальное описание решения](#Концептуальное-описание-решения)
- [Использование](#Использование)
- [Настройка версионирования в приложении](#Настройка-версионирования-в-приложении)
- [Управление доступностью версий внешних приложений](#Управление-доступностью-версий-внешних-приложений)
- [Исключение работы версионирования на активностях, обычно на Splash Screen и Login Screen](#Исключение-работы-версионирования-на-активностях,-обычно-на-Splash-Screen-и-Login-Screen)
- [Конфигурация плагина](#Конфигурация-плагина)
- [Проверка версии через In-app updates](#Проверка-версии-через-In-app-updates)
- [Установка приложений по qr коду](#Установка-приложений-по-qr-коду)
- [Аналитика](#Аналитика)
- [Стилизация](#Стилизация)

#### Описание
Модуль `version-checker` используется, чтобы:
- проинформировать пользователя о наличии новой версии приложения
- предложить обновиться до последней опубликованной версии

Принудительное обновление блокирует доступ ко всему приложению, а рекомендуемое - нет.

Термины и определения:<br/>
- **Критически совместимая версия** - минимально поддерживаемая версия приложения, ниже которой существует критическая несовместимость.<br/>
- **Рекомендуемая для обновления версия** - версия приложения, ниже которой рекомендуется обновить приложение.

#### Концептуальное описание решения
Для приложения с сбис сервиса **apps** запрашиваются **критически совместимые версии** и **версии для рекомендуемого обновления**.
При первом запуске приложения, используя метод `MobileVersionControl.LoadReport` сервиса `apps/service` через [VersionServiceChecker](src/main/java/ru/tensor/sbis/version_checker/domain/service/VersionServiceChecker.kt)
загружается словарь версий, а затем кэшируется в хранилище приложения. Загрузка с сервера осуществляется
не чаще чем раз в сутки. При последующих запусках приложения словарь версий читается из кэша, а при
необходимости происходит актуализация данных с сервером. После получения данных о версиях 
запускается проверка текущей версии приложения к **критически совместимой версии** и **рекомендуемой для обновления версии**.

Конфигурация описана в json-формате и включает записи **versions** и **published_versions**.<br/>
Записи представлены в виде набора пар ключ:значение, где ключу соответствует идентификатор приложения, а значению версия.<br/>
В структуре **versions** описываются критически совместимые поддерживаемые версии (последние совместимые, указываются 
на [Версиях приложения](https://inside.saby.ru/page/ext-apps-settings), вкладка "Версии" по приложению).<br/>
В структуре **published_versions** описываются версии для рекомендуемого обновления (последняя успешно опубликованная версия).
Формирование **published_versions**: с prod рекомендуемый список версий формируются в соответствии с выпущенным на бой версиями
более 7 дней назад. Fix-стенд же ничего не знает про версии выпущенные на бою, поэтому правило выпуска 7 дней назад уже работает относительно 
версий выпущенных во внутреннее тестирование.

Опционально запускается проверка версии через сервис `Google Play Core Library` [InAppUpdateChecker](src/main/java/ru/tensor/sbis/version_checker/domain/service/InAppUpdateChecker.kt),
только если приложение было установлено с маркета Google Play.<br/>
(Подробнее в **Проверка версии через In-app updates**)

#### Использование
1. Инициализировать компонент принудительного обновления добавив плагин [VersionCheckerPlugin](src/main/java/ru/tensor/sbis/version_checker/VersionCheckerPlugin.kt)
в систему плагинов приложения, см. `PluginSystem`.

2. Предоставить информацию о приложении, реализовав `VersioningSettings.Provider` с указанием:
- текущей версии установленного приложения,
- идентификатора приложения,
- названия приложения,
- опционально: поддерживаемые источники обновления - маркеты, в которых приложения опубликованы, по умолчанию СБИС Маркет и Google Play,
- опционально: варианты проверки обновления, по умолчанию проверка через сбис сервис для
  критического и рекомендуемого обновления (проверка через `Google Play Core Library` по умолчанию не включена),
- опционально: интервал предложения рекомендуемого обновления приложения в днях, по умолчанию 7 дней (в режиме отладки - 1 минута).

Пример:
```kotlin
internal class AppVersioningSettings(private val appContext: Context) : VersioningSettings.Provider {
   override fun getVersioningSettings() = object : VersioningSettings {
      override val appVersion = BuildConfig.VERSION_NAME
      override val appId = BuildConfig.APPLICATION_ID
      override val appName = appContext.resources.getString(R.string.business_app_name)
   }
}
```

3. В плагине уровня приложения `AppPlugin` подключить:
- `VersioningSettings.Provider` - реализация настроек приложения, выполненные в шаге №2,
- `VersioningIntentProvider` - для получения интента на запуск принудительного обновления в шаге №4,
- `VersioningDispatcher.Provider` - для запуска версионирования по callback-ам жизненного цикла активностей и/или фрагментов
(про настройку `VersioningDispatcher` подробнее в **Настройка версионирования в приложении**)

Пример:
```kotlin
@Suppress("MemberVisibilityCanBePrivate")
internal object AppPlugin : BasePlugin<Unit>() {
   private val versioningSettingsProvider: VersioningSettings.Provider by lazy { AppVersioningSettings(application) }
   private lateinit var versioningIntentProvider: FeatureProvider<VersioningIntentProvider>
   private lateinit var versioningDispatcherProvider: FeatureProvider<VersioningDispatcher.Provider>

   override val api: Set<FeatureWrapper<out Feature>> = setOf(
      // ...
      FeatureWrapper(VersioningSettings::class.java) { versioningSettingsProvider.getVersioningSettings() },
      FeatureWrapper(AppComponent::class.java) { appComponent }
   )

   override val dependency: Dependency = Dependency.Builder()
      // ...
      .require(VersioningDispatcher.Provider::class.java) { versioningDispatcherProvider = it }
      .require(VersioningIntentProvider::class.java) { versioningIntentProvider = it }
      // ...
      .build()
   
   internal val appComponent: AppComponent by lazy {
      val dependency = object : ApplicationDependency,
         VersioningIntentProvider by versioningIntentProvider.get() {}
      return@lazy AppComponentInitializer(
         app = application,
         plugin = this,
         appDependency = dependency
      ).init(commonSingletonComponentProvider.get())
   }

   override fun doAfterInitialize() {
      versioningDispatcherProvider.get()
         .versioningDispatcher
         .start(application)
   }
}
```
4. Перед запуском главного экрана приложения, например в `LaunchActivity`, проверить необходимость показать
активити для принудительного обновление.
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
   super.onCreate(savedInstanceState)

   val criticalIntent by lazy { AppPlugin.appComponent.dependency.getForcedUpdateAppActivityIntent() }
   when {
      criticalIntent != null -> goToCriticalScreen(criticalIntent!!)
      else -> goToMainContentScreen()
   }
}

private fun goToCriticalScreen(criticalIntent: Intent) {
   startActivity(criticalIntent)
   finish()
   overridePendingTransition(0, 0)
}
```

#### Настройка версионирования в приложении
Диспетчер версионирования `VersioningDispatcher` позволяет отслеживать события жизненного цикла
приложения для периодического инициирования проверки версий.<br/>
Имеется несколько стратегий версионирования:<br/>
- `VersioningDispatcher.Strategy.REGULAR` (по умолчанию) - событие версионирования будет обработано из активности <br/>
- `VersioningDispatcher.Strategy.BY_FRAGMENTS`:
<br/> событие критического версионирования будет обработано из активити
<br/> событие рекомендуемого версионирования будет обработано из фрагмента
<br/> по умолчанию все `DialogFragment` исключены из версионирования.

Пример настройки стратегии версионирования в плагине уровня приложения:
```kotlin
override fun doAfterInitialize() {
    versioningDispatcherProvider.get()
        .versioningDispatcher
        .behaviour(Strategy.BY_FRAGMENTS)
        .start(application)
}
```

#### Управление доступностью версий внешних приложений
С настоящего момента для изменения доступности версии используется [inside](https://inside.saby.ru/page/ext-apps-settings) для настройки сервиса apps, 
запуск ВНР не требуется. Смотреть пункт Управление доступностью версий внешних приложений статьи Управление внешними 
приложениями [Управление внешними приложениями](https://n.sbis.ru/saby/knowledge?published=true&mode=readList&article=21679b9a-c398-487e-b755-d166171aae9e#toc_toc_5b86cf67-5468-4a49-9d36-4464b6619a83).

##### Исключение работы версионирования на активностях, обычно на Splash Screen и Login Screen
- Для предотвращения запуска функционала на неподходящей для этого активности, например на 
`LoginActivity`, `LaunchActivity` (Splash screen), необходимо наследовать активность от маркерного интерфейса `VersionedComponent`:
```kotlin
internal class LaunchActivity : AppCompatActivity(), VersionedComponent 
```
- Для того чтобы исключить только принудительное или рекомендуемое обновления, необходимо еще
переопределить `versioningStrategy`:
```kotlin
internal class LaunchActivity : AppCompatActivity(), VersionedComponent {
    override val versioningStrategy: VersionedComponent.Strategy
        get() = VersionedComponent.Strategy.CHECK_CRITICAL
}
```

#### Конфигурация плагина
Имеются настройки для конфигурации плагина `VersionCheckerPlugin.CustomizationOptions`:
- **deferInit** - настройка позволяет отложить инициализацию версионирования и выполнить ее вне плагина.
  В таком случае для инициализации необходимо использовать вызов `VersioningInitializer.init`.
  К сведению, для корректной обработки принудительного обновления инициализация должна быть выполнена до
  перехода с экрана являющегося отправной точкой приложения.
- **overrideThemeApplication** - настройка указывает на необходимость чтения атрибута `versioningTheme` принудительно из `Application`,
  а не из `Activity`, которая установила свою тему в соответствии с темой `Application` из `AndroidManifest.xml`
  (тег `android:theme`).<br/>
  Используется для МП приложений, которые могут менять свою тему в рантайме и не имеют возможности
  задать/подменить `versioningTheme` в `AndroidManifest.xml` при запуске приложения.
- **autoStartDispatcher** - настройка позволяет автоматически стартовать диспетчер `VersioningDispatcher` после инициализации данного плагина.
  Используется в случае если диспетчер не требует дополнительных настроек, иначе необходимо получить данную
  фичу из списка публичного API, выполнить настройку и инициировать запуск вызовом `VersioningDispatcher.start`.<br/>
  К сведению, для корректной обработки принудительного обновления старт диспетчера должна быть выполнен как можно
  раньше, например в коллбэке `doAfterInitialize` плагина приложения.

Установить настройки можно при подключении плагина `VersionCheckerPlugin` в `PluginSystem`:
```kotlin
object PluginSystem {

    fun initialize(
        app: Application,
        pluginManager: PluginManager = PluginManager()
    ) {
        pluginManager.registerPlugins(
            // ...
            VersionCheckerPlugin.apply {
                customizationOptions.deferInit = true
                customizationOptions.overrideThemeApplication = true
            }
        )
        pluginManager.configure(app)
    }
}
```

#### Проверка версии через In-app updates
Компонент поддерживает версионирование через функцию обновления в приложениях In-app updates (Google Play Core Library).
По умолчанию отключено, для включения следует переопределить реализацию метода [VersioningSettings.getAppUpdateBehavior]
и добавить [AppUpdateBehavior.PLAY_SERVICE_RECOMMENDED].

##### Тестирование версионирования с функционалом In-app updates
Тестирование возможно с использованием внутреннего совместного доступа к приложению через [GooglePlay Console](https://play.google.com/console/internal-app-sharing/);
1. Опубликовать приложение на тестовый трек GooglePlay маркета, если оно еще не опубликовано.
За этим можно обратиться к [Полатов В.И.](https://online.sbis.ru/person/3cff9eb5-04d1-404f-933f-82de46b345ea) или к [Аминов Д.Е.](https://online.sbis.ru/person/888c673e-66a2-4f3d-8d49-a148c6abf113).
2. Получить от тестирования аккаунт в Google, для которого включено внутренее тестирование приложения.
3. Авторизоваться под этим пользователем в GooglePlay, в поиске маркета найти нужное приложение.
Под кнопкой "Установить" должна появиться надпись красным цветом: "You're an internal tester. This app may be unsecure or unstable."
4. Установить приложение, авторизоваться.
5. В приложении включить режим отладки `AppConfig.isDebug`, в некоторых приложениях сбис это делается 
через 5 нажатий по номеру версии в приложении в настройках. Без режима отладки нужно ждать 7 дней
после выхода обновления, чтобы появилось предложение обновить (так задумано по проекту).
6. Дождаться, пока в GooglePlay появится предложение обновить приложение. Для этого нужно на 
online.sbis.ru отправить Merge Request c указанием сборки нового билда приложения. Либо подождать,
пока кто-нибудь другой добросит изменения и соберется новый билд приложения.
Новая версия автоматически загрузится на тестовый трек маркета после прохождения минимального ревью
GooglePlay в ~10-15 минут.
7. Не обновляя, зайти в приложение, и должно появиться предложение обновить приложение в течение суток.

Иногда на реальном устройстве может не работать, лучше всего для целей тестирования создать отдельный эмулятор c GooglePlay.<br/>
[Официальная документация](https://developer.android.com/guide/playcore/in-app-updates/test).

#### Установка и открытие приложений по qr коду
Компонент также может быть использован для поддержки установки и открытия Сбис приложений по ссылке из QR-кода.
Пример ссылки: `https://online.sbis.ru/auth/qrcode/sbis/?token=token_value`.

Для этого необходимо:
1. Реализовать интерфейс `InstallationComponent` на `Activity` обработчике входящих ссылок.
`BaseLaunchActivity` уже реализует данный интерфейс.
2. Использовать компонент открытия ссылок `link_opener`.

#### Аналитика
Для мониторинга частоты использования функционала версионирования добавлена сборка аналитики. События:
- показ окна рекомендованного обновления, `versioning_show_recommended`
- показ экрана обязательного обновления, `versioning_show_critical`
- клик обновить для рекомендованного обновления, `versioning_click_recommended`
- клик обновить для обязательного обновления, `versioning_click_critical`
- переход на установку МП по qr-коду, `versioning_install_app`
- переход к установленному МП по qr-коду, `versioning_installed_app`
Для кликов также передается пакет с доп. информацией (маркет обновления, доступен ли GooglePlay на устройстве).

#### Стилизация
Для стилизации используются глобальные атрибуты.
