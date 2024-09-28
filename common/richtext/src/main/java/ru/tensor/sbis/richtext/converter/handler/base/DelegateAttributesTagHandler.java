package ru.tensor.sbis.richtext.converter.handler.base;

import android.text.Editable;
import android.util.SparseArray;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.util.Predicate;

/**
 * Обработчик тега, который включает в себя набор разных атрибутов.
 * Каждый из них обрабатывается путем делегирования конкретной реализации.
 *
 * @author am.boldinov
 */
public final class DelegateAttributesTagHandler implements TagHandler {

    @Nullable
    private Map<String, TagHandler> mValueHandlerStore;
    @Nullable
    private List<ValueHandlerNode> mValueHandlerList;
    @NonNull
    private final SparseArray<String> mValueDomPositionArray = new SparseArray<>();
    @NonNull
    private final String mAttributeName;
    private int mStartDomPosition = 0;

    public DelegateAttributesTagHandler(@NonNull String attributeName) {
        mAttributeName = attributeName;
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        final String value = attributes.getValue(mAttributeName);
        findValueTagHandler(value, tagHandler -> tagHandler.onStartTag(stream, attributes));
        mStartDomPosition++;
        mValueDomPositionArray.append(mStartDomPosition, value);
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        final String value = mValueDomPositionArray.get(mStartDomPosition);
        findValueTagHandler(value, tagHandler -> tagHandler.onEndTag(stream));
        mValueDomPositionArray.remove(mStartDomPosition);
        mStartDomPosition--;
    }

    @Override
    public void recycle() {
        if (mValueHandlerStore != null) {
            for (TagHandler tagHandler : mValueHandlerStore.values()) {
                if (tagHandler != null) {
                    tagHandler.recycle();
                }
            }
        }
        if (mValueHandlerList != null) {
            for (ValueHandlerNode node : mValueHandlerList) {
                node.tagHandler.recycle();
            }
        }
        mValueDomPositionArray.clear();
        mStartDomPosition = 0;
    }

    /**
     * Регистрирует обработчик атрибута
     *
     * @param attrValue  название атрибута
     * @param tagHandler обработчик
     */
    public void putValueHandler(@NonNull String attrValue, @Nullable TagHandler tagHandler) {
        if (mValueHandlerStore == null) {
            mValueHandlerStore = new HashMap<>();
        }
        mValueHandlerStore.put(attrValue, tagHandler);
    }

    /**
     * Регистрирует обработчик для динамической проверки атрибута.
     * В случа если придикат возвращает true, то будет вызван соответствующий метод обработчика.
     *
     * @param attrPredicate предикат для проверки атрибута
     * @param tagHandler обработчик
     */
    public void addValueHandler(@NonNull Predicate<String> attrPredicate, @NonNull TagHandler tagHandler) {
        if (mValueHandlerList == null) {
            mValueHandlerList = new ArrayList<>();
        }
        mValueHandlerList.add(new ValueHandlerNode(attrPredicate, tagHandler));
    }

    private void findValueTagHandler(@Nullable String value, @NonNull Consumer<TagHandler> consumer) {
        if (value != null) {
            if (mValueHandlerStore != null) {
                final TagHandler valueHandler = mValueHandlerStore.get(value);
                if (valueHandler != null) {
                    consumer.accept(valueHandler);
                    return;
                }
                // значения могут перечисляться через пробел
                final String[] values = value.split(StringUtils.SPACE);
                if (values.length > 1) {
                    for (String subValue : values) {
                        final TagHandler subValueHandler = mValueHandlerStore.get(subValue);
                        if (subValueHandler != null) {
                            consumer.accept(subValueHandler);
                            return;
                        }
                    }
                }
            }
            if (mValueHandlerList != null) {
                for (ValueHandlerNode node : mValueHandlerList) {
                    if (node.predicate.apply(value)) {
                        consumer.accept(node.tagHandler);
                        return;
                    }
                }
            }
        }
    }

    private static final class ValueHandlerNode {

        @NonNull
        final Predicate<String> predicate;
        @NonNull
        final TagHandler tagHandler;

        ValueHandlerNode(@NonNull Predicate<String> predicate, @NonNull TagHandler tagHandler) {
            this.predicate = predicate;
            this.tagHandler = tagHandler;
        }
    }
}
