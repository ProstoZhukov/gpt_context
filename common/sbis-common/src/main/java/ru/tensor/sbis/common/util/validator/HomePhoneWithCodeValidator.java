package ru.tensor.sbis.common.util.validator;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

/**
 * Created by am.boldinov on 13.11.15.
 */

/**
 * Валидатор городских российских номеров телефона с кодом города
 */
public class HomePhoneWithCodeValidator implements Validator<String> {

    private final Pattern mPattern;

    public HomePhoneWithCodeValidator() {
        @SuppressWarnings("Annotator")
        String regExp = "(((8|\\+7)-?)\\s?\\(?\\d{5}\\)?(\\s|-)?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1})" + // +x(xxxxx)x-xx-xx
                "|(((8|\\+7)-?)\\s?\\(?\\d{4}\\)?(\\s|-)?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1})" + // +x(xxxx)xx-xx-xx
                "|(((8|\\+7)-?)\\s?\\(?\\d{3}\\)?(\\s|-)?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1}-?\\d{1})"; // +x(xxx)xxx-xx-xx

        mPattern = Pattern.compile(regExp);
    }

    @Override
    public boolean validate(@NonNull final String str) {
        return mPattern.matcher(str).matches();
    }
}
