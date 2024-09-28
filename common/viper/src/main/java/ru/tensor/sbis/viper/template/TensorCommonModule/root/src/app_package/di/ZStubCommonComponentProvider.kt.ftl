package ${packageName}.di

import android.content.Context

object ${moduleName}CommonComponentProvider {

    operator fun get(context: Context): ${moduleName}CommonComponent =
            (context.applicationContext as ${moduleName}CommonComponentHolder).${"${moduleName}"?lower_case}CommonComponent
}