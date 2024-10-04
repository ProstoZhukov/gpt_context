package ru.tensor.sbis.design.list_utils.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.list_utils.decoration.predicate.composition.AndPredicate;
import ru.tensor.sbis.design.list_utils.decoration.predicate.composition.OrPredicate;

import android.view.View;

import org.jetbrains.annotations.NotNull;

/**
 * Базовый класс для декорации элементов {@link RecyclerView},
 * использующий композицию вместо наследования для реализации конкретной декорации.
 * Разбивает декорирование элементов списка на 4 аспекта,
 * реализуемые при помощи 4 вспомогательных интерфейсов:
 * - Отступы элемента {@link ItemOffsetProvider};
 * - Отрисовка декорации ДО отрисовки элемента списка {@link BeforeDrawer};
 * - Отрисовка декорации ПОСЛЕ отрисовки элемента списка {@link AfterDrawer};
 * - Выбор элементов, которые нужно декорировать {@link Predicate}.
 * <p>
 * Данный подход позволяет переиспользовать реализации отдельных аспектов, а так же гибко
 * реализовывать отдельные аспекты с возможностью наследования - внутри аспекта.
 * Для каждого аспекта есть отдельный setter, поддерживающий BuilderStyle. (прим. {@link #setPredicate(Predicate)}).
 * Так же есть setter для объекта, реализующего сразу несколько аспектов - {@link #setDrawer(Object)}.
 *
 * @author sa.nikitin
 */
@SuppressWarnings({"unused"})
public class Decoration extends RecyclerView.ItemDecoration {

    //region Decoration configuration
    @Nullable
    private Predicate mPredicate;
    @Nullable
    private ItemOffsetProvider mOffsetProvider;
    @Nullable
    private BeforeDrawer mBeforeDrawer;
    @Nullable
    private AfterDrawer mAfterDrawer;
    //endregion

    //region Cache fields
    private final Rect mItemBounds = new Rect();
    private final Rect mItemOffsets = new Rect();
    // endregion

    //region Getters & Setters

    /**
     * Получить объект, отвечающий за выбор элементов, которые необходимо декорировать.
     */
    @Nullable
    public Predicate getPredicate() {
        return mPredicate;
    }

    /**
     * Задать объект, отвечающий за выбор элементов, которые необходимо декорировать.
     */
    public Decoration setPredicate(@Nullable Predicate predicate) {
        return setPredicate(predicate, Predicate.Strategy.REPLACE);
    }

    /**
     * Применить объект, отвечающий за выбор элементов, которые необходимо декорировать.
     *
     * @param predicate - предикат, который необходимо применить
     * @param strategy  - стратегия применения предиката
     */
    public Decoration setPredicate(@Nullable Predicate predicate, @NonNull Predicate.Strategy strategy) {
        mPredicate = strategy.apply(mPredicate, predicate);
        return this;
    }

    /**
     * Получить объект, отвечающий за размер отступов у элементов списка.
     */
    @Nullable
    public ItemOffsetProvider getOffsets() {
        return mOffsetProvider;
    }

    /**
     * Задать объект, отвечающий за размер отступов у элементов списка.
     */
    public Decoration setOffsets(@Nullable ItemOffsetProvider provider) {
        mOffsetProvider = provider;
        return this;
    }

    /**
     * Получить отрисовщик декорации до отрисовки элемента списка.
     */
    @Nullable
    public BeforeDrawer getBeforeDrawer() {
        return mBeforeDrawer;
    }

    /**
     * Задать отрисовщик декорации до отрисовки элемента списка.
     */
    public Decoration setBeforeDrawer(@Nullable BeforeDrawer drawer) {
        mBeforeDrawer = drawer;
        return this;
    }

    /**
     * Получить отрисовщик декорации после отрисовки элемента списка.
     */
    @Nullable
    public AfterDrawer getAfterDrawer() {
        return mAfterDrawer;
    }

