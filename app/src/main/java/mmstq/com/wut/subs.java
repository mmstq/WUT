package mmstq.com.wut;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class subs extends AppCompatActivity {
	private Calendar calendar;
	private TextView bt, lt, dt, ttt, imt, st, hdt;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Switch bfood, lfood, dfood, timeTable, im, survey, holiday;
	private int foodBreakfast = 0, foodLunch = 1, foodDinner = 2, timetableID = 3, holidayID = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subs);
		calendar = Calendar.getInstance();
		sp = getSharedPreferences("Phone", 0);
		editor = sp.edit();
		
		
		
		bfood = findViewById(R.id.bFood);
		lfood = findViewById(R.id.lFood);
		dfood = findViewById(R.id.dFood);
		timeTable = findViewById(R.id.sTimeTable);
		im = findViewById(R.id.im);
		survey = findViewById(R.id.survey);
		holiday = findViewById(R.id.holiday);
		imt = findViewById(R.id.imtext);
		st = findViewById(R.id.surveytext);
		bt = findViewById(R.id.bfoodtext);
		lt = findViewById(R.id.lfoodtext);
		dt = findViewById(R.id.dfoodtext);
		ttt = findViewById(R.id.tttext);
		hdt = findViewById(R.id.hdtext);
		
		checkSwitch();
		
		timeTable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (timeTable.isChecked()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							showTimePicker(timetableID, timeTable);
							
						}
					}, 200);
				} else {
					cancelAlarm(timetableID);
				}
			}
		});
		bfood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (bfood.isChecked()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							showTimePicker(foodBreakfast, bfood);
						}
					}, 200);
				} else {
					cancelAlarm(foodBreakfast);
				}
			}
		});
		lfood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (lfood.isChecked()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							showTimePicker(foodLunch, lfood);
						}
					}, 200);
				} else {
					cancelAlarm(foodLunch);
				}
			}
		});
		dfood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (dfood.isChecked()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							showTimePicker(foodDinner, dfood);
						}
					}, 200);
				} else {
					cancelAlarm(foodDinner);
				}
			}
		});
		im.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String state;
				if (im.isChecked()) {
					FirebaseMessaging.getInstance().subscribeToTopic("mmstq.com.wut.im");
					imt.setText(state = "On");
				} else {
					FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut.im");
					imt.setText(state = "Off");
					
				}
				Toast.makeText(subs.this, "Instant Messages : " + state, Toast.LENGTH_SHORT).show();
				editor.putBoolean("im", im.isChecked());
				editor.putString("imt", state);
				editor.apply();
			}
		});
		survey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String state;
				if (survey.isChecked()) {
					FirebaseMessaging.getInstance().subscribeToTopic("mmstq.com.wut.survey");
					st.setText(state = "On");
				} else {
					FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut.survey");
					st.setText(state = "Off");
				}
				Toast.makeText(subs.this, "WUT Messages : " + state, Toast.LENGTH_SHORT).show();
				editor.putBoolean("survey", survey.isChecked());
				editor.putString("st", state);
				editor.apply();
			}
		});
		holiday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (holiday.isChecked()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							showTimePicker(holidayID, holiday);
							
						}
					}, 200);
				} else {
					cancelAlarm(holidayID);
					
				}
				editor.apply();
			}
		});
	}
	
	public void setAlarm(long timeInMillis, int id, Context context, boolean run) {
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, MyAlarm.class);
		//creating a new intent specifying the broadcast receiver
		i.putExtra("Notification_ID", id);
		//creating a pending intent using the intent
		PendingIntent pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
		//setting the repeating alarm that will be fired every day
		if (am != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				
				am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				
				am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
			} else {
				am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
			}
			
			if (run) {
				editor.putBoolean(String.valueOf(id), true);
				editor.putLong("0" + id, timeInMillis);
				Toast.makeText(context, "Notification Set For " + formatTime(timeInMillis), Toast.LENGTH_SHORT).show();
				
				switch (id) {
					
					case 0:
						bt.setText(formatTime(timeInMillis));
						editor.putLong("bt", timeInMillis);
						break;
					case 1:
						lt.setText(formatTime(timeInMillis));
						editor.putLong("lt", timeInMillis);
						break;
					case 2:
						dt.setText(formatTime(timeInMillis));
						editor.putLong("dt", timeInMillis);
						break;
					case 3:
						ttt.setText(formatTime(timeInMillis));
						editor.putLong("ttt", timeInMillis);
						break;
					case 4:
						hdt.setText(formatTime(timeInMillis).replace("Everyday", "A Day Before Holiday"));
						editor.putLong("hdt", timeInMillis);
						break;
					default:
						break;
				}
				editor.apply();
			}
			
		}
	}
	
	private void cancelAlarm(int id) {
		Intent i = new Intent(this, MyAlarm.class);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		if (am != null) {
			am.cancel(pi);
			editor.putBoolean(String.valueOf(id), false);
			
			switch (id) {
				case 0:
					bt.setText(R.string.ns);
					editor.putLong("bt", 0);
					break;
				case 1:
					lt.setText(R.string.ns);
					editor.putLong("lt", 0);
					break;
				case 2:
					dt.setText(R.string.ns);
					editor.putLong("dt", 0);
					break;
				case 3:
					ttt.setText(R.string.ns);
					editor.putLong("ttt", 0);
					break;
				case 4:
					hdt.setText(R.string.ns);
					editor.putLong("hdt", 0);
					break;
				
				default:
					break;
			}
			editor.apply();
		}
	}
	
	private void showTimePicker(final int id, final Switch view) {
		final TimePicker timePicker;
		final Button pBtn, cancel;
		final Dialog dialog;
		
		dialog = new Dialog(subs.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.timepicker);
		
		timePicker = dialog.findViewById(R.id.tp);
		cancel = dialog.findViewById(R.id.negativeBtn);
		pBtn = dialog.findViewById(R.id.pBtn);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				view.setChecked(false);
			}
		});
		pBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Calendar calendar = Calendar.getInstance();
				
				if (android.os.Build.VERSION.SDK_INT >= 23) {
					calendar.set(calendar.get(Calendar.YEAR),
							  calendar.get(Calendar.MONTH),
							  calendar.get(Calendar.DAY_OF_MONTH),
							  timePicker.getHour(), timePicker.getMinute(), 0);
					
					if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
						calendar.add(Calendar.DATE, 1);
					}
					
				} else {
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
							  timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
					
					if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
						calendar.add(Calendar.DATE, 1);
					}
				}
				
				setAlarm(calendar.getTimeInMillis(), id, subs.this, true);
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	
	private void checkSwitch() {
		bfood.setChecked(sp.getBoolean("0", false));
		lfood.setChecked(sp.getBoolean("1", false));
		dfood.setChecked(sp.getBoolean("2", false));
		im.setChecked(sp.getBoolean("im", false));
		survey.setChecked(sp.getBoolean("survey", false));
		timeTable.setChecked(sp.getBoolean("3", false));
		holiday.setChecked(sp.getBoolean("4", false));
		bt.setText(formatTime(sp.getLong("bt", 0)));
		lt.setText(formatTime(sp.getLong("lt", 0)));
		dt.setText(formatTime(sp.getLong("dt", 0)));
		ttt.setText(formatTime(sp.getLong("ttt", 0)));
		imt.setText(sp.getString("imt", getString(R.string.ns)));
		st.setText(sp.getString("st", getString(R.string.ns)));
		if (sp.getLong("hdt", 0) != 0) {
			hdt.setText(formatTime(sp.getLong("hdt", 0)).replace("Everyday", "A Day Before Holiday"));
		} else {
			hdt.setText(formatTime(sp.getLong("hdt", 0)));
		}
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public String formatTime(long timeInMillis) {
		if (timeInMillis != 0) {
			calendar.setTimeInMillis(timeInMillis);
			return new SimpleDateFormat("hh:mm a").format(calendar.getTime()) + " | Everyday";
		} else {
			return "Off";
		}
	}
	
}
