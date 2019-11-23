/**
 * @author Raghav Sood
 * @version 1
 * @date 26 January, 2013
 */
package com.lierda.widget;

import com.lierda.utils.LogUtil;
import com.lierda.wificontroller.kapage.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * The Class CircularSeekBar.
 */
public class ArcSeekBar extends View {

	/** The context */
	private Context mContext;

	/** The listener to listen for changes */
	private OnArcSeekChangeListener mListener;
	private OnArcSeekProgressChangeListener mListenerProgress;
	
	/** The color of the progress ring */
	private Paint circleColor;

	/** the color of the inside circle. Acts as background color */
	private Paint innerColor;

	/** The progress circle ring background */
	private Paint circleRing;

	/** The angle of progress */
	private int angle = 0;

	/** The start angle (12 O'clock */
	private int startAngle = 15;
	private int middleAngle = 150;

	/** The width of the progress ring */
	private int barWidth = 100;
	private int offsetWidth = 20;

	/** The width of the view */
	private int width;

	/** The height of the view */
	private int height;

	/** The maximum progress amount */
	private int maxProgress = 100;

	/** The current progress */
	private int progress = 0;

	/** The progress percent */
	private int progressPercent;

	/** The radius of the inner circle */
	private float innerRadius;

	/** The radius of the outer circle */
	private float outerRadius;

	/** The circle's center X coordinate */
	private float cx;

	/** The circle's center Y coordinate */
	private float cy;

	/** The left bound for the circle RectF */
	private float left;

	/** The right bound for the circle RectF */
	private float right;

	/** The top bound for the circle RectF */
	private float top;

	/** The bottom bound for the circle RectF */
	private float bottom;

	/** The X coordinate for the top left corner of the marking drawable */
	private float dx;

	/** The Y coordinate for the top left corner of the marking drawable */
	private float dy;

	/** The X coordinate for 12 O'Clock */
	private float startPointX;

	/** The Y coordinate for 12 O'Clock */
	private float startPointY;

	/**
	 * The X coordinate for the current position of the marker, pre adjustment
	 * to center
	 */
	private float markPointX;

	/**
	 * The Y coordinate for the current position of the marker, pre adjustment
	 * to center
	 */
	private float markPointY;

	/**
	 * The adjustment factor. This adds an adjustment of the specified size to
	 * both sides of the progress bar, allowing touch events to be processed
	 * more user friendlily (yes, I know that's not a word)
	 */
	private float adjustmentFactor = 3;

	/** The progress mark when the view isn't being progress modified */
	private Bitmap progressMark;

	/** The progress mark when the view is being progress modified. */
	private Bitmap progressMarkPressed;

	/** The flag to see if view is pressed */
	private boolean IS_PRESSED = false;

	/**
	 * The flag to see if the setProgress() method was called from our own
	 * View's setAngle() method, or externally by a user.
	 */
	private boolean CALLED_FROM_ANGLE = false;

	/** The rectangle containing our circles and arcs. */
	private RectF rect = new RectF();
	private RectF rectInner = new RectF();