    /**
     * Задать отрисовщик декорации после отрисовки элемента списка.
     */
    public Decoration setAfterDrawer(@Nullable AfterDrawer drawer) {
        mAfterDrawer = drawer;
        return this;
    }

    /**
     * Задать объект, реализующий один или несколько аспектов декорирования.
     * Например, если объект реализует отрисовку до и после элемента, - данный метод
     * воспримет drawer и как {@link BeforeDrawer}, и как {@link AfterDrawer} одновременно.
     *
     * @param drawer - объект, реализующий несколько аспектов декорирования.
     */
    public Decoration setDrawer(@NonNull Object drawer) {
        boolean used = false;
        if (drawer instanceof BeforeDrawer) {
            setBeforeDrawer((BeforeDrawer) drawer);
            used = true;
        }
        if (drawer instanceof AfterDrawer) {
            setAfterDrawer((AfterDrawer) drawer);
            used = true;
        }
        if (drawer instanceof ItemOffsetProvider) {
            setOffsets((ItemOffsetProvider) drawer);
            used = true;
        }
        if (drawer instanceof Predicate) {
            setPredicate((Predicate) drawer);
            used = true;
        }
        if (!used) {
            // Делегат должен реализовывать хотя бы один аспект декорирования
            throw new IllegalArgumentException("Delegate " + drawer + " should implement any companion interfaces.");
        }
        return this;
    }
    //endregion

