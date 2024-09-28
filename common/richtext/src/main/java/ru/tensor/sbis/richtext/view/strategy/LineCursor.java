package ru.tensor.sbis.richtext.view.strategy;

/**
 * Курсор для перемещения по строкам текста
 *
 * @author am.boldinov
 */
public final class LineCursor {

    private int mLine = -1;

    /**
     * Открывает курсор
     *
     * @param line номер строки для первичного позиционирования
     */
    public void open(int line) {
        if (mLine != -1) {
            throw new IllegalStateException("You must close the cursor before opening again");
        }
        mLine = line;
    }

    /**
     * Возвращает текущий номер строки
     */
    public int get() {
        return mLine;
    }

    /**
     * Перемещается на предыдущую строку
     */
    public void moveToPrev() {
        ensureOpenCursor();
        mLine--;
    }

    /**
     * Перемещается на следующую строку
     */
    public void moveToNext() {
        ensureOpenCursor();
        mLine++;
    }

    /**
     * Перемещается на строку
     *
     * @param line номер строки для перемещения
     */
    public void moveTo(int line) {
        ensureOpenCursor();
        mLine = line;
    }

    /**
     * Закрывается, все последующие операции доступны после повторного открытия {@link #open(int)}
     */
    public void close() {
        mLine = -1;
    }

    private void ensureOpenCursor() {
        if (mLine == -1) {
            throw new IllegalStateException("You must open the cursor before processing");
        }
    }
}
