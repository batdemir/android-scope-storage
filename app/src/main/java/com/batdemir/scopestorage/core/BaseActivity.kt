package com.batdemir.scopestorage.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.batdemir.scopestorage.ui.theme.ScopeStorageTheme
import com.batdemir.scopestorage.utils.PermissionManager.permissions

@OptIn(ExperimentalMaterial3Api::class)
abstract class BaseActivity : ComponentActivity() {
    abstract val showBackPress: Boolean
    abstract val title: String
    abstract fun composeScreen(): @Composable ColumnScope.() -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissions(this)
        setContent {
            ScopeStorageTheme {
                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = { Text(text = title) },
                            navigationIcon = {
                                if (showBackPress) Icon(
                                    modifier = Modifier.clickable { onBackPressedDispatcher.onBackPressed() },
                                    painter = painterResource(id = android.R.drawable.arrow_down_float),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        composeScreen().invoke(this)
                    }
                }
            }
        }
    }
}
