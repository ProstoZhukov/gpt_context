package ru.tensor.sbis.appdesign.container

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import ru.tensor.sbis.appdesign.R

/**
 * Реализация контента для демо экрана контейнера.
 *
 * @author ma.kolpakov
 */
class SbisContainerFragment : Fragment(R.layout.fragment_container_content) {
    lateinit var additionalView: View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        additionalView = TextView(requireContext()).apply {
            text = "Дополнительная вью "
            layoutParams = LinearLayout.LayoutParams(400, 400)
        }
    }
    fun onCancel() {
        Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT)
            .show()
    }

    fun onAccept() {
        Toast.makeText(context, "Accept", Toast.LENGTH_SHORT)
            .show()
    }

    fun randomSize() {
        view?.let {
            val contentContainer = it.findViewById<LinearLayout>(R.id.demo_container_fragment_container)
            if(contentContainer.children.contains(additionalView)){
                contentContainer.removeView(additionalView)
            }else{
                contentContainer.addView(additionalView)
            }
        }
    }
}