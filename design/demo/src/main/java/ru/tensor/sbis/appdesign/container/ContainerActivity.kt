package ru.tensor.sbis.appdesign.container

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivityContainerBinding
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.createFragmentContainer
import ru.tensor.sbis.design.container.createParcelableFragmentContainer
import ru.tensor.sbis.design.container.createViewContainer
import ru.tensor.sbis.design.container.locator.*
import ru.tensor.sbis.base_components.R as RBaseComponents

/**
 * Demo activity для демонстрации работы с компонентом "Контейнер"
 *
 * @author ma.kolpakov
 */
class ContainerActivity : AppCompatActivity(R.layout.activity_container), View.OnClickListener {
    lateinit var binding: ActivityContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.center.setOnClickListener(this)
        binding.topLeft.setOnClickListener(this)
        binding.topRight.setOnClickListener(this)
        binding.bottomLeft.setOnClickListener(this)
        binding.bottomRight.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val container =
            createParcelableFragmentContainer(DemoHeaderFragmentCreator(DemoHeaderFragmentContent())).apply {
                isAnimated = true
            }
        container.dimType = DimType.valueOf(binding.containerDim.selectedItem as String)
        container.show(
            supportFragmentManager,
            createHorizontalLocator(
                LocatorType.valueOf((binding.locatorTypeHorizontal.selectedItem as String).toUpperCase()),
                v,
                convertRadioIdToEnum(binding.containerAlignmentHorizontal.checkedRadioButtonId),
                binding.containerForce.isChecked,
                binding.containerInner.isChecked
            ),
            createVerticalLocator(
                LocatorType.valueOf((binding.locatorTypeVertical.selectedItem as String).toUpperCase()),
                v,
                convertRadioIdToEnum(binding.containerAlignmentVertical.checkedRadioButtonId),
                binding.containerForce.isChecked,
                binding.containerInner.isChecked
            ),
        )
    }

    private fun updateFullscreen() {
        if (!resources.getBoolean(RBaseComponents.bool.is_tablet) && resources.getBoolean(RBaseComponents.bool.is_landscape) &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !isInMultiWindowMode)
        ) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        updateFullscreen()
    }

    override fun onContentChanged() {
        super.onContentChanged()
        updateFullscreen()
    }

    private fun createVerticalLocator(
        type: LocatorType,
        anchor: View,
        alignment: LocatorAlignment,
        force: Boolean,
        innerPosition: Boolean
    ): VerticalLocator {
        return when (type) {
            LocatorType.SCREEN -> {
                ScreenVerticalLocator(
                    VerticalAlignment.values().first { it.ordinal == alignment.ordinal },
                    if (binding.containerBounds.isChecked) binding.demoContainerContent.id else NO_ID
                )
            }
            LocatorType.ANCHOR -> {
                AnchorVerticalLocator(
                    VerticalAlignment.values().first { it.ordinal == alignment.ordinal },
                    if (binding.containerBounds.isChecked) binding.demoContainerContent.id else NO_ID,
                    force,
                    innerPosition
                ).apply { anchorView = anchor }
            }
        }
    }

    private fun createHorizontalLocator(
        type: LocatorType,
        anchor: View,
        alignment: LocatorAlignment,
        force: Boolean,
        innerPosition: Boolean
    ): HorizontalLocator {
        return when (type) {
            LocatorType.SCREEN -> {
                ScreenHorizontalLocator(
                    HorizontalAlignment.values().first() { it.ordinal == alignment.ordinal },
                    if (binding.containerBounds.isChecked) binding.demoContainerContent.id else NO_ID
                )
            }
            LocatorType.ANCHOR -> {
                AnchorHorizontalLocator(
                    HorizontalAlignment.values().first { it.ordinal == alignment.ordinal },
                    if (binding.containerBounds.isChecked) binding.demoContainerContent.id else NO_ID,
                    force,
                    innerPosition
                ).apply {
                    anchorView = anchor
                }
            }
        }
    }
}

fun convertRadioIdToEnum(id: Int): LocatorAlignment {
    return when (id) {
        R.id.container_left,
        R.id.container_top    -> {
            LocatorAlignment.START
        }
        R.id.container_right,
        R.id.container_bottom -> {
            LocatorAlignment.END
        }
        else                  -> {
            LocatorAlignment.CENTER
        }
    }
}

enum class LocatorType {
    SCREEN,
    ANCHOR
}