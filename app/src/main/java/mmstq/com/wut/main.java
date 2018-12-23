package mmstq.com.wut;

import android.app.Dialog;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.HashMap;
import pl.droidsonroids.gif.GifImageView;

public class main extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Constant.cellNumber = getSharedPreferences("Phone", 0).getString("Phone_No", "78dki765");
		
		Log.d("wutcell",Constant.cellNumber);
		if (FirebaseAuth.getInstance().getCurrentUser() == null) {
			LogIn.run(main.this);
			return;
		} else {
			setContentView(R.layout.main);
		}
		Constant.updateNotification(main.this);
		Button survey = findViewById(R.id.surveyButton);
		Button im = findViewById(R.id.im);
		MyAlarm myAlarm = new MyAlarm();
		
		final String value = myAlarm.getValue(main.this, Constant.cellNumber);
		
		if (value != null && !value.equals("")) {
			findViewById(R.id.admin).setVisibility(View.VISIBLE);
			Constant.isAdmin = true;
			survey.setEnabled(true);
			im.setEnabled(true);
		}
		
		
		FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut");
		survey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(main.this, survey.class));
			}
		});
		findViewById(R.id.subs).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(main.this, subs.class));
			}
		});
		findViewById(R.id.view_survey).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(main.this, buy.class));
			}
			
		});
		im.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText heading, description;
				final Button pBtn;
				final Dialog dialog;
				final ProgressBar pb;
				dialog = new Dialog(main.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.im_dialog);
				
				pb = dialog.findViewById(R.id.progress_bar);
				
				final GifImageView gib = dialog.findViewById(R.id.gifImageView);
				heading = dialog.findViewById(R.id.heading);
				description = dialog.findViewById(R.id.description);
				pBtn = dialog.findViewById(R.id.pBtn);
				pBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						String head, desc;
						if (heading.getText().toString().equals("")) {
							heading.setError("Field is necessary");
							heading.requestFocus();
							return;
						} else {
							head = heading.getText().toString();
						}
						if (description.getText().toString().equals("")) {
							description.setError("Field is necessary");
							description.requestFocus();
							return;
						} else {
							desc = description.getText().toString();
						}
						
						pBtn.setVisibility(View.GONE);
						pb.setVisibility(View.VISIBLE);
						HashMap<String, Object> map = new HashMap<>();
						map.put("name", head);
						map.put("text", desc + "\n(Regards: " + value + ")");
						DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("im");
						fd.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								pb.setVisibility(View.GONE);
								gib.setImageResource(R.drawable.sent);
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
									}
								}, 1850);
								
							}
						}).addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								pb.setVisibility(View.GONE);
								pBtn.setVisibility(View.VISIBLE);
								pBtn.setText("Re-Try");
								Toast.makeText(main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
				dialog.show();
			}
		});
	}
	
	
}
