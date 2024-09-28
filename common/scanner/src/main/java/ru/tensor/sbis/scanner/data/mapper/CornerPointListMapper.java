package ru.tensor.sbis.scanner.data.mapper;

import androidx.annotation.Nullable;

import io.reactivex.annotations.NonNull;
import ru.tensor.sbis.common.modelmapper.DefaultListMapper;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.generated.CornerCoordinates;

/**
 * @author am.boldinov
 */
public class CornerPointListMapper extends DefaultListMapper<CornerCoordinates, CornerPoint> {

    @Nullable
    @Override
    protected CornerPoint map(@NonNull CornerCoordinates coordinates) throws Exception {
        return new CornerPoint(coordinates.getX(), coordinates.getY());
    }
}
