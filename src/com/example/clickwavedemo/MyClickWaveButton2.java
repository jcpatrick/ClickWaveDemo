package com.example.clickwavedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Button;

public class MyClickWaveButton2 extends Button {
	private static final int INVALIDATE_DURATION = 15;//每次刷新时间间隔
	private static final int START_X = 0, START_Y = 0;//Button的起始未做
	private int viewWidth, viewHeight;//Button的宽高
	private int LONG_CLICK_TIME;//长按超时时间
	Paint bottomPaint, colorPaint;//底部画笔，我颜色画笔
	private static int DIFFUSE_GAP = 10;//半径增长量
	private boolean isBtnClick = false;
	private int shaderRadio; //波浪纹的半径
	private int maxRadio = 0;//波浪纹的最大半径
	private long Start_time = 0;//起始点击事件
	private int eventX, eventY;//点击的位置坐标

	public MyClickWaveButton2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
		LONG_CLICK_TIME = ViewConfiguration.getLongPressTimeout();//获得长按超时时间
	}
	/**
	 * 初始化一个画笔
	 */
	private void initPaint() {
		bottomPaint = new Paint();//背景颜色的画笔
		colorPaint = new Paint();//阴影部分的画笔
		colorPaint.setColor(getResources().getColor(R.color.reveal_color2));
		bottomPaint.setColor(getResources().getColor(R.color.bottom_color2));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//判断是否是第一次点击
			if (Start_time == 0) {
				Start_time = SystemClock.elapsedRealtime();
			}
			eventX = (int) event.getX();
			eventY = (int) event.getY();
			countMaxRadio();//获得最大的半径
			isBtnClick = true;//设置成已点击
			postInvalidateDelayed(INVALIDATE_DURATION);//延迟一定时间之后重新绘制
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			//不超过长按时间时直接绘制，否则，清空数据，绘制初始的view
			if (SystemClock.elapsedRealtime() - Start_time < LONG_CLICK_TIME) {
				DIFFUSE_GAP = 30;
				postInvalidate();//通知系统去绘制
			} else {
				clearData();//清空数据，绘制初始的view
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!isBtnClick)
			return;
		//绘制背景
		canvas.drawRect(START_X, START_Y, START_X + viewWidth, START_Y
				+ viewHeight, bottomPaint);
		canvas.save();
		//绘制波浪纹
		canvas.clipRect(START_X, START_Y, START_X + viewWidth, START_Y
				+ viewHeight);
		canvas.drawCircle(eventX, eventY, shaderRadio, colorPaint);
		canvas.restore();
		//如果小于最大半径则继续画，并增加阴影半径，否则的话的恢复到初始的view
		if (shaderRadio <= maxRadio) {
			postInvalidateDelayed(INVALIDATE_DURATION, START_X, START_Y,
					viewWidth, viewHeight);
			shaderRadio += DIFFUSE_GAP;
		} else {
			clearData();
		}
	}
	//清空数据，绘制初始的view
	private void clearData() {
		Start_time = 0;
		DIFFUSE_GAP = 10;
		isBtnClick = false;
		shaderRadio = 0;
		postInvalidate();
	}
	/**
	 * 计算获得最大半径
	 */
	private void countMaxRadio() {
		int maxX = (viewWidth - eventX) > eventX ? (viewWidth - eventX)
				: eventX;
		int maxY = (viewHeight - eventY) > eventY ? (viewHeight - eventY)
				: eventY;
		maxRadio = (int) Math.sqrt(maxY * maxY + maxX * maxX);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.viewWidth = w;
		this.viewHeight = h;
	}
}
