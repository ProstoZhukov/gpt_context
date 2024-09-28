package ru.tensor.sbis.scanner.data.mapper;

import androidx.annotation.Nullable;

import io.reactivex.annotations.NonNull;
import ru.tensor.sbis.common.modelmapper.DefaultListMapper;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.generated.CornerCoordinates;

/**
 * @author am.boldinov
 */
public class CornerCoordinatesListMapper extends DefaultListMapper<CornerPoint, CornerCoordinates> {

    @Nullable
    @Override
    protected CornerCoordinates map(@NonNull CornerPoint source) throws Exception {
        return new CornerCoordinates(source.x, source.y);
    }
}
