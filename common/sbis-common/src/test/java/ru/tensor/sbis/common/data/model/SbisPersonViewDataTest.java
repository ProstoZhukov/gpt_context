package ru.tensor.sbis.common.data.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewData;

public class SbisPersonViewDataTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(SbisPersonViewData.class)
                .suppress(Warning.NONFINAL_FIELDS).verify();
    }
}