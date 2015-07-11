package it.technocontrolsystem.hypercontrol.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;

/**
 *
 */
public abstract class CircleText extends TextView {


//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public CircleText(Context context) {
//        super(context);
//        ShapeDrawable circle = new ShapeDrawable( new OvalShape() );
//        circle.getPaint().setStrokeWidth(10);
//        circle.getPaint().setColor(getColor());
//        setBackground(circle);
//        setBackgroundDrawable(circle);
//    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public CircleText(Context context) {
        super(context);
        ShapeDrawable circle = new ShapeDrawable( new OvalShape() );
        circle.getPaint().setStrokeWidth(10);
        circle.getPaint().setColor(getColor());
        // ricordati di fornire sempre una alternativa
        if(Lib.hasJellyBean()){
            setBackground(circle);
        }else{
            setBackgroundDrawable(circle);
        }
    }


    protected abstract int getColor();

}
