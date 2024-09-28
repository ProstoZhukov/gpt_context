package ru.tensor.sbis.common.util.cache;

import android.content.Context;
import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import ru.tensor.sbis.common.R;
import ru.tensor.sbis.common.util.FileUtil;

public class CacheTypeUtils {

    @NonNull
    public static String prettySizeValue(@NonNull Context context, long sizeInByte) {
        return prettySizeValueSpecifySeparator(context, sizeInByte, ',');
    }

    @NonNull
    public static String prettySizeValueDotSeparator(@NonNull Context context, long sizeInByte) {
        return prettySizeValueSpecifySeparator(context, sizeInByte, '.');
    }

    @NonNull
    private static String prettySizeValueSpecifySeparator(@NonNull Context context, long sizeInByte, char separator) {
        if (sizeInByte == 0) {
            return 0 + " " + context.getString(R.string.common_kilobyte_short);
        } else if (sizeInByte < FileUtil.ONE_KB) {
            return sizeInByte + " " + context.getString(R.string.common_byte_short);
        } else {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setDecimalSeparator(separator);
            if (sizeInByte < FileUtil.ONE_MB) {
                DecimalFormat decimalFormat = new DecimalFormat("#", symbols);
                return decimalFormat.format((double) sizeInByte / FileUtil.ONE_KB) + " " + context.getString(R.string.common_kilobyte_short);
            } else if (sizeInByte < FileUtil.ONE_GB) {
                DecimalFormat decimalFormat = new DecimalFormat("#.#", symbols);
                return decimalFormat.format((double) sizeInByte / FileUtil.ONE_MB) + " " + context.getString(R.string.common_megabyte_short);
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                return decimalFormat.format((double) sizeInByte / FileUtil.ONE_GB) + " " + context.getString(R.string.common_gigabyte_short);
            }
        }
    }

}
