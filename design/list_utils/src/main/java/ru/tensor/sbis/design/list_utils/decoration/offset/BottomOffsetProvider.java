package ru.tensor.sbis.design.list_utils.decoration.offset;

/**
 * Реализация отступа для декорации только снизу. Применима,
 * например, для разделителей в списке.
 *
 * @author sa.nikitin
 */
public class BottomOffsetProvider extends BaseOffsetProvider {

    public BottomOffsetProvider(int offset) {
        super(0, 0, 0, offset);
    }

}
