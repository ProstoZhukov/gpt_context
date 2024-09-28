package androidx.lifecycle

/**
 * Реализация [ViewModelStoreOwner] для поддержки обратной совместимости при переходи на версию 2.6.+.
 *
 * @author kv.martyshenko
 */
interface ViewModelStoreOwnerCompat : ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore get() = getViewModelStoreCompat()

    /**
     * Аналог [ViewModelStoreOwner.getViewModelStore].
     */
    fun getViewModelStoreCompat(): ViewModelStore
}