package ru.tensor.sbis.base_components.activity.content

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityContentFactory

/**
 * Реализация обработки контента активности с единственным фрагментом.
 *
 * @param fragmentFactory создать фрагмент
 * @param fragmentTag тэг фрагмента
 * @param fragmentInstall способ добавления фрагмента
 * @param onFragmentNotCreated действие, если фрагмент не установлен/
 *
 * @author kv.martyshenko
 */
class AloneFragmentContentFactory<in T: AppCompatActivity>(
    private val fragmentFactory: (Context, Intent?) -> Fragment?,
    val fragmentTag: String = "ALONE_FRAGMENT_TAG",
    private val fragmentInstall: (FragmentManager, Fragment, Int, String) -> Unit = { fm, f, id, tag ->
        fm.beginTransaction()
            .add(id, f, tag)
            .commit()
    },
    private val onFragmentNotCreated: (T, FrameLayout, Intent?) -> Unit = { act, _, _ ->
        act.finish()
    }
) : ActivityContentFactory<T> {

    override fun create(
        activity: T,
        parent: FrameLayout,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState == null) {
            val intent = activity.intent
            val fragment = fragmentFactory(activity, intent)
            if (fragment != null) {
                fragmentInstall(activity.supportFragmentManager, fragment, parent.id, fragmentTag)
            } else {
                onFragmentNotCreated(activity, parent, intent)
            }
        }
    }
}