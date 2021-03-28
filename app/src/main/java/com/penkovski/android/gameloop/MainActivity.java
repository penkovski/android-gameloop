package com.penkovski.android.gameloop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.penkovski.android.gameloop.renderview.RenderView1;
import com.penkovski.android.gameloop.renderview.RenderView2;
import com.penkovski.android.gameloop.renderview.RenderView3;

public class MainActivity extends AppCompatActivity {

    private Bitmap image;
    private int imageX, imageY;
    private int scrWidth, scrHeight;
    private Paint paint;

    Canvas canvas;
    Bitmap frameBuffer;
    RenderView3 renderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.paint = new Paint();
        this.image = loadImage();

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        this.scrWidth = size.x;
        this.scrHeight = size.y;

        frameBuffer = Bitmap.createBitmap(scrWidth, scrHeight, Bitmap.Config.RGB_565);
        canvas = new Canvas(frameBuffer);
        renderView = new RenderView3(this, frameBuffer);

        setContentView(renderView);
    }

    @Override
    public void onResume() {
        super.onResume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        renderView.pause();
    }

    public void update(float deltaTime) {
        imageX += deltaTime/2;
        if (imageX >= scrWidth) {
            imageX = -image.getWidth();
        }
        imageY = scrHeight / 2;
    }

    public void render(Canvas canvas) {
        if(canvas == null) {
            this.canvas.drawColor(Color.BLACK);
            this.canvas.drawBitmap(image, (float)imageX, (float)imageY, paint);
        } else {
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(image, (float)imageX, (float)imageY, paint);
        }
    }

    private Bitmap loadImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple, options);
            if(bitmap == null) throw new RuntimeException("Could not load bitmap resource '" + R.drawable.apple + "'");
        }
        catch(Exception e) {
            throw new RuntimeException("Could not load bitmap asset '" + R.drawable.apple  + "'");
        }

        return bitmap;
    }
}