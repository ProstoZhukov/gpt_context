package ru.tensor.sbis.design_dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.design_dialogs.R;

/**
 * Класс для реализации всплывающего меню, использующий PopupWindow с RecyclerView
 *
 * @see <a href="Стандарт всплывающего меню">http://axure.tensor.ru/MobileAPP/#p=%D0%B2%D1%81%D0%BF%D0%BB%D1%8B%D0%B2%D0%B0%D1%8E%D1%89%D0%B5%D0%B5_%D0%BE%D0%BA%D0%BD%D0%BE&g=1</a>
 */

@SuppressWarnings({"unused", "NotNullFieldNotInitialized"})
public class DefaultPopupMenu {

    private static final int SHOW_DELAY_MILLIS = 500;
    private static final String IS_POP_UP_MENU_DISPLAYED = "IS_POP_UP_MENU_DISPLAYED";

    public interface OnDismissListener {

        void onDismiss();
    }

    @NonNull
    private final FrameLayout mBasePopupView;
    @NonNull
    private PopupWindow mBasePopupMenu;
    @NonNull
    protected RecyclerView mRecyclerView;

    @Nullable
    private OnDismissListener onDismissListener;

    private long mLastInteractionTimeStamp;
    protected View mAnchor;
    protected int mExtraRightOffset = 0;
    protected int mMenuMarginRight;
    private int mExtraTopOffset;
    private int mMenuWidth = -1;
    private boolean alignLeftWithAnchor = false;
    private int alignLeftWithAnchorOffset = 0;
    private int maxWidth = -1;

    public DefaultPopupMenu(@NonNull Context context) {
        this(context, false);
    }

    public DefaultPopupMenu(@NonNull Context context, boolean usePopupWindowShadow) {
        this(context, usePopupWindowShadow, true);
    }

