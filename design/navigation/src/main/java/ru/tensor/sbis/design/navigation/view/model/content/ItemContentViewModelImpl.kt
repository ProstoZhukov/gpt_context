package ru.tensor.sbis.design.navigation.view.model.content

import android.view.LayoutInflater
import android.view.View
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.navigation.NavigationPlugin.navigationPreferences
import ru.tensor.sbis.design.navigation.view.ItemContentFactory
import ru.tensor.sbis.design.navigation.view.model.createLiveData

/**
 * Реализация [ItemContentViewModel].
 *
 * @author ma.kolpakov
 */
internal class ItemContentViewModelImpl(
    content: NavigationItemContent
) : ItemContentViewModel {

    private val expansionSubject =
        BehaviorSubject.createDefault(navigationPreferences.isExpanded(content.id))

    override val contentVisible = createLiveData(content.visibility)

    override val contentExpanded = createLiveData(
        Observable.combineLatest(
            content.visibility,
            expansionSubject
        ) { isVisible, isExpanded -> isVisible && isExpanded }
            .distinctUntilChanged()
            .doOnNext { expanded ->
                // Подписка на видимость контента с учётом раскрытия и общей видимости.
                if (expanded) {
                    content.onContentOpened()
                } else {
                    content.onContentClosed()
                }
                navigationPreferences.saveState(content.id, expanded)
            }
    )
    override val contentFactory: ItemContentFactory = { context, parent ->
        content.createContentView(LayoutInflater.from(context), parent)
    }

    override fun onExpandClicked(ignored: View, isExpand: Boolean?) {
        expansionSubject.onNext(isExpand ?: expansionSubject.value!!.not())
    }
}
