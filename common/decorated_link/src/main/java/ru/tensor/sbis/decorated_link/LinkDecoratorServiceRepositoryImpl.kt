package ru.tensor.sbis.decorated_link

import ru.tensor.sbis.decorated_link.mapper.mapLinkPreview
import ru.tensor.sbis.linkdecorator.generated.LinkDecoratorService
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository
import ru.tensor.sbis.toolbox_decl.linkopener.service.Subscription
import ru.tensor.sbis.platform.generated.Subscription as ControllerSubscription

/**
 * Реализация репозитория, осуществляющего работу с микросервисом декорирования ссылок
 *
 * @author us.bessonov
 */
internal class LinkDecoratorServiceRepositoryImpl(
    private val service: LinkDecoratorService
) : LinkDecoratorServiceRepository {

    override fun subscribe(callback: LinkDecoratorServiceRepository.DataRefreshedCallback): Subscription =
        SubscriptionWrapper(
            service.dataRefreshed().subscribe(object : LinkDecoratorService.DataRefreshedCallback() {
                override fun onEvent(data: ru.tensor.sbis.linkdecorator.generated.LinkPreview) {
                    callback.onEvent(data.mapLinkPreview())
                }
            })
        )

    override fun getDecoratedLinkWithoutDetection(url: String): LinkPreview? =
        service.getDecoratedLinkWithoutDetection(url)?.mapLinkPreview()

    override fun getDecoratedLinkWithDetection(url: String): LinkPreview? =
        service.getDecoratedLinkWithDetection(url)?.mapLinkPreview()

    override fun getDecoratedLinksWithoutDetection(urls: HashSet<String>): List<LinkPreview> =
        service.getDecoratedLinksWithoutDetection(urls).map { it.mapLinkPreview() }

    override fun toJson(text: String): String = service.toJson(text)
}

private class SubscriptionWrapper(private val subscription: ControllerSubscription) : Subscription {

    override fun enable() = subscription.enable()

    override fun disable() = subscription.disable()
}