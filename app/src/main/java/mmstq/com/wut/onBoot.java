package mmstq.com.wut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class onBoot extends BroadcastReceiver {
	private SharedPreferences sp;
	private subs s;
	private Calendar calendar;
	private int day, month, year;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action != null && action.equals("android.intent.action.BOOT_COMPLETED")) {
			sp = context.getSharedPreferences("Phone", 0);
			calendar = Calendar.getInstance();
			day = calendar.get(Calendar.DATE);
			month = calendar.get(Calendar.MONTH);
			year = calendar.get(Calendar.YEAR);
			s = new subs();
			
			for (int i = 0; i < 5; i++) {
				if (sp.getBoolean(String.valueOf(i), false)) {
					addAlarm(i, context);
				}
			}
		}
	}
	
	public void addAlarm(int key, Context context) {
		calendar.setTimeInMillis(sp.getLong("0" + key, 0));
		calendar.set(year,month,day);
		
		if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
			calendar.add(Calendar.DATE, 1);
			
		}
		
		s.setAlarm(calendar.getTimeInMillis(), key, context, false);
	}
	
}
