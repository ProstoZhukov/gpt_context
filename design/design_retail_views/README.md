# Модуль с набором прикладных View элементов используемых в МП Розница и МП Presto

| Ответственность | Ответственные |
|-----------------|---------------|
| Разработка | [Павлов Д.А.](https://online.sbis.ru/person/79a90449-389a-4b55-85cb-50758063289d) |
|-------|---------------|
| Участок работ | [СБИС Касса Android](https://online.sbis.ru/area/04b1601c-7028-4c8a-8450-f0e3d7f1f830) |
| Участок работ | [СБИС Касса Android](https://online.sbis.ru/area/a6372a6f-8ab0-4aee-b78c-982e3cf29db1) |

## Использование в приложениях
[Saby Courier](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
[СБИС Касса, СБИС Presto](https://git.sbis.ru/mobileworkspace/apps/droid/retail)

## Описание
Модуль представляет собой набор прикладных View компонентов, а также утилит для работы с ними.
Прикладные компоненты используются в МП СБИС Касса, МП СБИС Presto и Saby Courier.

Содержит прикладные View:
- [BanknoteView](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/banknote/BanknoteView.kt)
- [BonusButtonView](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/bonus_button/BonusButtonView.kt)
- [CardViewButton](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/card_button/CardViewButton.kt)
- [DoubleButton](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/double_button/DoubleButton.kt)
- [MoneyInputView](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/money_input/MoneyInputView.kt)
- [MoneyInputVieMoneyInputEditableField](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/money_input_field/MoneyInputEditableField.kt)
- [MoneyView](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/money_view/MoneyView.kt)
- [NumericKeyboard](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/numberic_keyboard/NumericKeyboard.kt)
- [PaymentView](README_payment_view.md)
- [ShadowScrollView](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/shadow_scroll_view/ShadowScrollView.kt)
- [TooltipView](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_views/src/main/java/ru/tensor/sbis/design/retail_views/tooltip/TooltipView.kt)

## Руководство по подключению и инициализации
1. При необходимости объявляем модуль в соответствующем `settings.gradle`:
```gradle
include ':design_retail_views'
project(':design_retail_views').projectDir = new File("<путь до модуля>/design_retail_views")
```
2. Добавляем в раздел зависимостей `build.gradle` модуля-потребителя:
```gradle
dependencies {
    implementation project(':module_1')
    implementation project(':module_2')
    ...
}
```
