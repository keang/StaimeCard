package com.kakada.staimecard;

import java.util.Random;

import com.kakada.staimecard.R;
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
import android.widget.Toast;

public abstract class Card extends View {
	//TODO: use factory pattern;
	//TODO: take care of different screen sizes
	public static String TAG="card.java";
	private Bitmap shop_cover_image;
	private String shop_name;
	private String reward_name;
	private Bitmap shopImage;
	private int points_to_reward;

	private Context mContext;
	private int collapsedCardHeight;
	private int cardWidth;
	private boolean finishedScalingSizes=false;
	private GestureDetector mDetector;


	private Paint cardBackgroundPaint;
	private Paint shopImagePaint;
	private Paint requiredPointPaint;
	private Paint mainTextPaint;
	private int mainTextColor;
	private int shopNameColor;
	private int cardBackgroundColor;
	private ShapeDrawable cardBackgroundDrawable;
	RectF collapsedCardShape;
	
	/*
	 * the following are initalized as ratio to cardHeight. 
	 * call scaleSizes() at the start of onDraw() to get to actual scale
	 */
	private float lineSpacingFactor = 0.02f;
	private float requiredPointTextSizeFactor = 1.0f;
	
	public Card(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
		final TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.Card, 0, 0);
		try {
			setCardBackgroundColor(attributes.getColor(
					R.styleable.Card_background_color, Color.parseColor("#CCF2F2F2")));//default to whitish translucent colour
			//setShopNameColor(attributes.getColor(
				//	R.styleable.Card_background_color, Color.parseColor("#1A1A1A")));
			setMainTextColor(attributes.getColor(
					R.styleable.Card_text_color, Color.parseColor("#1A1A1A")));//default to dark grey
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
		cardBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

		//Shop bitmap:
		int i = new Random().nextInt(3);
		switch (i){
			case 1:
				shopImage = BitmapFactory.decodeResource(getContext().getResources(), 
						   R.drawable.shop_cover_image);
				break;
			case 2:
				shopImage = BitmapFactory.decodeResource(getContext().getResources(), 
						   R.drawable.shop_cover_image_2);
				break;
			default:
				shopImage = BitmapFactory.decodeResource(getContext().getResources(), 
						   R.drawable.shop_cover_image_3);				
			
		}
		
		collapsedCardShape = new RectF(0,0, 0, 0);
		
		shopImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		shopImagePaint.setFilterBitmap(true);
		shopImagePaint.setStyle(Paint.Style.FILL);

		
		//text paint:
		requiredPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainTextPaint.setColor(mainTextColor);
		
		// Create a gesture detector to handle onTouch messages
        mDetector = new GestureDetector(Card.this.getContext(), new GestureListener());
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
		//Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));
		/*
		 * credit card ratio 85.6mm:54mm = 1.585
		 */
		float widthHeightRatio = (float) 1.58; 
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int chosenWidth = chooseDimension(widthMode, widthSize);
		
		collapsedCardHeight=(int) (0.45f*chosenWidth/widthHeightRatio);
			
	
		setMeasuredDimension(chosenWidth, collapsedCardHeight);
		cardWidth = chosenWidth;
	}
	
	private void scaleSizes() {
		if(!finishedScalingSizes){
			requiredPointPaint.setTextSize(requiredPointTextSizeFactor*collapsedCardHeight);
			//save card dimensions

			collapsedCardShape.set(0, 0, cardWidth, collapsedCardHeight);
		} 
		finishedScalingSizes = true;
		BitmapShader shopImageShader = new BitmapShader(shopImage,
			    Shader.TileMode.CLAMP, 
			    Shader.TileMode.CLAMP);
		Matrix localmatrix = new Matrix();
		localmatrix.setScale(cardWidth*1.0f/shopImage.getWidth(), collapsedCardHeight*1.0f/shopImage.getHeight());
		shopImageShader.setLocalMatrix(localmatrix);
		shopImagePaint.setShader(shopImageShader);
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
		scaleSizes();
		
		//draw card background to take care of edge offsets
		canvas.drawRoundRect(collapsedCardShape,0, 0, cardBackgroundPaint);
		
		//draw shop image
		canvas.drawRoundRect(collapsedCardShape, 0,0, shopImagePaint);
		
		//draw required point number text
		requiredPointPaint.setStyle(Style.FILL);
		requiredPointPaint.setColor(cardBackgroundColor);
		canvas.drawText(Integer.toString(points_to_reward),lineSpacingFactor*collapsedCardHeight,collapsedCardHeight,requiredPointPaint);
		requiredPointPaint.setStyle(Style.STROKE);
		requiredPointPaint.setStrokeWidth(1f);
		requiredPointPaint.setColor(Color.GRAY);
		canvas.drawText(Integer.toString(points_to_reward),lineSpacingFactor*collapsedCardHeight,collapsedCardHeight,requiredPointPaint);
			
	}



	/*
	 * Reads database and load the details of the cards
	 * During development i'm gonna hard code it	
	 */
		private void loadCardResources() {
			//hard coding Brown's cover image:
			shop_cover_image = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.shop_cover_image);
			shop_name = "Brown Coffee and Bakery";
			points_to_reward = 5;
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
                Log.i("ontouchevent", "u lifted finger from picture");
                onTouchEventCallback();
                
                result = true;
            }
        }
        return result;
    }
    
	public abstract void onTouchEventCallback();
/****************Setters and getters*****************/
	
	
	public Bitmap getShop_cover_image() {
		return shop_cover_image;
	}

	public void setShop_cover_image(Bitmap shop_cover_image) {
		this.shop_cover_image = shop_cover_image;
	}

	public String getShop_name() {
		return shop_name;
		
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public int getNext_shop_reward_require_point() {
		return points_to_reward;
	}

	public void setPoints_to_reward(int p) {
		this.points_to_reward = p;
	}

	public String getReward_name() {
		return reward_name;
	}

	public void setReward_name(String reward_name) {
		this.reward_name = reward_name;
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

/************Endof setters and getters***************/


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            
            return true;
        }
    }


}
