package com.jang.ykk.login.model;

import lombok.ToString;

@ToString
public class OrderCreateForm {
	 private String name;		// 이름
	 private int totalPrice; 	// 결제금
	 private int payId;
	 
	 public String getName () {
		 return name;
	 }
	 
	 public int getTotalPrice() {
		 return totalPrice;
	 }
	 
	 public int getPayId() {
		 return payId;
	 }
}