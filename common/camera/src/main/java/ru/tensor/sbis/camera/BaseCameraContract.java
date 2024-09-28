package ru.tensor.sbis.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;

import io.reactivex.Observable;
import ru.tensor.sbis.mvp.presenter.BasePresenter;

/**
 * Контрактный интерфейс описывающий взаимодейтсвие между view и presenter-ом
 * @author am.boldinov
 */
@SuppressWarnings({"JavaDoc", "unused", "RedundantSuppression"})
public interface BaseCameraContract {

    /** @SelfDocumented */
    interface View {

        /** @SelfDocumented */
        @Nullable
        Set<String> getGrantedPermissions();

        /** @SelfDocumented */
        @NonNull
        Set<String> getRequiredPermissions();

        /** @SelfDocumented */
        void requestPermissions(@NonNull Set<String> permissions);

        /** @SelfDocumented */
        void configurePreviewTransformation(int previewWidth, int previewHeight);

        /** @SelfDocumented */
        void displayStartingPreviewError();

        /** @SelfDocumented */
        @Nullable
        SurfaceProvider getSurfaceProvider();

        /** @SelfDocumented */
        Observable<Boolean> getSurfacePreparingObservable();
    }

    /** @SelfDocumented */
    interface Presenter<V extends View> extends BasePresenter<V> {

        /** @SelfDocumented */
        void onViewStarted();

        /** @SelfDocumented */
        void onViewStopped();

        /** @SelfDocumented */
        void onPermissionsRequestResult(@Nullable Set<String> grantedPermissions);
    }
}
