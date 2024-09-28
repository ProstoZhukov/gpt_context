/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet;

import static ru.tensor.sbis.design_dialogs.dialogs.container.util.Utils.dismissSafe;
import static ru.tensor.sbis.design_dialogs.dialogs.container.util.Utils.slideContentDownAndThenDismiss;
import static ru.tensor.sbis.design_dialogs.dialogs.container.util.Utils.slideContentDownAndThenDismissAllowingStateLoss;

import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import ru.tensor.sbis.design.design_dialogs.R;

/**
 * Копия класса {@link BottomSheetDialogFragment}, отличающаяся от
 * стандартной тем, что использует {@link CustomBottomSheetDialog}
 */
public class CustomBottomSheetDialogFragment extends AppCompatDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int customContainerBottomSheetTheme = getThemeRes(R.attr.customBottomSheetTheme);
        setStyle(STYLE_NORMAL, customContainerBottomSheetTheme != 0 ? customContainerBottomSheetTheme : R.style.CustomBottomSheetTheme);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new CustomBottomSheetDialog(requireContext(), getTheme(), true);
    }

    /**
     * Выполняет закрытие экрана сразу. Следует вызывать если содержимое уже скрыто
     */
    public void dismissAfterSlidingDown() {
        dismissSafe(this, super::dismiss, super::dismissAllowingStateLoss);
    }

    @Override
    public void dismiss() {
        slideContentDownAndThenDismiss(this, super::dismiss, super::dismissAllowingStateLoss);
    }

    @Override
    public void dismissAllowingStateLoss() {
        slideContentDownAndThenDismissAllowingStateLoss(this, super::dismissAllowingStateLoss);
    }

    private int getThemeRes(int attr) {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
