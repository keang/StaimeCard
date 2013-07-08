package com.kakada.staimecard;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

public class MainActivity extends Activity{
	private CardCompound n;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("on create newCard", "parent width= "
						+Integer.toString(((View)findViewById(R.id.staime1)).getWidth()));
				
			}
		});
		
		NewCardCompound nc = (NewCardCompound)findViewById(R.id.new1);
		if(nc!=null){
			Log.i("main oncreate", "nc not null");
			nc.setTotalPoint(6);
			nc.setPointsToReward(10);
			nc.addShopReward("Free icecream", 10, "", "");
			nc.addShopReward("Charged icecream", 5, "Still need to pay", "");
		}
		nc = (NewCardCompound)findViewById(R.id.new2);
		if(nc!=null){
			Log.i("main oncreate", "nc not null");
			nc.setTotalPoint(6);
			nc.setPointsToReward(14);
			nc.addShopReward("Free icecream", 14, "", "");
			nc.addShopReward("Charged icecream", 6, "Still need to pay", "");
		}
	}
	
	
	public void add(View v){
		n.setTotalPoint(6);
		n.setPointsToReward(4);
		n.populateProgressLayout();
	}

}
