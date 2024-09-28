package ru.tensor.sbis.network_native.httpclient;

import ru.tensor.sbis.network_native.error.SbisError;

/**
 * Интерфейс описывает события возникающие при загрузке файла
 * <p>
 * Created by Nail Sharipov on 05/06/15.
 */
public interface FileDownloadEvent {

    /**
     * Вызвается при успешном сохранение файла на диске
     */
    void onSuccess();

    /**
     * Вызвается при возникновении ошибки в процессе загрузки файла
     *
     * @param error описание возникшей ошибки.
     */
    void onFail(SbisError error);

    /**
     * Вызвается в процессе сохранения файла и возврощает % загруженной части файла
     *
     * @param percent процент загруженной части файла.
     */
    void onProgress(int percent);

}
