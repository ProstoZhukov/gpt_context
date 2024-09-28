package ru.tensor.sbis.common.util.validator;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by am.boldinov on 13.11.15.
 */
public class PhoneValidator implements Validator<String> {

    private final Pattern mPattern;

    @Inject
    public PhoneValidator() {
        @SuppressWarnings("Annotator")
        String regExp = "((8|\\+7)-?)\\s?\\(?\\d{3}\\)?(\\s|-)?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}";
        mPattern = Pattern.compile(regExp);
    }

    @Override
    public boolean validate(@NonNull final String str) {
        return mPattern.matcher(str).matches();
    }
}
