package ru.tensor.sbis.design.text_span;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;
import java.util.Objects;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import ru.tensor.sbis.design.text_span.util.FontUtilsKt;

/**
 * View для отображения информации в виде заголовка, сообщения и деталей,
 * расположенных вертикально.
 * @author ev.grigoreva
 */
public class SimpleInformationView extends LinearLayout {

    private TextView mHeader;
    private TextView mBaseInfo;
    private TextView mDetails;

    public SimpleInformationView(Context context) {
        super(context);
        init(context);
    }

    public SimpleInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimpleInformationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int paddingHorizontal = context.getResources().getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.empty_view_padding);
        setPadding(paddingHorizontal, getPaddingTop(), paddingHorizontal, getPaddingBottom());
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);
        setMinimumHeight(getResources().getDimensionPixelOffset(ru.tensor.sbis.design.R.dimen.empty_view_min_height));

        mHeader = new TextView(new ContextThemeWrapper(context, ru.tensor.sbis.design.R.style.LightStyle));
        mHeader.setId(R.id.header);
        LinearLayout.LayoutParams headerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        headerLayoutParams.gravity = Gravity.CENTER;
        mHeader.setLayoutParams(headerLayoutParams);
        mHeader.setTextColor(ContextCompat.getColor(new ContextThemeWrapper(context, ru.tensor.sbis.design.R.style.LightStyle), android.R.color.black));
        mHeader.setFreezesText(true);
        mHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        mHeader.setGravity(Gravity.CENTER_HORIZONTAL);

        mBaseInfo = new TextView(new ContextThemeWrapper(context, ru.tensor.sbis.design.R.style.RegularStyle));
        mBaseInfo.setId(R.id.base_info);
        LinearLayout.LayoutParams baseInfoLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        baseInfoLayoutParams.gravity = Gravity.CENTER;
        mBaseInfo.setLayoutParams(baseInfoLayoutParams);
        mBaseInfo.setTextColor(ContextCompat.getColor(new ContextThemeWrapper(context, ru.tensor.sbis.design.R.style.RegularStyle), ru.tensor.sbis.design.R.color.empty_view_text_color));
        mBaseInfo.setFreezesText(true);
        mBaseInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mBaseInfo.setGravity(Gravity.CENTER_HORIZONTAL);

        mDetails = new TextView(new ContextThemeWrapper(context, ru.tensor.sbis.design.R.style.RegularStyle));
        mDetails.setId(R.id.details);
        LinearLayout.LayoutParams detailsLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        detailsLayoutParams.gravity = Gravity.CENTER;
        mDetails.setLayoutParams(detailsLayoutParams);
        mDetails.setTextColor(ContextCompat.getColor(new ContextThemeWrapper(context, ru.tensor.sbis.design.R.style.RegularStyle), ru.tensor.sbis.design.R.color.empty_view_text_color));
        mDetails.setFreezesText(true);
        mDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mDetails.setGravity(Gravity.CENTER_HORIZONTAL);
        mDetails.setLinkTextColor(ContextCompat.getColor(new ContextThemeWrapper(context, ru.tensor.sbis.design.R.style.RegularStyle), ru.tensor.sbis.design.R.color.text_color_link_1));
        mDetails.setLinksClickable(true);
        mDetails.setMovementMethod(LinkMovementMethod.getInstance());

        addView(mHeader);
        addView(mBaseInfo);
        addView(mDetails);
    }

    /**
     * Заполнение данными.
     * @param headerText заголовок
     * @param baseInfoText сообщение
     * @param detailsText детали
     */
    public void fillData(@Nullable CharSequence headerText, @Nullable CharSequence baseInfoText, @Nullable CharSequence detailsText) {
        mHeader.setText(FontUtilsKt.asRobotoLight(getContext(), headerText));
        mBaseInfo.setText(FontUtilsKt.asRoboto(getContext(), baseInfoText));
        mDetails.setText(FontUtilsKt.asRoboto(getContext(), detailsText));

        mHeader.setVisibility(headerText == null ? GONE : VISIBLE);
        mBaseInfo.setVisibility(baseInfoText == null ? GONE : VISIBLE);
        mDetails.setVisibility(detailsText == null ? GONE : VISIBLE);
    }

    /**
     * Заполнение данными.
     * @param headerText заголовок
     * @param baseInfoText сообщение
     * @param detailsText детали
     */
    @SuppressWarnings("unused")
    public void fillData(@StringRes int headerText, @StringRes int baseInfoText,
                         @StringRes int detailsText) {
        Context context = getContext();
        fillData(headerText != 0 ? context.getString(headerText) : null,
                baseInfoText != 0 ? context.getString(baseInfoText) : null,
                detailsText != 0 ? context.getString(detailsText) : null);
    }

    /**
     * Заполнение данными.
     * @param content {@link Content}
     */
    public void fillData(@Nullable Content content) {
        if (content != null) {
            fillData(content.getHeaderText(), content.getBaseInfoText(), content.getDetailsText());
        } else {
            fillData(null, null, null);
        }
    }

    /**
     * Установка цвета текста.
     * @param color цвет текста
     */
    public void setTextColor(@ColorInt int color) {
        mHeader.setTextColor(color);
        mBaseInfo.setTextColor(color);
        mDetails.setTextColor(color);
    }

    /**
     * Класс, содержащий данные для отображения.
     */
    public static class Content {

        private CharSequence mHeaderText;
        private CharSequence mBaseInfoText;
        private CharSequence mDetailsText;
        @Nullable
        private Map<Integer, Function0<Unit>> mClickActions;

        @StringRes
        private int mHeaderResId;
        @StringRes
        private int mBaseInfoResId;
        @StringRes
        private int mDetailsResId;

        /**
         * SelfDocumented
         */
        @SuppressWarnings("unused")
        public Content() {
        }

        /**
         * SelfDocumented
         */
        public Content(CharSequence headerText, CharSequence baseInfoText, CharSequence detailsText) {
            this.mHeaderText = headerText;
            this.mBaseInfoText = baseInfoText;
            this.mDetailsText = detailsText;
        }

        /**
         * SelfDocumented
         */
        public Content(@NonNull Context context, @StringRes int headerResId,
                       @StringRes int baseInfoResId, @StringRes int detailsResId, @Nullable Map<Integer, Function0<Unit>> actions) {
            set(context, headerResId, baseInfoResId, detailsResId, actions);
        }

        /**
         * SelfDocumented
         */
        public Content(@NonNull Context context, @StringRes int headerResId,
                       @StringRes int baseInfoResId, @StringRes int detailsResId) {
            this(context, headerResId, baseInfoResId, detailsResId, null);
        }

        /**
         * SelfDocumented
         */
        public void set(@NonNull Context context, @StringRes int headerResId,
                        @StringRes int baseInfoResId, @StringRes int detailsResId, @Nullable Map<Integer, Function0<Unit>> actions) {
            mHeaderResId = headerResId;
            setHeaderText(headerResId != 0 ? context.getString(headerResId) : null);
            mBaseInfoResId = baseInfoResId;
            setBaseInfoText(baseInfoResId != 0 ? context.getString(baseInfoResId) : null);
            mDetailsResId = detailsResId;
            setDetailsText(detailsResId != 0 ? context.getString(detailsResId) : null);
            mClickActions = actions;
        }

        /**
         * SelfDocumented
         */
        public CharSequence getHeaderText() {
            return mHeaderText;
        }

        /**
         * SelfDocumented
         */
        public void setHeaderText(CharSequence headerText) {
            mHeaderText = headerText;
        }

        /**
         * SelfDocumented
         */
        public CharSequence getBaseInfoText() {
            return mBaseInfoText;
        }

        /**
         * SelfDocumented
         */
        public void setBaseInfoText(CharSequence baseInfoText) {
            mBaseInfoText = baseInfoText;
        }

        /**
         * SelfDocumented
         */
        public CharSequence getDetailsText() {
            return mDetailsText;
        }

        /**
         * SelfDocumented
         */
        public void setDetailsText(CharSequence detailsText) {
            mDetailsText = detailsText;
        }

        /**
         * SelfDocumented
         */
        @StringRes
        public int getHeaderResId() {
            return mHeaderResId;
        }

        /**
         * SelfDocumented
         */
        @StringRes
        public int getBaseInfoResId() {
            return mBaseInfoResId;
        }

        /**
         * SelfDocumented
         */
        @StringRes
        public int getDetailsResId() {
            return mDetailsResId;
        }

        /**
         * SelfDocumented
         */
        @Nullable
        public Map<Integer, Function0<Unit>> getActionsOnClick() {
            return mClickActions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Content content = (Content) o;

            if (mHeaderResId != content.mHeaderResId) return false;
            if (mBaseInfoResId != content.mBaseInfoResId) return false;
            if (mDetailsResId != content.mDetailsResId) return false;
            if (!TextUtils.equals(mHeaderText, content.mHeaderText))
                return false;
            if (!TextUtils.equals(mBaseInfoText, content.mBaseInfoText))
                return false;
            if (mClickActions != null && content.mClickActions != null
                    ? !Objects.equals(mClickActions.keySet(), content.mClickActions.keySet())
                    : content.mClickActions != null || mClickActions != null) {
                return false;
            }
            return TextUtils.equals(mDetailsText, content.mDetailsText);
        }

        @Override
        public int hashCode() {
            int result = mHeaderText != null ? mHeaderText.hashCode() : 0;
            result = 31 * result + (mBaseInfoText != null ? mBaseInfoText.hashCode() : 0);
            result = 31 * result + (mDetailsText != null ? mDetailsText.hashCode() : 0);
            result = 31 * result + mHeaderResId;
            result = 31 * result + mBaseInfoResId;
            result = 31 * result + mDetailsResId;
            result = 31 * result + (mClickActions != null ? mClickActions.keySet().hashCode() : 0);
            return result;
        }
    }

}