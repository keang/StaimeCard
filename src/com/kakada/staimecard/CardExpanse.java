package com.kakada.staimecard;

import com.kakada.staimecard.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

public class CardExpanse extends View {
	//TODO: use factory pattern;
	//TODO: take care of different screen sizes
	public static String TAG="card.java";
	private Bitmap shop_cover_image;
	private String shop_name;
	private String reward_name;
	private String required_point_text;
	private Bitmap shopImage;
	private int next_shop_reward_require_point;

	private Context mContext;
	private int fullCardHeight;
	private int fullCardWidth;
	private int drawHeight;
	private boolean finishedScalingSizes=false;
	private GestureDetector mDetector;
	public boolean isShown=true;
	
	private Paint cardBackgroundPaint;
	private Paint shopNamePaint;
	private Paint shopImagePaint;
	private Paint requiredPointPaint;
	private Paint mainTextPaint;
	private int mainTextColor;
	private int shopNameColor;
	private int tertiaryColor;
	private int cardBackgroundColor;
	private ShapeDrawable cardBackgroundDrawable;
	RectF cardShape;
	
	/*
	 * the following are initalized as ratio to cardHeight. 
	 * call scaleSizes() at the start of onDraw() to get to actual scale
	 */
	private float rightMarginFactor = 0.1f;
	private float lineSpacingFactor = 0.02f;
	private float shopNameTextSizeFactor = 0.08f;
	private float requiredPointTextSizeFactor = 0.4f;
	
	public CardExpanse(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
		final TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.Card, 0, 0);
		try {
			setCardBackgroundColor(attributes.getColor(
					R.styleable.Card_background_color, Color.parseColor("#E0999999")));
			setShopNameColor(attributes.getColor(
					R.styleable.Card_background_color, Color.parseColor("#1A1A1A")));
			setMainTextColor(attributes.getColor(
					R.styleable.Card_text_color, Color.parseColor("#1A1A1A")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "constructor error");
			e.printStackTrace();
		}

		attributes.recycle();
		init();
		loadCardResources();
		
	}
	public CardExpanse(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private void init(){
		//card background
		cardBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cardBackgroundPaint.setColor(getCardBackgroundColor());
		cardBackgroundPaint.setStyle(Paint.Style.FILL);
		//setBackgroundColor(Color.YELLOW);
		//cardBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
		
		
		//cardBackgroundDrawable = new ShapeDrawable(new RoundRectShape(new float[]{10,10,10,10,0,0,0,0,}, null, null));
		//cardBackgroundDrawable.setBounds(0, 0, getWidth(), getHeight());

		//Shop name:
		shopNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shopNamePaint.setColor(shopNameColor);
		shopNamePaint.setTextAlign(Paint.Align.RIGHT);
		
		//Shop bitmap:
		shopImage = BitmapFactory.decodeResource(getContext().getResources(), 
				   R.drawable.shop_cover_image);
		
		shopImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shopImagePaint.setFilterBitmap(true);
		shopImagePaint.setStyle(Paint.Style.FILL);

		
		//required point paint:
		requiredPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		requiredPointPaint.setColor(mainTextColor);
		requiredPointPaint.setTextAlign(Paint.Align.LEFT);
		
		//text paint:
		mainTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainTextPaint.setStyle(Style.FILL);
		mainTextPaint.setColor(mainTextColor);
		
		// Create a gesture detector to handle onTouch messages
        mDetector = new GestureDetector(CardExpanse.this.getContext(), new GestureListener());
        
        //hide the card expanse by:
        drawHeight = 0;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));
		/*
		 * credit card ratio 85.6mm:54mm = 1.585
		 */
		float widthHeightRatio = (float) 1.58; 
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		fullCardHeight=(int) ((float)chosenWidth/widthHeightRatio);

