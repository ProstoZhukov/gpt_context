package androidx.lifecycle

/**
 * Реализация [Lifecycle] для поддержки обратной совместимости при переходи на версию 2.6.+.
 *
 * @author kv.martyshenko
 */
abstract class LifecycleCompat : Lifecycle() {

    abstract fun getCurrentStateCompat(): State

    override val currentState: State get() = getCurrentStateCompat()

}