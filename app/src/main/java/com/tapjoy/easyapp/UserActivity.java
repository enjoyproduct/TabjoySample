package com.tapjoy.easyapp;

import com.tapjoy.Tapjoy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class UserActivity extends Activity implements View.OnClickListener, OnItemSelectedListener, OnFocusChangeListener {

	// UI 
	private Button buttonMainTab;
	private Button buttonEventTab;
	private Button buttonUserInfoSet;
	private Button buttonUserInfoClear;
	private Button buttonUserTagsAdd;
	private Button buttonUserTagsSub;
	private Button buttonUserTagsClear;

	private EditText editTextUserId;
	private EditText editTextUserLevel;
	private EditText editTextUserFriends;
	private EditText editTextUserTag;
	private EditText editTextCohort;

	private Spinner spinnerCohorts;

	// Saved user input values
	private String inputUserId;
	private int inputUserLevel;
	private int inputUserFriends;

	private String[] cohortValues = new String[5];
	private int currentCohortIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);
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
	 * Sets user info
	 */
	private void setUserInfo() {
		// Store current input
		storeInput(getCurrentFocus().getId());
		
		// Set user info
		Tapjoy.setUserID(inputUserId);
		Tapjoy.setUserLevel(inputUserLevel);
		Tapjoy.setUserFriendCount(inputUserFriends);

		// Set all user tags
		//Tapjoy.setUserTags(new HashSet<String>(userTagsValues));

		// Set User cohorts
		for (int i = 0; i < cohortValues.length; i++) {
			Tapjoy.setUserCohortVariable(i + 1, cohortValues[i]);
		}
		
		Toast.makeText(this, "User data set", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Clears all input fields
	 */
	private void clearInputs() {
		// Clear edit texts
		cohortValues = new String[5];
		editTextUserId.setText("");
		editTextUserLevel.setText("");
		editTextUserFriends.setText("");
		editTextCohort.setText("");
		
		// Clear stored values
		inputUserId = "";
		inputUserLevel = 0;
		inputUserFriends = 0;

		// Set User cohorts
		for (int i = 0; i < cohortValues.length; i++) {
			cohortValues[i] = "";
		}
		Toast.makeText(this, "Inputs cleared", Toast.LENGTH_SHORT).show();
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
				case R.id.buttonMainTab:
					Intent mainIntent = new Intent(getApplicationContext(), TapjoyEasyApp.class);
					startActivity(mainIntent);
					break;
				case R.id.buttonEventTab:
					Intent userIntent = new Intent(getApplicationContext(), EventActivity.class);
					startActivity(userIntent);
					break;
				case R.id.buttonUserTagAdd:
					Tapjoy.addUserTag(editTextUserTag.getText().toString());
					editTextUserTag.setText("");
					break;
				case R.id.buttonUserTagSubtract:
					Tapjoy.removeUserTag(editTextUserTag.getText().toString());
					editTextUserTag.setText("");
					break;
				case R.id.buttonUserTagClear:
					Tapjoy.clearUserTags();
					editTextUserTag.setText("");
					break;
				case R.id.buttonUserInfoSet:
					setUserInfo();
					break;
				case R.id.buttonUserInfoClear:
					clearInputs();
					break;
			}
		}
	}

	/**
	 * Store user input when edit text loses focus
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			storeInput(v.getId());
		}
	}

	/**
	 * Changes text of {@link #editTextCohort}
	 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int index, long arg3) {
		// store current input and load new input 
		cohortValues[currentCohortIndex] = editTextCohort.getText().toString();
		currentCohortIndex = index;
		editTextCohort.requestFocus();
		editTextCohort.setText(cohortValues[index]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	/**
	 * Store user input to use in track event calls.
	 * 
	 * @param id
	 *            the id of the edit text to get input from
	 */
	private void storeInput(int id) {
		switch (id) {
			case R.id.inputUserId: {
				inputUserId = editTextUserId.getText().toString();
			}
			case R.id.inputUserLevel: {
				String userLevel = editTextUserLevel.getText().toString();
				if (userLevel != null && !userLevel.equals("")) {
					inputUserLevel = Integer.parseInt(userLevel);
				}
			}
			case R.id.inputUserFriends: {
				String userFriends = editTextUserFriends.getText().toString();
				if (userFriends != null && !userFriends.equals("")) {
					inputUserFriends = Integer.parseInt(userFriends);
				}
			}
			case R.id.inputCohort: {
				cohortValues[currentCohortIndex] = editTextCohort.getText().toString();
			}
		}
	}

	/**
	 * Grabs references to UI elements and sets up listeners
	 */
	private void setupUI() {
		buttonMainTab = (Button) findViewById(R.id.buttonMainTab);
		buttonMainTab.setOnClickListener(this);
		buttonEventTab = (Button) findViewById(R.id.buttonEventTab);
		buttonEventTab.setOnClickListener(this);
		buttonUserInfoSet = (Button) findViewById(R.id.buttonUserInfoSet);
		buttonUserInfoSet.setOnClickListener(this);
		buttonUserInfoClear = (Button) findViewById(R.id.buttonUserInfoClear);
		buttonUserInfoClear.setOnClickListener(this);

		// User tags
		buttonUserTagsAdd = (Button) findViewById(R.id.buttonUserTagAdd);
		buttonUserTagsAdd.setOnClickListener(this);

		buttonUserTagsSub = (Button) findViewById(R.id.buttonUserTagSubtract);
		buttonUserTagsSub.setOnClickListener(this);

		buttonUserTagsClear = (Button) findViewById(R.id.buttonUserTagClear);
		buttonUserTagsClear.setOnClickListener(this);

		editTextUserId = (EditText) findViewById(R.id.inputUserId);
		editTextUserId.setOnFocusChangeListener(this);

		editTextUserLevel = (EditText) findViewById(R.id.inputUserLevel);
		editTextUserLevel.setOnFocusChangeListener(this);

		editTextUserFriends = (EditText) findViewById(R.id.inputUserFriends);
		editTextUserFriends.setOnFocusChangeListener(this);

		editTextUserTag = (EditText) findViewById(R.id.inputUserTag);
		editTextUserTag.setOnFocusChangeListener(this);

		editTextCohort = (EditText) findViewById(R.id.inputCohort);
		editTextCohort.setOnFocusChangeListener(this);

		spinnerCohorts = (Spinner) findViewById(R.id.spinnerCohorts);
		spinnerCohorts.setOnItemSelectedListener(this);

		CheckBox pushNotificationEnabled = (CheckBox) findViewById(R.id.pushNotificationEnabled);
		pushNotificationEnabled.setChecked(!Tapjoy.isPushNotificationDisabled());
		pushNotificationEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Tapjoy.setPushNotificationDisabled(!isChecked);
			}
		});
	}
}
