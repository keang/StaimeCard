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
	private int shopId;
	private Context mContext;
	private AttributeSet mAttr;
	private Card collapsedCard;
	private CardExpanse cardExpanse;
	private GridLayout progressLayout;
	private LayoutParams cardLayoutParam;
	private int next_shop_reward_require_point;
	private int total_point; //total point the user already have so far for this card
	private String reward_name;
	
	public CardCompound(final Context context, final AttributeSet attr) {

		super(context, attr);
		mContext = context;
		mAttr = attr;
		
		collapsedCard = new Card(context, attr) {
			@Override
			public void onTapEvent() {
				
				if(cardExpanse == null){
					Log.i("onclick listener", "creating");
					createExpanse(getWidth(), reward_name);
				}
				
				
				if(cardExpanse!=null && cardExpanse.isShown){					
					//start flipping here instead!
					Log.i("onclick listener", "collapsing2");
                	cardExpanse.collapseNow();
                	
	            	progressLayout.setVisibility(GONE);
	            }else{
	            	
	            	Log.i("onclick listener", "expanding");
 	            	cardExpanse.expandNow();
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
	protected void createExpanse(int cardWidth, String reward) {
		cardExpanse = new CardExpanse(mContext, mAttr);
		cardExpanse.setHeight((int)(cardWidth/1.58));
		cardExpanse.setReward_name(reward);
		cardExpanse.setShop_name(collapsedCard.getShop_name());
		cardExpanse.isShown=false;
		addView(cardExpanse,0, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		//adding the staimes:
		progressLayout = createProgressBar(mContext, mAttr);
		progressLayout.setLeft(3/16*collapsedCard.getWidth());
    	addView(progressLayout, 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
    	
    	progressLayout.setVisibility(GONE);
	
		
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
	
	/*
	 * setters
	 */
	public void setShopID(int id){ shopId=id;}
	public void setShopName(String name){collapsedCard.setShop_name(name);}
	public void setTotalPoint(int total){total_point = total;}
	public void setPointToNext(int next){
		next_shop_reward_require_point = next;
		collapsedCard.setNext_shop_reward_require_point(next);
	}
	public void setNextRewardName(String nextName){reward_name = (nextName);}
}