package ru.tensor.sbis.appdesign.cloudview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.cloudview.data.switchDemoData
import ru.tensor.sbis.appdesign.cloudview.list.DemoCloudViewAdapter
import ru.tensor.sbis.appdesign.cloudview.resources.DemoMessageResourcesHolder
import ru.tensor.sbis.appdesign.databinding.ActivityCloudViewBinding
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool

/**
 * Демо экран для демонстации возможностей CloudView
 *
 * @author ma.kolpakov
 */
class CloudViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityCloudViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val viewPool = MessagesViewPool(
            this,
            DemoMessageResourcesHolder(this),
        )
        val adapter = DemoCloudViewAdapter(viewPool).apply {
            viewBinding.list.adapter = this
            submitList(switchDemoData(this@CloudViewActivity))
        }
        viewBinding.btnSwitch.setOnClickListener {
            adapter.submitList(switchDemoData(this@CloudViewActivity))
        }
    }
}