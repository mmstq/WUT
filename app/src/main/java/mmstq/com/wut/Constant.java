package mmstq.com.wut;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

class Constant {
	static int mRealSizeWidth;
	static String cellNumber;
	static String share_link;
	static boolean isAdmin = false;
	
	
	static void updateNotification(final Context context) {
		WindowManager windowManager =
				  (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();
		Point outPoint = new Point();
		if (Build.VERSION.SDK_INT >= 19) {
			// include navigation bar
			display.getRealSize(outPoint);
		} else {
			// exclude navigation bar
			display.getSize(outPoint);
		}
		if (outPoint.y > outPoint.x) {
			mRealSizeWidth = outPoint.x - 20;
		} else {
			mRealSizeWidth = outPoint.y - 20;
		}
		final HashMap<String, String> map = new HashMap<>();
		DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
		DatabaseReference uidRef = rootRef.child("tt");
		ValueEventListener valueEventListener = new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot ds : dataSnapshot.getChildren()) {
					String value = ds.child("value").getValue(String.class);
					String key = ds.child("key").getValue(String.class);
					map.put(key, value);
				}
				Log.d("TAG", String.valueOf(map));
				addHashMap(context, map);
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		};
		uidRef.addListenerForSingleValueEvent(valueEventListener);
	}
	
	private static void addHashMap(Context context, HashMap<String, String> map) {
		
		Gson gson = new Gson();
		SharedPreferences prefs = context.getSharedPreferences("notification", MODE_PRIVATE);
		//convert to string using Gson
		String hashMapString = gson.toJson(map);
		Log.d("cons",hashMapString);
		//save in shared prefs
		prefs.edit().putString("hashString", hashMapString).apply();
		
	}
	
}
