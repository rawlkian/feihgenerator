package com.kian.feihgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kian.feihgenerator.ui.screens.MainScreen
import com.kian.feihgenerator.ui.theme.FEIHGeneratorTheme // 请确保这行和你本地的 Theme 名字完全一致，如果报错可以自动修复导入

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FEIHGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 强制且唯一的应用入口
                    MainScreen()
                }
            }
        }
    }
}