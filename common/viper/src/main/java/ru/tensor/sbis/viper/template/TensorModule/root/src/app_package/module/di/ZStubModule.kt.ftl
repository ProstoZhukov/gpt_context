package ${packageName}.di

import dagger.Module
import dagger.Provides
import ${packageName}.contract.${modelName}Feature

@Module
class ${modelName}Module {

    @Provides
    @${modelName}Scope
    internal fun provideFeature(): ${modelName}Feature = ${modelName}Feature()
}