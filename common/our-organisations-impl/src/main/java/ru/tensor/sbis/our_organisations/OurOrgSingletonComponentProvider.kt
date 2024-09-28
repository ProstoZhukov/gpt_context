package ru.tensor.sbis.our_organisations

/**
 * Контракт компонента предоставляющего доступ к [OurOrgDiComponent].
 *
 * @author mv.ilin
 */
object OurOrgSingletonComponentProvider {
    fun get(): OurOrgDiComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return OurOrgPlugin.ourOrgComponent
    }
}
