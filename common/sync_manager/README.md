# Компонент синхронизации SyncManager

Синхронизация построена на базе компонента системы *Android* - `SyncAdapter`.
Подробности по работе механизма можно найти в [документации](https://developer.android.com/reference/android/content/AbstractThreadedSyncAdapter).
Синхронизация выполнеяется автоматически на уровне системы. 
Также есть механизм ручного управления запуском синхронизации - [SyncManager](https://git.sbis.ru/mobileworkspace/android-serviceAPI/-/blob/08bad5b345bbd4be23df93b7e52e45c0e395c7dd/declaration/src/main/java/ru/tensor/sbis/declaration/SyncManager.kt).

С нашей стороны сделана дополнительная надстройка в виде [ModuleSyncAdapter](https://git.sbis.ru/mobileworkspace/android-utils/-/blob/03ce503d46dc29b9fefc6e15f02b0974b4fd8100/sbis-common/src/main/java/ru/tensor/sbis/common/ModuleSyncAdapter.kt), 
позволяющей прикладным модулям регистрировать свои компонеты 
(унаследованные от `ru.tensor.sbis.common.ModuleSyncAdapter`) для участия в синхронизации.

Упрощенная схема вызова синхронизации выглдит следующим образом:
запрос на синхронизацию (от системы или от пользователя) -> `SyncService` -> `SyncAdapter` -> `ModuleSyncAdapter`.

### Активация в приложении
На уровне приложения регистрируется [SyncManagerPlugin](https://git.sbis.ru/mobileworkspace/android-utils/-/blob/03ce503d46dc29b9fefc6e15f02b0974b4fd8100/sync_manager/src/main/java/ru/tensor/sbis/sync_manager/SyncManagerPlugin.kt) в плагинную систему. Он позволяет автоматически управлять синхронизацией на основе событий авторизации, а также автоматически собирать прикладные `ModuleSyncAdapter` для последующего их выполнения.

### FAQ:
1. Q: Почему может возникать исключение `java.lang.InterruptedException` в ходе выполения синхронизации?
   A: Согласно документации, системный компонент `AbstractThreadedSyncAdapter`, на базе которого построено наше решение, 
   пораждает новый поток при запуске. Позже система может позвать (по разным причинам) `Thread#interrupt()`, 
   что приведет к возникновению данного исключения. 
   Рекомендуется либо в явном виде внутри реализации `AbstractThreadedSyncAdapter` делать проверку на `Thread#interrupted()`, 
   либо должным образом реализовать `AbstractThreadedSyncAdapter#onSyncCanceled()`. 
   В качестве дополнительной информации есть [обсуждение](https://github.com/ReactiveX/RxJava/issues/1804#issuecomment-62131666) 
   по возникновению данного исключения в связке с RxJava.

#### Используется в модулях:
- [Новости](https://git.sbis.ru/mobileworkspace/android-news)
- [Увдомления](https://git.sbis.ru/mobileworkspace/apps/droid/waiter2)
- [Выездные работы](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Гео-трекинга](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)

#### Используется в приложениях:
- [СБИС Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [SABY Задачи](https://git.sbis.ru/mobileworkspace/apps/droid/saby-tasks)
- [СБИС Курьер](https://git.sbis.ru/mobileworkspace/apps/droid/courier)

#### Дополнительная информация
- Ответственный: [Быков Д.Ю](https://online.sbis.ru/person/1aee1e1d-892b-480e-8131-b6386b5b7bc0)