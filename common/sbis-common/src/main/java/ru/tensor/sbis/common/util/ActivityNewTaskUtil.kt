package ru.tensor.sbis.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * @SeldDocumented
 *
 * @author da.zolotarev
 * */
fun Intent.setNewTaskFlagIfNeeded(context: Context){
    if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}