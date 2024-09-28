package ru.tensor.sbis.recipient_selection.profile.contract

import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionProvider

interface RecipientSelectionFeature :
    RecipientSelectionProvider,
    RecipientSelectionResultManagerProvider,
    RepostRecipientSelectionResultManagerProvider