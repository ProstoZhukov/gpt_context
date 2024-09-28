package ru.tensor.sbis.persons;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.UUID;

import ru.tensor.sbis.persons.util.PersonNameTemplate;
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus;

/**
 * Created by vi.demidov on 28.03.18.
 * Интерфейс модели данных, общих для работы с персоной на любом экране.
 */
public interface IPersonModel {

    /**
     * Возвращает идентификатор пользователя.
     * @return идентификатор пользователя
     */
    UUID getUUID();

    /**
     * Возвращает "сырую" ссылку на аватарку пользователя.
     * @return "сырая" ссылка
     */
    String getRawPhoto();

    /**
     * Возвращает подготовленную ссылку на аватарку пользователя.
     * @param width     - ширина аватарки
     * @param height    - высота аватарки
     * @return подготовленная ссылка на аватарку пользователя
     */
    String getPreparedPhoto(int width, int height);

    /**
     * Возвращает модель имени пользователя.
     * @return модель имени пользователя
     */
    PersonName getName();

    /**
     * Возвращает "склеенное" по указанному шаблону имя пользователя.
     * @param template - шаблон имени
     * @return имя пользователя
     */
    String getRenderedName(@SuppressWarnings("deprecation") @NonNull PersonNameTemplate template);

    /**
     * Возвращает "склеенное" имя пользователя.
     * @return имя пользователя
     */
    String getRenderedName();

    /**
     * Возвращает статус активности пользователя.
     * @return статус активности пользователя
     */
    ActivityStatus getActivityStatus();

    /**
     * @return список позиций для выделения имени при поиске.
     */
    List<Integer> getNameHighlight();

    /**
     * Задает идентификатор пользователя.
     * @param uuid - идентификатор
     */
    void setUUID(UUID uuid);

    /**
     * Задает список позиций для выделения имени при поиске.
     * @param highlights - список позиций для выделения.
     */
    void setNameHighlight(List<Integer> highlights);

    /**
     * Задает "сырую" ссылку на аватарку пользователя.
     * @param photoUrl - "сырая" ссылка на аватарку пользователя
     */
    void setRawPhoto(String photoUrl);

    /**
     * Задает модель имени пользователя.
     * @param name - модель имени пользователя
     */
    void setName(PersonName name);

    /**
     * Задает статус активности пользователя.
     * @param activityStatus - статус активности
     */
    void setActivityStatus(ActivityStatus activityStatus);
}
