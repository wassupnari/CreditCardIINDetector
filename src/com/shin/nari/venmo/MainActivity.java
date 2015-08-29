package com.shin.nari.venmo;

import java.util.Calendar;

import com.shin.nari.venmo.R.drawable;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Nari Shin (wassupnari@gmail.com)
 *
 */
public class MainActivity extends Activity {
	
	private EditText mCardNumber;
	private EditText mExpMM;
	private EditText mExpYY;
	private EditText mCVV;
	private ImageView mCardImg;
	private Button mSubmit;
	
	private IINDetector mIINDetector;
	
	private int mCardType;
	
	private AlertDialog alert = null;
	
	private int mValidLength = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mIINDetector = new IINDetector();
		
		setupUI();
	}
	
	/**
	 * UI setup
	 */
	public void setupUI() {
		
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		
		LayoutInflater inflator = (LayoutInflater)getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.actionbar_layout, null);
		
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText("ADD CARD");
		
		getActionBar().setCustomView(v);
		
		mCardNumber = (EditText) findViewById(R.id.card_number);
		mExpMM = (EditText) findViewById(R.id.card_mm);
		mExpYY = (EditText) findViewById(R.id.card_yy);
		mCVV = (EditText) findViewById(R.id.card_cvv);
		mCardImg = (ImageView) findViewById(R.id.card_img);
		mSubmit = (Button) findViewById(R.id.btn_submit);
		
		mCardNumber.addTextChangedListener(new CardInfoTextWatcher(mCardNumber));
		
		mSubmit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				boolean isValidNum = isValidNumber();
				boolean isValidDate = isValidDate();
				boolean isValidCVV = isValidCVV();
				if(isValidNum && isValidDate && isValidCVV) {
					showSuccessDialog();
				} else {
					if(!isValidNum) {
						showFailDialog("Your card number is incorrect!");
					} else if(!isValidDate) {
						showFailDialog("Card expiration date is not correct!");
					} else if(!isValidCVV) {
						showFailDialog("Please check your CVV");
					} else {
						showFailDialog("Card number and expiration date is not correct!");
					}
				}
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	
	public void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager)getSystemService(
			      MainActivity.this.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	/**
	 * Check if card number is valid or not
	 * @return true : valid, false : invalid
	 */
	public boolean isValidNumber() {
		int sum = 0;
		boolean toggle = false;
		String cardNumber = mCardNumber.getText().toString();
		
		// Luhn10 algorithm
		for(int i=cardNumber.length()-1; i>=0; i--) {
			int num = cardNumber.charAt(i) - '0';
			if(toggle) {
				int tmp = num *2;
				if(tmp > 9) {
					sum += 1 + (tmp % 10);
				} else {
					sum += tmp;
				}
				toggle = false;
			} else {
				sum += num;
				toggle = true;
			}
		}
		if(sum%10 == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if expiration date is valid
	 * @return true : valid, false : invalid
	 */
	public boolean isValidDate() {
		Calendar cal = Calendar.getInstance();
		Calendar calInput = Calendar.getInstance();
		
		// Check date is empty
		if(mExpMM.getText().toString().length() < 2 || mExpYY.getText().toString().length() < 2) {
			return false;
		}
		
		int inputMonth = Integer.parseInt(mExpMM.getText().toString());
		int inputYear = Integer.parseInt(mExpYY.getText().toString());
		
		if(inputMonth < 1 || inputMonth > 12 || 2000+inputYear < cal.get(Calendar.YEAR)) {
			return false;
		}
		
		calInput.set(Calendar.MONTH, inputMonth);
		calInput.set(Calendar.YEAR, 2000+inputYear);
		calInput.set(Calendar.DAY_OF_MONTH, -1); // Moving the last day of previous month
		
		if(cal.getTimeInMillis() > calInput.getTimeInMillis()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Checi if CVV number has value
	 * @return true : has value, false : no value or less than 3 digits
	 */
	public boolean isValidCVV() {
		if(mCVV.getText().toString().length() == 3) {
			return true;
		} else {
			return false;
		}
	}	
	
	/**
	 * Show success dialog
	 */
	public void showSuccessDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage("Success!").setPositiveButton(R.string.ok_button,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				alert.dismiss();
			}
		});
		alert = builder.create();
		alert.show();
	}
	
	/**
	 * Show failed dialog with error message
	 * @param msg
	 */
	public void showFailDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(msg).setPositiveButton(R.string.ok_button,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				alert.dismiss();
			}
		});
		alert = builder.create();
		alert.show();
	}
	
	/**
	 * 
	 * TextWatcher class
	 *
	 */
	private class CardInfoTextWatcher implements TextWatcher {

		private View view;
		
		private CardInfoTextWatcher(View view) {
			this.view = view;
		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			switch (view.getId()) {
			case R.id.card_number:
				if(editable.length() == 0) {
					mCardType = IINDetector.NONE;
				} else {
					mCardType = mIINDetector.getCardType(editable.toString());
				}
				updateCardImage(mCardType);
				break;
			}
		}
	}
	
	/**
	 * Update card image according to card number input.
	 * @param cardType
	 */
	public void updateCardImage(int cardType) {

		switch(cardType) {
		case IINDetector.VISA:
			mValidLength = 16;
			mCardImg.setBackground(getResources().getDrawable(drawable.bt_visa));
			mCardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mValidLength)});
			break;
		case IINDetector.MASTER:
			mValidLength = 16;
			mCardImg.setBackground(getResources().getDrawable(drawable.bt_mastercard));
			mCardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mValidLength)});
			break;
		case IINDetector.AMEX:
			mValidLength = 15;
			mCardImg.setBackground(getResources().getDrawable(drawable.bt_amex));
			mCardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mValidLength)});
			break;
		case IINDetector.DISCOVER:
			mValidLength = 16;
			mCardImg.setBackground(getResources().getDrawable(drawable.bt_discover));
			mCardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mValidLength)});
			break;
		case IINDetector.JCB:
			mValidLength = 16;
			mCardImg.setBackground(getResources().getDrawable(drawable.bt_jcb));
			mCardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mValidLength)});
			break;
		case IINDetector.DINERS:
			mValidLength = 14;
			mCardImg.setBackground(getResources().getDrawable(drawable.bt_diners_club));
			mCardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mValidLength)});
			break;
		case IINDetector.NONE:
			mCardImg.setBackground(getResources().getDrawable(drawable.bt_generic_card));
			break;
		
		}
	}
}
