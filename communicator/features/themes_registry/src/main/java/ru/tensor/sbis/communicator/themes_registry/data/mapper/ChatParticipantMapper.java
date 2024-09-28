package ru.tensor.sbis.communicator.themes_registry.data.mapper;

import android.content.Context;
import androidx.annotation.NonNull;

import ru.tensor.sbis.persons.ContactVM;

/**
 * Маппер моделей участников чата
 *
 * @author vv.chekurda
 */
public class ChatParticipantMapper extends AbstractContactVMMapper<ContactVM> {

    public ChatParticipantMapper(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected ContactVM createBlank() {
        return new ContactVM();
    }

}
