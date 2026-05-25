package com.kian.feihgenerator

import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable?
)

class IconGenViewModel : ViewModel() {

    var installedApps by mutableStateOf<List<AppInfo>>(emptyList())
        private set

    var isLoadingApps by mutableStateOf(false)
        private set

    var selectedBaseIconSource by mutableStateOf<Any?>(null)
    var isUserCustomIcon by mutableStateOf(false)
    var iconScale by mutableStateOf(0.7f)

    // 🛠 手势微调底座：现已重设，精准绑定并用来控制第二步用户自选的背景图片
    var bgScale by mutableStateOf(1.0f)
    var bgOffsetX by mutableStateOf(0f)
    var bgOffsetY by mutableStateOf(0f)

    // --- 背景相关状态 ---
    var backgroundType by mutableStateOf(0)
    var backgroundColor by mutableStateOf(0xFFC2D1C2L)
    var rawBackgroundUri by mutableStateOf<Uri?>(null)
    var croppedBackgroundBitmap by mutableStateOf<Bitmap?>(null)

    var colorPickerRed by mutableStateOf(194f / 255f)
    var colorPickerGreen by mutableStateOf(209f / 255f)
    var colorPickerBlue by mutableStateOf(194f / 255f)

    fun updateColorFromRGB() {
        val r = (colorPickerRed * 255).toLong()
        val g = (colorPickerGreen * 255).toLong()
        val b = (colorPickerBlue * 255).toLong()
        backgroundColor = 0xFF000000L or (r shl 16) or (g shl 8) or b
    }

    // --- 边框相关状态 ---
    var borderType by mutableStateOf(0)
    var borderColor by mutableStateOf(0xFF102A10L)
    var borderWidth by mutableStateOf(14f)

    var gradientStartColor by mutableStateOf(0xFFFF0000L)
    var gradientEndColor by mutableStateOf(0xFF0000FFL)

    var userBorderBitmap by mutableStateOf<Bitmap?>(null)

    var borderPickerRed by mutableStateOf(16f / 255f)
    var borderPickerGreen by mutableStateOf(42f / 255f)
    var borderPickerBlue by mutableStateOf(16f / 255f)

    fun updateBorderColorFromRGB() {
        val r = (borderPickerRed * 255).toLong()
        val g = (borderPickerGreen * 255).toLong()
        val b = (borderPickerBlue * 255).toLong()
        borderColor = 0xFF000000L or (r shl 16) or (g shl 8) or b
    }

