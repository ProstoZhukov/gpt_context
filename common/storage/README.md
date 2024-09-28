#### Компоненты предоставляющие доступ к внутреннему/внешнему хранилищу.

|Класс|Ответственные|Добавить|
|-----|-------------|--------|
|[InternalStorageProvider](src/main/java/ru/tensor/sbis/storage/contract/InternalStorageProvider.kt)|[Мартышенко К.В.](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|
|[ExternalStorageProvider](src/main/java/ru/tensor/sbis/storage/contract/ExternalStorageProvider.kt)|[Мартышенко К.В.](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|
|[StorageFeature](src/main/java/ru/tensor/sbis/storage/contract/StorageFeature.kt)|[Мартышенко К.В.](https://online.sbis.ru/person/7ae2600c-8e7c-4c7a-aafe-7ff6f2fd34ea)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|
|[SbisExternalStorage](src/main/java/ru/tensor/sbis/storage/external/SbisExternalStorage.kt)|[Никитин С.А.](https://online.sbis.ru/person/312e2356-e6c6-4cfa-8db0-a5b1daae1736)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|
|[SbisInternalStorage](src/main/java/ru/tensor/sbis/storage/internal/SbisInternalStorage.kt)|[Никитин С.А.](https://online.sbis.ru/person/312e2356-e6c6-4cfa-8db0-a5b1daae1736)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|
|[SbisStorage](src/main/java/ru/tensor/sbis/storage/SbisStorage.kt)|[Никитин С.А.](https://online.sbis.ru/person/312e2356-e6c6-4cfa-8db0-a5b1daae1736)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|

##### Описание
[SbisStorage](src/main/java/ru/tensor/sbis/storage/SbisStorage.kt) представляет базовый класс с методом для получения папки текущего пользователя.

[SbisInternalStorage](src/main/java/ru/tensor/sbis/storage/internal/SbisInternalStorage.kt) и [SbisExternalStorage](src/main/java/ru/tensor/sbis/storage/SbisExternalStorage.kt) расширяют [SbisStorage](src/main/java/ru/tensor/sbis/storage/SbisStorage.kt), предоставляя более специфичный функионал для каждого из хранилищ.

[InternalStorageProvider](src/main/java/ru/tensor/sbis/storage/contract/InternalStorageProvider.kt) и [ExternalStorageProvider](src/main/java/ru/tensor/sbis/storage/contract/ExternalStorageProvider.kt) предназначены для использования в прикладных модулях - через них должен осуществляться доступ к объектам хранилищ.

[StorageFeature](src/main/java/ru/tensor/sbis/storage/contract/StorageFeature.kt) является реализацией [InternalStorageProvider](src/main/java/ru/tensor/sbis/storage/contract/InternalStorageProvider.kt) и [ExternalStorageProvider](src/main/java/ru/tensor/sbis/storage/contract/ExternalStorageProvider.kt) и предназначен для использования на уровне приложения (**прикладные модули не должны напрямую использовать этот класс**).

##### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [SMS](https://git.sbis.ru/mobileworkspace/apps/droid/sms)
- [СБИС на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)
- [Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)
- [СБИС маркет](https://git.sbis.ru/mobileworkspace/apps/droid/appmarket)
- [Экран повара](https://git.sbis.ru/mobileworkspace/apps/droid/cookscreen)
- [Мобильный официант](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [СБИС касса](https://git.sbis.ru/mobileworkspace/apps/droid/retail)