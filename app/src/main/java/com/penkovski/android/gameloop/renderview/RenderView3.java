package com.penkovski.android.gameloop.renderview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.penkovski.android.gameloop.MainActivity;

public class RenderView3 extends SurfaceView implements Runnable {
    private static final int FRAMES_PER_SECOND = 60;
    private static final int FRAME_LENGTH_MS = 1000 / FRAMES_PER_SECOND;

    private MainActivity game;
    private Thread renderThread;
    private SurfaceHolder holder;
    private volatile boolean running;

    public RenderView3(MainActivity context, Bitmap frameBuffer) {
        super(context);
        this.game = context;
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
        long currentTime = SystemClock.uptimeMillis();

        while(running) {
            if (!holder.getSurface().isValid()) continue;

            Canvas c = null;
            long newTime = SystemClock.uptimeMillis();
            long frameTime = newTime - currentTime;
            currentTime = newTime;

            while (frameTime > 0) {
                int deltaTime = frameTime <= FRAME_LENGTH_MS ? (int) frameTime : FRAME_LENGTH_MS;
                game.update(deltaTime);
                frameTime -= deltaTime;
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    c = holder.lockHardwareCanvas();
                } else {
                    c = holder.lockCanvas();
                }

                if (c != null) {
                    game.render(c);
                }
            } finally {
                if (c != null && holder.getSurface().isValid()) {
                    try { holder.unlockCanvasAndPost(c); } catch (Exception ignored) { }
                }
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