	{
		mListener = new OnArcSeekChangeListener() {

			@Override
			public void onProgressChange(ArcSeekBar view, int newProgress) {

			}
		};
		
		
		mListenerProgress = new OnArcSeekProgressChangeListener() {

			@Override
			public void onProgressChange(ArcSeekBar view, int newProgress) {

			}
		};

		circleColor = new Paint();
		innerColor = new Paint();
		circleRing = new Paint();

		circleColor.setColor(Color.TRANSPARENT); // Set default Color.parseColor("#ff33b5e5")
																// progress
																// color to holo
																// blue.
		innerColor.setColor(Color.TRANSPARENT); // Set default background color to
											// black
		circleRing.setColor(Color.TRANSPARENT);// Set default background color to Gray

		circleColor.setAntiAlias(true);
		innerColor.setAntiAlias(true);
		circleRing.setAntiAlias(true);

		circleColor.setStrokeWidth(5);
		innerColor.setStrokeWidth(5);
		circleRing.setStrokeWidth(5);

		circleColor.setStyle(Paint.Style.FILL);
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public ArcSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initDrawable();
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public ArcSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initDrawable();
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 */
	public ArcSeekBar(Context context) {
		super(context);
		mContext = context;
		initDrawable();
	}

	/**
	 * Inits the drawable.
	 */
	public void initDrawable() {
		clear();
		progressMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.select);
		progressMarkPressed = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.select);
	}
	
	public void clear(){
		if(progressMark!= null && !progressMark.isRecycled())
			progressMark.recycle();
		progressMark = null;
		if(progressMarkPressed!= null && !progressMarkPressed.isRecycled())
			progressMarkPressed.recycle();
		progressMarkPressed = null;
		System.gc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = getWidth() - offsetWidth; // Get View Width
		height = getHeight()- offsetWidth;// Get View Height

		int size = (width > height) ? height : width; // Choose the smaller
														// between width and
														// height to make a
														// square

		cx = getWidth() / 2; // Center X for circle
		cy = getHeight() / 2; // Center Y for circle
		outerRadius = size / 2; // Radius of the outer circle

		innerRadius = outerRadius - barWidth; // Radius of the inner circle

		left = cx - outerRadius; // Calculate left bound of our rect
		right = cx + outerRadius;// Calculate right bound of our rect
		top = cy - outerRadius;// Calculate top bound of our rect
		bottom = cy + outerRadius;// Calculate bottom bound of our rect

		startPointX = (float) (cx + Math.cos(startAngle* Math.PI / 180) * cx); // 3 O'clock X coordinate
		startPointY = (float) (cy + Math.sin(startAngle* Math.PI / 180) * cy);// 3 O'clock Y coordinate
		
		LogUtil.printInfo(Float.toString(startPointX));
		LogUtil.printInfo(Float.toString(startPointY));
		
		markPointX = startPointX - offsetWidth;// Initial locatino of the marker X coordinate
		markPointY = startPointY;// Initial locatino of the marker Y coordinate

		rect.set(left, top, right, bottom); // assign size to rect
		
		rectInner.set(left+ barWidth, top, right- barWidth, bottom- barWidth); // assign size to rect
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		dx = getXFromAngle();
		dy = getYFromAngle();

		//canvas.drawCircle(cx, cy, innerRadius, innerColor);
		drawMarkerAtProgress(canvas);
		//canvas.drawCircle(cx, cy, outerRadius, circleRing);
		
		//canvas.drawArc(rect, startAngle, middleAngle, true, circleRing);		
		//canvas.drawArc(rect, startAngle, angle, true, circleColor);
		//canvas.drawArc(rectInner, startAngle, middleAngle, true, innerColor);
		
		canvas.drawArc(rect, startAngle, middleAngle, true, circleRing);		
		canvas.drawArc(rect, startAngle+angle, middleAngle-(angle), true, circleColor);
		canvas.drawArc(rectInner, startAngle, middleAngle, true, innerColor);
		
		

		super.onDraw(canvas);
	}

	/**
	 * Draw marker at the current progress point onto the given canvas.
	 * 
	 * @param canvas
	 *            the canvas
	 */
	public void drawMarkerAtProgress(Canvas canvas) {
		
		if(angle > middleAngle/2){
			dx = (float) (cx + Math.cos((startAngle+angle)* Math.PI / 180) * cx) - offsetWidth + progressMark.getWidth()/4;
		}
		else
			dx = (float) (cx + Math.cos((startAngle+angle)* Math.PI / 180) * cx) - offsetWidth - progressMark.getWidth()/5;
		dy = (float) (cy + Math.sin((startAngle+angle)* Math.PI / 180) * cy) - offsetWidth - progressMark.getHeight()/2 ;
		
		if (IS_PRESSED) {
			canvas.drawBitmap(progressMarkPressed, dx, dy, null);
		} else {
			canvas.drawBitmap(progressMark, dx, dy, null);
		}
	}

	/**
	 * Gets the X coordinate of the arc's end arm's point of intersection with
	 * the circle
	 * 
	 * @return the X coordinate
	 */
	public float getXFromAngle() {
		int size1 = progressMark.getWidth();
		int size2 = progressMarkPressed.getWidth();
		int adjust = (size1 > size2) ? size1 : size2;
		float x = markPointX - (adjust / 2);
		return x;
	}

	/**
	 * Gets the Y coordinate of the arc's end arm's point of intersection with
	 * the circle
	 * 
	 * @return the Y coordinate
	 */
	public float getYFromAngle() {
		int size1 = progressMark.getHeight();
		int size2 = progressMarkPressed.getHeight();
		int adjust = (size1 > size2) ? size1 : size2;
		float y = markPointY - (adjust / 2);
		return y;
	}

	/**
	 * Get the angle.
	 * 
	 * @return the angle
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * Set the angle.
	 * 
	 * @param angle
	 *            the new angle
	 */
	public void setAngle(int angle) {
		this.angle = angle;
		float donePercent = (((float) this.angle) / middleAngle) * 100;
		float progress = (donePercent / 100) * getMaxProgress();
		setProgressPercent(Math.round(donePercent));
		CALLED_FROM_ANGLE = true;
		setProgress(Math.round(progress));
	}

	/**
	 * Sets the seek bar change listener.
	 * 
	 * @param listener
	 *            the new seek bar change listener
	 */
	public void setSeekBarChangeListener(OnArcSeekChangeListener listener) {
		mListener = listener;
	}
	
	public void setSeekBarProgressChangeListener(OnArcSeekProgressChangeListener listener){
		mListenerProgress = listener;
	}
	public OnArcSeekProgressChangeListener getSeekBarProgressChangeListener() {
		return mListenerProgress;
	}

	/**
	 * Gets the seek bar change listener.
	 * 
	 * @return the seek bar change listener
	 */
	public OnArcSeekChangeListener getSeekBarChangeListener() {
		return mListener;
	}

	/**
	 * Gets the bar width.
	 * 
	 * @return the bar width
	 */
	public int getBarWidth() {
		return barWidth;
	}

	/**
	 * Sets the bar width.
	 * 
	 * @param barWidth
	 *            the new bar width
	 */
	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	/**
	 * The listener interface for receiving onSeekChange events. The class that
	 * is interested in processing a onSeekChange event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>setSeekBarChangeListener(OnSeekChangeListener)<code> method. When
	 * the onSeekChange event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see OnSeekChangeEvent
	 */
	public interface OnArcSeekChangeListener {

		/**
		 * On progress change.
		 * 
		 * @param view
		 *            the view
		 * @param newProgress
		 *            the new progress
		 */
		public void onProgressChange(ArcSeekBar view, int newProgress);
	}
	
	public interface OnArcSeekProgressChangeListener {

		/**
		 * On progress change.
		 * 
		 * @param view
		 *            the view
		 * @param newProgress
		 *            the new progress
		 */
		public void onProgressChange(ArcSeekBar view, int newProgress);
	}

	/**
	 * Gets the max progress.
	 * 
	 * @return the max progress
	 */
	public int getMaxProgress() {
		return maxProgress;
	}

	/**
	 * Sets the max progress.
	 * 
	 * @param maxProgress
	 *            the new max progress
	 */
	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	/**
	 * Gets the progress.
	 * 
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * Sets the progress.
	 * 
	 * @param progress
	 *            the new progress
	 */
	public void setProgress(int progress) {
		if (this.progress != progress) {
			this.progress = progress;
			if (!CALLED_FROM_ANGLE) {				
				int newPercent = (this.progress* 100 / this.maxProgress) ;
				int newAngle = (newPercent * middleAngle / 100);
				this.setAngle(newAngle);
				this.setProgressPercent(newPercent);
				//LogUtil.printInfo("setProgress:newAngle="+Integer.toString(newAngle)+"progress="+Integer.toString(progress));
			}
			mListenerProgress.onProgressChange(this, this.getProgress());
			CALLED_FROM_ANGLE = false;
		}
		
		//this.invalidate();
	}

	/**
	 * Gets the progress percent.
	 * 
	 * @return the progress percent
	 */
	public int getProgressPercent() {
		return progressPercent;
	}

	/**
	 * Sets the progress percent.
	 * 
	 * @param progressPercent
	 *            the new progress percent
	 */
	public void setProgressPercent(int progressPercent) {
		this.progressPercent = progressPercent;
	}

	/**
	 * Sets the ring background color.
	 * 
	 * @param color
	 *            the new ring background color
	 */
	public void setRingBackgroundColor(int color) {
		circleRing.setColor(color);
	}

	/**
	 * Sets the back ground color.
	 * 
	 * @param color
	 *            the new back ground color
	 */
	public void setBackGroundColor(int color) {
		innerColor.setColor(color);
	}

	/**
	 * Sets the progress color.
	 * 
	 * @param color
	 *            the new progress color
	 */
	public void setProgressColor(int color) {
		circleColor.setColor(color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		boolean up = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//LogUtil.printInfo("arc ACTION_DOWN");
			moved(x, y, up);
			break;
		case MotionEvent.ACTION_MOVE:
			moved(x, y, up);
			break;
		case MotionEvent.ACTION_UP:
		{
			up = true;
			moved(x, y, up);
			mListener.onProgressChange(this, this.getProgress());
			break;
		}
		}
		return true;
	}

	/**
	 * Moved.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param up
	 *            the up
	 */
	private void moved(float x, float y, boolean up) {
		float distance = (float) Math.sqrt(Math.pow((x - cx), 2) + Math.pow((y - cy), 2));
		//distance < outerRadius + adjustmentFactor && distance > innerRadius - adjustmentFactor &&
		if ( !up) {
			
			//if(y < startPointY){ 
			//	//top half circular
			//	IS_PRESSED = false;
			//	invalidate();
			//}
			//else
			{
				IS_PRESSED = true;
	
				markPointX = x;
				markPointY = y;
	
				float degrees = (float) ((float) ((Math.toDegrees(Math.atan2(x - cx, cy - y)) - 90.0 - startAngle  + 360.0)) % 360.0);
				// and to make it count 0-180
				if (degrees < 0) {
					degrees += 2 * Math.PI;
				}
				
				//LogUtil.printInfo("degrees="+Float.toString(degrees));
				if(degrees < 0 || (degrees > 335 )){
					degrees = (float) ( 0);
				}
				else if(degrees > ((middleAngle) )){
					degrees = (float) ((middleAngle) );
				}
				
				setAngle(Math.round(degrees));
				
				
				
				invalidate();
			}

		} else {
			IS_PRESSED = false;
			invalidate();
		}

	}

	/**
	 * Gets the adjustment factor.
	 * 
	 * @return the adjustment factor
	 */
	public float getAdjustmentFactor() {
		return adjustmentFactor;
	}

	/**
	 * Sets the adjustment factor.
	 * 
	 * @param adjustmentFactor
	 *            the new adjustment factor
	 */
	public void setAdjustmentFactor(float adjustmentFactor) {
		this.adjustmentFactor = adjustmentFactor;
	}
}
