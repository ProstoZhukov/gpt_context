package ru.tensor.sbis.scanner.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.scanner.generated.ScannerRotateAngle;

/**
 * @author am.boldinov
 */
public enum Rotation {
    HORIZONTAL(0),
    VERTICAL(90),
    HORIZONTAL_INVERSE(180),
    VERTICAL_INVERSE(270);

    public static final Rotation DEFAULT = HORIZONTAL; // считаем за дефолт, поворот будет считаться него

    private final int degrees;

    Rotation(int degrees) {
        this.degrees = degrees;
    }

    @Nullable
    public static Rotation fromValue(final int value) {
        for (Rotation rotation : Rotation.values()) {
            if (rotation.getDegrees() == value) {
                return rotation;
            }
        }
        return null;
    }

    public int getDegrees() {
        return degrees;
    }

    @NonNull
    public ScannerRotateAngle toAngleRotate() {
        final Rotation rotation = fromValue(degrees);
        switch (CommonUtils.checkNotNull(rotation)) {
            case HORIZONTAL_INVERSE:
                return ScannerRotateAngle.ROTATE_180;
            case VERTICAL:
                return ScannerRotateAngle.ROTATE_90_CLOCKWISE;
            case VERTICAL_INVERSE:
                return ScannerRotateAngle.ROTATE_90_COUNTERCLOCKWISE;
            default:
                return ScannerRotateAngle.NOT_ROTATE;
        }
    }

    @NonNull
    public Rotation nextRotation(boolean cw) {
        final Rotation current = CommonUtils.checkNotNull(fromValue(degrees));
        final Rotation[] rotations = Rotation.values();
        for (int i = 0; i < rotations.length; i++) {
            if (rotations[i] == current) {
                if (cw) {
                    if (i < rotations.length - 1) {
                        return rotations[i + 1];
                    } else {
                        return rotations[0];
                    }
                } else {
                    if (i > 0) {
                        return rotations[i - 1];
                    } else {
                        return rotations[rotations.length - 1];
                    }
                }
            }
        }
        return current;
    }
}
