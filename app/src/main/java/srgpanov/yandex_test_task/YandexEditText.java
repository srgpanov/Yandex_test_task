package srgpanov.yandex_test_task;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by Пан on 26.03.2017.
 */

public class YandexEditText extends android.support.v7.widget.AppCompatEditText {

    private Paint mPaint;
    private Rect mRect;

    public YandexEditText(Context context) {
        super(context);
        init();
    }

    public YandexEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YandexEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //устанавливаем отступ справа, чтобы каретка не наезжала на наши кнопки
    @Override
    public int getCompoundPaddingRight() {
        //здесь мы устанавливаем отступ справа, чтобы каретка не наезжала на наши кнопки
        int paddingRight = super.getCompoundPaddingRight();
        paddingRight = paddingRight + getSizeInDp(36) ;
        return paddingRight;
    }

    @Override
    public int getCompoundPaddingEnd() {
        int paddingEnd = super.getCompoundPaddingEnd();
        paddingEnd = paddingEnd + getSizeInDp(36);
        return paddingEnd;
    }

    @Override
    public int getCompoundPaddingTop() {
        int paddingTop = super.getCompoundPaddingTop();
        paddingTop = paddingTop + getSizeInDp(16);
        return paddingTop;
    }

    @Override
    public int getCompoundPaddingLeft() {
        int paddingLeft = super.getCompoundPaddingLeft();
        paddingLeft = paddingLeft + getSizeInDp(16);
        return paddingLeft;
    }

    @Override
    public int getCompoundPaddingStart() {
        int paddingStart = super.getCompoundPaddingStart();
        paddingStart = paddingStart + getSizeInDp(16);
        return paddingStart;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRect.set(getSizeInDp(8), getSizeInDp(8), canvas.getWidth()- getSizeInDp(8), canvas.getHeight()- getSizeInDp(8));
        canvas.drawRect(mRect,mPaint);

    }

    @Override
    public int getCompoundPaddingBottom() {
        int paddingBottom = super.getCompoundPaddingBottom();
        paddingBottom =paddingBottom + getSizeInDp(36);
        return paddingBottom;
    }
    private int getSizeInDp(int padding){
        final float scale = getResources().getDisplayMetrics().density; //переводим DP в пиксели
        return (int)(padding * scale + 0.5f);
    }
    private void init() {

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.grey_light));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getSizeInDp(1));
        mRect=new Rect();
    }

    }