    /**
     * @param context              контекст
     * @param usePopupWindowShadow если true, то использует elevation PopupWindow для создания тени, вместо ресурса заднего фона.
     *                             Нужно для случаев когда надо прижать меню к краю экрана. Использование заднего фона помешает это сделать.
     * @param isFocusable          доступен ли фокус. Если включен, возможны моргания при открытой клавиатуре
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public DefaultPopupMenu(@NonNull Context context, boolean usePopupWindowShadow, boolean isFocusable) {
        mBasePopupView = (FrameLayout) FrameLayout.inflate(context, R.layout.design_dialogs_popup_menu, null);
        if (usePopupWindowShadow) {
            mBasePopupView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            mMenuMarginRight = (int) (context.getResources().getDimension(R.dimen.popup_menu_right_margin) +
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, context.getResources().getDisplayMetrics()));
        } else {
            mBasePopupView.setBackground(context.getResources().getDrawable(android.R.drawable.picture_frame));
            mMenuMarginRight = (int) context.getResources().getDimension(R.dimen.popup_menu_right_margin);
        }
        mBasePopupView.addView(createView());
        initPopupWindow(context, usePopupWindowShadow, isFocusable);
    }

    private void initPopupWindow(@NonNull Context context, boolean usePopupWindowShadow, boolean isFocusable) {
        mBasePopupMenu = new PopupWindow(mBasePopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBasePopupMenu.setAnimationStyle(R.style.PopupMenuAppearance);
        mBasePopupMenu.setOutsideTouchable(true);

        setShadowIfNeed(context, usePopupWindowShadow);

        mBasePopupMenu.setFocusable(isFocusable);
        mBasePopupMenu.setOnDismissListener(() -> {
            mLastInteractionTimeStamp = System.currentTimeMillis();
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
        });
    }

    private void setShadowIfNeed(@NonNull Context context, boolean usePopupWindowShadow) {
        if (usePopupWindowShadow) {
            addPopupWindowShadow(context);
        } else {
            mBasePopupMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /**
     * На SDK до 26 ппап не закрывается при нажатии вне его поверхности.
     * Чтобы закрывался, нужно выставить какой-нибудь бэкграунд.     *
     *
     * @param context для получения ресурся тени для бэкграунда
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void addPopupWindowShadow(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mBasePopupMenu.setBackgroundDrawable(context.getResources().getDrawable(ru.tensor.sbis.design.R.drawable.spinner_popup_bg));
        }
        mBasePopupMenu.setElevation(context.getResources().getDimensionPixelSize(R.dimen.popup_menu_innate_shadow));
    }

    @NonNull
    protected Context getContext() {
        return mBasePopupView.getContext();
    }

    @NonNull
    public View createView() {
        View view = View.inflate(getContext(), R.layout.design_dialogs_popup_menu_with_recyclerview, null);
        mRecyclerView = view.findViewById(R.id.design_dialogs_popup_menu_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(null);
        return view;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMenuMarginRight(@Px int menuMarginRight) {
        this.mMenuMarginRight = menuMarginRight;
    }

    @SuppressWarnings("rawtypes")
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setOnDismissListener(@Nullable OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public boolean showMenu(@NonNull View anchor, boolean alignLeftWithAnchor) {
        return showMenu(anchor, alignLeftWithAnchor, 0, 0);
    }

    public boolean showMenuAboveAnchor(@NonNull View anchor, int horizontalOffset, int extraVerticalOffset) {
        // Если максимальная ширина не задана, то считаем, что popup занимает весь экран
        int popupWidth = maxWidth == -1 ? anchor.getContext().getResources().getDisplayMetrics().widthPixels : maxWidth;
        mBasePopupView.measure(View.MeasureSpec.makeMeasureSpec(popupWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.UNSPECIFIED);
        this.mExtraTopOffset = -mBasePopupView.getMeasuredHeight() + extraVerticalOffset;
        return showMenu(anchor, horizontalOffset);
    }

    public boolean showMenuAboveScreenBottom(@NonNull View anchor, boolean alignLeftWithAnchor, int alignLeftWithAnchorHorizontalOffset, int extraTopOffset) {
        int calculatedExtraTopOffset = extraTopOffset;

        mBasePopupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] anchorLocation = new int[2];
        anchor.getLocationOnScreen(anchorLocation);
        int screenHeight = anchor.getContext().getResources().getDisplayMetrics().heightPixels;
        int spaceAvailableForPopup = screenHeight - anchorLocation[1];
        int extraSpaceBetweenMenuAndScreenBottom = spaceAvailableForPopup - mBasePopupView.getMeasuredHeight();
        if (extraSpaceBetweenMenuAndScreenBottom < 0) {
            calculatedExtraTopOffset += extraSpaceBetweenMenuAndScreenBottom;
        }

        return showMenu(anchor, alignLeftWithAnchor, alignLeftWithAnchorHorizontalOffset, calculatedExtraTopOffset);
    }

    public boolean showMenu(@NonNull View anchor, boolean alignLeftWithAnchor, int alignLeftWithAnchorHorizontalOffset, int extraTopOffset) {
        this.alignLeftWithAnchor = alignLeftWithAnchor;
        this.alignLeftWithAnchorOffset = alignLeftWithAnchorHorizontalOffset;
        this.mExtraTopOffset = extraTopOffset;
        return showMenu(anchor);
    }

    public boolean showMenu(@NonNull View anchor) {
        return showMenu(anchor, 0);
    }

    public boolean showMenu(@NonNull View anchor, int extraRightOffset) {
        return showMenu(anchor, extraRightOffset, false, -1);
    }

    /**
     * Отображение всплывающего меню, которое будет прижато к правому краю якоря. Отступ до правого
     * края хостящего фрагмента должен составлять 12dp по стандарту всплывающего окна
     * http://axure.tensor.ru/MobileAPP/#p=%D0%B2%D1%81%D0%BF%D0%BB%D1%8B%D0%B2%D0%B0%D1%8E%D1%89%D0%B5%D0%B5_%D0%BE%D0%BA%D0%BD%D0%BE&g=1
     *
     * @param anchor              якорь, к которому будет прижато меню
     * @param extraRightOffset    используется для коррекции отступа, например если якорь не прижат к
     *                            правому краю
     * @param showBelowAnchorView если true меню будет привязано под anchorView
     * @param menuWidth           ширина содержимого меню, по дефолту WRAP_CONTENT
     */
    public boolean showMenu(@NonNull View anchor, int extraRightOffset, boolean showBelowAnchorView, int menuWidth) {
        mAnchor = anchor;
        mMenuWidth = menuWidth;
        int extraTopOffset = showBelowAnchorView ? 0 : -mAnchor.getHeight() + mExtraTopOffset;
        mExtraRightOffset = extraRightOffset;
        boolean isNeedShow = System.currentTimeMillis() - mLastInteractionTimeStamp > SHOW_DELAY_MILLIS;
        if (isNeedShow) {
            //Если максимальная ширина не задана считаем что за максимум можно взять весь экран
            if (maxWidth == -1) {
                maxWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            }
            //Костыль, для того чтобы попап использовал всё свободное место для показа содерждимого. В некоторых случаях он использует меньше свободного места чем есть.
            mBasePopupView.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.UNSPECIFIED);
            if (mMenuWidth == -1) {
                mBasePopupMenu.setWidth(mBasePopupView.getMeasuredWidth());
            }
            mBasePopupMenu.showAsDropDown(anchor, getMenuHorizontalOffset(), extraTopOffset);
            //По какой-то причине первый mBasePopupView.getMeasuredWidth() возвращает неправильное значение.
            //Чтобы его поправить тут же вызываем update как меню отрисуется.
            mBasePopupView.post(() -> mBasePopupMenu.update(mAnchor, getMenuHorizontalOffset(), extraTopOffset, mMenuWidth, -1));
            mLastInteractionTimeStamp = System.currentTimeMillis();
        }
        return isNeedShow;
    }

    /**
     * Отображение всплывающего меню по позиции.
     *
     * @param anchor    якорь, к которому будет прижато меню
     * @param menuWidth ширина содержимого меню
     * @param x         координата по X
     * @param y         координата по Y
     * @param gravity   притяжение для всплывающего меню
     */
    public boolean showOnPosition(@NonNull View anchor, int menuWidth, int x, int y, int gravity) {
        mAnchor = anchor;
        mMenuWidth = menuWidth;

        boolean isNeedShow = System.currentTimeMillis() - mLastInteractionTimeStamp > SHOW_DELAY_MILLIS;

        if (isNeedShow) {
            mBasePopupMenu.showAtLocation(anchor, gravity, x, y);
            mLastInteractionTimeStamp = System.currentTimeMillis();
        }

        return isNeedShow;
    }

    protected int getMenuHorizontalOffset() {
        if (alignLeftWithAnchor) {
            return alignLeftWithAnchorOffset;
        } else {
            return -mBasePopupView.getMeasuredWidth() + mAnchor.getWidth() - mMenuMarginRight + mExtraRightOffset;
        }
    }

    /**SelfDocumented*/
    protected void updatePopupWindow(){
        mBasePopupMenu.update();
    }

    public void hideMenu() {
        mBasePopupMenu.dismiss();
    }

    public boolean isShowing() {
        return mBasePopupMenu.isShowing();
    }

    public void saveDisplayState(@NonNull Bundle bundle) {
        bundle.putBoolean(IS_POP_UP_MENU_DISPLAYED, isShowing());
    }

    public boolean needDisplayMenu(@Nullable Bundle bundle) {
        return bundle != null && bundle.getBoolean(IS_POP_UP_MENU_DISPLAYED);
    }
}
