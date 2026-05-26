package com.kian.feihgenerator.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.Typeface as AndroidTypeface
import android.graphics.Path as AndroidPath
import android.graphics.RectF as AndroidRectF
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch
import java.io.InputStream

enum class AppScreen {
    WELCOME,
    ICON_GENERATOR,
    HERO_GENERATOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }

    val iconViewModel: IconGenViewModel = viewModel()
    val heroViewModel: HeroGenViewModel = viewModel()

    Scaffold(
        topBar = {
            if (currentScreen != AppScreen.WELCOME) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (currentScreen == AppScreen.ICON_GENERATOR) "前端图标生成工具" else "Hero图生成工具",
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
                    onNavigateToIcon = { currentScreen = AppScreen.ICON_GENERATOR },
                    onNavigateToHero = { currentScreen = AppScreen.HERO_GENERATOR }
                )
                AppScreen.ICON_GENERATOR -> IconGeneratorScreen(viewModel = iconViewModel)
                AppScreen.HERO_GENERATOR -> HeroGeneratorScreen(iconViewModel = iconViewModel, heroViewModel = heroViewModel)
            }
        }
    }
}

@Composable
fun WelcomeLandingScreen(onNavigateToIcon: () -> Unit, onNavigateToHero: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617))))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color(0xFF3B82F6).copy(alpha = 0.06f), radius = 400.dp.toPx(), center = Offset(0f, 0f))
            drawCircle(color = Color(0xFFFF9800).copy(alpha = 0.04f), radius = 350.dp.toPx(), center = Offset(size.width, size.height * 0.4f))
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
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 20.dp)) {
                Box(modifier = Modifier.size(80.dp).background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFFEF4444))), shape = RoundedCornerShape(22.dp)).padding(2.dp)) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A), shape = RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "FEIH Generator", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Front-end Icons & Hero Art Generator", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(color = Color(0xFF3B82F6).copy(alpha = 0.15f), shape = RoundedCornerShape(100.dp)) {
                    Text(text = "专为 iiSU 与 Cocoon 前端定制", fontSize = 11.sp, color = Color(0xFF60A5FA), modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), fontWeight = FontWeight.SemiBold)
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "请选择要使用的功能：", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                WelcomeEngineCard(title = "Icon Generator", description = "选取应用图标、调配纯色/自选背景，高度定制无损 1024x1024 圆角方形图标。", icon = Icons.Filled.AccountBox, accentColor = Color(0xFF2196F3), onClick = onNavigateToIcon)
                WelcomeEngineCard(title = "Hero Generator", description = "依照模板通过简单的操作生成美观的Hero图，支持泛光多行艺术字与 360° 旋转装饰贴图。", icon = Icons.Filled.Star, accentColor = Color(0xFFFF9800), onClick = onNavigateToHero)
            }
            Text(text = "Made by Kian", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.padding(bottom = 12.dp))
        }
    }
}

@Composable
fun WelcomeEngineCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color(0xFF1E293B).copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFF334155)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).background(accentColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 11.sp, color = Color(0xFF94A3B8), lineHeight = 15.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
        }
    }
}

