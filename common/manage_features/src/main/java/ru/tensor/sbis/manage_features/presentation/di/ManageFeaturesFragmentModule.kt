package ru.tensor.sbis.manage_features.presentation.di

import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.verification_decl.account.PersonalAccount
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.manage_features.data.GetValueInteractor
import ru.tensor.sbis.manage_features.presentation.ManageFeaturesFragment
import ru.tensor.sbis.manage_features.presentation.ManageFeaturesViewModel

/**@SelfDocumented */
@Module
internal class ManageFeaturesFragmentModule {

    /**@SelfDocumented */
    @Provides
    fun provideVMFactory(
        currentAccount: UserAccount?,
        currentPersonalAccount: PersonalAccount,
        interactor: GetValueInteractor
    ): ManageFeaturesViewModel.Factory = ManageFeaturesViewModel.Factory(currentAccount, currentPersonalAccount, interactor)

    /**@SelfDocumented */
    @Provides
    fun provideViewModel(fragment: ManageFeaturesFragment, factory: ManageFeaturesViewModel.Factory) =
        ViewModelProviders.of(fragment, factory).get(ManageFeaturesViewModel::class.java)
}