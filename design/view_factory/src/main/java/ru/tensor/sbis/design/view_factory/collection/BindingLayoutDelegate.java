package ru.tensor.sbis.design.view_factory.collection;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * Делегат макета с одним проходом onMeasure для подготовки макета и последующим ребиндингом данных.
 * @param <T> - тип макета
 *
 * @author am.boldinov
 */
public abstract class BindingLayoutDelegate<T extends ViewGroup & DelegatingLayout> extends LayoutDelegate<T> {

    /**
     * Флаг сигнализирующий о том, готов ли макет к заполнению данными.
     */
    private boolean mPrepared = false;

    /**
     * Флаг, сигналиризующий о том, что необходимо выполнить привязку данных.
     */
    private boolean mRequiredBinding = true;

    /**
     * Конструктор для создания объекта {@link BindingLayoutDelegate}.
     *
     * @param view view, который необходимо построить.
     * */
    public BindingLayoutDelegate(@NonNull T view) {
        super(view);
    }

    /**
     * Запрашиваем биндинг на следующий onMeasure.
     */
    protected void requestBinding() {
        mRequiredBinding = true;
    }

    /**
     * Запустить перестроение макета. Должен быть вызван в случае,
     * если изменились параметры макета или отображаемые данные.
     */
    public void processLayout() {
        if (mPrepared) {
            mPrepared = false;
            layout.requestLayout();
        }
    }

    /**
     * В зависимости от значения флага {@link #mPrepared} выполняется либо подготовка макета
     * к отображению данных и привязка данных, либо только привязка данных (в случае если макет
     * уже подготовлен).
     *
     * @return true - если подготовка макета прошла успешно или не требуется, false - иначе
     */
    @Override
    public boolean onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int maxWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        final int availableWidth = maxWidth - layout.getPaddingLeft() - layout.getPaddingRight();
        if (availableWidth > 0) {
            // Проверяем валидность конфигурации
            if (needInvalidateConfiguration(widthMeasureSpec, heightMeasureSpec)) {
                mPrepared = false;
            }
            if (!mPrepared) {
                // Вычисляем размер
                boolean result = measure(widthMeasureSpec, heightMeasureSpec);
                if (result) {
                    // Привязываем данные
                    bindDataOnMeasure();
                    mPrepared = true;
                    return true;
                }
                // Не удалось вычислить конфигурацию:
                // Удаляем все дочерние view
                layout.removeAllViewsInLayout();
                return false;
            } else {
                // Конфигурация уже вычислена:
                if (mRequiredBinding) {
                    // Привязываем данные
                    bindDataOnMeasure();
                }
                layout.setMeasuredDimensions(layout.getMeasuredWidth(), layout.getMeasuredHeight());
                return true;
            }
        }
        return false;
    }

    /**
     * Нужно ли пересчитать конфигурацию.
     * @return true - необходимо пересчитать конфигурацию, false - иначе
     */
    protected boolean needInvalidateConfiguration(int widthMeasureSpec, int heightMeasureSpec) {
        return false;
    }

    /**
     * Выполнить привязку данных и сбросить флаг биндинга.
     * Может быть вызван только внутри {@link #onMeasure(int, int)}.
     */
    protected void bindDataOnMeasure() {
        bindDataInLayout();
        mRequiredBinding = false;
    }

    /**
     * Выполнить привязку данных к макету.
     */
    protected abstract void bindDataInLayout();

    /**
     * Выполнить вычисление размеров макета.
     *
     * @return true - если вычисление размеров выполнено успешно (в этом случае внутри метода
     * должен быть вызван метод {@link DelegatingLayout#setMeasuredDimensions(int, int)}
     * для перезадчи размеров макету), false - иначе
     */
    protected abstract boolean measure(int widthMeasureSpec, int heightMeasureSpec);

}
