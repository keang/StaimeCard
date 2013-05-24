package com.kakada.staimecard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class CardCompound extends FrameLayout{

	private Card collapsedCard;
	private CardExpanse cardExpanse;
	private LayoutParams cardLayoutParam;
	public CardCompound(Context context, AttributeSet attr) {
		
		super(context, attr);
		
		collapsedCard = new Card(context, attr) {
			
			@Override
			public void onTapEvent() {
				if(cardExpanse.isShown){
					
					Log.i("onclick listener", "collapsing");
	            	cardExpanse.collapseNow();
	            	cardExpanse.isShown=false;
	            }else{
	            	Log.i("onclick listener", "expanding");
	            	cardExpanse.expandNow();
	            	cardExpanse.isShown=true;
	            }
			};
		};
		
		cardExpanse = new CardExpanse(context, attr);
		cardExpanse.isShown=false;
		cardLayoutParam = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(cardExpanse, cardLayoutParam);
		addView(collapsedCard,cardLayoutParam);		
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
