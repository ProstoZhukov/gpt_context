# Модуль с набором data-моделей используемых в прикладных View элементах приложений МП Розница и МП Presto

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
Модуль представляет собой набор data-модулей для прикладных View компонентов. 

Содержит data-модели:
- [Amount](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_models/src/main/java/ru/tensor/sbis/design/retail_models/Amount.kt)
- [BonusValues](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_models/src/main/java/ru/tensor/sbis/design/retail_models/BonusValues.kt)
- [PaymentMethod](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_models/src/main/java/ru/tensor/sbis/design/retail_models/PaymentMethod.kt)
- [TaxationSystemBlockViewModels](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_models/src/main/java/ru/tensor/sbis/design/retail_models/TaxationSystemBlockViewModels.kt)
- [UiTaxSystemCode](https://git.sbis.ru/mobileworkspace/android-design/-/blob/rc-23.2100/design_retail_models/src/main/java/ru/tensor/sbis/design/retail_models/UiTaxSystemCode.kt)

## Руководство по подключению и инициализации
1. При необходимости объявляем модуль в соответствующем `settings.gradle`:
```gradle
include ':design_retail_models'
project(':design_retail_models').projectDir = new File("<путь до модуля>/design_retail_models")
```
2. Добавляем в раздел зависимостей `build.gradle` модуля-потребителя:
```gradle
dependencies {
    implementation project(':module_1')
    implementation project(':module_2')
    ...
}
```
