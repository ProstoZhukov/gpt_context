package ru.tensor.sbis.richtext.span.view.table;

import java.util.List;

import androidx.annotation.NonNull;
import ru.tensor.sbis.jsonconverter.generated.PrecomputedTable;
import ru.tensor.sbis.jsonconverter.generated.PrecomputedTableItem;

/**
 * Вью-модель с данными для биндинга во View с таблицей
 *
 * @author am.boldinov
 */
public final class TableViewData {

    @NonNull
    private final List<TableCell> mCells;
    @NonNull
    private final PrecomputedTable mTable;
    private final int mItemCount;

    public TableViewData(@NonNull List<TableCell> cells,
                         @NonNull PrecomputedTable table) {
        mCells = cells;
        mTable = table;
        mItemCount = table.getItems().size();
    }

    /**
     * Возвращает модель ячейки по позиции
     */
    @NonNull
    public TableCell getCell(int position) {
        if (position < 0 || position >= getCellCount()) {
            throw new IndexOutOfBoundsException("Attempt to get cell for position " + position);
        }
        final int ordinal = mTable.getItems().get(position).getOrdinal();
        return mCells.get(ordinal);
    }

    /**
     * Возвращает позицию ячейки в таблице на основе ее порядкового номера
     */
    public int getCellPosition(int ordinal) {
        if (isShrink()) {
            final List<PrecomputedTableItem> items = mTable.getItems();
            if (ordinal < items.size() && items.get(ordinal).getOrdinal() == ordinal) {
                return ordinal;
            }
            for (int i = 0; i < items.size(); i++) {
                final PrecomputedTableItem item = items.get(i);
                if (item.getOrdinal() == ordinal) {
                    return i;
                }
            }
            return 0;
        } else {
            return ordinal;
        }
    }

    /**
     * Является ли таблица ограниченной по размеру
     */
    public boolean isShrink() {
        return mItemCount < mCells.size();
    }

    /**
     * Возвращает общее число ячеек в таблице
     */
    public int getCellCount() {
        return mItemCount;
    }

    /**
     * Возвращает модель-представление таблицы для использования в измерениях ее размеров
     */
    @NonNull
    public PrecomputedTable getTable() {
        return mTable;
    }
}
