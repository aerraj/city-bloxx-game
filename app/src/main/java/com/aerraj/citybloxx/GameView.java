package com.aerraj.citybloxx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public final class GameView extends View {
    static {
        System.loadLibrary("citybloxx");
    }

    private static native float[] nativeLand(float movingLeft, float movingWidth,
                                             float targetLeft, float targetWidth);

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<RectF> tower = new ArrayList<>();
    private final float density;

    private float blockHeight;
    private float movingLeft;
    private float movingTop;
    private float movingWidth;
    private float horizontalSpeed;
    private int direction = 1;
    private int score;
    private int lives;
    private long previousFrame;
    private boolean dropping;
    private boolean landingChecked;
    private boolean gameOver;

    public GameView(Context context) {
        super(context);
        density = getResources().getDisplayMetrics().density;
        paint.setTypeface(android.graphics.Typeface.create("sans", android.graphics.Typeface.BOLD));
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        restart();
    }

    private void restart() {
        tower.clear();
        score = 0;
        lives = 3;
        gameOver = false;
        blockHeight = dp(48);
        float baseWidth = getWidth() * 0.62f;
        float baseLeft = (getWidth() - baseWidth) / 2f;
        float baseBottom = getHeight() - dp(58);
        tower.add(new RectF(baseLeft, baseBottom - blockHeight, baseLeft + baseWidth, baseBottom));
        movingWidth = baseWidth;
        horizontalSpeed = dp(175);
        previousFrame = 0L;
        spawnBlock();
        invalidate();
    }

    private void spawnBlock() {
        movingLeft = direction > 0 ? -movingWidth : getWidth();
        movingTop = dp(118);
        dropping = false;
        landingChecked = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSky(canvas);
        drawTower(canvas);
        drawHud(canvas);

        long now = System.nanoTime();
        if (previousFrame == 0L) {
            previousFrame = now;
        }
        float delta = Math.min((now - previousFrame) / 1_000_000_000f, 0.033f);
        previousFrame = now;
        if (!gameOver) {
            update(delta);
            postInvalidateOnAnimation();
        }
    }

    private void update(float delta) {
        if (!dropping) {
            movingLeft += direction * horizontalSpeed * delta;
            if (movingLeft < 0f) {
                movingLeft = 0f;
                direction = 1;
            } else if (movingLeft + movingWidth > getWidth()) {
                movingLeft = getWidth() - movingWidth;
                direction = -1;
            }
            return;
        }

        movingTop += dp(620) * delta;
        RectF target = tower.get(tower.size() - 1);
        if (!landingChecked && movingTop + blockHeight >= target.top) {
            landingChecked = true;
            float[] landing = nativeLand(movingLeft, movingWidth, target.left, target.width());
            if (landing[2] > 0.5f) {
                movingLeft = landing[0];
                movingWidth = landing[1];
                tower.add(new RectF(movingLeft, target.top - blockHeight,
                        movingLeft + movingWidth, target.top));
                score++;
                horizontalSpeed = Math.min(horizontalSpeed + dp(7), dp(310));
                keepTowerVisible();
                spawnBlock();
            }
        }

        if (landingChecked && movingTop > getHeight()) {
            lives--;
            if (lives == 0) {
                gameOver = true;
            } else {
                movingWidth = tower.get(tower.size() - 1).width();
                spawnBlock();
            }
        }
    }

    private void keepTowerVisible() {
        RectF top = tower.get(tower.size() - 1);
        float guide = getHeight() * 0.38f;
        if (top.top >= guide) {
            return;
        }
        float shift = guide - top.top;
        for (RectF block : tower) {
            block.offset(0f, shift);
        }
    }

    private void drawSky(Canvas canvas) {
        canvas.drawColor(Color.rgb(116, 196, 235));
        paint.setColor(Color.argb(150, 255, 255, 255));
        canvas.drawCircle(dp(54), dp(82), dp(24), paint);
        canvas.drawCircle(dp(80), dp(78), dp(31), paint);
        canvas.drawCircle(getWidth() - dp(48), dp(165), dp(27), paint);

        paint.setColor(Color.rgb(56, 105, 108));
        canvas.drawRect(0f, getHeight() - dp(58), getWidth(), getHeight(), paint);
    }

    private void drawTower(Canvas canvas) {
        int[] colors = {
                Color.rgb(218, 79, 68), Color.rgb(243, 166, 61),
                Color.rgb(70, 138, 166), Color.rgb(111, 92, 153)
        };
        for (int i = 0; i < tower.size(); i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawRoundRect(tower.get(i), dp(4), dp(4), paint);
            drawWindows(canvas, tower.get(i));
        }
        if (!gameOver) {
            RectF moving = new RectF(movingLeft, movingTop,
                    movingLeft + movingWidth, movingTop + blockHeight);
            paint.setColor(colors[tower.size() % colors.length]);
            canvas.drawRoundRect(moving, dp(4), dp(4), paint);
            drawWindows(canvas, moving);
        }
    }

    private void drawWindows(Canvas canvas, RectF block) {
        paint.setColor(Color.rgb(255, 224, 121));
        float size = dp(7);
        float y = block.centerY() - size / 2f;
        for (float x = block.left + dp(15); x + size < block.right - dp(10); x += dp(22)) {
            canvas.drawRect(x, y, x + size, y + size, paint);
        }
    }

    private void drawHud(Canvas canvas) {
        paint.setTextSize(dp(24));
        paint.setColor(Color.WHITE);
        canvas.drawText("Floors " + score, dp(18), dp(40), paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Chances " + lives, getWidth() - dp(18), dp(40), paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(dp(18));
        if (gameOver) {
            paint.setTextSize(dp(32));
            canvas.drawText("Tower complete", getWidth() / 2f, getHeight() * 0.32f, paint);
            paint.setTextSize(dp(18));
            canvas.drawText("Tap to build again", getWidth() / 2f, getHeight() * 0.38f, paint);
        } else if (score == 0 && !dropping) {
            canvas.drawText("Tap to drop the block", getWidth() / 2f, dp(86), paint);
        }
        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }
        if (gameOver) {
            restart();
        } else if (!dropping) {
            dropping = true;
        }
        return true;
    }

    private float dp(float value) {
        return value * density;
    }
}
