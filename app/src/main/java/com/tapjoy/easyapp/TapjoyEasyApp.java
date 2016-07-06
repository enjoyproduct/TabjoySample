package com.tapjoy.easyapp;

import java.util.ArrayList;
import java.util.Hashtable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TJAwardCurrencyListener;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TapjoyLog;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.TJVideoListener;

@SuppressLint("NewApi")
public class TapjoyEasyApp extends Activity implements View.OnClickListener, TJGetCurrencyBalanceListener, TJPlacementListener {
	public static final String TAG = "TapjoyEasyApp";

	// UI elements
	private String displayText = "";
	private Button getCurrencyBalanceButton;
	private Button getDirectPlayVideoAd;
	private Button buttonRequestPlacement;
	private Button buttonShowPlacement;
	private Button buttonEventTab;
	private Button buttonUserTab;
	private EditText placementNameInput;
	private TextView outputTextView;

	// Tapjoy Placements
	private TJPlacement directPlayPlacement;
	private TJPlacement examplePlacement;
	private TJPlacement offerwallPlacement;

	private boolean earnedCurrency = false;

	private Button currentButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupUI();
		connectToTapjoy();
	}

	/**
	 * Attempts to connect to Tapjoy
	 */
	private void connectToTapjoy() {
		// OPTIONAL: For custom startup flags.
		Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
		connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

		// If you are not using Tapjoy Managed currency, you would set your own user ID here.
		//	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");

		// Connect with the Tapjoy server.  Call this when the application first starts.
		// REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
		String tapjoySDKKey = "u6SfEbh_TA-WMiGqgQ3W8QECyiQIURFEeKm0zbOggubusy-o5ZfXp33sTXaD";

		Tapjoy.setGcmSender("34027022155");

		// NOTE: This is the only step required if you're an advertiser.
		Tapjoy.connect(this, tapjoySDKKey, connectFlags, new TJConnectListener() {
			@Override
			public void onConnectSuccess() {
				TapjoyEasyApp.this.onConnectSuccess();
			}

			@Override
			public void onConnectFailure() {
				TapjoyEasyApp.this.onConnectFail();
			}
		});
	}

	/**
	 * Handles a successful connect to Tapjoy. Pre-loads direct play placement
	 * and sets up Tapjoy listeners
	 */
	public void onConnectSuccess() {
		updateTextInUI("Tapjoy SDK connected");

		// Start preloading direct play event upon successful connect
		directPlayPlacement = new TJPlacement(this, "video_unit", this);
		directPlayPlacement.requestContent();

		// NOTE:  The get/spend/award currency methods will only work if your virtual currency
		// is managed by Tapjoy.
		//
		// For NON-MANAGED virtual currency, Tapjoy.setUserID(...)
		// must be called after requestTapjoyConnect.

		// Setup listener for Tapjoy currency callbacks
		Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
			@Override
			public void onEarnedCurrency(String currencyName, int amount) {
				earnedCurrency = true;
				updateTextInUI("You've just earned " + amount + " " + currencyName);
				showPopupMessage("You've just earned " + amount + " " + currencyName);
			}
		});

		// Setup listener for Tapjoy video callbacks
		Tapjoy.setVideoListener(new TJVideoListener() {
			@Override
			public void onVideoStart() {
				Log.i(TAG, "video has started");
			}

			@Override
			public void onVideoError(int statusCode) {
				Log.i(TAG, "there was an error with the video: " + statusCode);
			}

			@Override
			public void onVideoComplete() {
				Log.i(TAG, "video has completed");

				// Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
				Tapjoy.getCurrencyBalance(TapjoyEasyApp.this);
			}
		});
	}

	/**
	 * Handles a failed connect to Tapjoy
	 */
	public void onConnectFail() {
		Log.e(TAG, "Tapjoy connect call failed");
		updateTextInUI("Tapjoy connect failed!");
	}

	/**
	 * Notify Tapjoy the start of this activity for session tracking
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Tapjoy.onActivityStart(this);
	}

	/**
	 * Notify Tapjoy the end of this activity for session tracking
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Tapjoy.onActivityStop(this);
	}

	/**
	 * Handles button clicks
	 */
	public void onClick(View v) {
		if (v instanceof Button) {
			currentButton = ((Button) v);
			int id = currentButton.getId();

			switch (id) {
				case R.id.buttonGetCurrency:
					// Disable button
					currentButton.setEnabled(false);
					// Retrieve virtual currency balance from the server
					Tapjoy.getCurrencyBalance(TapjoyEasyApp.this);
					break;
				case R.id.buttonSpendCurrency:
					// Disable button
					currentButton.setEnabled(false);
					// Spend Currency
					callSpendCurrency(10);
					break;
				case R.id.buttonAwardCurrency:
					// Disable button
					currentButton.setEnabled(false);
					// Award Currency
					callAwardCurrency(20);
					break;
				case R.id.buttonShowOffers:
					// Disable button
					currentButton.setEnabled(false);
					// Show Offers Placement
					callShowOffers();
					break;
				case R.id.buttonGetDirectPlayVideoAd:
					// Disable button
					currentButton.setEnabled(false);
					// Show Direct Play Placement
					showDirectPlayContent();
					break;
				case R.id.buttonRequestPlacement:
					// Disable button
					currentButton.setEnabled(false);
					// Request Custom Placement
					requestPlacement();
					break;
				case R.id.buttonShowPlacement:
					// Disable button
					currentButton.setEnabled(false);
					// Show Custom Placement
					examplePlacement.showContent();
					break;
				case R.id.buttonPurchase:
					Tapjoy.trackPurchase("product1", "USD", 0.99f, null);
					updateTextInUI("Sent track purchase");
					break;
				case R.id.buttonPurchase2:
					Tapjoy.trackPurchase("product2", "USD", 1.99f, "TestCampaignID");
					updateTextInUI("Sent track purchase 2");
					break;
				case R.id.buttonPurchase3:
					{
						String skuDetails = getDummySkuDetails(); // WARNING! Replace this with the Google Play API
						Intent responseIntent = getDummyResponseIntent(); // WARNING! Replace this with the response intent sent by Google Play
						String purchaseData = responseIntent.getStringExtra("INAPP_PURCHASE_DATA");
						String dataSignature = responseIntent.getStringExtra("INAPP_DATA_SIGNATURE");
						String campaignId = "TestCampaignID2";
						Tapjoy.trackPurchase(skuDetails, purchaseData, dataSignature, campaignId);
					}
					updateTextInUI("Sent track purchase 2");
					break;
				case R.id.buttonEventTab:
					Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
					startActivity(eventIntent);
					break;
				case R.id.buttonUserTab:
					Intent userIntent = new Intent(getApplicationContext(), UserActivity.class);
					startActivity(userIntent);
					break;
			}
		}
	}

	/**
	 * Constructs TJPlacement {@link TapjoyEasyApp#examplePlacement} and
	 * requests placement content from Tapjoy. The name of the placement is
	 * pulled from {@link TapjoyEasyApp#placementNameInput}.
	 */
	private void requestPlacement() {
		// Grab placement name from input field
		String placementName = placementNameInput.getText().toString();

		// Construct TJPlacement
		examplePlacement = new TJPlacement(this, placementName, new TJPlacementListener() {
			@Override
			public void onRequestSuccess(TJPlacement placement) {
				updateTextInUI("onRequestSuccess for placement " + placement.getName());

				if (!placement.isContentAvailable()) {
					updateTextInUI("No content available for placement " + placement.getName());
				}

				setButtonEnabledInUI(buttonRequestPlacement, true);
			}

			@Override
			public void onRequestFailure(TJPlacement placement, TJError error) {
				setButtonEnabledInUI(buttonRequestPlacement, true);
				updateTextInUI("onRequestFailure for placement " + placement.getName() + " -- error: " + error.message);
			}

			@Override
			public void onContentReady(TJPlacement placement) {
				updateTextInUI("onContentReady for placement " + placement.getName());
				setButtonEnabledInUI(buttonShowPlacement, true);
			}

			@Override
			public void onContentShow(TJPlacement placement) {
				TapjoyLog.i(TAG, "onContentShow for placement " + placement.getName());
			}

			@Override
			public void onContentDismiss(TJPlacement placement) {
				setButtonEnabledInUI(buttonRequestPlacement, true);
				setButtonEnabledInUI(buttonShowPlacement, false);

				TapjoyLog.i(TAG, "onContentDismiss for placement " + placement.getName());

				// Best Practice: We recommend calling getCurrencyBalance as often as possible so the user's balance is always up-to-date.
				Tapjoy.getCurrencyBalance(TapjoyEasyApp.this);
			}

			@Override
			public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
				// Dismiss the placement content
				Intent intent = new Intent(getApplicationContext(), TapjoyEasyApp.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);

				String message = "onPurchaseRequest -- product id: " + productId + ", token: " + request.getToken() + ", request id: " + request.getRequestId();
				AlertDialog dialog = new AlertDialog.Builder(TapjoyEasyApp.this).setTitle("Got on purchase request").setMessage(message)
						.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
							}
						}).create();
				dialog.show();

				// Your app must call either callback.completed() or callback.cancelled() to complete the lifecycle of the request
				request.completed();
			}

			@Override
			public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
				// Dismiss the placement content
				Intent intent = new Intent(getApplicationContext(), TapjoyEasyApp.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);

				String message = "onRewardRequest -- item id: " + itemId + ", quantity: " + quantity + ", token: " + request.getToken() + ", request id: " + request.getRequestId();
				AlertDialog dialog = new AlertDialog.Builder(TapjoyEasyApp.this).setTitle("Got on reward request").setMessage(message)
						.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
							}
						}).create();
				dialog.show();

				// Your app must call either callback.completed() or callback.cancelled() to complete the lifecycle of the request
				request.completed();
			}
		});

		updateTextInUI("Requesting placement content");
		examplePlacement.requestContent();
	}

	/**
	 * Shows pre-loaded direct play placement. Content is initially requested
	 * and pre-loaded in {@link TapjoyEasyApp#onConnectSuccess} and re-sent
	 * again after the content has been dismissed in
	 * {@link TapjoyEasyApp#onContentDismiss}
	 */
	private void showDirectPlayContent() {
		// Check if content is available and if it is ready to show
		if (directPlayPlacement.isContentAvailable()) {
			if (directPlayPlacement.isContentReady()) {
				directPlayPlacement.showContent();
			} else {
				setButtonEnabledInUI(currentButton, true);
				updateTextInUI("Direct play video not ready to show");
			}

		} else {
			setButtonEnabledInUI(currentButton, true);
			updateTextInUI("No direct play video to show");
		}
	}

	private void callShowOffers() {
	  // Construct TJPlacement to show Offers web view from where users can download the latest offers for virtual currency.
      offerwallPlacement = new TJPlacement(this, "offerwall_unit", new TJPlacementListener() {
          @Override
          public void onRequestSuccess(TJPlacement placement) {
             updateTextInUI("onRequestSuccess for placement " + placement.getName());

             if (!placement.isContentAvailable()) {
               updateTextInUI("No Offerwall content available");
             }

             setButtonEnabledInUI(currentButton, true);
          }

          @Override
          public void onRequestFailure(TJPlacement placement, TJError error) {
            setButtonEnabledInUI(currentButton, true);
            updateTextInUI("Offerwall error: " + error.message);
          }

          @Override
          public void onContentReady(TJPlacement placement) {
            TapjoyLog.i(TAG, "onContentReady for placement " + placement.getName());

            updateTextInUI("Offerwall request success");
            placement.showContent();
          }

          @Override
          public void onContentShow(TJPlacement placement) {
             TapjoyLog.i(TAG, "onContentShow for placement " + placement.getName());
          }

          @Override
          public void onContentDismiss(TJPlacement placement) {
             TapjoyLog.i(TAG, "onContentDismiss for placement " + placement.getName());
          }

          @Override
          public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
          }

          @Override
          public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
          }
      });
      offerwallPlacement.requestContent();
	}

	/**
	 * Wrapper method to call {@link Tapjoy.#spendCurrency(int, TJSpendCurrencyListener}
	 */
	private void callSpendCurrency(int amount) {
		// Spend virtual currency
		Tapjoy.spendCurrency(amount, new TJSpendCurrencyListener() {
			@Override
			public void onSpendCurrencyResponse(String currencyName, int balance) {
				updateTextInUI(currencyName + ": " + balance);
				setButtonEnabledInUI(currentButton, true);
			}

			@Override
			public void onSpendCurrencyResponseFailure(String error) {
				updateTextInUI("spendCurrency error: " + error);
				setButtonEnabledInUI(currentButton, true);
			}
		});
	}

	/**
	 * Wrapper method to call {@link Tapjoy.#awardCurrency(int, TJAwardCurrencyListener}
	 */
	private void callAwardCurrency(int amount) {
		// Award virtual currency
		Tapjoy.awardCurrency(amount, new TJAwardCurrencyListener() {
			@Override
			public void onAwardCurrencyResponseFailure(String error) {
				updateTextInUI("awardCurrency error: " + error);
				setButtonEnabledInUI(currentButton, true);
			}

			@Override
			public void onAwardCurrencyResponse(String currencyName, int balance) {
				updateTextInUI(currencyName + ": " + balance);
				setButtonEnabledInUI(currentButton, true);
			}
		});
	}

	//================================================================================
	// TapjoyListener Methods
	//================================================================================
	@Override
	public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
		Log.i(TAG, "currencyName: " + currencyName);
		Log.i(TAG, "balance: " + balance);

		if (earnedCurrency) {
			updateTextInUI(displayText + "\n" + currencyName + ": " + balance);
			earnedCurrency = false;
		} else {
			updateTextInUI(currencyName + ": " + balance);
		}
		setButtonEnabledInUI(getCurrencyBalanceButton, true);
	}

	@Override
	public void onGetCurrencyBalanceResponseFailure(String error) {
		updateTextInUI("getCurrencyBalance error: " + error);
		setButtonEnabledInUI(getCurrencyBalanceButton, true);
	}

	/*
	 * TJPlacement callbacks
	 */
	@Override
	public void onRequestSuccess(TJPlacement placement) {
		// If content is not available you can note it here and act accordingly as best suited for your app
		Log.i(TAG, "Tapjoy on request success, contentAvailable: " + placement.isContentAvailable());
	}

	@Override
	public void onRequestFailure(TJPlacement placement, TJError error) {
		Log.i(TAG, "Tapjoy send event " + placement.getName() + " failed with error: " + error.message);
	}

	@Override
	public void onContentReady(TJPlacement placement) {
	}

	@Override
	public void onContentShow(TJPlacement placement) {
	}

	@Override
	public void onContentDismiss(TJPlacement placement) {
		Log.i(TAG, "Tapjoy direct play content did disappear");

		setButtonEnabledInUI(getDirectPlayVideoAd, true);

		// Best Practice: We recommend calling getCurrencyBalance as often as possible so the user's balance is always up-to-date.
		Tapjoy.getCurrencyBalance(TapjoyEasyApp.this);

		// Begin preloading the next placement after the previous one is dismissed
		directPlayPlacement = new TJPlacement(this, "video_unit", this);
		directPlayPlacement.requestContent();
	}

	@Override
	public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
	}

	@Override
	public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
	}

	/**
	 * Grabs references to UI elements and sets up listeners
	 */
	private void setupUI() {
		// This button shows the offers page
		((Button) findViewById(R.id.buttonShowOffers)).setOnClickListener(this);

		buttonEventTab = (Button) findViewById(R.id.buttonEventTab);
		buttonEventTab.setOnClickListener(this);
		buttonUserTab = (Button) findViewById(R.id.buttonUserTab);
		buttonUserTab.setOnClickListener(this);

		// This button displays a direct play video
		getDirectPlayVideoAd = (Button) findViewById(R.id.buttonGetDirectPlayVideoAd);
		getDirectPlayVideoAd.setOnClickListener(this);

		// This button retrieves the virtual currency info from the Tapjoy server
		getCurrencyBalanceButton = (Button) findViewById(R.id.buttonGetCurrency);
		getCurrencyBalanceButton.setOnClickListener(this);

		placementNameInput = (EditText) findViewById(R.id.inputPlacementName);

		// This spends virtual currency for this device
		((Button) findViewById(R.id.buttonSpendCurrency)).setOnClickListener(this);

		// This awards virtual currency for this device
		((Button) findViewById(R.id.buttonAwardCurrency)).setOnClickListener(this);

		// Placements
		buttonRequestPlacement = (Button) findViewById(R.id.buttonRequestPlacement);
		buttonRequestPlacement.setOnClickListener(this);

		buttonShowPlacement = ((Button) findViewById(R.id.buttonShowPlacement));
		buttonShowPlacement.setOnClickListener(this);

		// Purchases
		((Button) findViewById(R.id.buttonPurchase)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonPurchase2)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonPurchase3)).setOnClickListener(this);

		outputTextView = (TextView) findViewById(R.id.textViewOutput);
	}

	/**
	 * Update the text view on the UI thread
	 *
	 * @param text
	 *            text to display in UI
	 */
	private void updateTextInUI(final String text) {
		displayText = text;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (outputTextView != null) {
					outputTextView.setText(text);
				}
			}
		});
	}

	/**
	 * Re-enable button on the UI thread
	 *
	 * @param button
	 *            button to enable
	 * @param enabled
	 *            whether to enable the button or not
	 */
	private void setButtonEnabledInUI(final Button button, final Boolean enabled) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				button.setEnabled(enabled);
			}
		});
	}

	/**
	 * Helper function to show a Toast to the user
	 *
	 * @param text
	 *            text that you want to display in the Toast
	 */
	private void showPopupMessage(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}

	// For trackPurchase. WARNING! Not for production use
	private String getDummySkuDetails() {
		return "{\"title\":\"TITLE\",\"price\":\"$3.33\",\"type\":\"inapp\",\"description\":\"DESC\",\"price_amount_micros\":3330000,\"price_currency_code\":\"USD\",\"productId\":\"3\"}";
	}

	// For trackPurchase. WARNING! Not for production use
	private Intent getDummyResponseIntent() {
		return new Intent();
	}
}
