package com.kian.feihgenerator.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RectF as AndroidRectF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.kian.feihgenerator.IconGenViewModel
import com.kian.feihgenerator.HeroGenViewModel
import com.kian.feihgenerator.ui.theme.FEIHGeneratorTheme

enum class AppScreen {
    WELCOME,
    ICON_GENERATOR,
    HERO_GENERATOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var themeMode by remember { mutableIntStateOf(0) } // 0: System, 1: Light, 2: Dark
    var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }

    FEIHGeneratorTheme(themeMode = themeMode) {
        val iconViewModel: IconGenViewModel = viewModel()
        val heroViewModel: HeroGenViewModel = viewModel()

        Scaffold(
            topBar = {
                if (currentScreen != AppScreen.WELCOME) {
                    TopAppBar(
                        title = {
                            Text(
                                text = if (currentScreen == AppScreen.ICON_GENERATOR) "Icon Generator" else "Hero Generator",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { currentScreen = AppScreen.WELCOME }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回主页")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(if (currentScreen == AppScreen.WELCOME) PaddingValues(0.dp) else paddingValues)) {
                when (currentScreen) {
                    AppScreen.WELCOME -> WelcomeLandingScreen(
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it },
                        onNavigateToIcon = { currentScreen = AppScreen.ICON_GENERATOR },
                        onNavigateToHero = { currentScreen = AppScreen.HERO_GENERATOR }
                    )
                    AppScreen.ICON_GENERATOR -> IconGeneratorScreen(viewModel = iconViewModel)
                    AppScreen.HERO_GENERATOR -> HeroGeneratorScreen(iconViewModel = iconViewModel, heroViewModel = heroViewModel)
                }
            }
        }
    }
}

@Composable
fun WelcomeLandingScreen(
    themeMode: Int,
    onThemeModeChange: (Int) -> Unit,
    onNavigateToIcon: () -> Unit,
    onNavigateToHero: () -> Unit
) {
    val isDark = when(themeMode) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }

    val bgColor1 = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val bgColor2 = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val bgColor3 = if (isDark) Color(0xFF020617) else Color(0xFFF1F5F9)
    
    val accentColor = MaterialTheme.colorScheme.primary
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(bgColor1, bgColor2, bgColor3)))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = accentColor.copy(alpha = 0.08f), radius = 400.dp.toPx(), center = Offset(0f, 0f))
            drawCircle(color = Color(0xFFFF9800).copy(alpha = 0.06f), radius = 350.dp.toPx(), center = Offset(size.width, size.height * 0.4f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        ThemeModeItem(icon = Icons.Default.SettingsSuggest, isSelected = themeMode == 0) { onThemeModeChange(0) }
                        ThemeModeItem(icon = Icons.Default.LightMode, isSelected = themeMode == 1) { onThemeModeChange(1) }
                        ThemeModeItem(icon = Icons.Default.DarkMode, isSelected = themeMode == 2) { onThemeModeChange(2) }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 10.dp)) {
                Box(modifier = Modifier.size(80.dp).background(Brush.linearGradient(listOf(accentColor, Color(0xFFEF4444))), shape = RoundedCornerShape(22.dp)).padding(2.dp)) {
                    Box(modifier = Modifier.fillMaxSize().background(if(isDark) Color(0xFF0F172A) else Color.White, shape = RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(36.dp), tint = accentColor)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "FEIH Generator", fontSize = 32.sp, fontWeight = FontWeight.Black, color = textColor, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Front-end Icons & Hero Art Generator", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = subTextColor, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(color = accentColor.copy(alpha = 0.12f), shape = RoundedCornerShape(100.dp)) {
                    Text(text = "专为 iiSU 与 Cocoon 前端定制", fontSize = 11.sp, color = accentColor, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "请选择功能模块：", fontSize = 12.sp, color = subTextColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                WelcomeEngineCard(title = "Icon Generator", description = "为App生成风格统一的圆角矩形图标，固定导出为1024 × 1024像素大小。", icon = Icons.Filled.AccountBox, accentColor = Color(0xFF2196F3), onClick = onNavigateToIcon, isDark = isDark)
                WelcomeEngineCard(title = "Hero Generator", description = "通过简单的操作，依照模板生成美观的Hero图，支持额外添加文字与Deco图饰。", icon = Icons.Filled.Star, accentColor = Color(0xFFFF9800), onClick = onNavigateToHero, isDark = isDark)
            }
            Text(text = "Made by Kian", fontSize = 11.sp, color = subTextColor.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 12.dp))
        }
    }
}

