package androidx.lifecycle

/**
 * Реализация [LifecycleOwner] для поддержки обратной совместимости при переходи на версию 2.6.+.
 *
 * @author kv.martyshenko
 */
interface LifecycleOwnerCompat : LifecycleOwner {

    override val lifecycle: Lifecycle get() = getLifecycleCompat()

    /**
     * Аналог [LifecycleOwner.getLifecycle].
     */
    fun getLifecycleCompat(): Lifecycle
}