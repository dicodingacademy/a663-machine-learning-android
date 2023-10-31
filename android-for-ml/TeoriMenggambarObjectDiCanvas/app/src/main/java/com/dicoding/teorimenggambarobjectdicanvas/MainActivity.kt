package com.dicoding.teorimenggambarobjectdicanvas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.dicoding.teorimenggambarobjectdicanvas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        binding.myImageView.setImageBitmap(mBitmap)

        val mCanvas = Canvas(mBitmap)
        mCanvas.drawColor(ResourcesCompat.getColor(resources, R.color.blue_500, null))

        val mRect = Rect()
        mRect.set(mBitmap.width/2 - 100, mBitmap.height/2 - 100, mBitmap.width/2 + 100, mBitmap.height/2 + 100)

        mCanvas.save() // menyimpan pengaturan sebelumnya.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mCanvas.clipRect(mRect)
        } else {
            mCanvas.clipOutRect(mRect)
        }

        val mPaint = Paint()
        mPaint.color = ResourcesCompat.getColor(resources, R.color.pink_200, null)
        mCanvas.drawCircle((mBitmap.width/2).toFloat(), (mBitmap.height/2).toFloat(), 200f, mPaint)

        mCanvas.restore() // mengembalikan pengaturan yang telah tersimpan.

        val mPaintText =  Paint(Paint.FAKE_BOLD_TEXT_FLAG)
        mPaintText.textSize = 20F
        mPaintText.color = ResourcesCompat.getColor(resources, R.color.white, null)

        val text = "Selamat Datang!"
        val mBounds = Rect()
        mPaintText.getTextBounds(text, 0, text.length, mBounds)

        val x: Int = mBitmap.width/2 - mBounds.centerX()
        val y: Int = mBitmap.height/2 - mBounds.centerY()
        mCanvas.drawText(text, x.toFloat(), y.toFloat(), mPaintText)

    }
}

// Contoh menggunakan CustomView
class CanvasView(context: Context): View(context)
