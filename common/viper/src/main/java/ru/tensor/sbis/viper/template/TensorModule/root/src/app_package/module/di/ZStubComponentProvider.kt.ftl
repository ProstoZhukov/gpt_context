package ${packageName}.di

import android.content.Context

object ${modelName}ComponentProvider {

	operator fun get(context: Context): ${modelName}Component =
            (context.applicationContext as ${modelName}ComponentHolder).${"${modelName}"?lower_case}Component
}