package com.kakada.staimecard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

public class CardCompound extends FrameLayout{
	private static final int STAIME_COLUMN_COUNT = 5;
	
	private int shopId;
	private Context mContext;
	private AttributeSet mAttr;
	private Card collapsedCard;
	private CardExpanse cardExpanse;
	private GridLayout progressLayout;
	private LayoutParams cardLayoutParam;
	private int staimeWidth;
	
	private int points_to_reward;
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
					//Log.i("onclick listener", "collapsing2");
                	cardExpanse.collapseNow();
                	
	            	progressLayout.setVisibility(GONE);
	            }else{
	            	
	            	//Log.i("onclick listener", "expanding");
 	            	cardExpanse.expandNow();
	            	progressLayout.setVisibility(VISIBLE);
	            }
			};
		};
		
		cardLayoutParam = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		cardLayoutParam.setMargins(0, 20, 0, 0);
		addView(collapsedCard, cardLayoutParam);
		
		staimeWidth = collapsedCard.getWidth()/STAIME_COLUMN_COUNT;
		
	}
	protected void createExpanse(int cardWidth, String reward) {
		cardExpanse = new CardExpanse(mContext, mAttr);
		cardExpanse.setHeight((int)(cardWidth/1.58));
		cardExpanse.setReward_name(reward);
		cardExpanse.setShop_name(collapsedCard.getShop_name());
		cardExpanse.isShown=false;
		addView(cardExpanse,0, cardLayoutParam);
		
		//adding the staimes:
		progressLayout = createProgressBar(mContext, mAttr);
		progressLayout.setLeft(3/16*collapsedCard.getWidth());
    	addView(progressLayout, 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
    	
    	progressLayout.setVisibility(GONE);
	
		
	}
	protected GridLayout createProgressBar(Context context, AttributeSet attrs) {
		GridLayout gl = new GridLayout(context, attrs);
		int rowCount = (total_point + points_to_reward)/STAIME_COLUMN_COUNT;
		gl.setRowCount(rowCount);
		gl.setColumnCount(STAIME_COLUMN_COUNT);
		gl.setAlignmentMode(GridLayout.ALIGN_BOUNDS); 
		android.widget.GridLayout.LayoutParams param = new GridLayout.LayoutParams();
		for(int i=0; i<total_point; i++){
			ImageView checkedImage = new ImageView(context);
			checkedImage.setImageResource(R.drawable.staime_checked);
			gl.addView(checkedImage, staimeWidth, staimeWidth);
		}
		for(int j=0; j<points_to_reward; j++){
			ImageView unCheckedImage = new ImageView(context);
			unCheckedImage.setImageResource(R.drawable.staime_unchecked);
			gl.addView(unCheckedImage, staimeWidth, staimeWidth);
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
	public void setPointsToReward(int p){
		points_to_reward = p;
		collapsedCard.setPoints_to_reward(p);
	}
	public void setNextRewardName(String nextName){reward_name = (nextName);}
}