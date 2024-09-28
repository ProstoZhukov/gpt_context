package ru.tensor.sbis.persons.recipientselection;

import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by se.petrova on 12/1/17.
 */

public interface RecipientSelectionItem extends Parcelable {
    /**
     * @return amount of contacts at recipient item
     */
    int getItemCount();

    /**
     * @return uuid
     */
    UUID getUUID();
}
