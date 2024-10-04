#### Счётчик
| Класс                                                                                  | Ответственные                                                                          |
|----------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| [SbisCounter](src/main/java/ru/tensor/sbis/design/counters/sbiscounter/SbisCounter.kt) | [Золотарев Даниил](https://online.sbis.ru/person/fb135a4c-c712-4f6e-b52a-6a36dfbdff5e) |

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)

##### Внешний вид
![Oval counter](doc_resources/img/oval-counter.png)
![Oval counter dark](doc_resources/img/oval-counter-dark.png)  
[Стандарт внешнего вида](http://axure.tensor.ru/MobileStandart8/#p=%D0%BD%D0%B8%D0%B6%D0%BD%D1%8F%D1%8F_%D0%BD%D0%B0%D0%B2%D0%B8%D0%B3%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D0%B0%D1%8F_%D0%BF%D0%B0%D0%BD%D0%B5%D0%BB%D1%8C__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_2___%D1%82%D0%B0%D0%B1%D0%B1%D0%B0%D1%80_&g=1)  

##### Описание
Компонент для отображения _приближённого_ целочисленного количества:  

| Правило        | Формат           | Ввод   | Вывод    |
|----------------|------------------|--------|----------|
| 0<n<1000       | Как есть         | 123    | 123      |
| 1000≤n<10000   | Тысчи.Сотни**K** | 1234   | 1.2**K** |
| 10000≤n<100000 | Десткитысяч**K** | 12345  | 12**K**  |
| n≥100000       | 99**K**          | 123456 | 99**K**  |

*Примечание:* счётчик скрывается, если его значение меньше минимального значения (см. атрибут `minCount`). 

[Обсуждение формата](https://online.sbis.ru/open_dialog.html?guid=2ecc9516-ef86-4acc-8823-7c38706cb36f&message=8bc5b5c6-42b2-4dce-b0be-4dcb929bdd77) и реализация [FormatUtils.formatCount()](../design_utils/src/main/java/ru/tensor/sbis/design/utils/FormatUtils.kt).

##### xml атрибуты
- `SbisCounter_isEnabled` - Состояние счетчика (активен/неактивен)
- `SbisCounter_backgroundColor` - Цвет фона под счётчиком
- `SbisCounter_backgroundDisabledColor` - Цвет фона под неактивным счётчиком
- `SbisCounter_textColor` - Цвет текста счетчика
- `SbisCounter_textDisabledColor` - Цвет текста неактивного счетчика
- `SbisCounter_textSize` - Размер текста счетчика
- `SbisCounter_paddingHorizontal` - Размер горизонтальных отступов счетчика
- `SbisCounter_paddingVertical` - Размер вертикальных отступов счетчика
- `SbisCounter_minCount` - минимальное значение счётчика, при котором он отображается. По умолчанию значение 1. При отрицательных значениях поведение счётчика не определено.
- `SbisCounter_formatter` - правила форматирования счётчика

##### Стилизация
Для переопределения стандартной темы компонента существуют атрибуты для каждого стиля счетчика в зависимости от места использования.

- `primaryRegularSbisCounterTheme` - Стиль акцентного счётчика, расположенного не в навигации
- `primaryNavigationSbisCounterTheme` - Стиль акцентного счётчика, расположенного в навигации
- `infoRegularSbisCounterTheme` - Стиль неакцентного счётчика, расположенного не в навигации
- `infoNavigationSbisCounterTheme` - Стиль неакцентного счётчика, расположенного в навигации

```xml
<style name="AppTheme">
    <item name="primaryRegularSbisCounterTheme">@style/CustomSbisCounterTheme</item>
</style>
```

###### Переопределение темы
Для переопределения темы рекомендуется расширить одну из [стандартных тем](src/main/res/values/theme_oval_badge.xml):

```xml
<style name="SbisCounterRegularDefaultPrimaryTheme.MyTheme"> <!-- "dot notation" способ расширения темы -->
    <item name="backgroundColor">?secondaryBackgroundColor</item> <!-- переопределение атрибута -->
</style>
```

##### Переопределение форматтера
По умолчанию используется следующий форматтер:
Если количество:
- равно 1000, то пишем 1K,
- от 1000 до 9 999, пишем с сокращением до десятых 1.1K - 9.9K (тысячи.сотни K)
- от 10 000 до 100 000, пишем 10K - 99K
- от 100 000 и более, пишем 99K

Дефолтный форматтер можно заменить на кастомный:
```kotlin
import java.util.function.Function

// ВАРИАНТ №1
viewBinding.counterCustomFormatter.formatter = CustomCounterFormatter()::apply

class CustomCounterFormatter : Function<Int, String> {
    override fun apply(count: Int): String =
        TODO(определяем какая строка будет отображаться в счетчиках)
    
}

// ВАРИАНТ №2
viewBinding.designDemoCounterCustomFormatter.formatter = ::customCounterFormatter

fun customCounterFormatter(count: Int): String =
    TODO(определяем какая строка будет отображаться в счетчиках)
```

##### Трудозатраты внедрения
0.5 ч/д
