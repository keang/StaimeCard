package com.kakada.staimecard;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

public class MainActivity extends Activity{
	private CardCompound n;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LinearLayout cardContainer  = (LinearLayout) findViewById(R.id.container_layout);
		n = new CardCompound(this, null);
		n.setShopID(4);
		//card.setShopImage(cur.getBlob(1));
		n.setShopName("Brown Coffee");
		n.setTotalPoint(4);
		//point gap = pointToNextReward - totalPoint, as pointed out to jupiter
		n.setPointsToReward(6);
		n.setNextRewardName("free latte");	
		CardCompound n1 = new CardCompound(this, null);
		n1.setShopID(2);
		//card.setShopImage(cur.getBlob(1));
		n1.setShopName("Geordarno");
		n1.setTotalPoint(2);
		//point gap = pointToNextReward - totalPoint, as pointed out to jupiter
		n1.setPointsToReward(13);
		n1.setNextRewardName("20% off next item");	
		CardCompound n2 = new CardCompound(this, null);
		n2.setShopID(1);
		//card.setShopImage(cur.getBlob(1));
		n2.setShopName("Snow Yoghurt");
		n2.setTotalPoint(1);
		//point gap = pointToNextReward - totalPoint, as pointed out to jupiter
		n2.setPointsToReward(4);
		n2.setNextRewardName("Free small cup");	
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		float d = this.getResources().getDisplayMetrics().density;
		lp.setMargins(0, (int)(20*d), 0, 0);
		cardContainer.addView(n, lp);
		cardContainer.addView(n1, lp);
		cardContainer.addView(n2, lp);

	}
	
	public void add(View v){
		n.setTotalPoint(6);
		n.setPointsToReward(4);
		n.populateProgressLayout();
	}

}
