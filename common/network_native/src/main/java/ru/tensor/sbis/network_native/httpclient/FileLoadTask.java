package ru.tensor.sbis.network_native.httpclient;

import java.io.File;

import okhttp3.Call;

/**
 * Класс, описывающий задачу по загрузке файла
 * <p>
 * Created by Nail Sharipov on 05/06/15.
 */
public class FileLoadTask {

    private final Call call;
    private final File file;
    private boolean canceled;
    private boolean finished;

    @SuppressWarnings("unused")
    public boolean isFinished() {
        return finished;
    }

    public boolean isCanceled() {
        return canceled;
    }

    FileLoadTask(Call call, File file) {
        this.call = call;
        this.file = file;
        this.canceled = false;
        this.finished = false;
    }

    void finish() {
        this.finished = true;
    }

    /**
     * Отменить загрузку файла
     */
    @SuppressWarnings("unused")
    public void cancel() {
        if (!finished && !canceled) {
            canceled = true;
            call.cancel();
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

}
