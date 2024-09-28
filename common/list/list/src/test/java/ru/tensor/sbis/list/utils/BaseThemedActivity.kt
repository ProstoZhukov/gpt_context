package ru.tensor.sbis.list.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.design.R

open class BaseThemedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }
}