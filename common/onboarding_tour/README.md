### Onboarding tour (приветственный экран настроект приложения)
Модуль содержит компонент для отображения приветственного экрана настроек.

| Класс                 | Ответственные                                                                                                    | Добавить |
|-----------------------|------------------------------------------------------------------------------------------------------------------|----------|
| OnboardingTourPlugin  | [Чадов А.С.](https://online.sbis.ru/person/d38b3a75-f889-4725-8f12-69cb8bffca79) | [Задача на разработку компонента](https://online.sbis.ru/opendoc.html?guid=9555f0cb-7f91-4998-ba70-b1f64652fa7d) |

#### Использование в приложениях
- [Мобильный SabyAdmin](https://git.sbis.ru/mobileworkspace/apps/droid/sabyadmin)
- [Мобильная База знаний](https://git.sbis.ru/mobileworkspace/apps/droid/sabyknow)

#### Внешний вид  
[Проект](https://online.sbis.ru/opendoc.html?guid=99e4ad3e-a572-4afd-affb-4a8e91c34699)   
[Разработка компонента](https://online.sbis.ru/opendoc.html?guid=9555f0cb-7f91-4998-ba70-b1f64652fa7d)   
[Стандарт внешнего вида_sabyadmin](https://www.figma.com/proto/h4KK4XFIY2dVMl26ldwqD6/%D0%A3%D0%B4%D0%B0%D0%BB%D0%B5%D0%BD%D0%BD%D1%8B%D0%B9-%D0%BF%D0%BE%D0%BC%D0%BE%D1%89%D0%BD%D0%B8%D0%BA.-%D0%9C%D0%BE%D0%B1%D0%B8%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F-%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F?page-id=2%3A24&node-id=652%3A21776&viewport=669%2C629%2C0.27&scaling=min-zoom&starting-point-node-id=17%3A870)
[Стандарт внешнего вида](https://www.figma.com/proto/N8ztcntmCIRenioyYg3nEk/Onboarding?page-id=1%3A2&type=design&node-id=1690-25527&t=qDRgJyX3LXOWw9kX-0&scaling=min-zoom&starting-point-node-id=1690%3A25527&hide-ui=1)
[Стандарты интерфейса](https://n.sbis.ru/article/Onboarding)
[API](https://online.sbis.ru/shared/disk/e2041e11-c94d-4d99-afc5-5c79f20d559c)

#### Описание
Onboarding - это несколько приветственных экранов в виде слайдов. Он знакомит пользователя с основными возможностями продукта и его 
преимуществами, а также запрашивает необходимые разрешения для работы с приложением.

#### Использование
1. Добавить плагин `OnboardingTourPlugin` в систему плагинов приложения `PluginSystem`:
```kotlin
internal object AppPlugin : BasePlugin<Unit>() {
    //...
    override fun registerPlugins(app: Application, pluginManager: PluginManager) {
        pluginManager.registerPlugins(
            // ....
            OnboardingTourPlugin
        )
        pluginManager.configure(app)
    }
}
```

2. Реализация иллюстраций. 
Изображение необходимо заказывать у дизайнера по [инструкции](https://n.sbis.ru/article/fe5a2b55-964d-4db7-9f8a-5b5293e7e735).
Размер изображения не превышает 60% ширины экрана и высоты блока с картинкой, также не должна превышать установленного максимального
значения (размер 1х 250px; размеры нарезаются: для iOS (1х, 2х,3х), для Android (mdpi (1х), hdpi (1.5х), xhdpi (2х), xxhdpi (3х), xxxhdpi (4х)).

3. Описать интерфейс зависимостей требуемых модулем [OnboardingTourDependency](onboarding_tour/src/main/java/ru/tensor/sbis/onboarding_tour/contract/OnboardingTourDependency.kt).
Декларативно объявить отображение приветственного экрана настроек через создание [TourContent](onboarding_tour/src/main/java/ru/tensor/sbis/onboarding_tour/data/TourContent.kt)
посредством [OnboardingTourCreator](verification-decl/src/main/java/ru/tensor/sbis/verification_decl/onboarding_tour/OnboardingTourCreator.kt) 
или [TourContent](onboarding_tour/src/main/java/ru/tensor/sbis/onboarding_tour/data/TourContent.kt) если модуль имплементации `:onboarding_tour` подключен к использующему функционал модулю напрямую.
   
Пример:
```kotlin
private val provider: OnboardingTourProvider by lazy {
        tourFacade.get().onboardingTourCreator.createProvider(
            // приоритет показа конкретного тура относительно других онбордингов
            priority = TourPriority.HIGH
        ) {
            // общий баннер для тура
            defaultBanner {
                // изображения с логотипом и названием МП
                logoImage = R.drawable.logo
                // названия МП для шрифтового логотипа
                logoName = R.string.name
                // тип кнопки на баннере экрана тура
                buttonType = BannerButtonType.CLOSE
                // опциональный коллбэк выполняемый при клике на кнопку пропустить или закрыть в баннере
                onButtonClick {
                    // do something
                }
            }
            // опциональный коллбэк вызываемый по завершении отображения тура онбординга
            onDismiss {
                // do something
            }
            // опциональные правила отображения тура
            rules {
                // отображаться единожды для конкретного пользователя
                showOnlyOnce = false
                // эффект для бэкграунда (градиент, анимация, статика)
                backgroundEffect = BackgroundEffect.GRADIENT
                // поддержка переходов по свайпу пользователя
                swipeTransition = true
                // следует ли учитывать ранее показанный компонент онбординга ':onboarding' (прошлая реализация)
                showOnlyOnceConsideringOnboarding = true
            }
            // добавить новый экран тура
            page {
                // опциональный индекс позиции экрана
                position = 0
                // заголовок экрана
                title = R.string.onboarding_tour_test_title_1
                // описание экрана
                description = R.string.onboarding_tour_test_message_1
                // изображение на экране
                image = R.drawable.image_2
                // ресурс подписи на кнопке перехода к следующему экрану
                nextButtonTitle = R.string.onboarding_tour_test_button
                // специфичный баннер для текущего экрана тура
                customBanner {
                    // do something 
                }
                // расширенные настройки кнопки перехода текущего экрана (когда недостаточно nextButtonTitle)
                button {
                    // ресурс подписи на кнопке перехода к следующему экрану
                    title = R.string.onboarding_tour_test_title_1
                    // шрифтовая иконка для кнопки перехода
                    icon = SbisMobileIcon.Icon.smi_arrow
                    // положение icon относительно title по горизонтали
                    titlePosition = HorizontalPosition.LEFT
                    // опциональная команда выполняемая по клику на кнопку перехода
                    onCloseCommand {
                        flow {
                            // do something    
                            emit(true)
                        }
                    }
                }
                // подпись с условиями использования
                terms {
                    // ресурс полной подписи с текстом условий и положений
                    caption = R.string.onboarding_tour_test_terms
                    // список ссылок для caption
                    links = listOf("https://sbis.ru/help/integration", "https://sbis.ru/help/plugin/sbis3plugin")
                }
                // запрос прав и разрешений
                permissions {
                    // список системных разрешений запрашиваемых компонентом
                    permissions = listOf(Manifest.permission.CAMERA)
                    // являются ли предоставление permissions блокирующим для перехода к следующему экрану тура
                    isRequired = true
                    // опциональная команда выполняемая при необходимости обоснования предоставления [permissions] пользователем
                    onRequestRationale { fragment, list ->
                        flow {
                            // do something
                            emit(true)
                        }
                    }
                }
                // опциональный коллбэк проверки необходимости отображения экрана в туре онбординга
                checkIsRequired {
                    // do something
                    true
                }
            }
        }
    }
```

#### Подключение
Для добавления функционала в проект необходимо выполнить шаг подключения зависимости модуля в файл settings.gradle:

`include ':verification-decl'`
`project(':verification-decl').projectDir = new File(settingsDir, 'declaration/verification-decl')`

`include 'onboarding_tour'`
`project(':onboarding_tour').projectDir = new File(settingsDir, 'common/onboarding_tour')`

#### Темизация
Экран темизируется, в зависимости от основной темы оформления (стилизация на основе глобальных переменных тем).

##### Трудозатраты внедрения
1 ч/д