package com.example.pdf

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PdfRendererHelper(private val file: File) : AutoCloseable {

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private val bitmapCache = object : LruCache<Int, Bitmap>(16) {}

    init {
        try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            fileDescriptor?.let {
                pdfRenderer = PdfRenderer(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val pageCount: Int
        get() = pdfRenderer?.pageCount ?: 0

    suspend fun renderPageBitmap(
        pageIndex: Int,
        targetWidth: Int = 1080,
        densityMultiplier: Float = 2.0f
    ): Bitmap? = withContext(Dispatchers.Default) {
        val renderer = pdfRenderer ?: return@withContext null
        if (pageIndex < 0 || pageIndex >= renderer.pageCount) return@withContext null

        synchronized(bitmapCache) {
            bitmapCache.get(pageIndex)?.let { return@withContext it }
        }

        try {
            synchronized(renderer) {
                renderer.openPage(pageIndex).use { page ->
                    val width = (page.width * densityMultiplier).toInt().coerceAtLeast(300)
                    val height = (page.height * densityMultiplier).toInt().coerceAtLeast(400)
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    // White background default for clear contrast
                    bitmap.eraseColor(Color.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    synchronized(bitmapCache) {
                        bitmapCache.put(pageIndex, bitmap)
                    }
                    return@withContext bitmap
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    override fun close() {
        try {
            bitmapCache.evictAll()
            pdfRenderer?.close()
            fileDescriptor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
