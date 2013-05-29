package com.kakada.staimecard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

public class CardCompound extends FrameLayout{

	private Card collapsedCard;
	private CardExpanse cardExpanse;
	private GridLayout progressLayout;
	private LayoutParams cardLayoutParam;
	private int next_shop_reward_require_point;
	private int total_point; //total point the user already have so far for this card
	
	public CardCompound(final Context context, final AttributeSet attr) {
		
		super(context, attr);
		
		collapsedCard = new Card(context, attr) {	
			@Override
			public void onTapEvent() {
				
				if(cardExpanse == null){
					Log.i("onclick listener", "creating");
					cardExpanse = new CardExpanse(context, attr);
					cardExpanse.isShown=false;
					addView(cardExpanse,0, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					
					//adding the staimes:
					progressLayout = createProgressBar(context, attr);
					progressLayout.setLeft(3/16*collapsedCard.getWidth());
	            	addView(progressLayout, 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
	            	
	            	progressLayout.setVisibility(GONE);
				}
				
				
				if(cardExpanse!=null && cardExpanse.isShown){					
					//start flipping here instead!
					Log.i("onclick listener", "collapsing2");
                	cardExpanse.collapseNow();
                	cardExpanse.isShown=false;
	            	progressLayout.setVisibility(GONE);
	            }else{
	            	
	            	Log.i("onclick listener", "expanding");
 	            	cardExpanse.expandNow();
	            	cardExpanse.isShown=true;
	            	progressLayout.setVisibility(VISIBLE);
	            }
			};
		};
		
		
		
		cardLayoutParam = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(collapsedCard, cardLayoutParam);
		
		//some hardcodings:
		total_point = 6;
		next_shop_reward_require_point = 4;
		
	}
	protected GridLayout createProgressBar(Context context, AttributeSet attrs) {
		GridLayout gl = new GridLayout(context, attrs);
		gl.setRowCount(2);
		gl.setColumnCount(5);
		gl.setAlignmentMode(GridLayout.ALIGN_BOUNDS); 
		android.widget.GridLayout.LayoutParams param = new GridLayout.LayoutParams();
		for(int i=0; i<total_point; i++){
			ImageView checkedImage = new ImageView(context);
			checkedImage.setImageResource(R.drawable.staime_checked);
			gl.addView(checkedImage, collapsedCard.getWidth()/5, collapsedCard.getWidth()/8);
		}
		for(int j=0; j<next_shop_reward_require_point; j++){
			ImageView unCheckedImage = new ImageView(context);
			unCheckedImage.setImageResource(R.drawable.staime_unchecked);
			gl.addView(unCheckedImage, collapsedCard.getWidth()/5, collapsedCard.getWidth()/8);
		}
		return gl;
	}
	public CardCompound(Context context){
		super(context);
	}
	public class MarginAnim extends Animation {
	    int targetMargin;
	    View view;
	    boolean down;

	    public MarginAnim(View view, int targetMargin, boolean down) {
	        this.view = view;
	        this.targetMargin = targetMargin;
	        this.down = down;
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        int newMargin;
	        if (down) {
	            newMargin = (int) (targetMargin * interpolatedTime);
	        } else {
	            newMargin = (int) (targetMargin * (1 - interpolatedTime));
	        }
	        cardLayoutParam.setMargins(0, newMargin, 0, 0);
	        view.setLayoutParams(cardLayoutParam);
	        view.requestLayout();
	        Log.i("animation", "setting margin");
	    }

	    @Override
	    public void initialize(int width, int height, int parentWidth,
	            int parentHeight) {
	        super.initialize(width, height, parentWidth, parentHeight);
	    }

	    @Override
	    public boolean willChangeBounds() {
	        return true;
	    }
	}

}