    //region RecyclerView.ItemDecoration impl
    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View itemView,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        if (needToDecorate(itemView, parent, state)) {
            getItemOffsetsInternal(outRect, itemView, parent, state);
        } else {
            outRect.setEmpty();
        }
    }

    private void getItemOffsetsInternal(@NonNull Rect outRect,
                                        @NonNull View itemView,
                                        @NonNull RecyclerView parent,
                                        @NonNull RecyclerView.State state) {
        if (mOffsetProvider != null) {
            mOffsetProvider.getItemOffsets(outRect, itemView, parent, state);
        } else {
            outRect.setEmpty();
        }
    }

    @Override
    public void onDraw(@NotNull Canvas canvas,
                       @NotNull RecyclerView parent,
                       @NotNull RecyclerView.State state) {
        if (mBeforeDrawer != null) {
            // Выполняем отрисовку декорации до отрисовки элемента списка
            drawForEach(mBeforeDrawer, canvas, parent, state);
        }
    }

    @Override
    public void onDrawOver(@NotNull Canvas canvas,
                           @NotNull RecyclerView parent,
                           @NotNull RecyclerView.State state) {
        if (mAfterDrawer != null) {
            // Выполняем отрисовку декорации после отрисовки элемента списка
            drawForEach(mAfterDrawer, canvas, parent, state);
        }
    }

    private boolean needToDecorate(@NonNull View itemView,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
        return mPredicate == null || mPredicate.needToDecorate(itemView, parent, state);
    }

    /**
     * Выполнить отрисовку декорации для всех элементов, которые этого требуют.
     *
     * @param drawer - отрисовщик декорации
     * @param canvas - canvas для отрисовки
     * @param parent - родительский recycler view
     * @param state  - состояние recycler view
     */
    private void drawForEach(@NonNull Drawer drawer,
                             @NonNull Canvas canvas,
                             @NonNull RecyclerView parent,
                             @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null) return;

        canvas.save();

        final int parentLeft;
        final int parentRight;
        if (parent.getClipToPadding()) {
            // Ограничиваем канвас для того, чтобы предотвратить
            // отрисовку декорации за границами recycler view
            parentLeft = parent.getPaddingLeft();
            parentRight = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(
                    parentLeft,
                    parent.getPaddingTop(),
                    parentRight,
                    parent.getHeight() - parent.getPaddingBottom()
            );
        } else {
            parentLeft = 0;
            parentRight = parent.getWidth();
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            View itemView = parent.getChildAt(i);
            if (needToDecorate(itemView, parent, state)) {
                // Получаем границы элемента списка
                parent.getDecoratedBoundsWithMargins(itemView, mItemBounds);
                final int translationY = Math.round(itemView.getTranslationY());
                // Применяем смещение по вертикали
                final int top = mItemBounds.top + translationY;
                final int bottom = mItemBounds.bottom + translationY;
                // Достаём отступы
                getItemOffsetsInternal(mItemOffsets, itemView, parent, state);
                // Отрисовываем декорацию для элемента списка
                drawer.draw(canvas, itemView, parentLeft, top, parentRight, bottom, mItemOffsets);
            }
        }

        canvas.restore();
    }
    //endregion

    //region Companion Interfaces

    /**
     * Интерфейс объекта, отвечающего за выбор элементов списка, которые нужно декорировать.
     */
    public interface Predicate {

        /**
         * Нужно ли декорировать элемент списка.
         *
         * @param itemView - view элемента
         * @param parent   - родительский recycler view
         * @param state    - состояние recycler view
         * @return true - если элемент нужно декорировать, false - иначе
         */
        boolean needToDecorate(@NonNull View itemView,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state);

        /**
         * Стратегия применения предиката.
         */
        enum Strategy {

            /**
             * Заменяем предикат.
             */
            REPLACE {
                @Nullable
                @Override
                public Predicate apply(@Nullable Predicate current, @Nullable Predicate recent) {
                    return recent;
                }
            },

            /**
             * Связываем предикаты логическим AND.
             */
            AND {
                @Nullable
                @Override
                public Predicate apply(@Nullable Predicate current, @Nullable Predicate recent) {
                    if (current == null) {
                        return recent;
                    }
                    if (recent == null) {
                        return current;
                    }
                    return new AndPredicate(current, recent);
                }
            },

            /**
             * Связываем предикаты логическим OR.
             */
            OR {
                @Nullable
                @Override
                public Predicate apply(@Nullable Predicate current, @Nullable Predicate recent) {
                    if (current == null) {
                        return recent;
                    }
                    if (recent == null) {
                        return current;
                    }
                    return new OrPredicate(current, recent);
                }
            };

            /**
             * Применить новый предикат к текущему.
             *
             * @param current - текущий предикат
             * @param recent  - новый предикат
             * @return результат применения нового предиката
             */
            @Nullable
            public abstract Predicate apply(@Nullable Predicate current, @Nullable Predicate recent);
        }
    }

    /**
     * Интерфейс объекта, отвечающего за размер отступов у элемента списка.
     */
    public interface ItemOffsetProvider {

        /**
         * Получить отступы для элемента списка.
         *
         * @param outRect  - объект, в который нужно передать отступы
         * @param itemView - view для которого вычисляем отступы
         * @param parent   - родительский recycler view
         * @param state    - состояние recycler view
         */
        void getItemOffsets(@NonNull Rect outRect,
                            @NonNull View itemView,
                            @NonNull RecyclerView parent,
                            @NonNull RecyclerView.State state);
    }

    /**
     * Интерфейс отрисовщика декорации
     */
    public interface Drawer {

        /**
         * Отрисовать декорацию до отрисовки элемента списка.
         *
         * @param canvas   - canvas для отрисовки декорации
         * @param itemView - view элемента списка внутри recycler view
         * @param left     - левая граница для декорации
         * @param top      - верхняя граница для декорации
         * @param right    - правая граница для декорации
         * @param bottom   - нижняя граница для декорации
         * @param offsets  - отступы для декорирования
         */
        void draw(@NonNull Canvas canvas,
                  @NonNull View itemView,
                  int left, int top, int right, int bottom,
                  @NonNull Rect offsets);
    }

    /**
     * Интерфейс отрисовщика декорации до отрисовки элемента списка.
     */
    public interface BeforeDrawer extends Drawer {
    }

    /**
     * Интерфейс отрисовщика декорации после отрисовки элемента списка.
     */
    public interface AfterDrawer extends Drawer {
    }
    //endregion

}
