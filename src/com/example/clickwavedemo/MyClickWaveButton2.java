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
	private static final int INVALIDATE_DURATION = 15;//ÿ��ˢ��ʱ����
	private static final int START_X = 0, START_Y = 0;//Button����ʼδ��
	private int viewWidth, viewHeight;//Button�Ŀ��
	private int LONG_CLICK_TIME;//������ʱʱ��
	Paint bottomPaint, colorPaint;//�ײ����ʣ�����ɫ����
	private static int DIFFUSE_GAP = 10;//�뾶������
	private boolean isBtnClick = false;
	private int shaderRadio; //�����Ƶİ뾶
	private int maxRadio = 0;//�����Ƶ����뾶
	private long Start_time = 0;//��ʼ����¼�
	private int eventX, eventY;//�����λ������

	public MyClickWaveButton2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
		LONG_CLICK_TIME = ViewConfiguration.getLongPressTimeout();//��ó�����ʱʱ��
	}
	/**
	 * ��ʼ��һ������
	 */
	private void initPaint() {
		bottomPaint = new Paint();//������ɫ�Ļ���
		colorPaint = new Paint();//��Ӱ���ֵĻ���
		colorPaint.setColor(getResources().getColor(R.color.reveal_color2));
		bottomPaint.setColor(getResources().getColor(R.color.bottom_color2));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//�ж��Ƿ��ǵ�һ�ε��
			if (Start_time == 0) {
				Start_time = SystemClock.elapsedRealtime();
			}
			eventX = (int) event.getX();
			eventY = (int) event.getY();
			countMaxRadio();//������İ뾶
			isBtnClick = true;//���ó��ѵ��
			postInvalidateDelayed(INVALIDATE_DURATION);//�ӳ�һ��ʱ��֮�����»���
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			//����������ʱ��ʱֱ�ӻ��ƣ�����������ݣ����Ƴ�ʼ��view
			if (SystemClock.elapsedRealtime() - Start_time < LONG_CLICK_TIME) {
				DIFFUSE_GAP = 30;
				postInvalidate();//֪ͨϵͳȥ����
			} else {
				clearData();//������ݣ����Ƴ�ʼ��view
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
		//���Ʊ���
		canvas.drawRect(START_X, START_Y, START_X + viewWidth, START_Y
				+ viewHeight, bottomPaint);
		canvas.save();
		//���Ʋ�����
		canvas.clipRect(START_X, START_Y, START_X + viewWidth, START_Y
				+ viewHeight);
		canvas.drawCircle(eventX, eventY, shaderRadio, colorPaint);
		canvas.restore();
		//���С�����뾶�����������������Ӱ�뾶������Ļ��Ļָ�����ʼ��view
		if (shaderRadio <= maxRadio) {
			postInvalidateDelayed(INVALIDATE_DURATION, START_X, START_Y,
					viewWidth, viewHeight);
			shaderRadio += DIFFUSE_GAP;
		} else {
			clearData();
		}
	}
	//������ݣ����Ƴ�ʼ��view
	private void clearData() {
		Start_time = 0;
		DIFFUSE_GAP = 10;
		isBtnClick = false;
		shaderRadio = 0;
		postInvalidate();
	}
	/**
	 * ���������뾶
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
