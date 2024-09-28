package ru.tensor.sbis.design.cloud_view.content.certificate;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.cloud_view.R;
import ru.tensor.sbis.design.cloud_view.content.utils.MessageResourcesHolder;
import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.design.theme.global_variables.FontSize;

/**
 * View, предназначенный для отображения подписи документа в ячейке-облаке
 *
 * @author ma.kolpakov
 */
public class CertificateView extends LinearLayout {

    @NonNull
    private final SbisTextView mBadgeView;
    @NonNull
    private final SbisTextView mOwnerInfo;
    private MessageResourcesHolder mResourcesHolder;

    public CertificateView(Context context) {
        this(context, null);
    }

    public CertificateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CertificateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);

        final Resources resources = getResources();

        final int badgeSize = resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.size_body1_scaleOff);
        final int textSize = FontSize.M.getScaleOnDimenPx(context);
        final int textLeftMargin = resources.getDimensionPixelOffset(ru.tensor.sbis.design.cloud_view.R.dimen.cloud_view_certificate_view_owner_text_margin_left);

        mBadgeView = new SbisTextView(context);
        mBadgeView.setId(R.id.cloud_view_message_block_badge_view_id);
        mBadgeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, badgeSize);
        LayoutParams badgeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        badgeParams.gravity = Gravity.CENTER_VERTICAL;
        mBadgeView.setLayoutParams(badgeParams);
        mBadgeView.setTypeface(TypefaceManager.getSbisMobileIconTypeface(context));
        mBadgeView.setText(resources.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_certificate_badge));

        mOwnerInfo = new SbisTextView(context);
        mOwnerInfo.setId(R.id.cloud_view_message_block_owner_info_id);
        mOwnerInfo.setTypeface(TypefaceManager.getRobotoRegularFont(context));
        LayoutParams infoParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        infoParams.leftMargin = textLeftMargin;
        infoParams.gravity = Gravity.CENTER_VERTICAL;
        mOwnerInfo.setIncludeFontPadding(false);
        mOwnerInfo.setLayoutParams(infoParams);
        mOwnerInfo.setMaxLines(1);
        mOwnerInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mOwnerInfo.setEllipsize(TextUtils.TruncateAt.END);

        addView(mBadgeView);
        addView(mOwnerInfo);
    }

    /**
     * Устанавливает модель подписи для отображения
     *
     * @param signature данные подписи
     */
    public void setCertificate(@NonNull Signature signature) {
        final int badgeColor = mResourcesHolder.getCertificateBadgeColor(signature.isMine());
        final int infoTextColor = mResourcesHolder.getOwnerInfoColor(signature.isMine());
        mBadgeView.setTextColor(badgeColor);
        mOwnerInfo.setTextColor(infoTextColor);
        String certificateText = signature.getTitle();
        mOwnerInfo.setText(certificateText);
    }

    /**
     * Устанавливает источник цвета иконки сертификата подписи
     *
     * @param resourceHolder источник прикладных цветов
     */
    public void setResourceHolder(@NonNull MessageResourcesHolder resourceHolder) {
        mResourcesHolder = resourceHolder;
    }
}
