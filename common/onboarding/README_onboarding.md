### Onboarding (приветственный экран)
Модуль содержит стандартный комопнент для отображения приветственного экрана, который необходимо повторно использовать в нескольких приложениях

| Класс | Ответственные | Добавить |
|-------|---------------|----------|
|[OnboardingFeature](https://git.sbis.ru/mobileworkspace/android-serviceAPI/blob/development/declaration/src/main/java/ru/tensor/sbis/declaration/onboarding/OnboardingFeature.kt) | [Чадов А.С.](https://online.sbis.ru/person/d38b3a75-f889-4725-8f12-69cb8bffca79) | [Задача на разработку компонента](https://online.sbis.ru/opendoc.html?guid=becc2243-b21b-4dad-90c8-4fb94eea4876) |

#### Использование в приложениях
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Экран повара](https://git.sbis.ru/mobileworkspace/apps/droid/cookscreen)

#### Внешний вид  
[Стандарт внешнего вида](http://axure.tensor.ru/MobileAPP/#p=onboarding&g=1)   
[ТЗ](https://online.sbis.ru/shared/disk/d404d2aa-9e63-4821-991c-e33ae3465514)
[Ответственный проектировщик Зорькина Елена](https://online.sbis.ru/person/bd693f69-adc2-451d-bf74-57ecebab97d8)

#### Описание  
**Приветственный экран** используется для:
* информирования пользователя об основных функциях приложения перед началом работы
* обратить внимание на "killer"-фичи приложения
* рассказать пользователю зачем нужны те или иные права приложению
* запросить права уровня системы или приложения до начала работы

Каждый **экран фичи** комопнента приветственного экрана содержит:
* иконку с логотипом приложения
* заголовок приложения
* краткое описание фичи в несколько строк
* изображение для визуализации фичи

#### Использование
1. Инициализировать Onboarding компонент [OnboardingSingletonComponent](onboarding/src/main/java/ru/tensor/sbis/onboarding/di/OnboardingSingletonComponent.kt)
Для поддержки страниц заглушек об отсутствии прав на область передать опциональный [PermissionFeature](https://git.sbis.ru/mobileworkspace/android-serviceAPI/blob/development/declaration/src/main/java/ru/tensor/sbis/declaration/permission/PermissionFeature.kt)
Для поддержки персонализации события отображения онбординга передать опциональный [LoginInterface](https://git.sbis.ru/mobileworkspace/android-serviceAPI/blob/development/declaration/src/main/java/ru/tensor/sbis/declaration/LoginInterface.java) 
   
Пример: 
```kotlin
OnboardingSingletonComponentInitializer(applicationContext, 
                                               moduleDependencies,
                                               permissionSingletonComponent.permissionFeature,
                                               loginInterface).init()      
``` 
                                                  
2. Описать зависимости требуемые модулем [OnboardingDependency](onboarding/src/main/java/ru/tensor/sbis/onboarding/contract/OnboardingDependency.kt)
Декларативно объявить представление приветственного экрана через создание [Onboarding](onboarding/src/main/java/ru/tensor/sbis/onboarding/contract/providers/content/OnboardingContent.kt)
   
Пример: 
```kotlin
class OnboardingProviderImpl() : OnboardingProvider {
                 override fun getOnboardingContent(): Onboarding = Onboarding {     // cодержимое компонента
                     backPressedSwipe = true
                     preventBackSwipe = true
                     swipeLeaving = true
                     stickyIndicator = true
                     header {                       // область заголовка
                         textResId = R.string.onboarding_title
                         imageResId = R.drawable.logo_business
                     }
                     page {                         // область экрана фичи
                         descriptionResId = R.string.onboarding_main
                         imageResId = R.drawable.onbording_main
                         permission(Manifest.permission.READ_CONTACTS)     // системные разрешения востребуемые описаной фичей
                     }
                     page {
                         descriptionResId = R.string.onboarding_notifications
                         imageResId = R.drawable.onbording_notifications
                         action {                   // пользовательское действие для экрана фичи 
                            execute = { post -> /*custom action*/ post(true) }   // пользовательское действие (содержит аргумент колл-бэк информирования о
                                                                                 // завершении пользовательского действия и его успешности)
                         }
                         defaultButton = true                             // необходимо отобразить кнопку по-умодчанию
                     }
                     customPage {                                         // пользовательский экран
                         creator = { SaleListFragment.newInstance() }
                     }
                     noPermissionPage {                                   // экран отсутсвия прав на управляемую область полномочий
                         permissionScope(BUSINESS)                        // область полномочий
                         descriptionResId = R.string.onboarding_no_permission
                         imageResId = R.drawable.onbording_no_permission
                         inclusiveStrategy = false                        // стратегия отображения
                     }
                 }
             }
```
В котором предусмотрены следующие свойства содержимого компонента:
- общие для приветственного экрана
  - **backPressedSwipe** - осуществляется ли свайп назад по нажатию на кнопку "Назад"
  - **preventBackSwipe** - запрещен ли свайп назад по экранам фич
  - **swipeLeaving** - осуществляется ли выход с приветственного экран по свайпу последнего экрана фичи
  - **stickyIndicator** - задает фиксированность баннера и индикатора пейджера
  - **finally** - целевое намерение после покидания приветственного экрана
- заголовка
  - **textResId** - id ресурса теста в заголовке
  - **imageResId** - id ресурса логотипа
  - **gravityToBottom** - true если заголовок выровнен по низу логотипа, false по центру
- страницы фичи
  - **descriptionResId** идентификатор текста описания экрана
  - **imageResId** идентификатор основного изображения экрана
  - **defaultButton** true если необходимо отобразить кнопку по-умодчанию
  - **permissionList** - системные зависимости
  - **action** - пользовательское действие для экрана фичи
  - **suppressed** - стратегия подавления функциональности экрана при отсутствии прав у пользователя
- кнопки на фич странице
  - **textResId** - id ресурса заголовка
  - **action** - обработчик действия по клику на кнопку
  - **defaultAction** - true если применяем стандартный обработчик действия, иначе false

3. Использовать интерфейс публичного api компонента для его запуска [OnboardingFeature](https://git.sbis.ru/mobileworkspace/android-serviceAPI/blob/development/declaration/src/main/java/ru/tensor/sbis/declaration/onboarding/OnboardingFeature.kt)

-  получить api интерфейс. Пример получения через [OnboardingSingletonComponentProvider](onboarding/src/main/java/ru/tensor/sbis/onboarding/di/OnboardingSingletonComponentProvider.kt)
```kotlin
@Provides
internal fun provideOnboardingFeature(context: Context): OnboardingFeature {
    return OnboardingSingletonComponentProvider.getOnboardingFeature(context.applicationContext)
}                              
```      
-  использовать любой из методов `OnboardingFeature` для запуска **приветственного экрана**
   Пример с подменой корневого экрана приложения:
```kotlin
class AppRouter(
        private val onboardingFeature: OnboardingFeature
) : RouterInterface by NotImplementationsHolder {
    ...
    override fun getMainActivityId() = onboardingFeature.substituteIntentAction()
    ...
}                              
```
Пример отображения диалог фрагмент **приветственного экрана** в контейнере (для планшета)
```kotlin
fun showOnboardingDialog() {
    runCommand { _, fragmentManager ->
        if (!onboardingFeature.isOnboardingShown()) {
            val fragment = onboardingFeature.getOnboardingDialogFragment()
            fragmentManager.showDialog(fragment::class.java.canonicalName!!, true) { fragment }
        }
    }
}               
```

4. Добавление пользовательских экранов на экран приветствия. 
Если объявления **customPage** недостаточно для добавления кастомных страниц приветственного экрана, то для этих целей
дополнительно может быть использован [OnboardingNavigator](https://git.sbis.ru/mobileworkspace/android-serviceAPI/blob/development/declaration/src/main/java/ru/tensor/sbis/declaration/onboarding/OnboardingNavigator.kt)
Доступен из `OnboardingFeature`
`OnboardingNavigator` навигатор по фрагментам **приветственного экрана** может быть использован для:
* навигации вне компонента по экранам-фичам объявленным декларативно через dsl
* открытия/скрытия пользовательский фрагментов поверх основного приветственного экрана 
  (если компонент открыт через активность приветственного экрана)

#### Стилизация
Если используется активность [OnboardingActivity](onboarding/src/main/java/ru/tensor/sbis/onboarding/view/OnboardingActivity.kt) 
ее возможно стилизировать через объявление кастомного атрибута **onboardingTheme** в теме приложения
По умолчанию используется тема [OnboardingTheme](onboarding/src/main/res/values/theme.xml)

Фич-экраны компонента стилизируются при описании компонента через DSL
- [Onboarding](onboarding/src/main/java/ru/tensor/sbis/onboarding/contract/providers/content/OnboardingContent.kt)  
- [BasePage](onboarding/src/main/java/ru/tensor/sbis/onboarding/contract/providers/content/OnboardingContent.kt)

**themeResId** - стиль экрана фичи, должен быть наследником [FeatureTheme](https://git.sbis.ru/mobileworkspace/android-utils/blob/development/onboarding/src/main/res/values/theme.xml)
**tabletThemeResId** - стиль экрана фичи под планшет, должен быть наследником [FeatureTheme_Dialog](https://git.sbis.ru/mobileworkspace/android-utils/blob/development/onboarding/src/main/res/values/theme.xml)

#### Демо проект
Для запуска demo приложения необходимо добавить сл. строки в настройки settings.gradle
```gradle
include ':onboardingDemo'
project(':onboardingDemo').projectDir = new File(settingsDir, 'common/onboarding/demo')      
```
При работе с демо модулем `onboardingDemo`, можно закомментировать все модули кроме следующих затронутых: 
`onboarding`, `core`, `declaration`, `design`, `mvvm`, `push-notification`, `testing`