    fun checkAndSetUserBorder(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    null
                }
            }

            if (bitmap != null && bitmap.width == 1024 && bitmap.height == 1024) {
                userBorderBitmap = bitmap
                borderType = 3
                onResult(true)
            } else {
                borderType = 0
                onResult(false)
            }
        }
    }

    fun loadInstalledApps(context: Context) {
        if (installedApps.isNotEmpty()) return
        viewModelScope.launch {
            isLoadingApps = true
            val apps = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                allApps.filter { app ->
                    (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || pm.getLaunchIntentForPackage(app.packageName) != null
                }.map { app ->
                    AppInfo(
                        label = app.loadLabel(pm).toString(),
                        packageName = app.packageName,
                        icon = app.loadIcon(pm)
                    )
                }.sortedBy { it.label }
            }
            installedApps = apps
            isLoadingApps = false
        }
    }

    // 1024x1024 离屏合成入库：同步换算你在背景上进行的手势平移移动与捏合缩放系数
    fun saveCombinedIconToGallery(context: Context, viewSizePx: Float, borderWidthPx: Float, cornerRadiusPx: Float) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    val targetSize = 1024
                    val resultBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(resultBitmap)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    val rectF = RectF(0f, 0f, targetSize.toFloat(), targetSize.toFloat())

                    val scaleFactor = targetSize.toFloat() / viewSizePx
                    val strokeW = borderWidthPx * scaleFactor
                    val cornerRadius = cornerRadiusPx * scaleFactor

                    val layerCode = canvas.saveLayer(0f, 0f, targetSize.toFloat(), targetSize.toFloat(), null)

                    // 先平铺基础背景色
                    paint.color = backgroundColor.toInt()
                    canvas.drawRect(rectF, paint)

                    // 🛠 核心微调映射：自选背景大图在此处同步承载你的手势偏移量（bgOffsetX/Y 和 bgScale）
                    if (backgroundType == 2 && croppedBackgroundBitmap != null) {
                        canvas.save()
                        // 映射你在屏幕预览区通过单指移动创造的偏置量
                        canvas.translate(bgOffsetX * scaleFactor, bgOffsetY * scaleFactor)

                        val bgW = croppedBackgroundBitmap!!.width.toFloat()
                        val bgH = croppedBackgroundBitmap!!.height.toFloat()

                        // 🛠 核心修正：移除错误的命名参数 maximumValue，改用标准的纯参数对比，彻底杀掉 159 行报错
                        val initBgScale = targetSize.toFloat() / bgW.coerceAtMost(bgH)

                        // 叠加你在屏幕预览区通过双指捏合创造的缩放系数
                        val finalBgScale = initBgScale * bgScale
                        canvas.scale(finalBgScale, finalBgScale) // 显式匹配两轴等比

                        val drawBgX = (targetSize / finalBgScale - bgW) / 2f
                        val drawBgY = (targetSize / finalBgScale - bgH) / 2f
                        canvas.drawBitmap(croppedBackgroundBitmap!!, drawBgX, drawBgY, paint)
                        canvas.restore()
                    }

                    // 基础主体图标回到原位：100% 居中，不再受用户手势拖动干扰
                    selectedBaseIconSource?.let { source ->
                        var srcBitmap: Bitmap? = null
                        if (source is Drawable) {
                            if (source is BitmapDrawable) {
                                srcBitmap = source.bitmap
                            } else {
                                val bmp = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
                                val dCanvas = Canvas(bmp)
                                source.setBounds(0, 0, dCanvas.width, dCanvas.height)
                                source.draw(dCanvas)
                                srcBitmap = bmp
                            }
                        } else if (source is Uri) {
                            val inputStream: InputStream? = context.contentResolver.openInputStream(source)
                            srcBitmap = BitmapFactory.decodeStream(inputStream)
                        }

                        srcBitmap?.let { baseBmp ->
                            val iconTargetSize = (targetSize * iconScale).toInt()
                            val scaledIcon = Bitmap.createScaledBitmap(baseBmp, iconTargetSize, iconTargetSize, true)
                            val left = (targetSize - iconTargetSize) / 2f
                            val top = (targetSize - iconTargetSize) / 2f
                            canvas.drawBitmap(scaledIcon, left, top, paint)
                        }
                    }

                    if (borderType == 3 && userBorderBitmap != null) {
                        paint.style = Paint.Style.FILL
                        paint.shader = null
                        canvas.drawBitmap(userBorderBitmap!!, 0f, 0f, paint)
                    } else {
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = strokeW
                        val borderRect = RectF(strokeW / 2f, strokeW / 2f, targetSize - strokeW / 2f, targetSize - strokeW / 2f)

                        when (borderType) {
                            1 -> {
                                paint.shader = LinearGradient(0f, 0f, targetSize.toFloat(), targetSize.toFloat(),
                                    intArrayOf(0xFF00FFFF.toInt(), 0xFFFF00FF.toInt(), 0xFFFFFF00.toInt()),
                                    null, Shader.TileMode.CLAMP)
                            }
                            2 -> {
                                paint.shader = LinearGradient(0f, 0f, targetSize.toFloat(), targetSize.toFloat(),
                                    gradientStartColor.toInt(), gradientEndColor.toInt(), Shader.TileMode.CLAMP)
                            }
                            else -> {
                                paint.shader = null
                                paint.color = borderColor.toInt()
                            }
                        }
                        canvas.drawRoundRect(borderRect, cornerRadius - strokeW / 2f, cornerRadius - strokeW / 2f, paint)
                    }

                    val maskBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
                    val maskCanvas = Canvas(maskBitmap)
                    val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFFFFF.toInt() }
                    maskCanvas.drawRoundRect(rectF, cornerRadius, cornerRadius, maskPaint)

                    paint.style = Paint.Style.FILL
                    paint.shader = null
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                    canvas.drawBitmap(maskBitmap, 0f, 0f, paint)
                    paint.xfermode = null

                    canvas.restoreToCount(layerCode)

                    val filename = "FEIH_Icon_1024_${System.currentTimeMillis()}.png"
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FEIHGenerator")
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                    val resolver = context.contentResolver
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    imageUri?.let { uri ->
                        val os: OutputStream? = resolver.openOutputStream(uri)
                        os?.use { stream ->
                            resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                            resolver.update(uri, contentValues, null, null)
                        }
                        true
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (success) {
                Toast.makeText(context, "1024x1024 超清圆角图标已写入相册！", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "保存失败，请检查剩余空间", Toast.LENGTH_SHORT).show()
            }
        }
    }
}