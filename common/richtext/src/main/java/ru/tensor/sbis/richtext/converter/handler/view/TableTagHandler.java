package ru.tensor.sbis.richtext.converter.handler.view;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.jsonconverter.generated.MeasureConstraint;
import ru.tensor.sbis.jsonconverter.generated.MeasureConstraintKind;
import ru.tensor.sbis.jsonconverter.generated.TableShrinkParams;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.cfg.TableConfiguration;
import ru.tensor.sbis.richtext.converter.cfg.TableSize;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.converter.handler.base.MultiTagWrapper;
import ru.tensor.sbis.richtext.span.view.ViewSizeType;
import ru.tensor.sbis.richtext.span.view.table.TableAttributesVM;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.HtmlTag;
import ru.tensor.sbis.richtext.span.view.table.TableCell;
import timber.log.Timber;

/**
 * Обработчик тегов для таблиц.
 * Включает в себя обработку всех тегов и атрибутов для рендера ячеек, заголовков, стилей таблицы.
 *
 * @author am.boldinov
 */
public final class TableTagHandler extends ContentViewTagHandler implements MultiTagWrapper {

    @NonNull
    private final TableShrinkParams mShrinkParams;

    @Nullable
    private String mCurrentTag;

    public TableTagHandler(@NonNull Context context, @NonNull TableConfiguration configuration) {
        super(context);
        final TableSize size = configuration.getTableSize();
        mShrinkParams = new TableShrinkParams(size.getColumnLimit(), size.getRowLimit());
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        if (isTag(HtmlTag.TABLE)) {
            super.onStartTag(stream, attributes);
        } else {
            final TableAttributesVM vm = getTableVM();
            if (vm != null) {
                if (isTag(HtmlTag.TABLE_ROW)) {
                    vm.addRow();
                } else if (isTag(HtmlTag.TABLE_CELL) || isTag(HtmlTag.TABLE_HEADER)) {
                    final TableCell cell = vm.addCell();
                    cell.setRowSpan(getAsInt(attributes, "rowspan", TableCell.DEFAULT_SPAN));
                    cell.setColSpan(getAsInt(attributes, "colspan", TableCell.DEFAULT_SPAN));
                    final MeasureConstraint widthConstraint = getCellWidthConstraint(attributes);
                    if (widthConstraint != null) {
                        cell.setWidthMeasureConstraint(widthConstraint);
                    }
                    startContent(stream);
                }
            }
        }
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        final TableAttributesVM vm = getTableVM();
        if (isTag(HtmlTag.TABLE)) {
            if (vm != null) {
                vm.computeViewData();
            }
            super.onEndTag(stream);
        } else if (vm != null) {
            if (isTag(HtmlTag.TABLE_CELL) || isTag(HtmlTag.TABLE_HEADER)) {
                final TableCell lastCell = vm.getLastCell();
                final Spannable content = stopContent(stream);
                if (lastCell != null) {
                    lastCell.setContent(content);
                }
            }
        }
    }

    @Override
    public void recycle() {
        super.recycle();
        mCurrentTag = null;
    }

    @NonNull
    @Override
    protected BaseAttributesVM createAttributesVM(@NonNull TagAttributes attributes) {
        return new TableAttributesVM(attributes.getTag(), mShrinkParams);
    }

    @Override
    public void setCurrentTag(@NonNull String tag) {
        mCurrentTag = tag;
    }

    private boolean isTag(@NonNull String tag) {
        return mCurrentTag != null && mCurrentTag.equals(tag);
    }

    @Nullable
    private MeasureConstraint getCellWidthConstraint(@NonNull TagAttributes attributes) {
        final String widthStyle = HtmlHelper.parseCssStyleValue(attributes.getValue("style"), "width");
        if (widthStyle != null) {
            return parseConstraintValue(widthStyle);
        }
        final String width = attributes.getValue("width");
        if (width != null) {
            return parseConstraintValue(width);
        }
        return null;
    }

    @Nullable
    private MeasureConstraint parseConstraintValue(@NonNull String value) {
        final ViewSizeType sizeType = ViewSizeType.detect(value);
        if (sizeType != null) {
            try {
                int size = Math.round(Float.parseFloat(value.replace(sizeType.getType(), "")));
                if (sizeType == ViewSizeType.PIXEL) {
                    size = Math.round(size * getDisplayMetrics().density);
                }
                return new MeasureConstraint(getConstraintKind(sizeType), size);
            } catch (Exception e) {
                Timber.e(e, "Unable to parse constraint value: %s", value);
            }
        }
        return null;
    }

    @NonNull
    private DisplayMetrics getDisplayMetrics() {
        return mContext.getResources().getDisplayMetrics();
    }

    @NonNull
    private static MeasureConstraintKind getConstraintKind(@NonNull ViewSizeType sizeType) {
        switch (sizeType) {
            case PERCENT:
                return MeasureConstraintKind.PERCENT;
            case PIXEL:
            default:
                return MeasureConstraintKind.PIXEL;
        }
    }

    @Nullable
    private TableAttributesVM getTableVM() {
        final BaseAttributesVM vm = getCurrentVM();
        return vm instanceof TableAttributesVM ? (TableAttributesVM) vm : null;
    }

    private static int getAsInt(@NonNull TagAttributes attributes, @NonNull String key,
                                @SuppressWarnings("SameParameterValue") int defaultValue) {
        final String value = attributes.getValue(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Timber.e(e);
            }
        }
        return defaultValue;
    }
}
