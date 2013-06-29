package com.kakada.staimecard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

public class CardCompound extends FrameLayout{
	private static final int STAIME_COLUMN_COUNT = 5;
	private static final int MARGIN_TOP = 20;
	
	private int shopId;
	private Context mContext;
	private AttributeSet mAttr;
	private Card collapsedCard;
	private CardExpanse cardExpanse;
	private GridLayout progressLayout;
	private LayoutParams cardLayoutParam;
	private int staimeWidth;
	
	private ScaleAnimation animCollapse;
	private ScaleAnimation animExpand;
	
	private int points_to_reward;
	private int total_point; //total point the user already have so far for this card
	private String reward_name;
	
	public CardCompound(final Context context, final AttributeSet attr) {

		super(context, attr);
		mContext = context;
		mAttr = attr;
		animExpand = new ScaleAnimation(1, 1, 0, 1);
		animExpand.setDuration(100);
		animCollapse = new ScaleAnimation(1, 1, 1, 0);
		animCollapse.setDuration(200);
		collapsedCard = new Card(context, attr) {
			@Override
			public void onTouchEventCallback() {
				
				if(cardExpanse == null){
					Log.i("onclick listener", "creating");
					createExpanse(getHeight(), getWidth(), reward_name);
				}
				
				decideExpandOrCollapse();
				
			};
		};
		
		cardLayoutParam = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		cardLayoutParam.setMargins(0, MARGIN_TOP, 0, 0);
		addView(collapsedCard, cardLayoutParam);
		
		staimeWidth = collapsedCard.getWidth()/STAIME_COLUMN_COUNT;
		
	}
	
	protected void decideExpandOrCollapse() {
		if(cardExpanse!=null && cardExpanse.isShown){					
			//start flipping here instead!
			//Log.i("onclick listener", "collapsing2");
        	cardExpanse.collapseNow();
        	progressLayout.startAnimation(animCollapse);
        	progressLayout.setVisibility(GONE);
        }else{
        	
        	//Log.i("onclick listener", "expanding");
         	cardExpanse.expandNow();
        	progressLayout.setVisibility(VISIBLE);
        	progressLayout.startAnimation(animExpand);
        }
	}

	protected void createExpanse(int collapsedHeight, int cardWidth, String reward) {
		//cardExpanse = new CardExpanse(mContext, mAttr);
		cardExpanse = new CardExpanse(mContext, mAttr) {
			@Override
			public void onTouchEventCallback() {
				decideExpandOrCollapse();				
			}
		};
		cardExpanse.setCollapsedHeight(collapsedHeight);
		cardExpanse.setCardWidth(cardWidth); //must be called before setStaimeRow
		cardExpanse.setStaimeRow(getRowCount());
		cardExpanse.setReward_name(reward);
		cardExpanse.setShop_name(collapsedCard.getShop_name());
		cardExpanse.isShown=false;
		addView(cardExpanse,0, cardLayoutParam);
		
		//adding the staimes:
		progressLayout = createProgressBar(mContext, mAttr);
		//progressLayout.setLeft(3/16*collapsedCard.getWidth());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
		params.setMargins(0, MARGIN_TOP, 0, 30);
    	addView(progressLayout, 1, params);
    	
    	progressLayout.setVisibility(GONE);		
	}
	

	
	private int getStaimeHeight(int width){return width *5/8;}
	
	private int getRowCount(){
		if(total_point!=0 || points_to_reward!=0)
			return (total_point+points_to_reward+STAIME_COLUMN_COUNT-1)/STAIME_COLUMN_COUNT;
		Log.d("getRowCOunt", "both total and next point is 0");
		return -100;
	}
	
	private int getExpanseHeightForRows(int collapsedHeight, int cardWidth, int rowCount){
		int rewardNameHeight = 15;
		return (int) rowCount*(cardWidth/7) +  collapsedHeight + rewardNameHeight;
	}
	
	
	protected GridLayout createProgressBar(Context context, AttributeSet attrs) {
		progressLayout = new GridLayout(context, attrs);
		progressLayout.setRowCount(getRowCount());
		progressLayout.setColumnCount(STAIME_COLUMN_COUNT);
		progressLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS); 
		android.widget.GridLayout.LayoutParams param = new GridLayout.LayoutParams();
		
		populateProgressLayout();
		
		
		return progressLayout;
	}
	public void populateProgressLayout() {
		staimeWidth = collapsedCard.getWidth()/(STAIME_COLUMN_COUNT);
		if(progressLayout!=null){
			progressLayout.removeAllViews();
			for(int i=0; i<total_point; i++){
				ImageView checkedImage = new ImageView(mContext);
				checkedImage.setImageResource(R.drawable.staime_checked);
				progressLayout.addView(checkedImage, staimeWidth, getStaimeHeight(staimeWidth));
			}
			for(int j=0; j<points_to_reward-1; j++){
				ImageView unCheckedImage = new ImageView(mContext);
				unCheckedImage.setImageResource(R.drawable.staime_unchecked);
				progressLayout.addView(unCheckedImage, staimeWidth, getStaimeHeight(staimeWidth));
			}
			ImageView rewardImage = new ImageView(mContext);
			rewardImage.setImageResource(R.drawable.staime_reward);
			progressLayout.addView(rewardImage, staimeWidth, getStaimeHeight(staimeWidth));
		}
	}

	public CardCompound(Context context){
		super(context);
	}
	
	/*
	 * setters
	 */
	public void setShopID(int id){ shopId=id;}
	public void setShopName(String name){collapsedCard.setShop_name(name);}
	public void setTotalPoint(int total){
		total_point = total;
	}
	public void setPointsToReward(int p){
		points_to_reward = p;
		collapsedCard.setPoints_to_reward(p);
	}
	public void setNextRewardName(String nextName){reward_name = (nextName);}
}