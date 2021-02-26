package com.thws.boyaddz;

import android.graphics.Canvas;

public abstract class BaseAnimation {
    private int duration;
    protected int currentFrame = 0;

    protected BaseAnimation(int duration) {
        this.duration = duration;
    }

    public final void render(Canvas canvas) {
        if (!isEnded()) {
            onRender(canvas);
            currentFrame++;
        }
    }

    protected abstract void onRender(Canvas canvas);

    public boolean isEnded() {
        return currentFrame >= duration;
    }
}
