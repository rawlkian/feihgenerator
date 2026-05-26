package com.kian.feihgenerator

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.Path as AndroidPath
import android.graphics.RectF as AndroidRectF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class HeroGenViewModel : ViewModel() {

    // ======= 第 0 步 ~ 基本色彩与画布状态 (严格锁死 Long 类型防溢出) =======
    var heroBackgroundColor by mutableStateOf(0xFF2A2A2AL)
    var heroBorderColor by mutableStateOf(0xFFE5F7FFL)
    var heroShadowColor by mutableStateOf(0x99000000L)

    var baseImageSource by mutableStateOf<Any?>(null)
    var loadedBaseBitmap by mutableStateOf<Bitmap?>(null)

    var imageScale by mutableStateOf(1f)
    var imageOffsetX by mutableStateOf(0f)
    var imageOffsetY by mutableStateOf(0f)

    var squircleTension by mutableStateOf(85f)
    var borderWidth by mutableStateOf(16f)

    var maskPaint: Paint? = null
    var frameBitmap: Bitmap? = null

    var pickerR by mutableStateOf(229f / 255f)
    var pickerG by mutableStateOf(247f / 255f)
    var pickerB by mutableStateOf(1f)
    var pickerA by mutableStateOf(1f)

    // ======= 文字标题高级图层状态量 =======
    var heroText by mutableStateOf("GAME TITLE")
    var heroTextSize by mutableStateOf(75f)
    var heroTextColor by mutableStateOf(0xFFFFFFFFL)
    var textBold by mutableStateOf(true)
    var textItalic by mutableStateOf(false)

    var textHasStroke by mutableStateOf(false)
    var textStrokeColor by mutableStateOf(0xFF000000L)
    var textStrokeWidth by mutableStateOf(6f)
    var textGlowType by mutableStateOf(0)
    var textGlowColor by mutableStateOf(0xFF00FFFFL)
    var textGlowRadius by mutableStateOf(12f)

    var textHasShadow by mutableStateOf(false)
    var textShadowColor by mutableStateOf(0xCC000000L)
    var textShadowDx by mutableStateOf(4f)
    var textShadowDy by mutableStateOf(4f)
    var textShadowRadius by mutableStateOf(8f)

    var textTranslateX by mutableStateOf(0f)
    var textTranslateY by mutableStateOf(350f)

    var customTypeface: Typeface? by mutableStateOf(null)
    var customFontName by mutableStateOf("系统默认字体")

    // ======= 挂件/装饰物挂载状态量 =======
    var decoratorBitmap: Bitmap? by mutableStateOf(null)
    var decoratorScale by mutableStateOf(1.0f)
    var decoratorX by mutableStateOf(0f)
    var decoratorY by mutableStateOf(-200f)
    var decoratorRotation by mutableStateOf(0f)
    var isDecoratorAboveText by mutableStateOf(true)

    // ======= 方案二物理资产预解算加载 =======
    fun initMode2Assets(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val overlayId = context.resources.getIdentifier("overlay", "raw", context.packageName)
                if (overlayId != 0) {
                    maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                }
                val frameId = context.resources.getIdentifier("frame", "raw", context.packageName)
                if (frameId != 0) {
                    val frameStream = context.resources.openRawResource(frameId)
                    frameBitmap = BitmapFactory.decodeStream(frameStream)
                    frameStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadBaseImage(context: Context, source: Any) {
        viewModelScope.launch {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context).data(source).size(Size.ORIGINAL).allowHardware(false).build()
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    val drawable = result.drawable
                    loadedBaseBitmap = if (drawable is BitmapDrawable) drawable.bitmap else {
                        val bmp = Bitmap.createBitmap(1200, 1200, Bitmap.Config.ARGB_8888)
                        val c = Canvas(bmp)
                        drawable.setBounds(0, 0, c.width, c.height)
                        drawable.draw(c)
                        bmp
                    }
                    baseImageSource = loadedBaseBitmap
                    imageScale = 1f; imageOffsetX = 0f; imageOffsetY = 0f
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun loadDecoratorImage(context: Context, source: Any) {
        viewModelScope.launch {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context).data(source).size(Size.ORIGINAL).allowHardware(false).build()
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    val drawable = result.drawable
                    decoratorBitmap = null
                    decoratorBitmap = if (drawable is BitmapDrawable) drawable.bitmap else {
                        val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
                        val c = Canvas(bmp)
                        drawable.setBounds(0, 0, c.width, c.height)
                        drawable.draw(c)
                        bmp
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun loadCustomFontFile(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("feih_font", ".ttf", context.cacheDir)
                tempFile.deleteOnExit()
                inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
                val tf = Typeface.createFromFile(tempFile)
                if (tf != null) {
                    customTypeface = tf
                    customFontName = uri.lastPathSegment ?: "自定义字体"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "字体资产解析失败，请检查规范", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateContainerBgColorFromRGB() {
        val r = (pickerR * 255).toLong()
        val g = (pickerG * 255).toLong()
        val b = (pickerB * 255).toLong()
        val a = (pickerA * 255).toLong()
        heroBackgroundColor = (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    fun updateBorderColorFromRGB() {
        val r = (pickerR * 255).toLong()
        val g = (pickerG * 255).toLong()
        val b = (pickerB * 255).toLong()
        val a = (pickerA * 255).toLong()
        heroBorderColor = (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    fun updateShadowColorFromRGB() {
        val r = (pickerR * 255).toLong()
        val g = (pickerG * 255).toLong()
        val b = (pickerB * 255).toLong()
        val a = (pickerA * 255).toLong()
        heroShadowColor = (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    // ======= 🚀 2600x1600 完全体多层离屏高保真像素无损导出流 =======
    fun saveCombinedHeroToGallery(context: Context, previewWidthPx: Float, currentMode: Int) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    val targetW = 2600
                    val targetH = 1600
                    val resultBitmap = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(resultBitmap)
                    val previewToFullScale = targetW.toFloat() / previewWidthPx
                    val imgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

                    // 1. 核心底座容器层绘制
                    if (currentMode == 0) {
                        val pVitaPath = generatePerfectVitaPath(targetW.toFloat(), targetH.toFloat(), squircleTension)
                        val shadowPaint = Paint().apply {
                            color = heroShadowColor.toInt()
                            isAntiAlias = true
                            style = Paint.Style.FILL
                            maskFilter = BlurMaskFilter(squircleTension * 0.6f, BlurMaskFilter.Blur.NORMAL)
                        }
                        canvas.drawPath(pVitaPath, shadowPaint)

                        canvas.save()
                        canvas.clipPath(pVitaPath)
                        canvas.drawColor(heroBackgroundColor.toInt())

                        if (loadedBaseBitmap != null) {
                            canvas.save()
                            canvas.translate(imageOffsetX * previewToFullScale, imageOffsetY * previewToFullScale)
                            val bmpW = loadedBaseBitmap!!.width.toFloat()
                            val bmpH = loadedBaseBitmap!!.height.toFloat()
                            val initScale = (targetW.toFloat() / bmpW).coerceAtLeast(targetH.toFloat() / bmpH)
                            val finalScale = initScale * imageScale
                            canvas.scale(finalScale, finalScale)
                            canvas.drawBitmap(loadedBaseBitmap!!, (targetW.toFloat() / finalScale - bmpW) / 2f, (targetH.toFloat() / finalScale - bmpH) / 2f, imgPaint)
                            canvas.restore()
                        }
                        canvas.restore()

                        val borderPaint = Paint().apply {
                            color = heroBorderColor.toInt()
                            style = Paint.Style.STROKE
                            strokeWidth = borderWidth * (targetH.toFloat() / 1000f) * 2f
                            isAntiAlias = true
                            maskFilter = BlurMaskFilter(borderWidth + 2f, BlurMaskFilter.Blur.NORMAL)
                        }
                        canvas.drawPath(pVitaPath, borderPaint)

                    } else if (currentMode == 1) {
                        // 模式二核心：2600 画幅无损正向内切与包边覆盖
                        val overlayId = context.resources.getIdentifier("overlay", "raw", context.packageName)
                        if (overlayId != 0) {
                            val overlayStream = context.resources.openRawResource(overlayId)
                            val rawOverlay = BitmapFactory.decodeStream(overlayStream)
                            overlayStream.close()
                            if (rawOverlay != null) {
                                val scaledOverlay = Bitmap.createScaledBitmap(rawOverlay, targetW, targetH, true)
                                canvas.saveLayer(0f, 0f, targetW.toFloat(), targetH.toFloat(), null)
                                canvas.drawBitmap(scaledOverlay, 0f, 0f, imgPaint)

                                val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN) }
                                canvas.saveLayer(0f, 0f, targetW.toFloat(), targetH.toFloat(), innerPaint)
                                canvas.drawColor(heroBackgroundColor.toInt())

                                if (loadedBaseBitmap != null) {
                                    canvas.save()
                                    canvas.translate(imageOffsetX * previewToFullScale, imageOffsetY * previewToFullScale)
                                    val bmpW = loadedBaseBitmap!!.width.toFloat()
                                    val bmpH = loadedBaseBitmap!!.height.toFloat()
                                    val initScale = (targetW.toFloat() / bmpW).coerceAtLeast(targetH.toFloat() / bmpH)
                                    val finalScale = initScale * imageScale
                                    canvas.scale(finalScale, finalScale)
                                    canvas.drawBitmap(loadedBaseBitmap!!, (targetW.toFloat() / finalScale - bmpW) / 2f, (targetH.toFloat() / finalScale - bmpH) / 2f, imgPaint)
                                    canvas.restore()
                                }
                                canvas.restore(); canvas.restore()
                            }
                        }
                        if (frameBitmap != null) {
                            val scaledFrame = Bitmap.createScaledBitmap(frameBitmap!!, targetW, targetH, true)
                            canvas.drawBitmap(scaledFrame, 0f, 0f, imgPaint)
                        }
                    } else {
                        // 模式三：无边框
                        canvas.drawColor(heroBackgroundColor.toInt())
                        if (loadedBaseBitmap != null) {
                            canvas.save()
                            canvas.translate(imageOffsetX * previewToFullScale, imageOffsetY * previewToFullScale)
                            val bmpW = loadedBaseBitmap!!.width.toFloat()
                            val bmpH = loadedBaseBitmap!!.height.toFloat()
                            val initScale = (targetW.toFloat() / bmpW).coerceAtLeast(targetH.toFloat() / bmpH)
                            val finalScale = initScale * imageScale
                            canvas.scale(finalScale, finalScale)
                            canvas.drawBitmap(loadedBaseBitmap!!, (targetW.toFloat() / finalScale - bmpW) / 2f, (targetH.toFloat() / finalScale - bmpH) / 2f, imgPaint)
                            canvas.restore()
                        }
                    }

                    // 2. 文本层与装饰挂件物理合并阶段
                    val executeDrawText = {
                        canvas.save()
                        // 统一坐标：textTranslateX/Y 已经是基于 2600/1600 设计步长的逻辑像素
                        canvas.translate(targetW / 2f + textTranslateX, targetH / 2f + textTranslateY)

                        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            textSize = heroTextSize // 直接使用逻辑像素大小，对应 1600 高度设计
                            textAlign = Paint.Align.CENTER
                            var flags = Typeface.NORMAL
                            if (textBold && textItalic) flags = Typeface.BOLD_ITALIC
                            else if (textBold) flags = Typeface.BOLD
                            else if (textItalic) flags = Typeface.ITALIC

                            typeface = if (customTypeface != null) {
                                Typeface.create(customTypeface, flags)
                            } else {
                                Typeface.create(Typeface.DEFAULT, flags)
                            }
                        }

                        if (textHasShadow) {
                            textPaint.setShadowLayer(textShadowRadius, textShadowDx, textShadowDy, textShadowColor.toInt())
                        }
                        if (textGlowType == 1) {
                            textPaint.maskFilter = BlurMaskFilter(textGlowRadius, BlurMaskFilter.Blur.OUTER)
                            textPaint.color = textGlowColor.toInt()
                            drawMultiLineText(canvas, heroText, textPaint)
                            textPaint.maskFilter = null
                        }
                        if (textGlowType == 2) {
                            textPaint.color = textGlowColor.toInt()
                            drawMultiLineText(canvas, heroText, textPaint)
                        }
                        if (textHasStroke) {
                            textPaint.style = Paint.Style.STROKE
                            textPaint.strokeWidth = textStrokeWidth
                            textPaint.color = textStrokeColor.toInt()
                            drawMultiLineText(canvas, heroText, textPaint)
                            textPaint.style = Paint.Style.FILL
                        }

                        textPaint.clearShadowLayer()
                        textPaint.color = heroTextColor.toInt()
                        drawMultiLineText(canvas, heroText, textPaint)
                        canvas.restore()
                    }

                    val executeDrawDecorator = {
                        decoratorBitmap?.let { logo ->
                            canvas.save()
                            canvas.translate(targetW / 2f + decoratorX, targetH / 2f + decoratorY)
                            canvas.rotate(decoratorRotation)
                            val lW = logo.width * decoratorScale
                            val lH = logo.height * decoratorScale
                            val logoRect = android.graphics.RectF(-lW / 2f, -lH / 2f, lW / 2f, lH / 2f)
                            canvas.drawBitmap(logo, null, logoRect, imgPaint)
                            canvas.restore()
                        }
                    }

                    if (isDecoratorAboveText) {
                        executeDrawText()
                        executeDrawDecorator()
                    } else {
                        executeDrawDecorator()
                        executeDrawText()
                    }

                    val filename = "FEIH_Hero_Combined_${System.currentTimeMillis()}.png"
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
                        os?.use { stream -> resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) }
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
                Toast.makeText(context, "2600x1600 完全体高清游戏海报已成功写入相册！", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "导出失败，请检查外置储存", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun drawMultiLineText(canvas: Canvas, text: String, paint: Paint) {
        val lines = text.split("\n")
        var yOffset = 0f
        val fontMetrics = paint.fontMetrics
        val lineHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading
        val totalHeight = lineHeight * (lines.size - 1)

        canvas.save()
        canvas.translate(0f, -totalHeight / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f)
        for (line in lines) {
            canvas.drawText(line, 0f, yOffset, paint)
            yOffset += lineHeight
        }
        canvas.restore()
    }

    fun generatePerfectVitaPath(w: Float, h: Float, ratio: Float): AndroidPath {
        val path = AndroidPath()
        val padX = w * (115f / 1600f)
        val padY = h * (85f / 1000f)
        val left = padX
        val right = w - padX
        val top = padY
        val bottom = h - padY

        val corner = h * (110f / 1000f)
        val k = 0.55228475f
        val offset = corner * (1f - k)

        val factor = ratio / 85f
        val tensionX = 18f * factor
        val tensionY = 3.5f * factor

        path.reset()
        path.moveTo(left + corner, top)
        path.cubicTo(left + corner + 300f, top - tensionY, right - corner - 300f, top - tensionY, right - corner, top)
        path.cubicTo(right - offset, top, right, top + offset, right, top + corner)
        path.cubicTo(right + tensionX, top + corner + 150f, right + tensionX, bottom - corner - 150f, right, bottom - corner)
        path.cubicTo(right, bottom - offset, right - offset, bottom, right - corner, bottom)
        path.cubicTo(right - corner - 300f, bottom + tensionY, left + corner + 300f, bottom + tensionY, left + corner, bottom)
        path.cubicTo(left + offset, bottom, left, bottom - offset, left, bottom - corner)
        path.cubicTo(left - tensionX, bottom - corner - 150f, left - tensionX, top + corner + 150f, left, top + corner)
        path.cubicTo(left, top + offset, left + offset, top, left + corner, top)
        path.close()
        return path
    }
}