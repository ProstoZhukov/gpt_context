package ru.tensor.sbis.richtext.span.view.table;

import android.text.Spannable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.jsonconverter.generated.PrecomputedTable;
import ru.tensor.sbis.jsonconverter.generated.Row;
import ru.tensor.sbis.jsonconverter.generated.Table;
import ru.tensor.sbis.jsonconverter.generated.TableShrinkParams;
import ru.tensor.sbis.jsonconverter.generated.TablesController;
import ru.tensor.sbis.richtext.span.view.ContentAttributesVM;
import ru.tensor.sbis.richtext.view.RichViewLayout;

/**
 * Вью-модель атрибутов таблицы
 *
 * @author am.boldinov
 */
public final class TableAttributesVM extends ContentAttributesVM {

    @NonNull
    private final ArrayList<Row> mRows = new ArrayList<>();
    @NonNull
    private final List<TableCell> mCells = new ArrayList<>();
    @NonNull
    private final TableShrinkParams mShrinkParams;
    @NonNull
    private final TableFullProvider mFullViewData = new TableFullProvider() {
        @NonNull
        @Override
        public TableViewData get() {
            return computeViewData(null);
        }
    };
    @Nullable
    private TableViewData mViewData;

    public TableAttributesVM(@NonNull String tag, @NonNull TableShrinkParams shrinkParams) {
        super(tag);
        mShrinkParams = shrinkParams;
    }

    /**
     * Возвращает вью-модель с данными для биндинга во View
     */
    @NonNull
    public TableViewData getViewData() {
        if (mViewData == null) {
            computeViewData();
        }
        return mViewData;
    }

    /**
     * Возвращает поставщик вью-модели с полными данными по таблице (игнорируя ShrinkParams)
     * для биндинга во View
     */
    @NonNull
    public TableFullProvider getFullViewData() {
        return mFullViewData;
    }

    /**
     * Подготавливает данные для рендера таблицы и вычисляет содержимое вью-модели для отрисовки
     */
    public void computeViewData() {
        mViewData = computeViewData(mShrinkParams);
    }

    /**
     * Добавляет новую строку в таблицу
     */
    public void addRow() {
        mRows.add(new Row());
    }

    /**
     * Добавляет новую ячейку к последней строке таблицы.
     * В случае отсутствия строк - создаст новую.
     */
    @NonNull
    public TableCell addCell() {
        if (mRows.isEmpty()) {
            addRow();
        }
        final TableCell cell = new TableCell();
        mCells.add(cell);
        mRows.get(mRows.size() - 1).getCells()
                .add(cell.getMeasureCell());
        return cell;
    }

    /**
     * Возвращает последнюю ячейку таблицы
     */
    @Nullable
    public TableCell getLastCell() {
        if (mCells.isEmpty()) {
            return null;
        }
        return mCells.get(mCells.size() - 1);
    }

    @Override
    protected int size() {
        return mCells.size();
    }

    @NonNull
    @Override
    protected Spannable getContent(int index) {
        return mCells.get(index).getContent();
    }

    @NonNull
    @Override
    public RichViewLayout.ViewHolderFactory createViewHolderFactory() {
        return (parent) -> {
            final RichTableLayout layout = new RichTableLayout(parent.getContext(), parent.getRecycledViewPool(), parent.getRichViewFactory());
            layout.setLayoutParams(new RichViewLayout.LayoutParams(RichViewLayout.LayoutParams.MATCH_PARENT, RichViewLayout.LayoutParams.WRAP_CONTENT));
            return new TableViewHolder(layout);
        };
    }

    @NonNull
    private TableViewData computeViewData(@Nullable TableShrinkParams params) {
        TableShrinkParams shrinkParams = null;
        if (params != null) {
            final int desiredCells = params.getRowsLimit() * params.getColumnsLimit();
            if (desiredCells == 0) { // одна из сторон может быть без ограничений
                shrinkParams = params;
            } else if (mCells.size() > desiredCells) {
                int rowsLimit = params.getRowsLimit();
                int columnsLimit = params.getColumnsLimit();
                if (mRows.size() < rowsLimit) {
                    rowsLimit = Math.max(mRows.size(), 1);
                    columnsLimit = desiredCells / rowsLimit;
                } else if (mRows.size() > rowsLimit) {
                    final int columnsSize = calculateColumnCount();
                    if (columnsSize < columnsLimit) {
                        columnsLimit = Math.max(columnsSize, 1);
                        rowsLimit = desiredCells / columnsLimit;
                    }
                }
                shrinkParams = new TableShrinkParams(columnsLimit, rowsLimit);
            }
        }
        final PrecomputedTable table = TablesController.getPrecomputedTable(new Table(mRows,
                TableCell.MEASURE_CONSTRAINT_BY_CONTENT,
                TableCell.MEASURE_CONSTRAINT_BY_CONTENT), shrinkParams);
        return new TableViewData(mCells, table);
    }

    private int calculateColumnCount() {
        int max = 0;
        for (int i = 0; i < mRows.size(); i++) {
            max = Math.max(mRows.get(i).getCells().size(), max);
        }
        return max;
    }

    private static final class TableViewHolder extends RichViewLayout.ViewHolder<TableAttributesVM> {

        @NonNull
        private final RichTableLayout mTableLayout;

        public TableViewHolder(@NonNull RichTableLayout view) {
            super(view);
            mTableLayout = view;
        }

        @Override
        public void bind(@NonNull TableAttributesVM attributesVM) {
            mTableLayout.setViewData(attributesVM.getViewData(), attributesVM.getFullViewData());
        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            mTableLayout.recycle();
        }
    }
}