@Composable
fun ThemeModeItem(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.size(36.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun WelcomeEngineCard(title: String, description: String, icon: ImageVector, accentColor: Color, onClick: () -> Unit, isDark: Boolean) {
    val cardBgColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.8f) else Color.White
    val borderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val titleColor = if (isDark) Color.White else Color(0xFF0F172A)
    val descColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Surface(
        onClick = onClick,
        color = cardBgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = if (isDark) 0.dp else 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).background(accentColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = titleColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 12.sp, color = descColor, lineHeight = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = descColor.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IconGeneratorScreen(viewModel: IconGenViewModel) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var showAppListDialog by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showBorderColorDialog by remember { mutableStateOf(false) }
    var showCropDialog by remember { mutableStateOf(false) }
    var showGradientColorDialog by remember { mutableStateOf(false) }
    var showBorderNoticeDialog by remember { mutableStateOf(false) }
    var isEditingStartColor by remember { mutableStateOf(true) }

    var hexLocalText by remember { mutableStateOf("") }
    var previewComponentSizePx by remember { mutableIntStateOf(0) }

    val baseIconLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.selectedBaseIconSource = it; viewModel.isUserCustomIcon = true }
    }
    val bgImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.rawBackgroundUri = it; showCropDialog = true }
    }
    val borderImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.checkAndSetUserBorder(context, it) { pass ->
                if (!pass) Toast.makeText(context, "边框图片大小错误！", Toast.LENGTH_LONG).show()
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isLandscape = maxWidth > maxHeight && maxWidth > 600.dp
        val previewBorderWidth = with(density) { (viewModel.borderWidth * (previewComponentSizePx.toFloat() / 1024f)).toDp() }
        val previewCornerRadius = with(density) { (70f * (previewComponentSizePx.toFloat() / 1024f)).toDp() }

        val previewContent = @Composable {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Card(
                    modifier = Modifier.fillMaxHeight(if (isLandscape) 0.85f else 0.7f).aspectRatio(1f).onGloballyPositioned { previewComponentSizePx = it.size.width },
                    shape = RoundedCornerShape(previewCornerRadius), colors = CardDefaults.cardColors(containerColor = Color.Transparent), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .clip(RoundedCornerShape(previewCornerRadius))
                            .background(if (viewModel.backgroundType == 0) Color(viewModel.backgroundColor.toInt()) else Color.Transparent)
                            .border(
                                width = if (viewModel.borderType == 3) 0.dp else previewBorderWidth,
                                brush = when (viewModel.borderType) {
                                    1 -> Brush.linearGradient(listOf(Color.Cyan, Color.Magenta, Color.Yellow))
                                    2 -> Brush.linearGradient(listOf(Color(viewModel.gradientStartColor.toInt()), Color(viewModel.gradientEndColor.toInt())))
                                    else -> Brush.linearGradient(listOf(Color(viewModel.borderColor.toInt()), Color(viewModel.borderColor.toInt())))
                                },
                                shape = RoundedCornerShape(previewCornerRadius)
                            )
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    if (viewModel.backgroundType == 2) {
                                        viewModel.bgScale = (viewModel.bgScale * zoom).coerceIn(0.4f, 4.0f)
                                        viewModel.bgOffsetX += pan.x
                                        viewModel.bgOffsetY += pan.y
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.backgroundType == 2 && viewModel.croppedBackgroundBitmap != null) {
                            Image(bitmap = viewModel.croppedBackgroundBitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = viewModel.bgScale, scaleY = viewModel.bgScale, translationX = viewModel.bgOffsetX, translationY = viewModel.bgOffsetY), contentScale = ContentScale.Crop)
                        }
                        viewModel.selectedBaseIconSource?.let { iconSource ->
                            Image(painter = rememberAsyncImagePainter(iconSource), contentDescription = null, modifier = Modifier.fillMaxSize(viewModel.iconScale), contentScale = ContentScale.Fit)
                        } ?: Text("未选取图标", color = Color.LightGray)
                        if (viewModel.borderType == 3 && viewModel.userBorderBitmap != null) {
                            Image(bitmap = viewModel.userBorderBitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
                        }
                    }
                }
            }
        }

        val controlsContent = @Composable {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                item {
                    Text("第一步：选取顶层图标", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Button(onClick = { viewModel.loadInstalledApps(context); showAppListDialog = true }) { Text("应用选择器") }
                        FilterChip(selected = viewModel.isUserCustomIcon, onClick = { baseIconLauncher.launch("image/*") }, label = { Text("用户自定义") })
                    }
                }
                item {
                    Text("调整顶层图标大小 (${(viewModel.iconScale * 100).toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                    Slider(value = viewModel.iconScale, onValueChange = { viewModel.iconScale = it }, valueRange = 0.4f..1.0f)
                }
                item {
                    HorizontalDivider()
                    Text("第二步：背景底图设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        FilterChip(selected = viewModel.backgroundType == 0, onClick = { viewModel.backgroundType = 0 }, label = { Text("纯色背景") })
                        FilterChip(selected = viewModel.backgroundType == 2, onClick = { bgImageLauncher.launch("image/*") }, label = { Text("用户自定义") })
                    }
                    if (viewModel.backgroundType == 0) {
                        Button(onClick = { hexLocalText = String.format("%06X", viewModel.backgroundColor and 0xFFFFFFL); showColorPickerDialog = true }, modifier = Modifier.padding(top = 8.dp)) { Text("配置自定义纯色") }
                    }
                }
                item {
                    HorizontalDivider()
                    Text("第三步：边框形态设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        FilterChip(selected = viewModel.borderType == 0, onClick = { viewModel.borderType = 0 }, label = { Text("纯色边框") })
                        FilterChip(selected = viewModel.borderType == 1, onClick = { viewModel.borderType = 1 }, label = { Text("霓虹渐变") })
                        FilterChip(selected = viewModel.borderType == 2, onClick = { viewModel.borderType = 2 }, label = { Text("双色渐变") })
                        FilterChip(selected = viewModel.borderType == 3, onClick = { showBorderNoticeDialog = true }, label = { Text("用户自定义") })
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        if (viewModel.borderType == 0) {
                            Button(onClick = { hexLocalText = String.format("%06X", viewModel.borderColor and 0xFFFFFFL); showBorderColorDialog = true }) { Text("🎨 核心边框颜色") }
                        }
                        if (viewModel.borderType == 2) {
                            Button(onClick = { isEditingStartColor = true; hexLocalText = String.format("%06X", viewModel.gradientStartColor and 0xFFFFFFL); showGradientColorDialog = true }) { Text("🏁 渐变起点") }
                            Button(onClick = { isEditingStartColor = false; hexLocalText = String.format("%06X", viewModel.gradientEndColor and 0xFFFFFFL); showGradientColorDialog = true }) { Text("🛑 渐变终点") }
                        }
                    }
                }
                item {
                    if (viewModel.borderType != 3) {
                        Text("边框粗细调整 (${viewModel.borderWidth.toInt()} px)")
                        Slider(value = viewModel.borderWidth, onValueChange = { viewModel.borderWidth = it }, valueRange = 4f..150f)
                    }
                }
                item {
                    Button(onClick = { viewModel.saveCombinedIconToGallery(context, previewComponentSizePx.toFloat()) }, modifier = Modifier.fillMaxWidth(), enabled = viewModel.selectedBaseIconSource != null, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                        Text("导出图标", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) { previewContent() }
                Card(modifier = Modifier.weight(1f).fillMaxHeight(), shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)) {
                    controlsContent()
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.8f).fillMaxWidth()) { previewContent() }
                Card(modifier = Modifier.weight(1.2f).fillMaxWidth(), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)) {
                    controlsContent()
                }
            }
        }
    }

    if (showBorderNoticeDialog) {
        AlertDialog(onDismissRequest = { showBorderNoticeDialog = false }, title = { Text("自选边框提示") }, text = { Text("请选择大小为1024×1024像素的边框图片。") }, confirmButton = { Button(onClick = { showBorderNoticeDialog = false; borderImageLauncher.launch("image/*") }) { Text("去选择图片") } })
    }
    if (showColorPickerDialog) {
        Dialog(onDismissRequest = { showColorPickerDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("自定义纯色背景", fontWeight = FontWeight.Bold)
                    Slider(value = viewModel.colorPickerRed, onValueChange = { viewModel.colorPickerRed = it; viewModel.updateColorFromRGB() })
                    Slider(value = viewModel.colorPickerGreen, onValueChange = { viewModel.colorPickerGreen = it; viewModel.updateColorFromRGB() })
                    Slider(value = viewModel.colorPickerBlue, onValueChange = { viewModel.colorPickerBlue = it; viewModel.updateColorFromRGB() })
                    OutlinedTextField(value = hexLocalText, onValueChange = { text -> hexLocalText = text; runCatching { viewModel.backgroundColor = "FF$text".toLong(16) } }, label = { Text("HEX代码") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { showColorPickerDialog = false }, modifier = Modifier.align(Alignment.End).padding(top = 12.dp)) { Text("完成") }
                }
            }
        }
    }
    if (showBorderColorDialog) {
        Dialog(onDismissRequest = { showBorderColorDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("自定义边框色彩", fontWeight = FontWeight.Bold)
                    Slider(value = viewModel.borderPickerRed, onValueChange = { viewModel.borderPickerRed = it; viewModel.updateBorderColorFromRGB() })
                    Slider(value = viewModel.borderPickerGreen, onValueChange = { viewModel.borderPickerGreen = it; viewModel.updateBorderColorFromRGB() })
                    Slider(value = viewModel.borderPickerBlue, onValueChange = { viewModel.borderPickerBlue = it; viewModel.updateBorderColorFromRGB() })
                    OutlinedTextField(value = hexLocalText, onValueChange = { text -> hexLocalText = text; runCatching { viewModel.borderColor = ("FF" + text).toLong(16) } }, label = { Text("HEX边框代码") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { showBorderColorDialog = false }, modifier = Modifier.align(Alignment.End).padding(top = 12.dp)) { Text("完成") }
                }
            }
        }
    }
    if (showGradientColorDialog) {
        Dialog(onDismissRequest = { showGradientColorDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(if (isEditingStartColor) "设置渐变起点色彩" else "设置渐变终点色彩", fontWeight = FontWeight.Bold)
                    var pR by remember(isEditingStartColor) { mutableFloatStateOf(if (isEditingStartColor) ((viewModel.gradientStartColor shr 16) and 0xFFL)/255f else ((viewModel.gradientEndColor shr 16) and 0xFFL)/255f) }
                    var pG by remember(isEditingStartColor) { mutableFloatStateOf(if (isEditingStartColor) ((viewModel.gradientStartColor shr 8) and 0xFFL)/255f else ((viewModel.gradientEndColor shr 8) and 0xFFL)/255f) }
                    var pB by remember(isEditingStartColor) { mutableFloatStateOf(if (isEditingStartColor) (viewModel.gradientStartColor and 0xFFL)/255f else (viewModel.gradientEndColor and 0xFFL)/255f) }
                    Slider(value = pR, onValueChange = { pR = it; val r=(it*255).toLong(); if(isEditingStartColor) viewModel.gradientStartColor=(viewModel.gradientStartColor and 0xFFFF00FFL) or (r shl 16) else viewModel.gradientEndColor=(viewModel.gradientEndColor and 0xFFFF00FFL) or (r shl 16); hexLocalText=String.format("%06X", if(isEditingStartColor) viewModel.gradientStartColor and 0xFFFFFFL else viewModel.gradientEndColor and 0xFFFFFFL) }, colors = SliderDefaults.colors(thumbColor = Color.Red))
                    Slider(value = pG, onValueChange = { pG = it; val g=(it*255).toLong(); if(isEditingStartColor) viewModel.gradientStartColor=(viewModel.gradientStartColor and 0xFFFF00FFL) or (g shl 8) else viewModel.gradientEndColor=(viewModel.gradientEndColor and 0xFFFF00FFL) or (g shl 8); hexLocalText=String.format("%06X", if(isEditingStartColor) viewModel.gradientStartColor and 0xFFFFFFL else viewModel.gradientEndColor and 0xFFFFFFL) }, colors = SliderDefaults.colors(thumbColor = Color.Green))
                    Slider(value = pB, onValueChange = { pB = it; val b=(it*255).toLong(); if(isEditingStartColor) viewModel.gradientStartColor=(viewModel.gradientStartColor and 0xFFFFFF00L) or b else viewModel.gradientEndColor=(viewModel.gradientEndColor and 0xFFFFFF00L) or b; hexLocalText=String.format("%06X", if(isEditingStartColor) viewModel.gradientStartColor and 0xFFFFFFL else viewModel.gradientEndColor and 0xFFFFFFL) }, colors = SliderDefaults.colors(thumbColor = Color.Blue))
                    OutlinedTextField(value = hexLocalText, onValueChange = { text -> hexLocalText = text; runCatching { if(isEditingStartColor) viewModel.gradientStartColor=("FF" + text).toLong(16) else viewModel.gradientEndColor=("FF" + text).toLong(16) } }, label = { Text("HEX色彩代码") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { showGradientColorDialog = false }, modifier = Modifier.align(Alignment.End).padding(top = 12.dp)) { Text("完成") }
                }
            }
        }
    }
    if (showAppListDialog) {
        Dialog(onDismissRequest = { showAppListDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.8f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("选择应用", fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(viewModel.installedApps) { app ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { viewModel.selectedBaseIconSource = app.icon; viewModel.isUserCustomIcon = false; showAppListDialog = false }.padding(12.dp)) {
                                Image(painter = rememberAsyncImagePainter(app.icon), contentDescription = null, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(app.label)
                            }
                        }
                    }
                }
            }
        }
    }
    if (showCropDialog && viewModel.rawBackgroundUri != null) {
        var cropScale by remember { mutableFloatStateOf(1f) }
        var cropOffset by remember { mutableStateOf(Offset.Zero) }
        var containerSize by remember { mutableStateOf(IntSize.Zero) }
        val srcBmp = remember(viewModel.rawBackgroundUri) { runCatching { val inputStream = context.contentResolver.openInputStream(viewModel.rawBackgroundUri!!); BitmapFactory.decodeStream(inputStream) }.getOrNull() }
        Dialog(onDismissRequest = { showCropDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxSize().background(Color.Black), color = Color.Black) {
                Box(modifier = Modifier.fillMaxSize().onGloballyPositioned { containerSize = it.size }) {
                    if (srcBmp != null) { Image(bitmap = srcBmp.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTransformGestures { _, pan, zoom, _ -> cropScale = (cropScale * zoom).coerceIn(0.5f, 5f); cropOffset += pan } }.graphicsLayer(scaleX = cropScale, scaleY = cropScale, translationX = cropOffset.x, translationY = cropOffset.y), contentScale = ContentScale.Fit) }
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sideLength = size.width.coerceAtMost(size.height) * 0.8f
                        drawRect(color = Color.Black.copy(alpha = 0.7f), size = size)
                        drawRect(color = Color.Transparent, topLeft = Offset((size.width - sideLength) / 2, (size.height - sideLength) / 2), size = Size(sideLength, sideLength), blendMode = BlendMode.DstOut)
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Box(modifier = Modifier.fillMaxWidth(0.8f).aspectRatio(1f).border(2.dp, Color.White, RoundedCornerShape(2.dp))) }
                    Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { showCropDialog = false }) { Text("取消", color = Color.White) }
                        Button(onClick = {
                            if (srcBmp != null && containerSize.width > 0) {
                                val sW = containerSize.width.toFloat(); val sH = containerSize.height.toFloat(); val side = sW.coerceAtMost(sH) * 0.8f
                                val bW = srcBmp.width.toFloat(); val bH = srcBmp.height.toFloat(); val initScale = (sW / bW).coerceAtMost(sH / bH); val currentTotalScale = initScale * cropScale
                                val srcLeft = (((sW - side) / 2f - (sW / 2f) - cropOffset.x) / currentTotalScale) + (bW / 2f)
                                val srcTop = (((sH - side) / 2f - (sH / 2f) - cropOffset.y) / currentTotalScale) + (bH / 2f)
                                val srcSide = side / currentTotalScale
                                runCatching {
                                    val finalCropSize = srcSide.toInt().coerceAtMost(srcBmp.width).coerceAtMost(srcBmp.height)
                                    viewModel.croppedBackgroundBitmap = Bitmap.createBitmap(srcBmp, srcLeft.toInt().coerceIn(0, srcBmp.width - finalCropSize), srcTop.toInt().coerceIn(0, srcBmp.height - finalCropSize), finalCropSize, finalCropSize)
                                    viewModel.backgroundType = 2; viewModel.bgScale = 1.0f; viewModel.bgOffsetX = 0f; viewModel.bgOffsetY = 0f
                                }
                            }
                            showCropDialog = false
                        }) { Text("确定导入背景", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HeroGeneratorScreen(iconViewModel: IconGenViewModel, heroViewModel: HeroGenViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var showColorPicker by remember { mutableStateOf(false) }
    var pickerTargetType by remember { mutableIntStateOf(0) }
    var hexLocalText by remember { mutableStateOf("") }
    var previewComponentSizePx by remember { mutableFloatStateOf(0f) }

    var currentHeroTabMode by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("自定义边框", "预设边框", "无边框")

    var showBaseCropDialog by remember { mutableStateOf(false) }
    var tempBasePhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showAppDecoratorDialog by remember { mutableStateOf(false) }

    val sharedPhotoLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { tempBasePhotoUri = it; showBaseCropDialog = true }
    }
    val fontFileLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { heroViewModel.loadCustomFontFile(context, it) }
    }
    val decoratorPhotoLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { heroViewModel.loadDecoratorImage(context, it) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isLandscape = maxWidth > maxHeight && maxWidth > 600.dp

        val previewContent = @Composable {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Card(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.625f).onGloballyPositioned { previewComponentSizePx = it.size.width.toFloat() },
                    shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Canvas(modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                heroViewModel.imageScale = (heroViewModel.imageScale * zoom).coerceIn(0.5f, 5f)
                                heroViewModel.imageOffsetX += pan.x
                                heroViewModel.imageOffsetY += pan.y
                            }
                        }
                    ) {
                        val canvasSize = size
                        val logicalWidth = 2600f
                        val logicalHeight = 1600f
                        val scaleX = canvasSize.width / logicalWidth
                        val scaleY = canvasSize.height / logicalHeight

                        drawIntoCanvas { canvas ->
                            val nativeCanvas = canvas.nativeCanvas

                            if (heroViewModel.maskPaint == null) {
                                heroViewModel.initMode2Assets(context)
                            }

                            if (currentHeroTabMode == 0) {
                                nativeCanvas.save()
                                nativeCanvas.scale(scaleX, scaleY)
                                val vitaPath = heroViewModel.generatePerfectVitaPath(logicalWidth, logicalHeight, heroViewModel.squircleTension)
                                val shadowPaint = Paint().apply { color = heroViewModel.heroShadowColor.toInt(); isAntiAlias = true; style = Paint.Style.FILL; maskFilter = BlurMaskFilter(heroViewModel.squircleTension * 0.4f, BlurMaskFilter.Blur.NORMAL) }
                                nativeCanvas.drawPath(vitaPath, shadowPaint)

                                nativeCanvas.save()
                                nativeCanvas.clipPath(vitaPath)
                                nativeCanvas.drawColor(heroViewModel.heroBackgroundColor.toInt())

                                heroViewModel.loadedBaseBitmap?.let { baseBmp ->
                                    val imgPaint = Paint().apply { isAntiAlias = true }
                                    nativeCanvas.save()
                                    nativeCanvas.translate(heroViewModel.imageOffsetX / scaleX, heroViewModel.imageOffsetY / scaleY)
                                    val bmpW = baseBmp.width.toFloat(); val bmpH = baseBmp.height.toFloat()
                                    val initScale = (logicalWidth / bmpW).coerceAtLeast(logicalHeight / bmpH); val finalScale = initScale * heroViewModel.imageScale
                                    nativeCanvas.scale(finalScale, finalScale)
                                    nativeCanvas.drawBitmap(baseBmp, (logicalWidth / finalScale - bmpW) / 2f, (logicalHeight / finalScale - bmpH) / 2f, imgPaint)
                                    nativeCanvas.restore()
                                }
                                nativeCanvas.restore()

                                val borderPaint = Paint().apply { color = heroViewModel.heroBorderColor.toInt(); style = Paint.Style.STROKE; strokeWidth = heroViewModel.borderWidth * 2f; isAntiAlias = true; maskFilter = BlurMaskFilter(heroViewModel.borderWidth + 2f, BlurMaskFilter.Blur.NORMAL) }
                                nativeCanvas.drawPath(vitaPath, borderPaint)
                                nativeCanvas.restore()

                            } else if (currentHeroTabMode == 1) {
                                val overlayId = context.resources.getIdentifier("overlay", "raw", context.packageName)
                                if (overlayId != 0) {
                                    val overlayStream = context.resources.openRawResource(overlayId)
                                    val rawOverlay = BitmapFactory.decodeStream(overlayStream); overlayStream.close()
                                    if (rawOverlay != null) {
                                        val scaledOverlay = Bitmap.createScaledBitmap(rawOverlay, canvasSize.width.toInt(), canvasSize.height.toInt(), true)
                                        val imgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                                        val layerId = nativeCanvas.saveLayer(0f, 0f, canvasSize.width, canvasSize.height, null)
                                        nativeCanvas.drawBitmap(scaledOverlay, 0f, 0f, imgPaint)

                                        val previewMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN) }
                                        nativeCanvas.saveLayer(0f, 0f, canvasSize.width, canvasSize.height, previewMaskPaint)
                                        nativeCanvas.drawColor(heroViewModel.heroBackgroundColor.toInt())

                                        heroViewModel.loadedBaseBitmap?.let { baseBmp ->
                                            nativeCanvas.save()
                                            nativeCanvas.translate(heroViewModel.imageOffsetX, heroViewModel.imageOffsetY)
                                            val bmpW = baseBmp.width.toFloat(); val bmpH = baseBmp.height.toFloat()
                                            val initScale = (canvasSize.width / bmpW).coerceAtLeast(canvasSize.height / bmpH); val finalScale = initScale * heroViewModel.imageScale
                                            nativeCanvas.scale(finalScale, finalScale)
                                            nativeCanvas.drawBitmap(baseBmp, (canvasSize.width / finalScale - bmpW) / 2f, (canvasSize.height / finalScale - bmpH) / 2f, imgPaint)
                                            nativeCanvas.restore()
                                        }
                                        nativeCanvas.restore(); nativeCanvas.restoreToCount(layerId)
                                    }
                                }
                                if (heroViewModel.frameBitmap != null) {
                                    val scaledFrame = Bitmap.createScaledBitmap(heroViewModel.frameBitmap!!, canvasSize.width.toInt(), canvasSize.height.toInt(), true)
                                    nativeCanvas.drawBitmap(scaledFrame, 0f, 0f, null)
                                }
                            } else {
                                nativeCanvas.drawColor(heroViewModel.heroBackgroundColor.toInt())
                                heroViewModel.loadedBaseBitmap?.let { baseBmp ->
                                    val imgPaint = Paint().apply { isAntiAlias = true }
                                    nativeCanvas.save()
                                    nativeCanvas.translate(heroViewModel.imageOffsetX, heroViewModel.imageOffsetY)
                                    val bmpW = baseBmp.width.toFloat(); val bmpH = baseBmp.height.toFloat()
                                    val initScale = (canvasSize.width / bmpW).coerceAtLeast(canvasSize.height / bmpH); val finalScale = initScale * heroViewModel.imageScale
                                    nativeCanvas.scale(finalScale, finalScale)
                                    nativeCanvas.drawBitmap(baseBmp, (canvasSize.width / finalScale - bmpW) / 2f, (canvasSize.height / finalScale - bmpH) / 2f, imgPaint)
                                    nativeCanvas.restore()
                                }
                            }

                            val renderTextBlock = {
                                nativeCanvas.save()
                                nativeCanvas.translate(canvasSize.width / 2f + heroViewModel.textTranslateX * scaleX, canvasSize.height / 2f + heroViewModel.textTranslateY * scaleY)
                                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                                    textSize = heroViewModel.heroTextSize * scaleY
                                    textAlign = Paint.Align.CENTER
                                    var flags = android.graphics.Typeface.NORMAL
                                    if (heroViewModel.textBold && heroViewModel.textItalic) flags = android.graphics.Typeface.BOLD_ITALIC
                                    else if (heroViewModel.textBold) flags = android.graphics.Typeface.BOLD
                                    else if (heroViewModel.textItalic) flags = android.graphics.Typeface.ITALIC
                                    typeface = if (heroViewModel.customTypeface != null) android.graphics.Typeface.create(heroViewModel.customTypeface, flags) else android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, flags)
                                }
                                if (heroViewModel.textHasShadow) { textPaint.setShadowLayer(heroViewModel.textShadowRadius * scaleY, heroViewModel.textShadowDx * scaleX, heroViewModel.textShadowDy * scaleY, heroViewModel.textShadowColor.toInt()) }
                                if (heroViewModel.textGlowType == 1) { textPaint.maskFilter = BlurMaskFilter(heroViewModel.textGlowRadius * scaleY, BlurMaskFilter.Blur.OUTER); textPaint.color = heroViewModel.textGlowColor.toInt(); heroViewModel.drawMultiLineText(nativeCanvas, heroViewModel.heroText, textPaint); textPaint.maskFilter = null }
                                if (heroViewModel.textGlowType == 2) { textPaint.color = heroViewModel.textGlowColor.toInt(); heroViewModel.drawMultiLineText(nativeCanvas, heroViewModel.heroText, textPaint) }
                                if (heroViewModel.textHasStroke) { textPaint.style = Paint.Style.STROKE; textPaint.strokeWidth = heroViewModel.textStrokeWidth * scaleY; textPaint.color = heroViewModel.textStrokeColor.toInt(); heroViewModel.drawMultiLineText(nativeCanvas, heroViewModel.heroText, textPaint); textPaint.style = Paint.Style.FILL }
                                textPaint.clearShadowLayer(); textPaint.color = heroViewModel.heroTextColor.toInt()
                                heroViewModel.drawMultiLineText(nativeCanvas, heroViewModel.heroText, textPaint)
                                nativeCanvas.restore()
                            }

                            val renderDecoratorBlock = {
                                heroViewModel.decoratorBitmap?.let { logo ->
                                    nativeCanvas.save()
                                    nativeCanvas.translate(canvasSize.width / 2f + heroViewModel.decoratorX * scaleX, canvasSize.height / 2f + heroViewModel.decoratorY * scaleY)
                                    nativeCanvas.rotate(heroViewModel.decoratorRotation)
                                    val lW = logo.width * heroViewModel.decoratorScale * scaleX
                                    val lH = logo.height * heroViewModel.decoratorScale * scaleY
                                    nativeCanvas.drawBitmap(logo, null, AndroidRectF(-lW / 2f, -lH / 2f, lW / 2f, lH / 2f), null)
                                    nativeCanvas.restore()
                                }
                            }

                            if (heroViewModel.isDecoratorAboveText) { renderTextBlock(); renderDecoratorBlock() } else { renderDecoratorBlock(); renderTextBlock() }
                        }
                    }
                }
            }
        }

        val controlsContent = @Composable {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = currentHeroTabMode) { tabTitles.forEachIndexed { index, text -> Tab(text = { Text(text, fontSize = 12.sp) }, selected = currentHeroTabMode == index, onClick = { currentHeroTabMode = index }) } }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        Text("第一步：设置缺省背景", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Button(onClick = { pickerTargetType = 0; hexLocalText = String.format("%08X", heroViewModel.heroBackgroundColor); showColorPicker = true }, modifier = Modifier.fillMaxWidth().height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(heroViewModel.heroBackgroundColor.toInt()))) { Text("调配衬底画布底色", fontSize = 12.sp, color = if(Color(heroViewModel.heroBackgroundColor.toInt()).luminance() > 0.5f) Color.Black else Color.White) }
                    }
                    item {
                        HorizontalDivider()
                        Text("第二步：导入主体图片", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Button(onClick = { sharedPhotoLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) { Text("选择Hero图主体图片", fontSize = 12.sp) }
                    }

                    if (currentHeroTabMode == 0) {
                        item {
                            HorizontalDivider()
                            Text("第三步：边框形态调节", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text("外围曲率 (${heroViewModel.squircleTension.toInt()} %)", fontSize = 11.sp)
                            Slider(value = heroViewModel.squircleTension, onValueChange = { heroViewModel.squircleTension = it }, valueRange = 30f..120f)
                            Text("边缘厚度 (${heroViewModel.borderWidth.toInt()} dp)", fontSize = 11.sp)
                            Slider(value = heroViewModel.borderWidth, onValueChange = { heroViewModel.borderWidth = it }, valueRange = 4f..45f)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { pickerTargetType = 1; hexLocalText = String.format("%08X", heroViewModel.heroBorderColor); showColorPicker = true }) { Text("发光色", fontSize = 11.sp) }
                                Button(onClick = { pickerTargetType = 2; hexLocalText = String.format("%08X", heroViewModel.heroShadowColor); showColorPicker = true }) { Text("外投影色", fontSize = 11.sp) }
                            }
                        }
                    }

                    item {
                        HorizontalDivider()
                        Text("第四步：文字详细设置", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(
                            value = heroViewModel.heroText,
                            onValueChange = { heroViewModel.heroText = it },
                            label = { Text("输入文字内容") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default).copy(platformImeOptions = PlatformImeOptions("android:imeOptions=flagNoExtractUi")),
                            trailingIcon = {
                                if (heroViewModel.heroText.isNotEmpty()) {
                                    IconButton(onClick = { focusManager.clearFocus() }) {
                                        Icon(imageVector = Icons.Default.Done, contentDescription = "收起键盘")
                                    }
                                }
                            }
                        )

                        FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { fontFileLauncher.launch("*/*") }) { Text("自定义字体文件", fontSize = 11.sp) }
                            OutlinedButton(onClick = { heroViewModel.customTypeface = null; heroViewModel.customFontName = "系统默认字体"; Toast.makeText(context, "已恢复默认字体", Toast.LENGTH_SHORT).show() }) { Text("恢复默认字体", fontSize = 11.sp) }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Text("字号 (${heroViewModel.heroTextSize.toInt()})", fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Slider(value = heroViewModel.heroTextSize, onValueChange = { heroViewModel.heroTextSize = it }, valueRange = 20f..540f, modifier = Modifier.weight(2f))
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = heroViewModel.textBold, onCheckedChange = { heroViewModel.textBold = it }); Text("粗体", fontSize = 11.sp) }
                            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = heroViewModel.textItalic, onCheckedChange = { heroViewModel.textItalic = it }); Text("斜体", fontSize = 11.sp) }
                            Button(onClick = { pickerTargetType = 3; hexLocalText = String.format("%08X", heroViewModel.heroTextColor); showColorPicker = true }) { Text("字体颜色", fontSize = 11.sp) }
                        }
                    }

                    item {
                        HorizontalDivider()
                        Text("第五步：调节Deco图饰", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                            Button(onClick = { iconViewModel.loadInstalledApps(context); showAppDecoratorDialog = true }) { Text("App图标提取", fontSize = 11.sp) }
                            Button(onClick = { decoratorPhotoLauncher.launch("image/*") }) { Text("用户自定义", fontSize = 11.sp) }
                            OutlinedButton(onClick = { heroViewModel.decoratorBitmap = null; Toast.makeText(context, "已清空图饰", Toast.LENGTH_SHORT).show() }) { Text("不使用", fontSize = 11.sp) }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { heroViewModel.saveCombinedHeroToGallery(context, previewComponentSizePx, currentHeroTabMode) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB2828))) { Text("导出Hero图", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp) }
                    }
                }
            }
        }

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1.1f).fillMaxHeight()) { previewContent() }
                Card(modifier = Modifier.weight(0.9f).fillMaxHeight(), shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    controlsContent()
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.8f).fillMaxWidth()) { previewContent() }
                Card(modifier = Modifier.weight(1.2f).fillMaxWidth(), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    controlsContent()
                }
            }
        }
    }

    if (showBaseCropDialog && tempBasePhotoUri != null) {
        var cropScale by remember { mutableFloatStateOf(1f) }
        var cropOffset by remember { mutableStateOf(Offset.Zero) }
        var cropContainerSize by remember { mutableStateOf(IntSize.Zero) }
        val srcBmp = remember(tempBasePhotoUri) { runCatching { val inputStream = context.contentResolver.openInputStream(tempBasePhotoUri!!); BitmapFactory.decodeStream(inputStream) }.getOrNull() }
        Dialog(onDismissRequest = { showBaseCropDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxSize().background(Color.Black), color = Color.Black) {
                Box(modifier = Modifier.fillMaxSize().onGloballyPositioned { cropContainerSize = it.size }) {
                    if (srcBmp != null) { Image(bitmap = srcBmp.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTransformGestures { _, pan, zoom, _ -> cropScale = (cropScale * zoom).coerceIn(0.5f, 5f); cropOffset += pan } }.graphicsLayer(scaleX = cropScale, scaleY = cropScale, translationX = cropOffset.x, translationY = cropOffset.y), contentScale = ContentScale.Fit) }
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val viewW = size.width; val viewH = size.height; val boxW = viewW * 0.85f; val boxH = boxW / 1.625f; val left = (viewW - boxW) / 2f; val top = (viewH - boxH) / 2f
                        drawRect(color = Color.Black.copy(alpha = 0.75f), size = size)
                        drawRect(color = Color.Transparent, topLeft = Offset(left, top), size = Size(boxW, boxH), blendMode = BlendMode.DstOut)
                    }
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), contentAlignment = Alignment.Center) { Box(modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1.625f).border(2.dp, Color.White, RoundedCornerShape(4.dp))) }
                    Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { showBaseCropDialog = false }) { Text("取消", color = Color.White) }
                        Button(onClick = {
                            if (srcBmp != null && cropContainerSize.width > 0) {
                                val sW = cropContainerSize.width.toFloat(); val sH = cropContainerSize.height.toFloat(); val boxW = sW * 0.85f; val boxH = boxW / 1.625f
                                val bW = srcBmp.width.toFloat(); val bH = srcBmp.height.toFloat(); val initScale = (sW / bW).coerceAtMost(sH / bH); val currentTotalScale = initScale * cropScale
                                val srcLeft = (((sW - boxW) / 2f - (sW / 2f) - cropOffset.x) / currentTotalScale) + (bW / 2f)
                                val srcTop = (((sH - boxH) / 2f - (sH / 2f) - cropOffset.y) / currentTotalScale) + (bH / 2f)
                                val srcWidth = boxW / currentTotalScale; val srcHeight = boxH / currentTotalScale
                                runCatching {
                                    val cropW = srcWidth.toInt().coerceAtMost(srcBmp.width); val cropH = srcHeight.toInt().coerceAtMost(srcBmp.height)
                                    heroViewModel.loadBaseImage(context, Bitmap.createBitmap(srcBmp, srcLeft.toInt().coerceIn(0, srcBmp.width - cropW), srcTop.toInt().coerceIn(0, srcBmp.height - cropH), cropW, cropH))
                                }
                            }
                            showBaseCropDialog = false
                        }) { Text("确认导入图片", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }

    if (showColorPicker) {
        Dialog(onDismissRequest = { showColorPicker = false }) {
            Card(modifier = Modifier.fillMaxWidth().wrapContentHeight(), shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val titleText = when(pickerTargetType) {
                        0 -> "设置背景色"
                        1 -> "设置边框外围发光色"
                        2 -> "设置边框立体投影色"
                        3 -> "调整字体正面色"
                        4 -> "设置字体边缘描边色"
                        5 -> "调配文字外发光颜色"
                        else -> "设置文字投影色"
                    }
                    Text(titleText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    val curColor = when(pickerTargetType) {
                        0 -> Color(heroViewModel.heroBackgroundColor.toInt())
                        1 -> Color(heroViewModel.heroBorderColor.toInt())
                        2 -> Color(heroViewModel.heroShadowColor.toInt())
                        3 -> Color(heroViewModel.heroTextColor.toInt())
                        4 -> Color(heroViewModel.textStrokeColor.toInt())
                        5 -> Color(heroViewModel.textGlowColor.toInt())
                        else -> Color(heroViewModel.textShadowColor.toInt())
                    }
                    Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(curColor).border(1.dp, Color.Gray, RoundedCornerShape(12.dp)))

                    Slider(value = heroViewModel.pickerR, onValueChange = { heroViewModel.pickerR = it; when(pickerTargetType){ 0->heroViewModel.updateContainerBgColorFromRGB() 1->heroViewModel.updateBorderColorFromRGB() 2->heroViewModel.updateShadowColorFromRGB() 3->{ val r=(it*255).toLong(); heroViewModel.heroTextColor=(heroViewModel.heroTextColor and 0xFF00FFFFL) or (r shl 16) } 4->{ val r=(it*255).toLong(); heroViewModel.textStrokeColor=(heroViewModel.textStrokeColor and 0xFF00FFFFL) or (r shl 16) } 5->{ val r=(it*255).toLong(); heroViewModel.textGlowColor=(heroViewModel.textGlowColor and 0xFF00FFFFL) or (r shl 16) } 6->{ val r=(it*255).toLong(); heroViewModel.textShadowColor=(heroViewModel.textShadowColor and 0xFF00FFFFL) or (r shl 16) } }; hexLocalText = String.format("%08X", when(pickerTargetType){ 0->heroViewModel.heroBackgroundColor 1->heroViewModel.heroBorderColor 2->heroViewModel.heroShadowColor 3->heroViewModel.heroTextColor 4->heroViewModel.textStrokeColor 5->heroViewModel.textGlowColor else->heroViewModel.textShadowColor }) }, colors = SliderDefaults.colors(thumbColor = Color.Red))
                    Slider(value = heroViewModel.pickerG, onValueChange = { heroViewModel.pickerG = it; when(pickerTargetType){ 0->heroViewModel.updateContainerBgColorFromRGB() 1->heroViewModel.updateBorderColorFromRGB() 2->heroViewModel.updateShadowColorFromRGB() 3->{ val g=(it*255).toLong(); heroViewModel.heroTextColor=(heroViewModel.heroTextColor and 0xFFFF00FFL) or (g shl 8) } 4->{ val g=(it*255).toLong(); heroViewModel.textStrokeColor=(heroViewModel.textStrokeColor and 0xFFFF00FFL) or (g shl 8) } 5->{ val g=(it*255).toLong(); heroViewModel.textGlowColor=(heroViewModel.textGlowColor and 0xFFFF00FFL) or (g shl 8) } 6->{ val g=(it*255).toLong(); heroViewModel.textShadowColor=(heroViewModel.textShadowColor and 0xFFFF00FFL) or (g shl 8) } }; hexLocalText = String.format("%08X", when(pickerTargetType){ 0->heroViewModel.heroBackgroundColor 1->heroViewModel.heroBorderColor 2->heroViewModel.heroShadowColor 3->heroViewModel.heroTextColor 4->heroViewModel.textStrokeColor 5->heroViewModel.textGlowColor else->heroViewModel.textShadowColor }) }, colors = SliderDefaults.colors(thumbColor = Color.Green))
                    Slider(value = heroViewModel.pickerB, onValueChange = { heroViewModel.pickerB = it; when(pickerTargetType){ 0->heroViewModel.updateContainerBgColorFromRGB() 1->heroViewModel.updateBorderColorFromRGB() 2->heroViewModel.updateShadowColorFromRGB() 3->{ val b=(it*255).toLong(); heroViewModel.heroTextColor=(heroViewModel.heroTextColor and 0xFFFFFF00L) or b } 4->{ val b=(it*255).toLong(); heroViewModel.textStrokeColor=(heroViewModel.textStrokeColor and 0xFFFFFF00L) or b } 5->{ val b=(it*255).toLong(); heroViewModel.textGlowColor=(heroViewModel.textGlowColor and 0xFFFFFF00L) or b } 6->{ val b=(it*255).toLong(); heroViewModel.textShadowColor=(heroViewModel.textShadowColor and 0xFFFFFF00L) or b } }; hexLocalText = String.format("%08X", when(pickerTargetType){ 0->heroViewModel.heroBackgroundColor 1->heroViewModel.heroBorderColor 2->heroViewModel.heroShadowColor 3->heroViewModel.heroTextColor 4->heroViewModel.textStrokeColor 5->heroViewModel.textGlowColor else->heroViewModel.textShadowColor }) }, colors = SliderDefaults.colors(thumbColor = Color.Blue))
                    Text("透明度 Alpha", fontSize = 10.sp)
                    Slider(value = heroViewModel.pickerA, onValueChange = { heroViewModel.pickerA = it; when(pickerTargetType){ 0->heroViewModel.updateContainerBgColorFromRGB() 1->heroViewModel.updateBorderColorFromRGB() 2->heroViewModel.updateShadowColorFromRGB() 3->{ val a=(it*255).toLong(); heroViewModel.heroTextColor=(heroViewModel.heroTextColor and 0x00FFFFFFL) or (a shl 24) } 4->{ val a=(it*255).toLong(); heroViewModel.textStrokeColor=(heroViewModel.textStrokeColor and 0x00FFFFFFL) or (a shl 24) } 5->{ val a=(it*255).toLong(); heroViewModel.textGlowColor=(heroViewModel.textGlowColor and 0x00FFFFFFL) or (a shl 24) } 6->{ val a=(it*255).toLong(); heroViewModel.textShadowColor=(heroViewModel.textShadowColor and 0x00FFFFFFL) or (a shl 24) } }; hexLocalText = String.format("%08X", when(pickerTargetType){ 0->heroViewModel.heroBackgroundColor 1->heroViewModel.heroBorderColor 2->heroViewModel.heroShadowColor 3->heroViewModel.heroTextColor 4->heroViewModel.textStrokeColor 5->heroViewModel.textGlowColor else->heroViewModel.textShadowColor }) })

                    OutlinedTextField(
                        value = hexLocalText,
                        onValueChange = { text ->
                            hexLocalText = text.take(8)
                            val cleanHex = text.replace("#", "").trim()
                            if (cleanHex.length == 6 || cleanHex.length == 8) {
                                runCatching {
                                    val parsedLong = if (cleanHex.length == 6) ("FF" + cleanHex).toLong(16) else cleanHex.toLong(16)
                                    when(pickerTargetType) {
                                        0 -> heroViewModel.heroBackgroundColor = parsedLong
                                        1 -> heroViewModel.heroBorderColor = parsedLong
                                        2 -> heroViewModel.heroShadowColor = parsedLong
                                        3 -> heroViewModel.heroTextColor = parsedLong
                                        4 -> heroViewModel.textStrokeColor = parsedLong
                                        5 -> heroViewModel.textGlowColor = parsedLong
                                        6 -> heroViewModel.textShadowColor = parsedLong
                                    }
                                }
                            }
                        },
                        label = { Text("HEX 代码输入 (#AARRGGBB)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showColorPicker = false }, modifier = Modifier.align(Alignment.End)) { Text("完成") }
                }
            }
        }
    }


    if (showAppDecoratorDialog) {
        Dialog(onDismissRequest = { showAppDecoratorDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.8f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("选择已有应用图标作为Deco图饰", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(iconViewModel.installedApps) { app ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { app.icon?.let { heroViewModel.loadDecoratorImage(context, it) }; showAppDecoratorDialog = false }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = rememberAsyncImagePainter(app.icon), contentDescription = null, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(app.label, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
