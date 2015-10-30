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
	private static final int INVALIDATE_DURATION = 15; // ÿ��ˢ�µ�ʱ����
	private static int DIFFUSE_GAP = 10; // ��ɢ�뾶����
	private static int TAP_TIMEOUT; // �жϵ���ͳ�����ʱ��

	private int viewWidth, viewHeight; // �ؼ����
	private int pointX, pointY; // �ؼ�ԭ�����꣨���Ͻǣ�
	private int maxRadio; // ��ɢ�����뾶
	private int shaderRadio; // ��ɢ�İ뾶

	private Paint bottomPaint, colorPaint; // ����:������ˮ����
	private boolean isPushButton; // ��¼�Ƿ�ť������

	private int eventX, eventY; // ����λ�õ�X,Y����
	private long downTime = 0; // ���µ�ʱ��

	public ClickWaveButton1(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
		TAP_TIMEOUT = ViewConfiguration.getLongPressTimeout();// ��ó�����ʱ��
	}

	/*
	 * ��ʼ������
	 */
	private void initPaint() {
		colorPaint = new Paint();
		bottomPaint = new Paint();
		colorPaint.setColor(getResources().getColor(R.color.reveal_color));
		bottomPaint.setColor(getResources().getColor(R.color.bottom_color));
	}
	/**
	 * ���̣���action_down֮�󣬻�õ����ʱ�����꣨x��y���������Բ�����뾶�����û��Ʒ������������ܸ�һ��ʱ�����һ����
	 * 		action_up����action_cancel֮���жϵ���¼���ʱ���Ƿ񳬹�������ʱ�䣬�����Ļ���ֹͣ����Ч�����ָ�ԭ״
	 * 		û�г����Ļ���������ơ�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (downTime == 0)
				downTime = SystemClock.elapsedRealtime();
			eventX = (int) event.getX();
			eventY = (int) event.getY();
			// �������뾶��
			countMaxRadio();
			isPushButton = true;
			postInvalidateDelayed(INVALIDATE_DURATION);
			Log.i("TAG", "---000---");
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// �жϵ���¼��Ƿ񳬹��˳�����ʱ�䣬û�г����Ļ��ͻ������ƣ������˾�ֱ�ӻָ�
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
	 * ÿ�ε���postInvalidate����ʱ�ͻ����dispatchDraw���������Խ���������»���
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!isPushButton)
			return; // �����ťû�б������򷵻�
		// ���ư��º����������
		canvas.drawRect(pointX, pointY, pointX + viewWidth,
				pointY + viewHeight, bottomPaint);
		canvas.save();
		// ������ɢԲ�α���
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
	 * �������뾶�ķ���
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
	 * �������ݵķ���
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
