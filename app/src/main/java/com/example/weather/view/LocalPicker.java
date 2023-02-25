package com.example.weather.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * LocalPicker:地区选择器
 * <p>
 * 使用时需要定义高和宽度的精确值 <br>
 * 默认样式为白色背景与黑色字体，样式可以通过 {@link LocalPicker.LocalPickerBuilder} 设置
 * </p>
 */
public class LocalPicker extends View {
    private              LocalPicker.OnItemSelected itemSelectedCallback;
    private              LocalPicker.resetTextState state;
    private              List<String>               content;
    private              ValueAnimator              animator;
    private static final int                        TEXT_FOREHEAD = 0;
    private static final int                        TEXT_CURRENT  = 1;
    private static final int                        TEXT_NEXT     = 2;

    private static float heightCurrent = 0;

    private static final float defaultTextPadding = 100f;

    private int height       = 0;
    private int width        = 0;
    private int widthCenter  = 540;
    private int heightCenter = 1059;

    private int contentIndex = 0;
    private int maxIndex     = 0;

    private float previousY;
    private float heightOffset = 0;

    Paint  backgroundPaint = new Paint();
    Paint  textPaint       = new Paint();
    Paint  overlayPaint    = new Paint();
    String TAG             = "LocalPicker";

    private int textColor       = 0xff212121;
    private int backgroundColor = 0xffffffff;
    private int overlayColor    = 0x00f78c6c;

    public LocalPicker(Context context) {
        super(context);
        init();
    }

    public LocalPicker(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    /**
     *
     * @param contentList 需要显示的数组
     */
    public void refreshContentList(List<String> contentList) {
        this.content = contentList;
        init();
    }

    /**
     * 当用户抬手的时候将触发回调，并返回数组下表
     * @param onItemSelectListener 接口回调
     */
    public void setOnItemSelectListener(LocalPicker.OnItemSelected onItemSelectListener) {
        this.itemSelectedCallback = onItemSelectListener;
    }

    private void setOnResetListener(LocalPicker.resetTextState state) {
        this.state = state;
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, width, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int layoutHeight = getMeasuredHeight();
        int layoutWidth  = getMeasuredWidth();
        if (height < layoutHeight) {
            height = layoutHeight;
        }
        if (width < layoutWidth) {
            width = layoutWidth;
        }
        heightCenter = height / 2;
        widthCenter = width / 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawUserInterface(canvas);
        canvas.drawLine(0, heightCenter, widthCenter * 2, heightCenter, overlayPaint);
        canvas.drawLine(0, heightCenter - defaultTextPadding, widthCenter * 2, heightCenter - defaultTextPadding, overlayPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - previousY;
                performPositionOffset(dy);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (itemSelectedCallback != null) {
                    this.itemSelectedCallback.getItem(getCurrentIndex(contentIndex));
                    if (state != null) {
                        this.state.reset();
                    }
                }
                break;
        }
        return true;
    }

    private void drawUserInterface(Canvas canvas) {
        drawText(canvas, TEXT_FOREHEAD, getForeheadIndex(contentIndex));
        drawText(canvas, TEXT_CURRENT, getCurrentIndex(contentIndex));
        drawText(canvas, TEXT_NEXT, getNextIndex(contentIndex));
    }

    private void drawText(Canvas canvas, int position, int index) {

        float textHeight;
        if (position == TEXT_FOREHEAD) {
            textHeight = heightCenter - defaultTextPadding + heightOffset % 100;
            heightCurrent = textHeight - defaultTextPadding;
        } else if (position == TEXT_CURRENT) {
            textHeight = heightCenter + heightOffset % 100;
            heightCurrent = textHeight;
        } else {
            textHeight = heightCenter + defaultTextPadding + heightOffset % 100;
            heightCurrent = textHeight + defaultTextPadding;
        }
        float textSize = transferTextSize(textHeight - heightCenter);
        textPaint.setTextSize(textSize);
        float textWidth = textPaint.measureText(content.get(index));
        canvas.drawText(content.get(index), widthCenter - textWidth / 2, textHeight, textPaint);

    }

    private float transferTextSize(float offset) {
        if (Math.abs(offset) > 50) {
            return 50;
        } else {
            return 100 - Math.abs(offset);
        }
    }

    private void performPositionOffset(float dy) {
        this.heightOffset = (heightOffset + dy) / 2;
        if (Math.abs(heightOffset) > defaultTextPadding) {
            contentIndex = -(int) (heightOffset / defaultTextPadding);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, width, height, backgroundPaint);
    }

    private int getForeheadIndex(int current) {
        int offset = (current - 1) % maxIndex;
        if (offset < 0) {
            return offset + maxIndex;
        } else {
            return offset;
        }
    }

    private int getCurrentIndex(int current) {
        int offset = current % maxIndex;
        if (offset < 0) {
            return offset + maxIndex;
        } else {
            return offset;
        }
    }

    private int getNextIndex(int current) {
        int offset = (current + 1) % maxIndex;
        if (offset < 0) {
            return offset + maxIndex;
        } else {
            return offset;
        }
    }

    private void init() {
        if (content != null) {
            this.maxIndex = content.size() - 1;
        }

        animator = new ValueAnimator();

        backgroundPaint.setColor(this.backgroundColor);
        textPaint.setColor(this.textColor);
        overlayPaint.setColor(this.overlayColor);
        overlayPaint.setStrokeWidth(5);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setOverlayColor(int overlayColor) {
        this.overlayColor = overlayColor;
    }

    public interface OnItemSelected {
        void getItem(int itemIndex);
    }

    private interface resetTextState {
        void reset();
    }

    public static class LocalPickerBuilder {
        private int textColor       = 0xff212121;
        private int backgroundColor = 0xffffffff;
        private int overlayColor    = 0x00f78c6c;

        public LocalPickerBuilder() {
        }

        /**
         * 设置字体颜色
         * @param textColor hex值
         */
        public LocalPickerBuilder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * 设置背景色
         * @param backgroundColor hex值
         */
        public LocalPickerBuilder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * 上层遮罩
         * @param overlayColor hex值
         */
        public LocalPickerBuilder setOverlayColor(int overlayColor) {
            this.overlayColor = overlayColor;
            return this;
        }

        /**
         * 应用更改
         * @param localPicker 目标 {@link LocalPicker}
         */
        public void build(LocalPicker localPicker) {
            localPicker.setTextColor(textColor);
            localPicker.setBackgroundColor(backgroundColor);
            localPicker.setOverlayColor(overlayColor);
        }
    }
}