// ======= 100% 满血复活版：Icon 模块界面 =======
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
    var previewComponentSizePx by remember { mutableStateOf(0f) }

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

    val previewBorderWidth = with(density) { (viewModel.borderWidth * (previewComponentSizePx / 1024f)).toDp() }
    val previewCornerRadius = with(density) { (70f * (previewComponentSizePx / 1024f)).toDp() }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxHeight().weight(0.9f).background(Color.White).padding(24.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxHeight(0.85f).aspectRatio(1f).onGloballyPositioned { previewComponentSizePx = it.size.width.toFloat() },
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

        Card(modifier = Modifier.fillMaxHeight().weight(1.1f), shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                item {
                    Text("第一步：选取基础主体图标", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Button(onClick = { viewModel.loadInstalledApps(context); showAppListDialog = true }) { Text("应用选择器") }
                        FilterChip(selected = viewModel.isUserCustomIcon, onClick = { baseIconLauncher.launch("image/*") }, label = { Text("用户自定义") })
                    }
                }
                item {
                    Text("前排图标规格微调 (${(viewModel.iconScale * 100).toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                    Slider(value = viewModel.iconScale, onValueChange = { viewModel.iconScale = it }, valueRange = 0.4f..1.0f)
                }
                item {
                    HorizontalDivider()
                    Text("第二步：底图背景设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        FilterChip(selected = viewModel.backgroundType == 0, onClick = { viewModel.backgroundType = 0 }, label = { Text("纯色背景") })
                        FilterChip(selected = viewModel.backgroundType == 2, onClick = { bgImageLauncher.launch("image/*") }, label = { Text("用户自选背景") })
                    }
                    if (viewModel.backgroundType == 0) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Button(onClick = { hexLocalText = String.format("%06X", viewModel.backgroundColor and 0xFFFFFFL); showColorPickerDialog = true }) { Text("配置自定义纯色") }
                        }
                    }
                }
                item {
                    HorizontalDivider()
                    Text("第三步：图标边框形态设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        FilterChip(selected = viewModel.borderType == 0, onClick = { viewModel.borderType = 0 }, label = { Text("自选纯色框") })
                        FilterChip(selected = viewModel.borderType == 1, onClick = { viewModel.borderType = 1 }, label = { Text("霓虹渐变边") })
                        FilterChip(selected = viewModel.borderType == 2, onClick = { viewModel.borderType = 2 }, label = { Text("自选双色渐变") })
                        FilterChip(selected = viewModel.borderType == 3, onClick = { showBorderNoticeDialog = true }, label = { Text("用户自选PNG") })
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (viewModel.borderType == 0) {
                            Button(onClick = { hexLocalText = String.format("%06X", viewModel.borderColor and 0xFFFFFFL); showBorderColorDialog = true }) { Text("🎨 配置核心边框颜色") }
                        }
                        if (viewModel.borderType == 2) {
                            Button(onClick = { isEditingStartColor = true; hexLocalText = String.format("%06X", viewModel.gradientStartColor and 0xFFFFFFL); showGradientColorDialog = true }) { Text("🏁 选渐变起点色") }
                            Button(onClick = { isEditingStartColor = false; hexLocalText = String.format("%06X", viewModel.gradientEndColor and 0xFFFFFFL); showGradientColorDialog = true }) { Text("🛑 选渐变终点色") }
                        }
                    }
                }
                item {
                    if (viewModel.borderType != 3) {
                        Text("边框发光粗细微调 (${viewModel.borderWidth.toInt()} px)")
                        Slider(value = viewModel.borderWidth, onValueChange = { viewModel.borderWidth = it }, valueRange = 4f..150f)
                    }
                }
                item {
                    Button(onClick = { viewModel.saveCombinedIconToGallery(context, previewComponentSizePx) }, modifier = Modifier.fillMaxWidth(), enabled = viewModel.selectedBaseIconSource != null, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                        Text("导出 1024x1024 物理 Icon", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (showBorderNoticeDialog) {
        AlertDialog(onDismissRequest = { showBorderNoticeDialog = false }, title = { Text("自选 PNG 边框提示") }, text = { Text("请选择大小为1024×1024像素的边框图片。") }, confirmButton = { Button(onClick = { showBorderNoticeDialog = false; borderImageLauncher.launch("image/*") }) { Text("去选择图片") } })
    }
    if (showColorPickerDialog) {
        Dialog(onDismissRequest = { showColorPickerDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("自定义背景纯色", fontWeight = FontWeight.Bold)
                    Slider(value = viewModel.colorPickerRed, onValueChange = { viewModel.colorPickerRed = it; viewModel.updateColorFromRGB() })
                    Slider(value = viewModel.colorPickerGreen, onValueChange = { viewModel.colorPickerGreen = it; viewModel.updateColorFromRGB() })
                    Slider(value = viewModel.colorPickerBlue, onValueChange = { viewModel.colorPickerBlue = it; viewModel.updateColorFromRGB() })
                    OutlinedTextField(value = hexLocalText, onValueChange = { text -> hexLocalText = text; runCatching { viewModel.backgroundColor = ("FF" + text).toLong(16) } }, label = { Text("HEX代码") }, modifier = Modifier.fillMaxWidth())
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
                    Text(if (isEditingStartColor) "设置双色渐变 - 起点色彩" else "设置双色渐变 - 终点色彩", fontWeight = FontWeight.Bold)
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
        var cropScale by remember { mutableStateOf(1f) }
        var cropOffset by remember { mutableStateOf<androidx.compose.ui.geometry.Offset>(androidx.compose.ui.geometry.Offset.Zero) }
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
                        }) { Text("确定引入裁剪背景", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}

// ======= 满血恢复版：Hero 模块界面 =======
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroGeneratorScreen(iconViewModel: IconGenViewModel, heroViewModel: HeroGenViewModel) {
    val context = LocalContext.current

    var showColorPicker by remember { mutableStateOf(false) }
    var pickerTargetType by remember { mutableStateOf(0) }
    var hexLocalText by remember { mutableStateOf("") }
    var previewComponentSizePx by remember { mutableStateOf(0f) }

    var currentHeroTabMode by remember { mutableStateOf(0) }
    val tabTitles = listOf("自定义模式", "预设边框模式", "无边框模式")

    var showBaseCropDialog by remember { mutableStateOf(false) }
    var tempBasePhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showAppDecoratorDialog by remember { mutableStateOf(false) }
    var showSystemFontDialog by remember { mutableStateOf(false) }

    val sharedPhotoLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { tempBasePhotoUri = it; showBaseCropDialog = true }
    }
    val fontFileLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { heroViewModel.loadCustomFontFile(context, it) }
    }
    val decoratorPhotoLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { heroViewModel.loadDecoratorImage(context, it) }
    }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxHeight().weight(1.1f).background(Color.White).padding(16.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.6f).onGloballyPositioned { previewComponentSizePx = it.size.width.toFloat() },
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
                            // 模式二核心：正向内切遮罩与顶层完美包边叠加
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
                            // 🛠️ 终极挂接：在模式二渲染树最底层，原汁原味地覆盖粉刷上 frame.png 资产包边！
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

        Card(modifier = Modifier.fillMaxHeight().weight(0.9f), shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = currentHeroTabMode) { tabTitles.forEachIndexed { index, text -> Tab(text = { Text(text, fontSize = 12.sp) }, selected = currentHeroTabMode == index, onClick = { currentHeroTabMode = index }) } }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        Text("第一步：设置背景色", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Button(onClick = { pickerTargetType = 0; hexLocalText = String.format("%08X", heroViewModel.heroBackgroundColor); showColorPicker = true }, modifier = Modifier.fillMaxWidth().height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(heroViewModel.heroBackgroundColor.toInt()))) { Text("调配衬底画布底色", fontSize = 12.sp, color = if(Color(heroViewModel.heroBackgroundColor.toInt()).luminance() > 0.5f) Color.Black else Color.White) }
                    }
                    item {
                        HorizontalDivider()
                        Text("第二步：主体图片导入", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Button(onClick = { sharedPhotoLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) { Text("📁 选择本地海报主体底图", fontSize = 12.sp) }
                    }

                    if (currentHeroTabMode == 0) {
                        item {
                            HorizontalDivider()
                            Text("第三步：边框调节", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text("外围曲率系数 (${heroViewModel.squircleTension.toInt()} %)", fontSize = 11.sp)
                            Slider(value = heroViewModel.squircleTension, onValueChange = { heroViewModel.squircleTension = it }, valueRange = 30f..120f)
                            Text("霓虹边缘厚度微调 (${heroViewModel.borderWidth.toInt()} dp)", fontSize = 11.sp)
                            Slider(value = heroViewModel.borderWidth, onValueChange = { heroViewModel.borderWidth = it }, valueRange = 4f..45f)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { pickerTargetType = 1; hexLocalText = String.format("%08X", heroViewModel.heroBorderColor); showColorPicker = true }, modifier = Modifier.weight(1f)) { Text("发光色", fontSize = 11.sp) }
                                Button(onClick = { pickerTargetType = 2; hexLocalText = String.format("%08X", heroViewModel.heroShadowColor); showColorPicker = true }, modifier = Modifier.weight(1f)) { Text("外投影色", fontSize = 11.sp) }
                            }
                        }
                    } else if (currentHeroTabMode == 1) {
                        item {
                            HorizontalDivider()
                            Text("第三步：使用预设边框", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("预设边框载入正常。", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider()
                        Text("第四步：文字配置", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(value = heroViewModel.heroText, onValueChange = { heroViewModel.heroText = it }, label = { Text("输入标题内容") }, modifier = Modifier.fillMaxWidth())

// 🛠️ 风格统一级重构：砍掉系统内置字体按钮，统一采用高颜值 OutlinedButton 和 Button
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // 导入自选外部字体的统一按钮
                            Button(
                                onClick = { fontFileLauncher.launch("*/*") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🔤 导入自选 ttf/otf", fontSize = 11.sp)
                            }

                            // 恢复默认字体的样式：由原先不统一的 Card 升级对齐为标准的 OutlinedButton 风格
                            OutlinedButton(
                                onClick = {
                                    heroViewModel.customTypeface = null
                                    heroViewModel.customFontName = "系统默认字体"
                                    Toast.makeText(context, "已成功恢复默认字体", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("恢复默认字体 ↩", fontSize = 11.sp)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Text("字号 (${heroViewModel.heroTextSize.toInt()})", fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Slider(value = heroViewModel.heroTextSize, onValueChange = { heroViewModel.heroTextSize = it }, valueRange = 20f..540f, modifier = Modifier.weight(2f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = heroViewModel.textBold, onCheckedChange = { heroViewModel.textBold = it }); Text("粗体", fontSize = 11.sp) }
                            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = heroViewModel.textItalic, onCheckedChange = { heroViewModel.textItalic = it }); Text("斜体", fontSize = 11.sp) }
                            Button(onClick = { pickerTargetType = 3; hexLocalText = String.format("%08X", heroViewModel.heroTextColor); showColorPicker = true }) { Text("字体主色", fontSize = 11.sp) }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Checkbox(checked = heroViewModel.textHasStroke, onCheckedChange = { heroViewModel.textHasStroke = it })
                            Text("启用外描边", fontSize = 11.sp)
                            if (heroViewModel.textHasStroke) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { pickerTargetType = 4; hexLocalText = String.format("%08X", heroViewModel.textStrokeColor); showColorPicker = true }) { Text("色", fontSize = 10.sp) }
                                Slider(value = heroViewModel.textStrokeWidth, onValueChange = { heroViewModel.textStrokeWidth = it }, valueRange = 2f..20f, modifier = Modifier.weight(1f))
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Text("发光特效:", fontSize = 11.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(selected = heroViewModel.textGlowType == 0, onClick = { heroViewModel.textGlowType = 0 }, label = { Text("无", fontSize = 10.sp) })
                                FilterChip(selected = heroViewModel.textGlowType == 1, onClick = { heroViewModel.textGlowType = 1 }, label = { Text("外发光", fontSize = 10.sp) })
                                FilterChip(selected = heroViewModel.textGlowType == 2, onClick = { heroViewModel.textGlowType = 2 }, label = { Text("叠加态", fontSize = 10.sp) })
                            }
                            if (heroViewModel.textGlowType != 0) {
                                IconButton(onClick = { pickerTargetType = 5; hexLocalText = String.format("%08X", heroViewModel.textGlowColor); showColorPicker = true }) { Box(modifier = Modifier.size(16.dp).background(Color(heroViewModel.textGlowColor.toInt()))) }
                                Slider(value = heroViewModel.textGlowRadius, onValueChange = { heroViewModel.textGlowRadius = it }, valueRange = 4f..40f, modifier = Modifier.weight(1f))
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Checkbox(checked = heroViewModel.textHasShadow, onCheckedChange = { heroViewModel.textHasShadow = it })
                            Text("启用专属阴影层", fontSize = 11.sp)
                            if (heroViewModel.textHasShadow) {
                                IconButton(onClick = { pickerTargetType = 6; hexLocalText = String.format("%08X", heroViewModel.textShadowColor); showColorPicker = true }) { Box(modifier = Modifier.size(16.dp).background(Color(heroViewModel.textShadowColor.toInt()))) }
                                Text("浓度:", fontSize = 9.sp)
                                Slider(value = heroViewModel.textShadowRadius, onValueChange = { heroViewModel.textShadowRadius = it }, valueRange = 2f..30f, modifier = Modifier.weight(1f))
                            }
                        }
                        if (heroViewModel.textHasShadow) {
                            Row { Text("方向X偏置", fontSize = 10.sp, modifier = Modifier.weight(1f)); Slider(value = heroViewModel.textShadowDx, onValueChange = { heroViewModel.textShadowDx = it }, valueRange = -30f..30f, modifier = Modifier.weight(3f)) }
                            Row { Text("方向Y偏置", fontSize = 10.sp, modifier = Modifier.weight(1f)); Slider(value = heroViewModel.textShadowDy, onValueChange = { heroViewModel.textShadowDy = it }, valueRange = -30f..30f, modifier = Modifier.weight(3f)) }
                        }
                        Text("标题横向 X轴 全景位移 (${heroViewModel.textTranslateX.toInt()})", fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                        Slider(value = heroViewModel.textTranslateX, onValueChange = { heroViewModel.textTranslateX = it }, valueRange = -1300f..1300f)
                        Text("标题纵向 Y轴 全景位移 (${heroViewModel.textTranslateY.toInt()})", fontSize = 11.sp)
                        Slider(value = heroViewModel.textTranslateY, onValueChange = { heroViewModel.textTranslateY = it }, valueRange = -800f..800f)
                    }

                    item {
                        HorizontalDivider()
                        Text("第五步：装饰图调节", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        // 🛠️ 局部行级扩增：在第四步首排并平铺新增“不使用”独立控制键，一键净化画面
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                            Button(
                                onClick = { iconViewModel.loadInstalledApps(context); showAppDecoratorDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("App徽章提取", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { decoratorPhotoLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("📁 外部图片", fontSize = 11.sp)
                            }

                            // ✅ 新增的“不使用”按钮，使用标准的 OutlinedButton 风格与其他面板保持完美对齐
                            OutlinedButton(
                                onClick = {
                                    heroViewModel.decoratorBitmap = null
                                    Toast.makeText(context, "已清空并移除当前装饰挂件", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(0.9f)
                            ) {
                                Text("❌ 不使用", fontSize = 11.sp)
                            }
                        }
                        if (heroViewModel.decoratorBitmap != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) { Text("挂件尺寸 (${(heroViewModel.decoratorScale*100).toInt()}%)", fontSize = 11.sp, modifier = Modifier.weight(1f)); Slider(value = heroViewModel.decoratorScale, onValueChange = { heroViewModel.decoratorScale = it }, valueRange = 0.2f..2.5f, modifier = Modifier.weight(2f)) }
                            Text("挂件横向 X轴 全景位置 (${heroViewModel.decoratorX.toInt()})", fontSize = 11.sp)
                            Slider(value = heroViewModel.decoratorX, onValueChange = { heroViewModel.decoratorX = it }, valueRange = -1300f..1300f)
                            Text("挂件纵向 Y轴 全景位置 (${heroViewModel.decoratorY.toInt()})", fontSize = 11.sp)
                            Slider(value = heroViewModel.decoratorY, onValueChange = { heroViewModel.decoratorY = it }, valueRange = -800f..800f)
                            Text("旋转角度微调 (${heroViewModel.decoratorRotation.toInt()}°)", fontSize = 11.sp)
                            Slider(value = heroViewModel.decoratorRotation, onValueChange = { heroViewModel.decoratorRotation = it }, valueRange = -180f..180f)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                                Text("图层覆盖顺序:", fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                                Row(modifier = Modifier.weight(2.8f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(selected = heroViewModel.isDecoratorAboveText, onClick = { heroViewModel.isDecoratorAboveText = true }, label = { Text("置于文字上方", fontSize = 10.sp) })
                                    FilterChip(selected = !heroViewModel.isDecoratorAboveText, onClick = { heroViewModel.isDecoratorAboveText = false }, label = { Text("置于文字下方", fontSize = 10.sp) })
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { heroViewModel.saveCombinedHeroToGallery(context, previewComponentSizePx, currentHeroTabMode) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB2828))) { Text("导出 2600x1600 完全体高清海报贴图", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp) }
                    }
                }
            }
        }
    }

    if (showBaseCropDialog && tempBasePhotoUri != null) {
        var cropScale by remember { mutableStateOf(1f) }
        var cropOffset by remember { mutableStateOf<androidx.compose.ui.geometry.Offset>(androidx.compose.ui.geometry.Offset.Zero) }
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
                        }) { Text("确认引入大图", fontWeight = FontWeight.Bold) }
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
                        5 -> "调配文字外发光幻彩色"
                        else -> "设置文字专属投影色"
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
                    Text("选择已有应用图标作为海报装饰物", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
