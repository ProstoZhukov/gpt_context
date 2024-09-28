package ru.tensor.sbis.calendar_decl.calendar.events

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design_dialogs.movablepanel.PanelWidth
import java.util.UUID

/** Вспомогательный объект для работы с меню создания событий */
interface CalendarCreateEventDIComponentCreator {

    /**
     * Создать компонент
     * @param fragment фрагмент, в котором будет открыто меню
     * @param profileUuid UUID пользователя, для которого загружаются возможные типы создания событий.
     * Если null, то типы событий будут загружены для текущего пользователя
     * @param shortMenu укороченное меню, включающее в себя возможность указывать рабочее время и выходной,
     * но лишенное некоторых пунктов ДУРВ (используется в Saby Clients)
     * @param includePlanVacationsOnly включать ли только отпуска, которые можно оформить как плановые
     * @param panelWidthForLandscape положение шторки в горизонтальной ориентации экрана
     */
    fun createComponent(
        fragment: Fragment,
        profileUuid: UUID?,
        shortMenu: Boolean = false,
        includePlanVacationsOnly: Boolean = false,
        panelWidthForLandscape: PanelWidth = PanelWidth.END_HALF,
    )

    /**
     * Запустить процесс создания события
     * @param departmentUUID UUID отдела пользователя
     * @param eventCreationPersonType для кого создается событие
     * @param params доп. параметры создания события
     */
     fun start(departmentUUID: UUID, eventCreationPersonType: EventCreationPersonType, params: CreateEventParams)

     /** Обновить UUID профиля пользователя [profileUuid] */
     fun updateProfile(profileUuid: UUID?)
}