#### Компоненты навигации

| Класс                                                                                        | Ответственные                                                                         | Добавить                                                                                 |
|----------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| [NavView](src/main/java/ru/tensor/sbis/design/navigation/view/view/NavView.kt)               | [Колпаков Михаил](https://online.sbis.ru/Person/6b7e7802-6118-4fe4-9ec3-1db87bc0853c) | [Задачу/поручение/ошибку](https://dev.sbis.ru/area/5cf6563d-4518-4d18-99dd-bb56db612cb7) |
| [TabNavView](src/main/java/ru/tensor/sbis/design/navigation/view/view/tabmenu/TabNavView.kt) | [Колпаков Михаил](https://online.sbis.ru/Person/6b7e7802-6118-4fe4-9ec3-1db87bc0853c) | [Задачу/поручение/ошибку](https://dev.sbis.ru/area/864da3b7-2e1c-49a4-8044-682d70f92791) |

Навигация представлена двумя UI компонентами: [Аккордеон](README_nav_view.md) и [ННП](README_tab_nav_view.md) (нижняя навигационная панель). Они позволяют добавить элементы меню и привязать к ним действие. Ключевые особенности:
- Синхронизация компонентов навигации  

    Для синхронизации компонентов навигации не требуется усилий со стороны разработчика, достаточно установить один адаптер:
    ``` kotlin
    val navAdapter = NavAdapter<MyNavItem>(lifecycleOwner)
    // инициализация элементов меню
    val accordion = findViewById<NavView>(R.id.accordeon)
    val tabNavView = findViewById<TabNavView>(R.id.bottom_navigation)
    // установка одного адаптера в оба компонента навигации для синхронизации выбранного элемента
    accordion.setAdapter(navAdapter)
    tabNavView.setAdapter(navAdapter)
    ```
- Автоматическая сортировка элементов  

    Элементы меню `NavigationItem` могут быть автоматически отсортированы, если они реализуют интерфейс `Comparable` и параметр сортировки указан при создании `NavAdapter`
    ``` kotlin
    val navAdapter = NavAdapter<MyNavItem>(lifecycleOwner = lifecycleOwner, sorted = true)
    ```
    Если параметр сортировки не указан, новый элемент будет добавлен в конец меню.
- Возможность добавлять/удалять элементы меню во время исполнения  

    При добавлении элементов меню во время исполнения, достаточно добавить элементы в адаптер `NavAdapter`, при этом элемент будет добавлен в соответствии с правилами сортировки (если его ещё нет в адаптере), а компоненты навигации будут обновлены с анимацией.
    ``` kotlin
    navAdapter.add(navigationItem)
    ```
    А также можно добавить сразу коллекцию элементов
    ``` kotlin
    val map: Map<NavigationItem, NavigationCounter> = mapOf()
    navAdapter.add(map)
    ```
- Возможность скрывать/отображать элементы меню во время исполнения  

    Во время исполнения может потребоваться скрыть/отобразить элементы меню при изменении каких-то внешних условий (например, изменение полномочий пользователя), в этом случае достаточно вызвать методы:
    ```kotlin
    /*
    Скрытие элемента (не удаляется из адаптера)
    */
    accordion.hideItem(item)
    tabNavView.hideItem(item)
    /*
    Отображение элемента
    */
    accordion.showItem(item)
    tabNavView.showItem(item)
    ```
    *Примечание:* подобного эффекта можно достичь удалением/добавлением элементов из адаптера, но в этом случае элемент будет удалён сразу из всех компонентов навигации и будет утрачено внутреннее состояние элемента (например, состояние выбора). 
    
- Возможность динамически изменить иконки и текст  

    Эта функция может потребоваться, если иконка или текст пункта меню зависят от какого-то внутреннго стостояния приложения. Например, иконка календаря может изменяться в зависимости от даты.  
    В большинстве случаев, элементы меню удобно определить в виде перечисления, это позволит работать с объектами конкретного типа из `NavAdapter` в бизнес логике приложения. Реализация динамического изменения для перечисления может быть оформлена в виде `companion object`:
    ```kotlin
    enum class NavigationTab(
            @StringRes val labelResId: Int,
            @StringRes iconId: Int,
            @StringRes iconSelectedId: Int
    ) : NavigationItem {
        NOTIFICATION(R.string.navigation_notifications, R.string.design_mobile_icon_notification, R.string.design_mobile_icon_notification_filled) {
            /**
             * Допустимо переопределение для пунктов меню, где требуется сокращённое название. 
             * Если короткое название необходимо нескольким пунктам меню, стоит 
             * рассмотреть хранимый атрибут со значением по умолчанию.
             * В данном случае название пункта меню неизменно
             */
            override val labelObservable: Observable<NavigationItemLabel> = Observable.just(NavigationItemLabel(labelResId, R.string.navigation_notifications_reduced))
        },
        MESSAGES(R.string.navigation_messages, R.string.design_mobile_icon_menu_messages, R.string.design_mobile_icon_menu_messages_filled),
        CALENDAR(R.string.navigation_calendar, R.string.design_mobile_icon_calendar, R.string.design_mobile_icon_calendar_filled);
    
        /**
         * Модель иконки, изменяемая
         */
        private val iconSubject = BehaviorSubject.createDefault(NavigationItemIcon(iconId, iconSelectedId))
    
        /**
         * В данном случае текст пунктов меню неизменный и не имеет сокращённой формы
         */
        override val labelObservable: Observable<NavigationItemLabel> = Observable.just(NavigationItemLabel(labelResId))
        override val iconObservable: Observable<NavigationItemIcon> = iconSubject
    
        /**
         * Пример реализации методов для изменения состояния пунктов меню
         */
        companion object IconUpdater {
    
            @JvmStatic
            fun changeCalendarIcons(@StringRes iconId: Int, @StringRes iconSelectedId: Int) =
                    CALENDAR.changeIcons(iconId, iconSelectedId)
    
            @JvmStatic
            fun NavigationTab.changeIcons(@StringRes iconId: Int, @StringRes iconSelectedId: Int) {
                iconSubject.onNext(NavigationItemIcon(iconId, iconSelectedId))
            }
        }
    }
    ```
    Изменение состояния элементов перечсления через `companion object` это неклассический подход. Более идеоматичный вариант - это объявить `object` элементы меню, которые могут иметь собственные модификаторы:
    ```kotlin
    enum class NavigationTab(
            @StringRes labelId: Int,
            @StringRes iconId: Int,
            @StringRes iconSelectedId: Int
    ) : NavigationItem {
        NOTIFICATION(R.string.navigation_notifications, R.string.design_mobile_icon_notification, R.string.design_mobile_icon_notification_filled),
        MESSAGES(R.string.navigation_messages, R.string.design_mobile_icon_menu_messages, R.string.design_mobile_icon_menu_messages_filled);
        
        /**
         * В данном случае текст пунктов меню и иконки неизменны
         */
        override val labelObservable: Observable<NavigationItemLabel> = Observable.just(NavigationItemLabel(labelId))
        override val iconObservable: Observable<NavigationItemIcon> = Observable.just(NavigationItemIcon(iconId, iconSelectedId))
    }
    
    /**
     * Пример реализации пункта меню с собственными модификаторами [changeIcons]
     */
    object Calendar : NavigationItem {
    
        private val iconSubject = BehaviorSubject.createDefault(
                NavigationItemIcon(R.string.design_mobile_icon_calendar, R.string.design_mobile_icon_calendar_filled))
    
        override val labelObservable: Observable<NavigationItemLabel> = Observable.just(NavigationItemLabel(R.string.navigation_calendar))
        override val iconObservable: Observable<NavigationItemIcon> = iconSubject
    
        @JvmStatic
        fun changeIcons(@StringRes iconId: Int, @StringRes iconSelectedId: Int) {
            iconSubject.onNext(NavigationItemIcon(iconId, iconSelectedId))
        }
    }
    ```
    Несмотря на элегантность такой реализации, важно понимать, что ближайшим общим супертипом является интерфейс `NavigationItem`, а это, в свою очередь, потребует дополнительных проверок в местах использования, где важны детали.  
    Этот подход можно применить только в случае, когда бизнес-логика слабо связана с деталями реализаций `NavigationItem`.
 
##### Подписка на события навигации
```kotlin
navAdapter.navigationEvents.observe(lifecycleOwner, Observer { event ->
    when (event) {
        is ItemSelectedByUser -> event.run { Timber.d("%s selected from %s", selectedItem, sourceName) }
        is ItemSelected -> event.run { Timber.d("%s selected programmatically", selectedItem) }
        AllItemsUnselected -> Timber.d("All navigation items was unselected")
    }
})
```

##### Форматирование счётчиков
Форматирование счётчиков по умолчанию реализовано с помощью метода `ru.tensor.sbis.design.utils.formatCount(count: Int)`. Чтобы изменить форматирование конкретного счётчика, необходимо переопределить метод `getFormatter(type: FormatterType)` из интерфейса `NavigationCounter` таким образом, чтобы он возвращал функцию, реализующую необходимое преобразование значения счетчика в строковое значение (при этом, если отображать счетчик не нужно, функция должна возвращать `null` или пустую строку). Для форматирования чисел без сокращений с пробелами у старших разрядов есть метод `ru.tensor.sbis.design.utils.formatCountSimple(count: Int)` . `FormatterType` не включает формат для ННП - в этом случае всегда применяется форматирование по умолчанию (см. документацию к компоненту Счётчик).  
Пример:
```kotlin
class MyCounter : NavigationCounter {
    
    private val myTotalFormatter = object : Function<Int, String?> {
        private val format = DecimalFormat(
                "#,###",
                DecimalFormatSymbols().apply { groupingSeparator = ' ' }
        )
        override fun apply(count: Int): String? = if (count < 1) null else format.format(count)
    }

    override fun getFormatter(type: FormatterType): Function<Int, String?> = when(type) {
        FormatterType.NAV_VIEW_NEW   -> DEFAULT_FORMAT
        FormatterType.NAV_VIEW_TOTAL -> myTotalFormatter
    }
}

class MySimpleCounter : NavigationCounter {
    
    private val myTotalFormatter = object : Function<Int, String?> {
        private val format = DecimalFormat(
                "#,###",
                DecimalFormatSymbols().apply { groupingSeparator = ' ' }
        )
        override fun apply(count: Int): String? = if (count < 1) null else format.format(count)
    }

    override fun getFormatter(type: FormatterType): Function<Int, String?> = when(type) {
        FormatterType.NAV_VIEW_NEW   -> SIMPLE_FORMAT
        FormatterType.NAV_VIEW_TOTAL -> myTotalFormatter
    }
}
```

##### Добавление дополнительного контента в элементы аккордеона
Оформить view дополнительного контента в виде xml ресурса:
```xml
<!-- Важно применить тему из атрибута navItemTheme, чтобы рабоало выделение элемента -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              android:theme="?attr/navItemTheme" 
              android:orientation="horizontal"
              tools:theme="@style/NavView">

    <!-- Прикладные UI элементы -->

</LinearLayout>
```
Реализовать модель дополнительного контента:
```kotlin
internal class CallItemContent private constructor(
    private val counterDisposable: SerialDisposable
) : NavigationItemContent<View>, Disposable by counterDisposable {

    constructor(): this(SerialDisposable())

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup): View {
        // создание view из xml или конструктором. Тут можно запомнить view для дальнейшего использования
    }

    override fun onContentOpened(view: View) {
        // обновление внешнего вида и добавление подписок
    }

    override fun onContentClosed(view: View) {
        // освобождение ресурсов
    }
}
```
Добавить элемент меню с дополнительным контентом и счётчиком, если нужен:
```kotlin
navAdapter.add(NavigationTab.CALL, CallItemContent() /*, CallCounter()*/)
```

Пример реализации в демо приложении.

##### Особенности работы `NavAdapter`
- Вызов метода `disposable()` прекращает работу подписок на обновления счётчиков.

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)