package org.egov.android.view.component.slider;

import org.egov.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public class SlidingDrawerLayout extends RelativeLayout implements
		AnimationListener, OnTouchListener, Runnable {

	public enum SlideDirection {
		LEFT(0), TOP(1), RIGHT(2), BOTTOM(3);

		private int data = 0;

		SlideDirection(int data) {
			this.data = data;
		}

		public int toInt() {
			return this.data;
		}
	}

	private static final String TAG = "SlidingDrawer";

	private float[][] position = { { 0, -1, 0, 0 }, { 0, 0, 0, -1 },
			{ 0, 1, 0, 0 }, { 0, 0, 0, 1 } };
	private int[][] margin = { { -1, 0, 1, 0 }, { 0, -1, 0, 1 },
			{ 1, 0, -1, 0 }, { 0, 1, 0, -1 } };

	private ViewGroup mainView = null;
	private int contentLayoutId = 0;

	private boolean isOpen = false;
	// private int drawerSize = 0;
	private Context context = null;

	private SlideDirection direction = null;
	private ISlidingDrawerListener listener = null;

	public SlidingDrawerLayout(Context context) {
		super(context, null);
		_init(context, null);
	}

	public SlidingDrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init(context, attrs);
	}

	private void _init(Context context, AttributeSet attrs) {
		this.context = context;
		this.setId(R.id.slidingDrawer);

		if (attrs == null) {
			return;
		}

		TypedArray arr = getContext().obtainStyledAttributes(attrs,
				R.styleable.SlidingDrawerLayout);
		if (!isInEditMode()) {
			int d = arr.getInt(R.styleable.SlidingDrawerLayout_slideDirection,
					0);
			if (d >= 0 && d < SlideDirection.values().length) {
				this.direction = SlideDirection.values()[d];
			} else {
				this.direction = SlideDirection.RIGHT;
				// throw new Exception("Slide Direction should be 0-4");
			}
			this.contentLayoutId = arr.getResourceId(
					R.styleable.SlidingDrawerLayout_contentLayoutId, -1);
			int drawerSize = (int) arr.getDimensionPixelSize(
					R.styleable.SlidingDrawerLayout_drawerSize, 0);

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.position[i][j] *= drawerSize;
					this.margin[i][j] *= drawerSize;
				}
			}
		}

		arr.recycle();
	}

	public ISlidingDrawerListener getListener() {
		return listener;
	}

	public void setListener(ISlidingDrawerListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mainView = (ViewGroup) getChildAt(0);
	}

	public SlideDirection getDirection() {
		return direction;
	}

	public void setDirection(SlideDirection direction) {
		this.direction = direction;
	}

	public void open() {
		if (isOpen == false) {
			new Handler().post(this);
			mainView.startAnimation(_getAnimation(1, this));
		}
	}

	public void close() {
		this.close(true);
		// if (isOpen) {
		// _removeOverlay();
		// /**
		// * toX and toY are relative position
		// */
		// mainView.startAnimation(_getAnimation(0, this));
		// if (this.listener != null) {
		// this.listener.onClose();
		// }
		// }
	}

	public void close(boolean animate) {
		if (!isOpen) {
			return;
		}
		_removeOverlay();
		if (animate) {
			/**
			 * toX and toY are relative position
			 */
			mainView.startAnimation(_getAnimation(0, this));

		} else {
			_setMargin(0, 0, 0, 0, false);
			/**
			 * Remove sliding panel content
			 */
			this.removeViewAt(0);
			isOpen = false;
		}
		if (this.listener != null) {
			this.listener.onClose();
		}
	}

	public void toggle() {
		if (isOpen) {
			close();
		} else {
			open();
		}
	}

	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		super.onAnimationEnd();
		Log.d(TAG, "===============================> Animation End");

		mainView.clearAnimation();
		if (isOpen) {
			_setMargin(0, 0, 0, 0, false);
			/**
			 * Remove sliding panel content
			 */
			this.removeViewAt(0);
			isOpen = false;
		} else {
			int[] f = this.margin[this.direction.toInt()];
			_setMargin(f[0], f[1], f[2], f[3], true);
			isOpen = true;
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	private void _setMargin(int left, int top, int right, int bottom,
			boolean addOverlay) {
		RelativeLayout.LayoutParams position = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		position.setMargins(left, top, right, bottom);
		mainView.setLayoutParams(position);
		if (addOverlay) {
			_addOverlay(left, top, right, bottom);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.close();
			break;
		}
		return true;
	}

	private void _addOverlay(int left, int top, int right, int bottom) {
		View overlay = new View(context);
		overlay.setBackgroundColor(0x33000000);
		overlay.setOnTouchListener(this);
		this.addView(overlay);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(left, top, right, bottom);
		overlay.setLayoutParams(lp);
	}

	private void _removeOverlay() {
		this.removeViewAt(this.getChildCount() - 1);
	}

	/*
	 * @flag 0 - close, 1 - open
	 */
	private TranslateAnimation _getAnimation(int flag,
			AnimationListener listener) {
		float data[] = (flag == 1) ? this._getDataForOpen() : this
				._getDataForClose();
		TranslateAnimation anim = new TranslateAnimation(data[0], data[1],
				data[2], data[3]);
		anim.setFillEnabled(true);
		anim.setFillAfter(true);
		anim.setDuration(250);
		anim.setAnimationListener(listener);
		return anim;
	}

	@Override
	public void run() {

		if (this.contentLayoutId == -1) {
			return;
		}
		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup contentView = (ViewGroup) inflater.inflate(contentLayoutId,
				this, false);
		this.addView(contentView, 0);

		RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) contentView
				.getLayoutParams();
		switch (this.direction) {
		case LEFT:
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			break;
		case RIGHT:
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			break;
		case TOP:
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			break;
		case BOTTOM:
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			break;
		}
		contentView.setLayoutParams(lp);
		if (this.listener != null) {
			this.listener.onOpen();
		}

	}

	private float[] _getDataForOpen() {
		return this.position[this.direction.toInt()];
	}

	private float[] _getDataForClose() {
		int t = (this.direction.toInt() == 0 || this.direction.toInt() == 2) ? 2
				: 4;
		return this.position[t - this.direction.toInt()];
	}

}
