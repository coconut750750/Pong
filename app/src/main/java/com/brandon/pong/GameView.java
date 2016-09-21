package com.brandon.pong;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/***
 * Created by Brandon on 9/16/16.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread _thread;
    private SurfaceHolder holder;
    private Context context;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        //So we can listen for events...
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);

        //and instantiate the thread
        //_thread = new GameThread(holder, context, new Handler());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return true;
    }

    //Implemented as part of the SurfaceHolder.Callback interface
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        //Mandatory, just swallowing it for this example
    }

    //Implemented as part of the SurfaceHolder.Callback interface
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        _thread = new GameThread(holder, context, new Handler());
        MainActivity.getData();
        _thread.start();
        _thread.onResume();
        holder.addCallback(this);
    }

    //Implemented as part of the SurfaceHolder.Callback interface
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        _thread.onPause();
        MainActivity.saveData();
    }
}