package com.shin.nari.venmo;

import android.util.Log;


/**
 * Issuer Identification Number Detector
 * 
 * @author Nari Shin (wassupnari@gmail.com)
 *
 */
public class IINDetector {
	
	public static final int NONE = 0;
	public static final int VISA = 1;
	public static final int MASTER = 2;
	public static final int AMEX = 3;
	public static final int DISCOVER = 4;
	public static final int JCB = 5;
	public static final int DINERS = 6;
	
	// Reference : http://www.cybersource.com/developers/test_and_manage/managing/best_practices/card_type_id/
	// Visa : Start with 4, valid length 13 or 16
	private String mPatternVisa = "^4";
	// Master : Start with 51~55, valid length 16
	private String mPatternMaster = "^5[1-5]";
	// Americal Express : Start with 34 or 37, valid length 15
	private String mPatternAmex = "^3[47]";
	// Discover : First 8 digits must be in those range - 60110000 through 60119999 or 65000000 through 65999999  or 62212600 through 62292599
	private String mPatternDiscover = "^6(?:011|5|4[4-9]|22[1-9])";
	// JCB : First four digits must be 3088, 3096, 3112, 3158, 3337, or the first eight digits must be in the range 35280000 through 35899999. valid length 16
	private String mPatternJCB = "^(?:2131|1800|35(2[8-9]|[3-8][0-9]|8[0-9]))";
	// Diners :First digit must be 3 and second digit must be 0, 6, or 8, valid length 14 
	private String mPatternDiners = "^3[068]";
	
	public int getCardType(String cardNumber) {
		if(cardNumber.matches(mPatternVisa)) {
			return VISA;
		} else if(cardNumber.matches(mPatternMaster)) {
			return MASTER;
		} else if(cardNumber.matches(mPatternAmex)) {
			return AMEX;
		} else if(cardNumber.matches(mPatternDiscover)) {
			return DISCOVER;
		} else if(cardNumber.matches(mPatternJCB)) {
			return JCB;
		} else if(cardNumber.matches(mPatternDiners)) {
			return DINERS;
		} else {
			return -1;
		}
		
	}

}
