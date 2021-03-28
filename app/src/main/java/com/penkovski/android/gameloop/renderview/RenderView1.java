package com.penkovski.android.gameloop.renderview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.penkovski.android.gameloop.MainActivity;

public class RenderView1 extends SurfaceView implements Runnable {
    private static final int FRAMES_PER_SECOND = 60;
    private static final int FRAME_LENGTH_MS = 1000 / FRAMES_PER_SECOND;

    private MainActivity game;
    private Bitmap frameBuffer;
    private Thread renderThread;
    private SurfaceHolder holder;
    private volatile boolean running;

    public RenderView1(MainActivity context, Bitmap frameBuffer) {
        super(context);
        this.game = context;
        this.frameBuffer = frameBuffer;
        this.holder = getHolder();
    }

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void pause() {
        running = false;
        while(true)
        {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void run() {
        Rect dstRect = new Rect();
        long currentTime = SystemClock.uptimeMillis();

        while (running) {
            if (!holder.getSurface().isValid()) continue;

            long time = SystemClock.uptimeMillis();
            long deltaTime = time - currentTime;
            currentTime = time;

            game.update(deltaTime);
            game.render(null);

            Canvas canvas = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas = holder.lockHardwareCanvas();
            } else {
                canvas = holder.lockCanvas();
            }
            if(canvas != null) {
                canvas.getClipBounds(dstRect);
                canvas.drawBitmap(frameBuffer, null, dstRect, null);
                holder.unlockCanvasAndPost(canvas);
            }

            // sleep if necessary
            long frameLength = SystemClock.uptimeMillis() - currentTime + 1;
            if(frameLength < FRAME_LENGTH_MS)
            {
                long sleep = FRAME_LENGTH_MS - frameLength;
                try { Thread.sleep(sleep); } catch (InterruptedException ignored) {}
            }
        }
    }
}
