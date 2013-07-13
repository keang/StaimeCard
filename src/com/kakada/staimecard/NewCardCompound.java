package com.kakada.staimecard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewCardCompound extends RelativeLayout{
	private static final int STAIME_COLUMN_COUNT = 5;
	private static final int MARGIN_TOP = 20;
	private static final int EXPANSE_PADDING = 10;
	final int dip = (int) getResources().getDisplayMetrics().density;
	

	private Context mContext;
	private AttributeSet mAttr;
	private int shopId;
	private String shopName;
	private String nextRewardName;
	private ImageView shopImage;
	private TextView nextPointText;
	private TextView shopNameText;
	private TextView nextRewardText;
	private GridLayout progressLayout;
	private int staimeWidth;
	private float cardWidth;
	private int collapsedCardHeight;
	private Typeface robotoThin;
	
	private ScaleAnimation animCollapse;
	private ScaleAnimation animExpand;
	private Boolean isShown;

	//point gap = point_to_reward - total_point, as pointed out to jupiter
	private int points_to_reward;
	private int total_point; //total point the user already have so far for this card
	private List<ShopReward> rewardList;
	
	
	public NewCardCompound(final Context context, final AttributeSet attr) {
		super(context, attr);
		
		//hardcode
		points_to_reward = 8;
		total_point = 2;
		
		robotoThin = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Thin.ttf"); 
		
		mContext = context;
		mAttr = attr;
		animExpand = new ScaleAnimation(1, 1, 0, 1);
		animExpand.setDuration(100);
		animCollapse = new ScaleAnimation(1, 1, 1, 0);
		animCollapse.setDuration(100);
		animCollapse.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {

			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
	        	progressLayout.setVisibility(GONE);				
			}
		});
		
		RelativeLayout.LayoutParams relativeParams = new LayoutParams(120*dip, 120*dip);
		
		//background is white, alpha=0.5
		setBackgroundColor(Color.parseColor("#80ffffff"));
		
		//margin:
		
		
		//cover image
		relativeParams.addRule(ALIGN_PARENT_TOP);
		relativeParams.addRule(ALIGN_PARENT_LEFT);
		//
		shopImage = new ImageView(context);
		shopImage.setId(R.id.shop_cover);
		shopImage.setAdjustViewBounds(true);
		shopImage.setImageResource(R.drawable.shop_cover_image);
		setClickable(true);
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shopImage.setScaleType(ScaleType.CENTER_CROP);
				
				Log.i("new card", "clicked image");
				if(progressLayout==null){
					createExpanse();
					populateProgressLayout();
					isShown=false;
				}
				decideExpandOrCollapse();
			}
		});
		addView(shopImage, relativeParams);
					
		
		//next point text
		relativeParams = null;
		relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(ALIGN_PARENT_TOP);
		relativeParams.addRule(ALIGN_PARENT_RIGHT);
		nextPointText = new TextView(context); 
		nextPointText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 72);
		nextPointText.setLineSpacing(0.0f, 0.8f);
		nextPointText.setIncludeFontPadding (true);
		nextPointText.setTypeface(Typeface.createFromAsset(
				getContext().getAssets(), "fonts/Roboto_Thin.ttf"));
		nextPointText.setTextColor(Color.parseColor("#BFBFBF"));
		nextPointText.setText(Integer.toString(points_to_reward - total_point));
		nextPointText.setId(R.id.point_to_next_text);
		addView(nextPointText, relativeParams);
		
		//shop name
		relativeParams = null;
		relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(ALIGN_PARENT_TOP);
		relativeParams.addRule(ALIGN_PARENT_RIGHT);
		relativeParams.setMargins(0, 0, 5*dip, 0);
		shopNameText = new TextView(context);
		shopNameText.setText(shopName);
		shopNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		shopNameText.setIncludeFontPadding (true);
		shopNameText.setTypeface(Typeface.createFromAsset(
				getContext().getAssets(), "fonts/Roboto_Condensed.ttf"));
		shopNameText.setTextColor(Color.parseColor("#404040"));
		//shopNameText.setId(R.id.shop_name_text);
		addView(shopNameText, relativeParams);
		
		//next reward name
		relativeParams = null;
		relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(ALIGN_PARENT_TOP);
		relativeParams.addRule(RIGHT_OF, R.id.shop_cover);
		nextRewardText = new TextView(context);
		nextRewardText.setText(nextRewardName);
		nextRewardText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		nextRewardText.setPadding(5*dip, 60*dip, 0, 0);
		nextRewardText.setIncludeFontPadding (true);
		nextRewardText.setTypeface(Typeface.createFromAsset(
				getContext().getAssets(), "fonts/Roboto_Light.ttf"));
		nextRewardText.setTextColor(Color.parseColor("#404040"));
		//shopNameText.setId(R.id.shop_name_text);
		addView(nextRewardText, relativeParams);
		
		//reward listing		
		rewardList = new ArrayList<ShopReward>();
		
	}
	
	protected void decideExpandOrCollapse() {
		if(isShown){					
			//start flipping here instead!
			//Log.i("onclick listener", "collapsing2");
        	progressLayout.startAnimation(animCollapse);
        	isShown=false;
        }else{
        	//Log.i("onclick listener", "expanding");
        	progressLayout.setVisibility(VISIBLE);
        	progressLayout.startAnimation(animExpand);
        	isShown=true;
        }
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		shopImage.setScaleType(ScaleType.CENTER_CROP);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			//in case there's no size specified, default to:
			return 300;
		} 
	}
	
	protected void createExpanse() {
		//cardExpanse = new CardExpanse(mContext, mAttr);
		
		//adding the staimes:
		progressLayout = createProgressBar(mContext);
		progressLayout.setId(R.id.staime_grid);
		//progressLayout.setLeft(3/16*collapsedCard.getWidth());
		
		RelativeLayout.LayoutParams relativeParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(ALIGN_PARENT_LEFT);
		relativeParams.addRule(BELOW, R.id.point_to_next_text); //append below the reward lists.
		addView(progressLayout, relativeParams);
		
    	//progressLayout.setVisibility(GONE);		
	}
	

	
	private int getStaimeHeight(int width){return width *5/8;}
	
	private int getRowCount(){
		if(total_point!=0 || points_to_reward!=0)
			return (total_point+points_to_reward+STAIME_COLUMN_COUNT+1)/STAIME_COLUMN_COUNT;
		Log.d("getRowCOunt", "both total and next point is 0");
		return 1;
	}
	
	private int getExpanseHeightForRows(int collapsedHeight, int cardWidth, int rowCount){
		int rewardNameHeight = 15;
		return (int) rowCount*(cardWidth/7) +  collapsedHeight + rewardNameHeight;
	}
	
	
	protected GridLayout createProgressBar(Context context) {
		progressLayout = new GridLayout(context);
		//progressLayout.setRowCount(getRowCount());
		//progressLayout.setBackgroundColor(Color.parseColor("#80ffffff"));
		progressLayout.setColumnCount(STAIME_COLUMN_COUNT);
		progressLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS); 	
		progressLayout.setPadding(EXPANSE_PADDING*dip,EXPANSE_PADDING*dip
				,EXPANSE_PADDING*dip,EXPANSE_PADDING*dip);
		return progressLayout;
	}
	public void populateProgressLayout() {
		//initialize staime size:
		staimeWidth = (getMeasuredWidth()-2*EXPANSE_PADDING*dip)/STAIME_COLUMN_COUNT;
		Log.i("staime width", Integer.toString(shopImage.getMeasuredWidth()));
		if(progressLayout!=null){
			
			progressLayout.removeAllViews();
			
			//add reward list first
				//order rewardlist
				Collections.sort(rewardList, new Comparator<ShopReward>(){
				     public int compare(ShopReward o1, ShopReward o2){
				         if(o1.requiredPoint == o2.requiredPoint)
				             return 0;
				         return o1.requiredPoint < o2.requiredPoint ? -1 : 1;
				     }
				});
			GridLayout.LayoutParams gridParams;
			for(int i=0; i<rewardList.size();i++){
				TextView rewardPointTextView = new TextView(mContext);
				rewardPointTextView.setText(Integer.toString(rewardList.get(i).requiredPoint)+": ");
				rewardPointTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
				rewardPointTextView.setGravity(Gravity.CENTER_HORIZONTAL);
				gridParams = new GridLayout.LayoutParams();
				gridParams.setGravity(Gravity.CENTER_HORIZONTAL);
				progressLayout.addView(rewardPointTextView, gridParams);
				TextView rewardNameTextView = new TextView(mContext);
				rewardNameTextView.setText(rewardList.get(i).name);
				rewardNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
				gridParams.setGravity(Gravity.LEFT);
				gridParams = new GridLayout.LayoutParams();
				gridParams.columnSpec=GridLayout.spec(1, STAIME_COLUMN_COUNT-1);
				progressLayout.addView(rewardNameTextView, gridParams);
			}
			
			//add a horizontal rule
			ImageView rule = new ImageView(mContext);
			//rule.setBackgroundColor(Color.parseColor("#999999"));
			//rule.setPadding(2*dip,2*dip,2*dip,2*dip);
			rule.setImageResource(R.drawable.line);
			gridParams = new GridLayout.LayoutParams();
			gridParams.columnSpec = GridLayout.spec(0, STAIME_COLUMN_COUNT);
			progressLayout.addView(rule,gridParams);
			
			//add the staime slots
			boolean staimePasted=false;
			if(!rewardList.isEmpty()){
				for(int i=0; i<rewardList.get(rewardList.size()-1).requiredPoint; i++){
					if(i<=total_point-1){
						for(ShopReward r : rewardList){
							if(i== r.requiredPoint-1){
								//rewarded
								ImageView checkedImage = new ImageView(mContext);
								checkedImage.setImageResource(R.drawable.staime_rewarded);
								progressLayout.addView(checkedImage, staimeWidth, getStaimeHeight(staimeWidth));
								staimePasted = true;
								break;
							}
						}
						if(!staimePasted){
							//add a regular checked staime
							ImageView checkedImage = new ImageView(mContext);
							checkedImage.setImageResource(R.drawable.staime_checked);
							progressLayout.addView(checkedImage, staimeWidth, getStaimeHeight(staimeWidth));
						}
						staimePasted=false;
					} 
					
					else if(i>total_point-1){
						for(ShopReward r : rewardList){
							if(i== r.requiredPoint-1){
								//reward slot
								ImageView checkedImage = new ImageView(mContext);
								checkedImage.setImageResource(R.drawable.staime_reward);
								progressLayout.addView(checkedImage, staimeWidth, getStaimeHeight(staimeWidth));
								staimePasted = true;
								break;
							}
						}
						if(!staimePasted){
							//add a regular checked staime
							ImageView checkedImage = new ImageView(mContext);
							checkedImage.setImageResource(R.drawable.staime_unchecked);
							progressLayout.addView(checkedImage, staimeWidth, getStaimeHeight(staimeWidth));
						}
						staimePasted=false;
					}
				}
			}
		}
	}

		
	/*
	 * setters
	 */
	public void setShopID(int id){ shopId=id;}
	public void setShopName(String name){
		shopName=name;
		shopNameText.setText(name);
	}
	public void setNextRewardName(String name){
		nextRewardName=name;
		nextRewardText.setText(name);
	}
	public void setTotalPoint(int total){
		total_point = total;
		populateProgressLayout();
		nextPointText.setText(Integer.toString(points_to_reward - total_point));
		
	}
	public void setPointsToReward(int p){
		points_to_reward = p;
		nextPointText.setText(Integer.toString(points_to_reward - total_point));
		
	}
	public void addShopReward(String n, int rp, String desc, String URL){
		ShopReward reward = new ShopReward(n, rp, desc, URL);
		for(ShopReward r : rewardList){
			if(n.equals(r.name)) return;
		}
		rewardList.add(reward);		
	}
}