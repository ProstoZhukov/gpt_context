package ru.tensor.sbis.richtext.span.view.table;

import android.text.Spannable;

import androidx.annotation.NonNull;
import ru.tensor.sbis.jsonconverter.generated.Cell;
import ru.tensor.sbis.jsonconverter.generated.MeasureConstraint;

/**
 * Модель-представление ячейки таблицы
 *
 * @author am.boldinov
 */
public final class TableCell {

    /**
     * Размер занимаемого пространства ячейки относительно других по умолчанию
     */
    public static final int DEFAULT_SPAN = 1;

    @NonNull
    public static final MeasureConstraint MEASURE_CONSTRAINT_BY_CONTENT = new MeasureConstraint();

    private Spannable mContent;
    @NonNull
    private final Cell mCell = new Cell(MEASURE_CONSTRAINT_BY_CONTENT, MEASURE_CONSTRAINT_BY_CONTENT, DEFAULT_SPAN, DEFAULT_SPAN);

    /**
     * Возвращает содержимое ячейки в виде стилизованного текста
     */
    @NonNull
    public Spannable getContent() {
        return mContent;
    }

    /**
     * Устанавливает содержимое ячейки
     */
    public void setContent(@NonNull Spannable content) {
        mContent = content;
    }

    /**
     * Устанавливает ограничения для измерения ячейки
     */
    public void setWidthMeasureConstraint(@NonNull MeasureConstraint constraint) {
        mCell.setWidthConstraint(constraint);
    }

    /**
     * Устанавливает размер занимаемого пространства ячейкой относительно других ячеек по вертикали
     * (относительно ячеек, которые расположены слева и справа от текущей)
     */
    public void setColSpan(int colSpan) {
        mCell.setColspan(colSpan);
    }

    /**
     * Устанавливает размер занимаемого пространства ячейкой относительно других ячеек по горизонтали
     * (относительно ячеек, которые расположены выше и ниже)
     */
    public void setRowSpan(int rowSpan) {
        mCell.setRowspan(rowSpan);
    }

    /**
     * Возвращает модель-представление ячейки, которая будет использоваться в измерениях View
     */
    @NonNull
    public Cell getMeasureCell() {
        return mCell;
    }
}
