package com.natie.wheely.view._custom

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class WheelyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val CANVAS_PAD = 20

    private val back = Paint()
    private val strokes = Paint()
    private val arcSize = RectF()
    private val options = Paint()

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    private var degrees = mutableListOf<Pair<Float, Float>>()
    private var degreeHalved = 0f
    private var radiusHalved = 0f

    private val textBounds = Rect()

    var counter = 0f

    var values = mutableListOf("Test1", "Test2", "Test3")
        set(value) {
            field = value
            updateValues()
        }

    init {
        //set up paints
        //Circle Wheel
        back.style = Paint.Style.FILL
        back.color = Color.WHITE
        back.isAntiAlias = true

        strokes.color = Color.BLACK
        strokes.isAntiAlias = true
        strokes.style = Paint.Style.STROKE
        strokes.strokeWidth = 10f

        //Option texts
        options.color = Color.BLACK
        options.isAntiAlias = true
        options.textSize = 50f
        options.isFakeBoldText = true
        options.isSubpixelText = true

        counter = 0f
    }

    private fun updateValues() {
        val total = values.size
        var previousDegree = 0f
        degrees.clear()
        for (i in 0 until total) {
            val nextDegree = previousDegree + (360f / total.toFloat())
            degrees.add(Pair(previousDegree, nextDegree))
            previousDegree = nextDegree
        }
        degreeHalved = degrees.first().second / 2f
        invalidate()
    }

    fun startSpin() {
        val randomStart = (1..degreeHalved.toInt()).random() * 360f
        var randomEnd = (0..degrees.size).random() * degrees.first().second

        if (degrees.size % 2 == 0 && randomEnd.toInt() % 45 == 0) {
            randomEnd += degreeHalved.toInt()
        }
        val anim = ObjectAnimator.ofFloat(
            this,
            "rotation", randomStart, randomEnd
        )

        anim.duration = (2000..8000).random().toLong()
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w < h) {
            radius = w / 2 - CANVAS_PAD.toFloat()
            centerX = radius + CANVAS_PAD
            centerY = (h / 2).toFloat()
        } else {
            radius = h / 2 - CANVAS_PAD.toFloat()
            centerX = (w / 2).toFloat()
            centerY = radius + CANVAS_PAD
        }
        radius -= CANVAS_PAD
        radiusHalved = radius / 2f
        arcSize[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)

        c?.drawCircle(centerX, centerY, radius, back)

        degrees.forEachIndexed { i, d ->
            c?.drawArc(arcSize, d.first, d.second, true, strokes)
            c?.save()
            val deg = d.first - degreeHalved
            c?.rotate(deg, centerX, centerY)
            val text = values[i]
            options.getTextBounds(text, 0, text.length, textBounds)
            c?.drawText(
                text,
                centerX + radius - textBounds.width() - CANVAS_PAD,
                centerY + (textBounds.height() / 2f),
                options
            )
            c?.restore()
        }
    }
}