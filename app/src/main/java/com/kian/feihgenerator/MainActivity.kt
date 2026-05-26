package com.kian.feihgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kian.feihgenerator.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 主题状态交由 MainScreen 内部管理，以支持落地页的手动切换与系统跟随
            MainScreen()
        }
    }
}
