package com.kakada.staimecard;

import com.kakada.staimecard.R;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class CardExpanse extends View {
	private static final int DEFAULT_CARD_WIDTH = 300;
	//TODO: use factory pattern;
	//TODO: take care of different screen sizes
	public static String TAG="cardexpanse.java";
	private Bitmap shop_cover_image;
	private String shop_name;
	private String reward_name;
	//private int next_shop_reward_require_point;

	private int fullCardHeight;
	private int fullCardWidth;
	private int drawHeight;
	private int collapsedHeight;
	private int rowCount;
	private GestureDetector mDetector;
	public boolean isShown=true;
	
	private Paint cardBackgroundPaint;
	private Paint shopNamePaint;
	private Paint shopImagePaint;
	private Paint requiredPointPaint;
	private Paint mainTextPaint;
	private int mainTextColor;
	private int shopNameColor;
	private int cardBackgroundColor;

	RectF cardExpanseLocation;
	private Context mContext;
	
	/*
	 * the following are initalized as ratio to cardHeight. 
	 * call scaleSizes() at the start of onDraw() to get to actual scale
	 */
	private float rightMarginFactor = 0.1f;
	private float lineSpacingFactor = 0.04f;
	public static float SHOP_NAME_TEXT_SIZE_FACTOR = 0.08f;
	private float requiredPointTextSizeFactor = 0.4f;
	
	public CardExpanse(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
		final TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.Card, 0, 0);
		try {
			setCardBackgroundColor(attributes.getColor(
					R.styleable.Card_background_color, Color.parseColor("#CCF2F2F2")));//default to whitish traslucent color
			setShopNameColor(attributes.getColor(
					R.styleable.Card_background_color, Color.parseColor("#4F4F4F")));//default to dark grey
			setMainTextColor(attributes.getColor(
					R.styleable.Card_text_color, Color.parseColor("#4F4F4F")));//default to dark grey
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "constructor error");
			e.printStackTrace();
		}

		attributes.recycle();
		init();
		loadCardResources();
		
	}
	
	private void init(){
		//card background
		cardBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cardBackgroundPaint.setColor(getCardBackgroundColor());
		cardBackgroundPaint.setStyle(Paint.Style.FILL);
		cardBackgroundPaint.setMaskFilter(new BlurMaskFilter(200, BlurMaskFilter.Blur.NORMAL));
		

		//Shop name:
		shopNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shopNamePaint.setColor(shopNameColor);
		shopNamePaint.setTextAlign(Paint.Align.CENTER);
		
		//text paint:
		mainTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainTextPaint.setStyle(Style.FILL);
		mainTextPaint.setColor(mainTextColor);
		
		cardExpanseLocation = new RectF(0, 0, 0, 0);
		
		// Create a gesture detector to handle onTouch messages
        mDetector = new GestureDetector(CardExpanse.this.getContext(), new GestureListener());
        
        //initially hide the card expanse by:
        drawHeight = 0;
        
        int MY_DIP_VALUE = 16; //5dp

        int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                      MY_DIP_VALUE, getResources().getDisplayMetrics());
        shopNamePaint.setTextSize(pixel);
        mainTextPaint.setTextSize(pixel);
	}
		
	
	@Override
	/*
	 * onMeasure will be called every step of the animation.
	 * Animator updates drawHeight, onMeasure updates the measurement,
	 * then animator call requestlayout() to push adjacent cards below as well.
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Log.i(TAG, "width spec: " + MeasureSpec.toString(widthMeasureSpec));
		//Log.i(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));
		/*
		 * credit card ratio 85.6mm:54mm = 1.585
		 */
		float widthHeightRatio = (float) 1.58; 
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		//fullCardHeight=(int) ((float)chosenWidth/widthHeightRatio);

		fullCardWidth = chosenWidth;
		
		setMeasuredDimension(chosenWidth, drawHeight);
		//Log.d(TAG, "Height full: " + MeasureSpec.toString(fullCardHeight));
		//Log.d(TAG, "Height drawn: " + MeasureSpec.toString(drawHeight));

}
	/*
	 * To be called at the start of onDraw() to correct the scale.
	 * as animator updates drawHeight, all other sizes are updated accordingly
	 */
	private void scaleSizes() {
		//shopNamePaint.setTextSize(SHOP_NAME_TEXT_SIZE_FACTOR*drawHeight);
		//mainTextPaint.setTextSize(SHOP_NAME_TEXT_SIZE_FACTOR*drawHeight);
		
		//save draw dimensions. Expanse background need to start slightly
		//before the halfway mark, to cover the rounded edge of the collapsed card

		cardExpanseLocation.set(0,0,fullCardWidth, drawHeight);
	}
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			//in case there's no size specified, default to:
			return DEFAULT_CARD_WIDTH;
		} 
	}
	
	@Override 	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);	
		scaleSizes();
		
		//draw expanse background
		canvas.drawRoundRect(cardExpanseLocation, 0, 0 , cardBackgroundPaint);
		
		//Log.d(TAG, "draw text from y: " + Float.toString(getWidth()-rightMarginFactor));
		
		//draw shop name. 0.45 is the ratio of collapsed card to full card
		//canvas.drawText(shop_name, getWidth()/2, 
		//				(lineSpacingFactor+SHOP_NAME_TEXT_SIZE_FACTOR+0.45f)*drawHeight, shopNamePaint);
						
		//draw reward name. 0.85 to push the reward description down a bit. to be updated.
		canvas.drawText(reward_name,getWidth()/2, 
								(1-lineSpacingFactor)*drawHeight, shopNamePaint);
		
		
	}



	/*
	 * Reads database and load the details of the cards
	 * During development i'm gonna hard code it	
	 */
		private void loadCardResources() {
			//hard coding Brown's cover image:
			//shop_cover_image = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.shop_cover_image);
			shop_name = "Brown Coffee and Bakery";
			reward_name = "Free latte of choice";	
		}
		
		
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpret this event
        boolean result = mDetector.onTouchEvent(event);

        // If the GestureDetector doesn't want this event, do some custom processing.
        // This code just tries to detect when the user is done scrolling by looking
        // for ACTION_UP events.
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // User is done scrolling, it's now safe to do things like autocenter
                //Log.i("ontouchevent", "u lifted finger from card expanse");
                onTouchEventCallback();
            }
        }
        return result;
    }
    
    public abstract void onTouchEventCallback();
    
    /*
     * collapse animation.
     * drawHeight animates from fullHeight to 0;
     */
    public void collapseNow(){
    	
    	AnimatorUpdateListener ls = new AnimatorUpdateListener(){
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				invalidate();
				requestLayout();
			
			}
		};
    	ObjectAnimator cardCloseAnim = ObjectAnimator.ofInt(this, "drawHeight", fullCardHeight, 0);
        cardCloseAnim.setDuration(200);
        cardCloseAnim.addUpdateListener(ls);
        cardCloseAnim.start();
        isShown=false;
    }
    
    /*
     * expand animation
     * drawHeight animates from 0 to fullHeight;
     */
    public void expandNow(){
    	isShown=true;
    	AnimatorUpdateListener ls = new AnimatorUpdateListener(){
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub			
				invalidate();
				requestLayout();
			}
		};
		

		invalidate();
		requestLayout();
		//Log.i("Before ani", "Before ani height = " + fullCardHeight);
    	ObjectAnimator cardExpandAnim = ObjectAnimator.ofInt(this, "drawHeight", 0, fullCardHeight);
        cardExpandAnim.setDuration(200);
        cardExpandAnim.addUpdateListener(ls);
        cardExpandAnim.start();  
        
    }
    
   
