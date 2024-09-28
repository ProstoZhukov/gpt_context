package androidx.databinding;

/**
 * Класс необходим для работы UI тестов при использовании databinding.
 * Этот класс форвардит вызов логики к сгенерированому DataBinderMapperImpl.
 * <p>
 * Подробности об аналогичной проблеме: https://github.com/robolectric/robolectric/issues/3789
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DataBinderMapperImpl extends MergedDataBinderMapper {
    DataBinderMapperImpl() {
        addMapper(new ru.tensor.sbis.design.selection.DataBinderMapperImpl());
    }
}
