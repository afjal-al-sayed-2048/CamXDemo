package com.dotescapesoftwarelab.camxdemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DrawOverlay(
    context: Context,
    attrs: AttributeSet? = null,
    //private val rect: Rect = Rect(35, 500, 550, 1050),
    private val rect: Rect = Rect(0, 0, 0, 0),
    private val text: String = ""
) : View(context, attrs) {

    //private val rect: Rect = Rect(35, 500, 550, 1050)
    //private val text: String = ""


    private val rectBackgroundColor = Color.parseColor("#40FFFFFF")
    private val rectStrokeColor = Color.WHITE
    private val rectStrokeWidth = 1F * resources.displayMetrics.density
    private val rectCornerRoundness = 8F * resources.displayMetrics.density
    private val textFontSize = 16F * resources.displayMetrics.density
    private var demoRect = RectF(rect.left.toFloat(), (rect.top.toFloat() - textFontSize), rect.right.toFloat(), rect.bottom.toFloat())
    private val textColor = Color.BLACK
    private val textBackgroundColor = Color.WHITE
    private val textX = demoRect.left + (10F * resources.displayMetrics.density)
    private val textY = demoRect.top + (20F * resources.displayMetrics.density)
    private val textBackgroundRectRound = RectF(demoRect.left, demoRect.top, demoRect.right, demoRect.top + (textFontSize * 2F))
    private val textBackgroundRect = RectF(demoRect.left, demoRect.top + textFontSize, demoRect.right, demoRect.top + (textFontSize * 2F))

    private val rectPaint = Paint().apply {
        color = rectBackgroundColor
        style = Paint.Style.FILL_AND_STROKE
    }

    private val rectStrokePaint = Paint().apply {
        color = rectStrokeColor
        style = Paint.Style.STROKE
        strokeWidth = rectStrokeWidth
    }

    private val textPaint = Paint().apply {
        color = textColor
        style = Paint.Style.FILL
        isAntiAlias = true
        textSize = textFontSize
    }

    private val textBackgroundPaint = Paint().apply {
        color = textBackgroundColor
        style = Paint.Style.FILL

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if(text.length < 1){
                demoRect.top += (textFontSize * 2F)
            }
            //border - fill & stroke
            it.drawRoundRect(demoRect,rectCornerRoundness, rectCornerRoundness, rectPaint)
            it.drawRoundRect(demoRect,rectCornerRoundness, rectCornerRoundness, rectStrokePaint)
            if(text.length > 0){
                //text background - fill & stroke
                it.drawRoundRect(textBackgroundRectRound,rectCornerRoundness, rectCornerRoundness, textBackgroundPaint)
                it.drawRect(textBackgroundRect, textBackgroundPaint)
                //text
                it.drawText(text, textX, textY, textPaint)
            }
        }
    }

}