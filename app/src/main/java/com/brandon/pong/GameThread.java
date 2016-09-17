package com.brandon.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;

/**
 * Created by Brandon on 9/16/16.
 */
public class GameThread extends Thread {

    /** Handle to the surface manager object we interact with */
    private SurfaceHolder _surfaceHolder;
    private Paint _paint;
    private GameState _state;

    public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler)
    {
        _surfaceHolder = surfaceHolder;
        _paint = new Paint();
        _state = new GameState(context);
    }

    @Override
    public void run() {
        while(true)
        {
            Canvas canvas = _surfaceHolder.lockCanvas();
            _state.update();
            _state.draw(canvas,_paint);
            try{
                _surfaceHolder.unlockCanvasAndPost(canvas);
            } catch(IllegalStateException e){
                break;
            }
        }
    }

    public GameState getGameState()
    {
        return _state;
    }
}