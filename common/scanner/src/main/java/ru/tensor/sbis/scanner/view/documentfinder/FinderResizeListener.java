package ru.tensor.sbis.scanner.view.documentfinder;

import androidx.annotation.NonNull;

import java.util.List;

import ru.tensor.sbis.scanner.data.model.CornerPoint;

/**
 * @author am.boldinov
 */
interface FinderResizeListener {
    void onFinderResize(@NonNull List<CornerPoint> cornerPointList);
}
