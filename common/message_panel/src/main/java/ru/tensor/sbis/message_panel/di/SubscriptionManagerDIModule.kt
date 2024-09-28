package ru.tensor.sbis.message_panel.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager

@Module
class SubscriptionManagerDIModule {

    @Provides
    fun eventManagerServiceSubscriber(context: Context): EventManagerServiceSubscriber =
        DefaultEventManagerServiceSubscriber(context)

    @Provides
    fun subscriptionManager(eventManagerServiceSubscriber: EventManagerServiceSubscriber): SubscriptionManager =
        SubscriptionManager(eventManagerServiceSubscriber)
}