package ru.tensor.sbis.scanner.di.editimage;

import dagger.Component;
import ru.tensor.sbis.scanner.di.ScannerSingletonComponent;
import ru.tensor.sbis.scanner.ui.editimage.EditImageContract;
import ru.tensor.sbis.scanner.ui.viewimage.ViewImageContract;

/**
 * @author am.boldinov
 */

@EditImageScope
@Component(
        dependencies = {
                ScannerSingletonComponent.class
        },
        modules = {
                EditImageModule.class
        }
)
public interface EditImageComponent {

    EditImageContract.Presenter getEditImagePresenter();

    ViewImageContract.Presenter getViewImagePresenter();
}
