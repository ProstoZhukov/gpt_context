package ru.tensor.sbis.design.profile.person.feature

import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.person_decl.profile.PersonActivityStatusNotifier
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин компонента "Фото сотрудника".
 *
 * @author us.bessonov
 */
object PersonViewPlugin : BasePlugin<Unit>() {

    private val personViewComponent = PersonViewComponentImpl()

    /** @SelfDocumented */
    internal var personClickListenerProvider: FeatureProvider<PersonClickListener>? = null

    /** @SelfDocumented */
    internal var personActivityStatusNotifier: FeatureProvider<PersonActivityStatusNotifier>? = null

    override val dependency = Dependency.Builder()
        .optional(PersonClickListener::class.java) { personClickListenerProvider = it }
        .optional(PersonActivityStatusNotifier::class.java) { personActivityStatusNotifier = it }
        .build()

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(PersonViewComponent::class.java) { personViewComponent }
    )

    override val customizationOptions = Unit

}