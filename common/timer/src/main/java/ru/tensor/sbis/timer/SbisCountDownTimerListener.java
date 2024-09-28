package ru.tensor.sbis.timer;

/**
 * Слушатель событий
 * @author am.boldinov
 */
public interface SbisCountDownTimerListener {

    /**
     * Событие "тика".
     */
    void onTick(long millisUntilFinished, String callerObject);

    /**
     * Событие окончания работы
     */
    void onFinish(String callerObject);

    /**
     * Событие возобновления работы после остановки
     */
    void onResume(long millisUntilFinished, String callerObject);

}
