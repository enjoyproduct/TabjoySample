package com.tapjoy.easyapp;

import com.tapjoy.Tapjoy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventActivity extends Activity implements View.OnClickListener {

	// Values used in event track calls
	private static final String DEFAULT_CATEGORY = "SDKTestCategory";
	private static final String DEFAULT_EVENT_NAME = "SDKTestEvent";
	private static final String DEFAULT_KEY = "TestKey1";
	private static final int DEFAULT_VALUE = 100;
	private static final String DEFAULT_KEY2 = "TestKey2";
	private static final long DEFAULT_VALUE2 = 200;
	private static final String DEFAULT_KEY3 = "TestKey3";
	private static final long DEFAULT_VALUE3 = 300;
	private static final String DEFAULT_PARAM1 = "Param1";
	private static final String DEFAULT_PARAM2 = "Param2";

	// Used to output status messages
	private TextView textViewOutput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		setupUI();
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
	@Override
	public void onClick(View v) {
		if (v instanceof Button) {
			final Button button = ((Button) v);
			int id = button.getId();

			switch (id) {
				case R.id.buttonBasic:
					Tapjoy.trackEvent(DEFAULT_EVENT_NAME);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME);
					break;
				case R.id.buttonParameter1:
					Tapjoy.trackEvent(DEFAULT_CATEGORY, DEFAULT_EVENT_NAME, DEFAULT_PARAM1, null);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME + ", param1: " + DEFAULT_PARAM1);
					break;
				case R.id.buttonValue:
					Tapjoy.trackEvent(DEFAULT_CATEGORY, DEFAULT_EVENT_NAME, null, null, DEFAULT_VALUE);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME + ", value: " + DEFAULT_VALUE);
					break;
				case R.id.buttonParameter1And2:
					Tapjoy.trackEvent(DEFAULT_CATEGORY, DEFAULT_EVENT_NAME, DEFAULT_PARAM1, DEFAULT_PARAM2);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME + ", param1: " + DEFAULT_PARAM1 + ", param2: " + DEFAULT_PARAM2);
					break;
				case R.id.buttonParameter1WithValue1:
					Tapjoy.trackEvent(DEFAULT_CATEGORY, DEFAULT_EVENT_NAME, DEFAULT_PARAM1, null, DEFAULT_VALUE);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME + ", param1: " + DEFAULT_PARAM1 + ", value: " + DEFAULT_VALUE);
					break;
				case R.id.buttonParameter1And2WithValue1:
					Tapjoy.trackEvent(DEFAULT_CATEGORY, DEFAULT_EVENT_NAME, DEFAULT_PARAM1, DEFAULT_PARAM2, DEFAULT_VALUE);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME + ", param1: " + DEFAULT_PARAM1 + ", param2: " + DEFAULT_PARAM2 + ", value: "
							+ DEFAULT_VALUE);
					break;
				case R.id.buttonParameter1And2WithValue1And2:
					Tapjoy.trackEvent(DEFAULT_CATEGORY, DEFAULT_EVENT_NAME, DEFAULT_PARAM1, DEFAULT_PARAM2, DEFAULT_KEY, DEFAULT_VALUE, DEFAULT_KEY2, DEFAULT_VALUE2);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME + ", param1: " + DEFAULT_PARAM1 + ", param2: " + DEFAULT_PARAM2 + ", value: {"
							+ DEFAULT_KEY + ":" + DEFAULT_VALUE + "}" + ", value2: {" + DEFAULT_KEY2 + ":" + DEFAULT_VALUE2 + "}");
					break;
				case R.id.buttonParameterAll:
					Tapjoy.trackEvent(DEFAULT_CATEGORY, DEFAULT_EVENT_NAME, DEFAULT_PARAM1, DEFAULT_PARAM2, DEFAULT_KEY, DEFAULT_VALUE, DEFAULT_KEY2, DEFAULT_VALUE2, DEFAULT_KEY3,
							DEFAULT_VALUE3);
					textViewOutput.setText("Sent track event with name: " + DEFAULT_EVENT_NAME + ", param1: " + DEFAULT_PARAM1 + ", param2: " + DEFAULT_PARAM2 + ", value: {"
							+ DEFAULT_KEY + ":" + DEFAULT_VALUE + "}" + ", value2: {" + DEFAULT_KEY2 + ":" + DEFAULT_VALUE2 + "}" + ", value3: {" + DEFAULT_KEY3 + ":"
							+ DEFAULT_VALUE3 + "}");
					break;
				case R.id.buttonMainTab:
					Intent mainIntent = new Intent(getApplicationContext(), TapjoyEasyApp.class);
					startActivity(mainIntent);
					break;
				case R.id.buttonUserTab:
					Intent userIntent = new Intent(getApplicationContext(), UserActivity.class);
					startActivity(userIntent);
					break;
			}
		}
	}

	/**
	 * Grabs references to UI elements and sets up listeners
	 */
	private void setupUI() {
		// output text view
		textViewOutput = (TextView) findViewById(R.id.textViewOutput);

		// Buttons
		((Button) findViewById(R.id.buttonMainTab)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonUserTab)).setOnClickListener(this);

		((Button) findViewById(R.id.buttonBasic)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonParameter1)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonValue)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonParameter1And2)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonParameter1WithValue1)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonParameter1And2WithValue1)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonParameter1And2WithValue1And2)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonParameterAll)).setOnClickListener(this);
	}
}
