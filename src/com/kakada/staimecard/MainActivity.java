package com.kakada.staimecard;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LinearLayout cardContainer  = (LinearLayout) findViewById(R.id.container_layout);
		CardCompound n = new CardCompound(this, null);
		CardCompound n1 = new CardCompound(this, null);
		CardCompound n2 = new CardCompound(this, null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		float d = this.getResources().getDisplayMetrics().density;
		lp.setMargins(0, (int)(20*d), 0, 0);
		cardContainer.addView(n, lp);
		cardContainer.addView(n1, lp);
		cardContainer.addView(n2, lp);

	}
	

}
