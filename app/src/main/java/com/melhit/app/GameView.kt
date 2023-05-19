package com.melhit.app

import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import kotlin.math.abs

class GameView(val ctx: Context,val attributeSet: AttributeSet): SurfaceView(ctx,attributeSet) {

    var ball = BitmapFactory.decodeResource(ctx.resources,R.drawable.ball)
    var bg = BitmapFactory.decodeResource(ctx.resources,R.drawable.bg)
    private var paintB: Paint = Paint(Paint.DITHER_FLAG)
    public var isWin = false
    private var paused = false
    private val colors = arrayOf(0,R.color.orange,R.color.lime,R.color.red,R.color.white)
    private var bordP = Paint().apply {
        color = ctx.getColor(R.color.yel)
        style = Paint.Style.FILL_AND_STROKE
    }
    private var bordP1 = Paint().apply {
        color = ctx.getColor(R.color.yel)
        style = Paint.Style.FILL_AND_STROKE
    }
    private var levels = arrayOf(2,4,5,6,16,25,30,32, 35, 40)
    var arr = arrayOf(
        arrayOf(-1,-1,-1, -1,-1,-1, -1,-1,-1, -1,-1,-1),
        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),
        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),

        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),
        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),
        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),

        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),
        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),
        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),

        arrayOf(-1,0,0, 0,0,0, 0,0,0, 0,0,-1),
        arrayOf(-1,0,0, 0,0,0, 0,0,-2, 0,0,-1),
        arrayOf(-1,-1,-1, -1,-1,-1, -1,-1,-1, -1,-1,-1),
    )

    var music = ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getBoolean("music",true)
    var sounds = ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getBoolean("sound",true)
    public var level = ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getInt("tmp",0)
    private var millis = 0
    private var random = Random()
    private var listener: EndListener? = null
    var player = MediaPlayer.create(ctx,R.raw.bg)
    var sound = MediaPlayer.create(ctx,R.raw.sound)

    val updateThread = Thread {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (!paused) {
                    millis ++
                    update.run()
                }
            }
        }, 500, 16)
    }

    init {
        player.setOnCompletionListener {
            it.start()
        }
        if(music) player.start()
        var stX = 5
        var stY = 5
        var count = 0
        var l = 1
        while(count<levels[level]) {
            for(j in 0..l) {
                if(count>=levels[level]) break
                arr[stY][stX+j] = random.nextInt(colors.size-1)+1
                count++
                if(count>=levels[level]) break
                arr[stY+j][stX+l] = random.nextInt(colors.size-1)+1
                count++
                if(count>=levels[level]) break
                arr[stY+l][stX+j] = random.nextInt(colors.size-1)+1
                count++
                if(count>=levels[level]) break
                arr[stY+j][stX] = random.nextInt(colors.size-1)+1
                count++
                if(count>=levels[level]) break
            }
            stX--
            stY--
            l+=2
        }

        bg = Bitmap.createScaledBitmap(bg,bg.width/2,bg.height/2,true)

        holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                val canvas = holder.lockCanvas()
                if(canvas!=null) {
                    ball = Bitmap.createScaledBitmap(ball,canvas.width/16,canvas.width/16,true)
                    draw(canvas)
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
              //  paused = true
              //  player.stop()
                updateThread.interrupt()
                paused = true
                player.stop()
            }

        })

        updateThread.start()

    }

    var downX = 0f
    var downY = 0f
    var min = 50f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if(abs(event.x-downX)> abs(event.y-downY)) {
                    if(abs(event.x-downX)>min) {
                        deltaX = if (event.x > downX) 1f else -1f
                        millis = 0
                        offset = 0f
                        deltaY = 0f
                        ost = 0f
                        stopBall = false
                        stopBlock = false
                        if(sounds) {
                            sound.seekTo(0)
                            sound.start()
                        }
                    }
                } else {
                    if(abs(event.y-downY)>min) {
                        deltaY = if (event.y > downY) 1f else -1f
                        millis = 0
                        ost = 0f
                        deltaX = 0f
                        offset = 0f
                        stopBall = false
                        stopBlock = false
                        if(sounds) {
                            sound.seekTo(0)
                            sound.start()
                        }
                    }
                }
            }
         }
        return true
    }


    var w = 0
    var h = 0
    val update = Runnable{
        var isEnd = false
        try {
            val canvas = holder.lockCanvas()
            w = canvas.width
            h = canvas.height
            if((deltaX!=0f || deltaY!=0f)) {
                offset+=canvas.width/240
                ost +=canvas.width/240
            }
            canvas.drawBitmap(bg,0f,0f,paintB)
            drawArr(canvas)
            if(millis==15) {
                offset = 0f
                millis = 0
                if(deltaX>0) {
                    right()
                } else if(deltaX<0) {
                    left()
                }
                if(deltaY>0) {
                    bottom()
                } else if(deltaY<0) {
                    top()
                }
                isWin = !check()
                if(stopBall && stopBlock) {
                    stopBall = false
                    stopBlock = false;
                    deltaX = 0f
                    deltaY = 0f
                    offset = 0f
                }
            }
            holder.unlockCanvasAndPost(canvas)
            isEnd = lose()
            if(isWin) isEnd = true
            if(isEnd) {
                Log.d("TAG","END")
                togglePause()
                if(listener!=null) listener!!.end()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var deltaX = 0f
    var deltaY = 0f
    var offset = 0f
    var stopBlock = false
    var stopBall = false
    var ost = 0f
    fun drawArr(canvas: Canvas): Boolean {
       if(deltaX!=0f) Log.d("TAG","$deltaX $stopBlock $stopBall")
        var ans = false
        for(i in arr.indices) {
            for(j in arr[i].indices) {
                var y = canvas.height/3f
                var x = canvas.width/8f
                var b = false
                if(deltaX>0 && arr[i][11]!=-1 && arr[i][11]!=0 || deltaX<0 && arr[i][0]!=-1 && arr[i][0]!=0
                    || deltaY>0 && arr[11][i]!=-1 && arr[11][i]!=0 || deltaY<0 && arr[i][0]!=-1) b = true
                //if(!b) ost = 0f
                if(arr[i][j]>0) {
                   if(!stopBlock)  x += offset * deltaX
                    if(!stopBlock) y += offset * deltaY
                }
                if(arr[i][j]==-2) {
                    if(deltaX>0 && arr[i][11]==0 || deltaX<0 && arr[i][0]==0 || !stopBall)  x += offset * deltaX
                    if(deltaY>0 && arr[11][i]==0 || deltaY<0 && arr[0][i]==0 || !stopBall)  y += offset * deltaY
                }
                when(arr[i][j]) {
                    -1 -> {
                        var path = Path()
                        path.moveTo(x+j*canvas.width/16,y+i*canvas.width/16)
                        path.lineTo(x+(j+1)*canvas.width/16,y+i*canvas.width/16)
                        path.lineTo(x+(j+1)*canvas.width/16,y+(i+1)*canvas.width/16)
                        path.lineTo(x+(j)*canvas.width/16,y+(i+1)*canvas.width/16)
                        path.close()
                        canvas.drawPath(path,bordP)
                    }
                    0 -> {

                    }
                    -2 -> {
                        canvas.drawBitmap(ball,x+j*canvas.width/16,y+i*canvas.width/16,paintB)
                    }
                    else -> {
                        var path = Path()
                        path.moveTo(x+j*canvas.width/16,y+i*canvas.width/16)
                        path.lineTo(x+(j+1)*canvas.width/16,y+i*canvas.width/16)
                        path.lineTo(x+(j+1)*canvas.width/16,y+(i+1)*canvas.width/16)
                        path.lineTo(x+(j)*canvas.width/16,y+(i+1)*canvas.width/16)
                        path.close()
                        canvas.drawPath(path,bordP1.apply {
                            color = ctx.getColor(colors[arr[i][j]])
                        })
                    }
                }
            }

        }
        if(stopBall && stopBlock) {
            stopBall = false
            stopBlock = false;
            ans = false
            deltaX = 0f
            deltaY = 0f
            offset = 0f
        }

        return ans
    }
    fun lose(): Boolean{
        var ans = false
        for(i in 0..11) {
            if(deltaX>0 && arr[i][11]!=-1 && arr[i][11]!=0 || deltaX<0 && arr[i][0]!=-1 && arr[i][0]!=0
                || deltaY>0 && arr[11][i]!=-1  && arr[11][i]!=0 || deltaY<0  && arr[0][i]!=-1 && arr[0][i]!=0) ans = true
        }
        return ans
    }
    fun right() {
        var t = false;
        for(i in 11 downTo 1) {
            for(j in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j][i-1]==-2) {
                        stopBall = true
                    } else if(arr[j][i-1]>0) stopBlock = true
                }
                else {
                    if((arr[j][i-1]==-2 && !stopBall) || (arr[j][i-1]>0 && !stopBlock)) {
                        arr[j][i] = arr[j][i - 1]
                        arr[j][i-1] = 0
                    }
                }
            }
        }
        for(i in 11 downTo 1) {
            for(j in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j][i-1]==-2) {
                        arr[j][i]= 0
                        stopBall = true
                    } else if(arr[j][i-1]>0) stopBlock = true
                }
            }
        }
    }
    fun left() {
        var t = false;
        for(i in 0..10) {
            for(j in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j][i+1]==-2) {
                        stopBall = true
                    } else if(arr[j][i+1]>0) stopBlock = true
                } else {
                    if((arr[j][i+1]==-2 && !stopBall) || (arr[j][i+1]>0 && !stopBlock)) {
                        arr[j][i] = arr[j][i + 1]
                        arr[j][i+1] = 0
                    }
                }
            }
        }
        for(i in 0..10) {
            for(j in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j][i+1]==-2) {
                        arr[j][i]= 0
                        stopBall = true
                    } else if(arr[j][i+1]>0) stopBlock = true
                }
            }
        }
    }
    fun bottom() {
        var t = false;
        for(j in 11 downTo 1) {
            for(i in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j-1][i]==-2) {
                        stopBall = true
                    } else if(arr[j-1][i]>0) stopBlock = true
                } else {
                    if((arr[j-1][i]==-2 && !stopBall) || (arr[j-1][i]>0 && !stopBlock)) {
                        arr[j][i] = arr[j-1][i]
                        arr[j-1][i] = 0
                    }
                }
            }
        }
        for(j in 11 downTo 1) {
            for(i in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j-1][i]==-2) {
                        arr[j][i]= 0
                        stopBall = true
                    } else if(arr[j-1][i]>0) stopBlock = true
                }
            }
        }
    }
    fun top() {
        var t = false;
        for(j in 0..10) {
            for(i in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j+1][i]==-2) {
                        stopBall = true
                        arr[j][i]= 0
                    } else if(arr[j+1][i]>0) stopBlock = true
                } else {
                    if((arr[j+1][i]==-2 && !stopBall) || (arr[j+1][i]>0 && !stopBlock)) {
                        arr[j][i] = arr[j+1][i ]
                        arr[j+1][i] = 0
                    }
                }
            }
        }
        for(j in 0..10) {
            for(i in 0..11) {
                if(arr[j][i]==-1) {
                    if(arr[j+1][i]==-2) {
                        stopBall = true
                        arr[j][i]= 0
                    } else if(arr[j+1][i]>0) stopBlock = true
                }
            }
        }
    }
    fun check(): Boolean {
        var ans = false
        for (i in 0..11) {
            for(j in 0..11) {
                if(arr[i][j]>0) ans = true
            }
        }
        return  ans
    }

    fun setEndListener(list: EndListener) {
        this.listener = list
    }
    fun togglePause() {
        paused = !paused
    }
    companion object {
        interface EndListener {
            fun end();
            fun score(score: Int);
        }

    }
}