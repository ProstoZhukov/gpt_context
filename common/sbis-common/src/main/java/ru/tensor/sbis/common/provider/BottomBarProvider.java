package ru.tensor.sbis.common.provider;

import androidx.annotation.Nullable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public interface BottomBarProvider {

    void setNavigationFabClickListener(@Nullable View.OnClickListener navigationFabClickListener);

    default void setTodayExtraFabClickListener(@Nullable View.OnClickListener extraFabClickListener){}

    void setExtraFabClickListener(@Nullable View.OnClickListener extraFabClickListener);

    void setExtraFab2ClickListener(@Nullable View.OnClickListener extraFabClickListener);

    void setExtraFab3ClickListener(@Nullable View.OnClickListener extraFabClickListener);

    void setExtraFab4ClickListener(@Nullable View.OnClickListener extraFabClickListener);

    default void showTodayExtraFabButton(@NotNull Date date, boolean isWorkDay, boolean animated){}

    void showExtraFabButton();

    void showExtraFab2Button();

    void showExtraFab3Button();

    void showExtraFab4Button();

    default void hideTodayExtraFabButton(boolean animated){}

    void hideExtraFabButton();

    void hideExtraFab2Button();

    void hideExtraFab3Button();

    void hideExtraFab4Button();

    void swapFabButton(boolean isAddItemEnabled);

}
