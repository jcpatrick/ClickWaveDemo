package com.example.clickwavedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Button;

public class ClickWaveButton1 extends Button {
	private static final int INVALIDATE_DURATION = 15; // 每次刷新的时间间隔
	private static int DIFFUSE_GAP = 10; // 扩散半径增量
	private static int TAP_TIMEOUT; // 判断点击和长按的时间

	private int viewWidth, viewHeight; // 控件宽高
	private int pointX, pointY; // 控件原点坐标（左上角）
	private int maxRadio; // 扩散的最大半径
	private int shaderRadio; // 扩散的半径

	private Paint bottomPaint, colorPaint; // 画笔:背景和水波纹
	private boolean isPushButton; // 记录是否按钮被按下

	private int eventX, eventY; // 触摸位置的X,Y坐标
	private long downTime = 0; // 按下的时间

	public ClickWaveButton1(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
		TAP_TIMEOUT = ViewConfiguration.getLongPressTimeout();// 获得长按的时间
	}

	/*
	 * 初始化画笔
	 */
	private void initPaint() {
		colorPaint = new Paint();
		bottomPaint = new Paint();
		colorPaint.setColor(getResources().getColor(R.color.reveal_color));
		bottomPaint.setColor(getResources().getColor(R.color.bottom_color));
	}
	/**
	 * 流程：当action_down之后，获得点击的时候、坐标（x，y），计算出圆的最大半径，调用绘制方法，这样就能隔一段时间绘制一次了
	 * 		action_up或者action_cancel之后，判断点击事件的时间是否超过长按的时间，超过的话则停止动画效果并恢复原状
	 * 		没有超过的话则继续绘制。
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (downTime == 0)
				downTime = SystemClock.elapsedRealtime();
			eventX = (int) event.getX();
			eventY = (int) event.getY();
			// 计算最大半径：
			countMaxRadio();
			isPushButton = true;
			postInvalidateDelayed(INVALIDATE_DURATION);
			Log.i("TAG", "---000---");
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// 判断点击事件是否超过了长按的时间，没有超过的话就画波浪纹，超过了就直接恢复
			if (SystemClock.elapsedRealtime() - downTime < TAP_TIMEOUT) {
				Log.i("TAG", "---111---");
				DIFFUSE_GAP = 30;
				postInvalidate();
			} else {
				Log.i("TAG", "---222---");
				clearData();

			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * 每次调用postInvalidate（）时就会调用dispatchDraw（）方法对界面进行重新绘制
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!isPushButton)
			return; // 如果按钮没有被按下则返回
		// 绘制按下后的整个背景
		canvas.drawRect(pointX, pointY, pointX + viewWidth,
				pointY + viewHeight, bottomPaint);
		canvas.save();
		// 绘制扩散圆形背景
		canvas.clipRect(pointX, pointY, pointX + viewWidth, pointY + viewHeight);
		canvas.drawCircle(eventX, eventY, shaderRadio, colorPaint);
		canvas.restore();
		
		if (shaderRadio < maxRadio) {

			postInvalidateDelayed(INVALIDATE_DURATION, pointX, pointY, pointX
					+ viewWidth, pointY + viewHeight);
			shaderRadio += DIFFUSE_GAP;
		} else {

			clearData();
		}
	}

	/*
	 * 计算最大半径的方法
	 */
	private void countMaxRadio() {
		if (viewWidth / 2 - eventX >= 0) {
			if (viewHeight / 2 - eventY >= 0) {
				maxRadio = (int) Math.sqrt((viewHeight - eventY)
						* (viewHeight - eventY) + (viewWidth - eventX)
						* (viewWidth - eventX));
			} else {
				maxRadio = (int) Math.sqrt(eventY
						* eventY + (viewWidth - eventX)
						* (viewWidth - eventX));
			}
		} else {
			if (viewHeight / 2 - eventY >= 0) {
				maxRadio = (int) Math.sqrt((viewHeight - eventY)
						* (viewHeight - eventY) +  eventX
						*  eventX);
			} else {
				maxRadio = (int) Math.sqrt(eventY
						* eventY + eventX
						* eventX);
			}
		}
	}

	/*
	 * 重置数据的方法
	 */
	private void clearData() {
		downTime = 0;
		DIFFUSE_GAP = 10;
		isPushButton = false;
		shaderRadio = 0;
		postInvalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.viewWidth = w;
		this.viewHeight = h;
	}
}
