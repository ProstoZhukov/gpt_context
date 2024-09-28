#### Таймер с обратным отсчётом, с регулярными уведомлениями через заданные промежутки времени.

|Класс|Ответственные|Добавить|
|-----|-------------|--------|
|[SbisCountDownTimer](src/main/java/ru/tensor/sbis/timer/SbisCountDownTimer.java)|[Болдинов А.М.](https://online.sbis.ru/person/24f28dc0-4a33-4cb9-9c87-8be072ea0e0c)|[Задачу/поручение/ошибку](https://online.sbis.ru/area/d5cff451-8688-4af0-970a-8127570b0308)|

#### Использование в приложениях
- [Коммуникатор](https://git.sbis.ru/mobileworkspace/apps/droid/communicator)
- [Мобильная витрина SabyGet](https://git.sbis.ru/mobileworkspace/apps/droid/showcase)
- [СБИС на складе](https://git.sbis.ru/mobileworkspace/apps/droid/storekeeper)
- [Бизнес](https://git.sbis.ru/mobileworkspace/apps/droid/business)

##### Описание
*Таймер* позволяет уведомлять подписчиков о периодических "тиках" через заданные интервалы времени.

Создание:
- **SbisCountDownTimer(long millisInFuture, long countDownInterval, SbisCountDownTimerListener listener)**
- **SbisCountDownTimer(String callerObject, long millisInFuture, long countDownInterval, SbisCountDownTimerListener listener)**

Запуск:
**fun startTimer()**

Остановка:
**fun cancelTimer()**


###### Слушатель событий
[SbisCountDownTimerListener](src/main/java/ru/tensor/sbis/timer/SbisCountDownTimerListener.java)

**fun onTick(long millisUntilFinished, String callerObject)** - событие "тика".
**fun onFinish(String callerObject)** - событие окончания работы.
**fun onResume(long millisUntilFinished, String callerObject)** - событие возобновления работы после остановки.


###### Сервис
[SbisTimerService](src/main/java/ru/tensor/sbis/timer/SbisTimerService.java)
Представляет собой сервис, уведомляющий о событиях таймера через EventBus.
В качестве события передаётся объект класса
**class SbisTimerEvent(Event event, long millisUntilFinished, String callerObjectName)**

Событие **Event:**
    - TICK
    - RESUME
    - FINISH
