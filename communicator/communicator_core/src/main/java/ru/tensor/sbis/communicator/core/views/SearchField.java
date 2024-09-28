package ru.tensor.sbis.communicator.core.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.reactivex.Observable;
import ru.tensor.sbis.communicator.core.R;
import ru.tensor.sbis.design.text_span.SbisEditText;

/**
 * Note!
 * If you need to use more than one {@link SearchField} view in layout then you should
 * specify for them {@link ru.tensor.sbis.communicator.common.R.attr}#field_id from styleable SearchField.
 * <p>
 * This value will be assign to {@link #mCustomEditTextId} variable.
 * See {@link #initProperties(AttributeSet)} method.
 * <p>
 * {@link #mCustomEditTextId} field need to Android can identify specified
 * {@link SbisEditText} from another for restoring state after screen rotation.
 * <p>
 * <p>
 * If {@link ru.tensor.sbis.communicator.common.R.attr}#field_id set and {@link #mCustomEditTextId} field is
 * initialized then id for {@link SearchField} was changed programmatically. See {@link #initView} method.
 * <p>
 * For example, see {@link ru.tensor.sbis.communicator.core.R.layout#communicator_new_contacts_extended_search}.
 * <p>
 * If you use only one {@link SearchField} per layout then not needed to have deal with
 * {@link ru.tensor.sbis.communicator.common.R.attr}#field_id attribute.
 */
public class SearchField extends FrameLayout {

    public static final String DEFAULT_SEARCH_QUERY = "";
    private static final int DEFAULT_CUSTOM_ID = 0;

    private SbisEditText mSearchField;
    private View mButtonCancel;
    private ConstraintLayout mConstraintLayout;

    private boolean mButtonCancelVisible;
    private int mType;
    private boolean mNeedFocus;

    // See javadoc for this class
    private int mCustomEditTextId;

    public SearchField(@NonNull Context context) {
        super(context);
        initView();
    }

    public SearchField(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initProperties(attrs);
        initView();
    }

    public SearchField(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProperties(attrs);
        initView();
    }

    public SbisEditText getSearchField() {
        return mSearchField;
    }

    private void initProperties(AttributeSet attrs) {
        if (isInEditMode() || attrs == null) {
            return;
        }
        TypedArray attributeArray = getContext().getTheme().obtainStyledAttributes(attrs, ru.tensor.sbis.communicator.design.R.styleable.SearchField, 0, 0);
        try {
            mType = attributeArray.getInt(ru.tensor.sbis.communicator.design.R.styleable.SearchField_searchFieldType, Type.NAME.ordinal());
            mNeedFocus = attributeArray.getBoolean(ru.tensor.sbis.communicator.design.R.styleable.SearchField_needFocus, false);
            mButtonCancelVisible = false;

            // See javadoc for this class
            mCustomEditTextId = attributeArray.getInt(ru.tensor.sbis.communicator.design.R.styleable.SearchField_field_id, DEFAULT_CUSTOM_ID);
        } finally {
            attributeArray.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        View.inflate(getContext(), R.layout.communicator_search_field, this);

        mConstraintLayout = findViewById(R.id.communicator_search_field_root_view);
        mButtonCancel = findViewById(R.id.communicator_search_field_button_cancel);
        mButtonCancel.setVisibility(mButtonCancelVisible ? VISIBLE : GONE);
        mSearchField = findViewById(R.id.communicator_search_field_edit_text);

        // See javadoc for this class
        if (mCustomEditTextId != DEFAULT_CUSTOM_ID) {
            mSearchField.setId(mCustomEditTextId);
        }

        if (mNeedFocus) {
            mSearchField.requestFocus();
        }

        if (mType == Type.NAME.ordinal()) {
            mSearchField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            mSearchField.setHint(ru.tensor.sbis.communicator.design.R.string.communicator_contacts_adding_surname);
        } else if (mType == Type.PHONE.ordinal()) {
            mSearchField.setInputType(InputType.TYPE_CLASS_PHONE);
            mSearchField.setHint(ru.tensor.sbis.communicator.design.R.string.communicator_contacts_adding_number);
        } else if (mType == Type.EMAIL.ordinal()) {
            mSearchField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mSearchField.setHint(ru.tensor.sbis.communicator.design.R.string.communicator_contacts_adding_address);
        }
    }

    public void setSearchQuery(@NonNull String searchQuery) {
        mSearchField.setText(searchQuery);
    }

    public void setSearchFieldEnabled(boolean enabled) {
        mSearchField.setEnabled(enabled);
    }

    public void setSearchFieldBackgroundColor(int color) {
        mSearchField.setBackgroundColor(color);
        mButtonCancel.setBackgroundColor(color);
        mConstraintLayout.setBackgroundColor(color);
    }

    public void setSearchFieldButtonCancelEnabled(boolean enabled) {
        mButtonCancel.setVisibility(enabled ? VISIBLE : GONE);
    }

    public Observable<Integer> searchFieldEditorActionsObservable() {
        return RxTextView.editorActions(mSearchField);
    }

    public Observable<Object> cancelSearchObservable() {
        return RxView.clicks(mButtonCancel);
    }

    public Observable<String> searchQueryChangedObservable(boolean isCancelButtonEnabled) {
        return RxTextView.textChangeEvents(mSearchField)
                .distinctUntilChanged()
                .doOnNext(searchQueryChangeEvent -> {
                    if (isCancelButtonEnabled) {
                        mButtonCancel.setVisibility(searchQueryChangeEvent.text().toString().isEmpty() ? GONE : VISIBLE);
                    }
                })
                .map(searchQueryChangeEvent -> searchQueryChangeEvent.text().toString());
    }

    enum Type {
        NAME,
        PHONE,
        EMAIL
    }

}
