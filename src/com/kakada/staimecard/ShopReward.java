package com.kakada.staimecard;

public class ShopReward {
	public String name;
	public int requiredPoint;
	public String description;
	public String imgURL;
	
	public ShopReward(){}
	
	public ShopReward(String n, int rp, String desc, String URL){
		name=n;
		requiredPoint = rp;
		description = desc;
		imgURL = URL;
	}
	
	public int compareTo(ShopReward s){
		return (requiredPoint - s.requiredPoint);
	}
}
