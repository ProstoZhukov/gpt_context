package ru.tensor.sbis.persons;

import ru.tensor.sbis.design.profile_decl.person.InitialsStubData;

/**
 * Created by vi.demidov on 28.03.18.
 */

public interface IContactVM extends IPersonModel {

    /**
     * Возвращает должность, если персона в нашей компании
     * Возвращает подразделение или компания, если персона из другой компании
     * @return должность/подразделение/компания
     */
    String getData1();

    /**
     * Возвращает подразделение или компания, если персона из другой компании
     * @return подразделение/компания/null
     */
    String getData2();

    /**@SelfDocumented */
    boolean isWithSubAttribute();

    /**
     * Возвращает данные для отображения заглушки с инициалами
     * @return данные для отображения заглушки с инициалами
     */
    InitialsStubData getInitialsStubData();

    /**
     * Устанавливает должность, если персона в нашей компании
     * Устанавливает подразделение или компания, если персона из другой компании
     */
    void setData1(String data1);

    /**
     * Устанавливает подразделение или компания, если персона из другой компании
     */
    void setData2(String data2);

    /**@SelfDocumented */
    void setWithSubAttribute(boolean isWithSubAttribute);

    /**
     * Устанавливает данные для отображения заглушки с инициалами
     */
    void setInitialsStubData(InitialsStubData initialsStubData);
}
