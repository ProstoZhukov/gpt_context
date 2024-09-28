package ${packageName}.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.mapper.base.BaseItemMapper
import ${packageName}.contract.${moduleName}CommonFeature

@Module
class ${moduleName}CommonModule {

    @${moduleName}CommonScope
    @Provides
    internal fun provideFeature(context: Context): ${moduleName}CommonFeature =
    ${moduleName}CommonFeature(context)

    @${moduleName}CommonScope
    @Provides
    internal fun provideBaseItemMapper(): BaseItemMapper = BaseItemMapper()
}