		fullCardWidth = chosenWidth;
		//drawHeight = (int) ( 0.45f* fullCardHeight);
		setMeasuredDimension(chosenWidth, drawHeight);

}
	private void scaleSizes() {
		shopNamePaint.setTextSize(shopNameTextSizeFactor*drawHeight);
		requiredPointPaint.setTextSize(requiredPointTextSizeFactor*drawHeight);
		mainTextPaint.setTextSize(shopNameTextSizeFactor*drawHeight);
		
		BitmapShader shopImageShader = new BitmapShader(shopImage,
			    Shader.TileMode.CLAMP, 
			    Shader.TileMode.CLAMP);
		Matrix localmatrix = new Matrix();
		localmatrix.setScale(fullCardWidth*1.0f/shopImage.getWidth(), fullCardHeight*0.45f/shopImage.getHeight());
		shopImageShader.setLocalMatrix(localmatrix);
		shopImagePaint.setShader(shopImageShader);
		
		//save draw dimensions
		cardShape = new RectF(0,fullCardHeight*2/5,fullCardWidth, drawHeight);
	}
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			//in case there's no size specified, default to:
			return 300;
		} 
	}
	
	@Override 	
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);	
		//requestLayout();
		scaleSizes();
		
		//draw pic corners
		canvas.drawRect(new RectF(0,fullCardHeight*2/5, fullCardWidth,fullCardHeight*0.45f), shopImagePaint);
		//draw cardshape
		canvas.drawRoundRect(cardShape,0.04f*drawHeight, 0.04f*drawHeight, cardBackgroundPaint);
		
		Log.d(TAG, "draw text from y: " + Float.toString(getWidth()-rightMarginFactor));
		//cardBackgroundDrawable.draw(canvas);
		
		//draw name
		
		canvas.drawText(shop_name, getWidth() - rightMarginFactor*drawHeight, 
						(lineSpacingFactor+shopNameTextSizeFactor+0.45f)*drawHeight, shopNamePaint);
						
		//draw reward name
		canvas.drawText(reward_name,lineSpacingFactor*drawHeight,drawHeight*0.85f, mainTextPaint);
		
		
	}



		/*
	 * Reads database and load the details of the cards
	 * During development i'm gonna hard code it	
	 */
		private void loadCardResources() {
			//hard coding Brown's cover image:
			shop_cover_image = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.shop_cover_image);
			shop_name = "Brown Coffee and Bakery";
			next_shop_reward_require_point = 5;
			reward_name = "Free latte of choice";	
			required_point_text="Tra more to:";
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
                Log.i("ontouchevent", "u lifted finger");
                if(isShown){
                	collapseNow();
                	
                }else{
                	expandNow();
                	
                }
				
                result = true;
            }
        }
        return result;
    }
   
    void collapseNow(){
    	
    	AnimatorUpdateListener ls = new AnimatorUpdateListener(){
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				measure(getLayoutParams().width, drawHeight);	
				invalidate();
				requestLayout();
			
			}
		};
    	ObjectAnimator cardCloseAnim = ObjectAnimator.ofInt(this, "drawHeight", fullCardHeight, (int)(0.0f*fullCardHeight));
        cardCloseAnim.setDuration(100);
        cardCloseAnim.addUpdateListener(ls);
        cardCloseAnim.start();

        invalidate();
        isShown=false;
    }
    void expandNow(){
    	isShown=true;
    	AnimatorUpdateListener ls = new AnimatorUpdateListener(){
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				measure(getLayoutParams().width, drawHeight);				
				invalidate();
				requestLayout();
			}
		};
    	ObjectAnimator cardExpandAnim = ObjectAnimator.ofInt(this, "drawHeight", (int)(0.0f*fullCardHeight), fullCardHeight);
        cardExpandAnim.setDuration(100);
        cardExpandAnim.addUpdateListener(ls);
         cardExpandAnim.start();

        
        
    }
    
    /*Setters and getters*/
	
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

	public int getNext_shop_reward_require_point() {
		return next_shop_reward_require_point;
	}

	public void setNext_shop_reward_require_point(int next_shop_reward_require_point) {
		this.next_shop_reward_require_point = next_shop_reward_require_point;
		   invalidate();
		   requestLayout();
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

/*Endof setters and getters*/


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            
            return true;
        }
    }

	public int getTopMargin() {
		return (int)(lineSpacingFactor+shopNameTextSizeFactor + lineSpacingFactor)*drawHeight;
	}


}
