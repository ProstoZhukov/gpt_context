package ru.tensor.sbis.communicator.sbis_conversation.data.mapper;

import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.communicator.common.util.InitialsStubDataUtilsKt;
import ru.tensor.sbis.communicator.contacts_declaration.model.contact.ContactProfile;
import ru.tensor.sbis.communicator.generated.Contact;
import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.persons.PersonName;
import ru.tensor.sbis.profiles.generated.Person;

/** SelfDocumented */
public class ContactVMAndProfileMapper {

    /**
     * Метод для маппинга списка моделей контроллера {@link Contact} в список моделей {@link ContactVM}
     *
     * @param contacts - список моделей контроллера {@link Contact}
     * @return список моделей {@link ContactVM}
     */
    public static ArrayList<ContactVM> mapContactToContactVM(List<ContactProfile> contacts) {
        ArrayList<ContactVM> contactModels = new ArrayList<>();
        if (contacts != null) {
            ContactVM model;
            for (ContactProfile contact : contacts) {
                model = new ContactVM();
                model.setUUID(contact.getUuid());
                model.setRawPhoto(contact.getPhotoUrl());
                model.setName(new PersonName(contact.getName().getFirstName(), contact.getName().getLastName(), contact.getName().getPatronymicName()));
                model.setData1(contact.getPosition());
                model.setData2(contact.getCompanyOrDepartment());
                model.setHasAccess(contact.getPerson().getHasAccess());
                model.setInitialsStubData(contact.getPerson().getInitialsStubData());
                model.setNameHighlight(contact.getNameHighlight());

                switch (contact.getGender()) {
                    case FEMALE:
                        model.setGender(ru.tensor.sbis.person_decl.profile.model.Gender.FEMALE);
                        break;
                    case MALE:
                        model.setGender(ru.tensor.sbis.person_decl.profile.model.Gender.MALE);
                        break;
                    default:
                        model.setGender(ru.tensor.sbis.person_decl.profile.model.Gender.UNKNOWN);
                        break;
                }

                contactModels.add(model);
            }
        }
        return contactModels;
    }

    /**
     * Метод для маппинга списка моделей {@link ContactProfile} в список моделей {@link ContactVM}
     *
     * @param persons - список моделей {@link ContactProfile}
     * @return список моделей {@link ContactVM}
     */
    public static ArrayList<ContactVM> mapPersonToContactVM(ArrayList<Person> persons) {
        ArrayList<ContactVM> contactModels = new ArrayList<>();
        if (persons != null) {
            ContactVM model;
            for (Person person : persons) {
                model = new ContactVM();
                model.setUUID(person.getUuid());
                model.setRawPhoto(person.getPhotoUrl());
                model.setName(new PersonName(person.getName().getFirst(), person.getName().getLast(), person.getName().getPatronymic()));
                model.setHasAccess(person.getHasAccess());
                model.setInitialsStubData(InitialsStubDataUtilsKt.mapPersonDecorationToInitialsStubData(person.getPhotoDecoration()));

                switch (person.getGender()) {
                    case FEMALE:
                        model.setGender(ru.tensor.sbis.person_decl.profile.model.Gender.FEMALE);
                        break;
                    case MALE:
                        model.setGender(ru.tensor.sbis.person_decl.profile.model.Gender.MALE);
                        break;
                    default:
                        model.setGender(ru.tensor.sbis.person_decl.profile.model.Gender.UNKNOWN);
                        break;
                }

                contactModels.add(model);
            }
        }
        return contactModels;
    }
}