/****************Setters and getters*****************/
	
	public Bitmap getShop_cover_image() {
		return shop_cover_image;
	}

	public void setShop_cover_image(Bitmap shop_cover_image) {
		this.shop_cover_image = shop_cover_image;
		   invalidate();
		   requestLayout();
	}

	public String getShop_name() {
		return shop_name;
		
	}

	public int getDrawHeight() {
		return drawHeight;
	}
	public void setDrawHeight(int drawHeight) {
		this.drawHeight = drawHeight;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public String getReward_name() {
		return reward_name;
	}

	public void setReward_name(String reward_name) {
		this.reward_name = reward_name;
		   invalidate();
		   requestLayout();
	}
	public int getMainTextColor() {
		return mainTextColor;
	}
	public void setMainTextColor(int mainTextColor) {
		this.mainTextColor = mainTextColor;
	}
	public int getShopNameColor() {
		return shopNameColor;
	}
	public void setShopNameColor(int shopNameColor) {
		this.shopNameColor = shopNameColor;
	}
	public int getCardBackgroundColor() {
		return cardBackgroundColor;
	}
	public void setCardBackgroundColor(int cardBackgroundColor) {
		this.cardBackgroundColor = cardBackgroundColor;
	}
	public void setHeight(int h){
		fullCardHeight = h;
		//Log.i("heightfull", Integer.toString(h));
	}
/*************Endof setters and getters*************/

/*Convenience functions*/
	
	/*
	 * Gesture detector to identfy tap action to call animations
	 */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            
            return true;
        }
    }

    /*
     * 
     */
	public int getTopMargin() {
		return (int)(lineSpacingFactor+SHOP_NAME_TEXT_SIZE_FACTOR + lineSpacingFactor)*drawHeight;
	}

	public void setStaimeRow(int row) {
		if(fullCardWidth==0) fullCardWidth=DEFAULT_CARD_WIDTH;
		rowCount = row;
		SHOP_NAME_TEXT_SIZE_FACTOR=((1f/(7.96f+row*2.47f)));
		int rewardNameTargetHeight = (int) ((lineSpacingFactor+0.05)*fullCardWidth);
		setHeight( (int) rowCount*(fullCardWidth/8) +  collapsedHeight + rewardNameTargetHeight);

	}

	public void setCollapsedHeight(int h) {collapsedHeight = h;	}
	public void setCardWidth (int w){ fullCardWidth = w;}

}
