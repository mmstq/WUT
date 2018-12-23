package mmstq.com.wut;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class Adapter extends FirestoreRecyclerAdapter<myData, Adapter.dataHolder> {
	private int finalOpinion = 0;
	private String finalOpstr = "";
	private DatabaseReference dr;
	private Context context;
	int length = Constant.mRealSizeWidth;
	View view;
	
	/**
	 * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
	 * FirestoreRecyclerOptions} for configuration options.
	 *
	 * @param options
	 */
	
	Adapter(@NonNull FirestoreRecyclerOptions<myData> options) {
		super(options);
	}
	
	@Override
	protected void onBindViewHolder(@NonNull final dataHolder holder, int position, @NonNull final myData model) {
		
		int vote;
		if (model.isLikeDislike()) {
			vote = model.getNoSay() + model.getNoDislike() + model.getNoLike();
			if ((vote / 74) >= 1) {
				view.getLayoutParams().width = length;
			} else {
				view.getLayoutParams().width = (vote * length) / 74;
			}
		} else {
			vote = model.getNoWut() + model.getNoSay();
			if (vote / 74 >= 1) {
				view.getLayoutParams().width = length;
			} else {
				view.getLayoutParams().width = (vote * length) / 74;
			}
		}
		if (!model.isOpen()) {
			view.setBackgroundColor(context.getResources().getColor(R.color.Red));
		}
		holder.textTop.setText(model.getHeading());
		holder.textTop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Constant.isAdmin) {
					adminOption(model, holder.textTop);
				}
			}
		});
		holder.textMid.setText(model.getDescription());
		holder.textMid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Constant.isAdmin) {
					adminOption(model, holder.textMid);
				}
			}
		});
		holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(model);
			}
		});
	}
	
	class dataHolder extends RecyclerView.ViewHolder {
		TextView textTop, textMid;
		ImageView imageView;
		
		dataHolder(@NonNull View itemView) {
			super(itemView);
			view = itemView.findViewById(R.id.progress);
			textTop = itemView.findViewById(R.id.upperText);
			textMid = itemView.findViewById(R.id.lowertext);
			imageView = itemView.findViewById(R.id.book);
		}
	}
	
	@NonNull
	@Override
	public dataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		context = viewGroup.getContext();
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
		return new dataHolder(v);
	}
	
	
	@SuppressLint("SimpleDateFormat")
	private void showDialog(final myData model) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(model.getSurveyNo());
		
		final int[] noLike = new int[1];
		final int[] noDislike = new int[1];
		final int[] noSay = new int[1];
		
		noDislike[0] = model.getNoDislike();
		noSay[0] = model.getNoSay();
		final boolean[] like = {false};
		final boolean[] dislike = {false};
		final boolean[] say = {false};
		final boolean[] wut_optional = {model.isWutOptional()};
		final TextView title, description, date_text, say_text, dislike_text, like_text;
		final Button pBtn;
		final ImageView say_img, like_img, dislike_img;
		final RelativeLayout rlld, rlsay;
		final EditText wut_field;
		final Dialog dialog;
		final ProgressBar pb;
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.ads_dialog);
		
		pb = dialog.findViewById(R.id.progress_bar);
		rlld = dialog.findViewById(R.id.rl1);
		rlsay = dialog.findViewById(R.id.ll1);
		wut_field = dialog.findViewById(R.id.survey_wut);
		say_text = dialog.findViewById(R.id.say_text);
		dislike_text = dialog.findViewById(R.id.dislike_text);
		like_text = dialog.findViewById(R.id.like_text);
		say_img = dialog.findViewById(R.id.say);
		like_img = dialog.findViewById(R.id.like);
		dislike_img = dialog.findViewById(R.id.dislike);
		title = dialog.findViewById(R.id.heading);
		description = dialog.findViewById(R.id.desc);
		date_text = dialog.findViewById(R.id.date_text);
		pBtn = dialog.findViewById(R.id.pBtn);
		title.setText(model.getHeading());
		description.setText(model.getDescription());
		date_text.setText(new SimpleDateFormat("E, dd-MMM yy, hh:mm a").format(calendar.getTime()));
		
		if (!model.isOpen()) {
			pBtn.setEnabled(false);
			wut_field.setEnabled(false);
			pBtn.setText("Survey Closed");
		}
		
		if (!model.isLikeDislike()) {
			rlld.setVisibility(View.GONE);
		}
		
		if (!model.isSay()) {
			rlsay.setVisibility(View.GONE);
		}
		
		if (model.isWut()) {
			wut_field.setVisibility(View.VISIBLE);
			if (model.isWutOptional()) {
				wut_field.setHint("What You Think (Optional)");
			}
		}
		
		pBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				final HashMap<String, Object> map = new HashMap<>();
				
				if (like[0]) {
					finalOpinion = noLike[0];
					finalOpstr = "noLike";
					if (model.isWut() && !wut_optional[0] && wut_field.getText().toString().equals("")) {
						wut_field.setError("Field Is Necessary");
						wut_field.requestFocus();
						return;
						
					}
				} else if (dislike[0]) {
					finalOpinion = noDislike[0];
					finalOpstr = "noDislike";
					if (model.isWut() && !wut_optional[0] && wut_field.getText().toString().equals("")) {
						wut_field.setError("Field Is Necessary");
						wut_field.requestFocus();
						return;
					}
					
				} else if ((!model.isLikeDislike())) {
					if (!say[0]) {
						wut_field.setHint("What You Think (Necessary)");
						if (wut_field.getText().toString().equals("")) {
							wut_field.setError("Field Is Necessary");
							wut_field.requestFocus();
							return;
						}
						finalOpinion = model.getNoWut() + 1;
						finalOpstr = "noWut";
					}
					
				} else if (say[0]) {
					finalOpinion = noSay[0];
					finalOpstr = "noSay";
					
				} else {
					Toast.makeText(context, "Select One Opinion", Toast.LENGTH_SHORT).show();
					return;
				}
				
				pb.setVisibility(View.VISIBLE);
				pBtn.setVisibility(View.GONE);
				
				map.put("phone", Constant.cellNumber);
				map.put("wut", wut_field.getText().toString());
				
				dr = FirebaseDatabase.getInstance().getReference().child("Feedback").child(String.valueOf(model.getSurveyNo()));
				dr.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists()) {
							pb.setVisibility(View.GONE);
							pBtn.setVisibility(View.VISIBLE);
							pBtn.setEnabled(false);
							wut_field.setText("");
							wut_field.setEnabled(false);
							pBtn.setText("Voted Already");
							
						} else {
							dr.child(Constant.cellNumber).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									DocumentReference query = FirebaseFirestore.getInstance()
											  .collection("Survey")
											  .document(String.valueOf(model.getSurveyNo()));
									query.update(finalOpstr, finalOpinion).addOnSuccessListener(new OnSuccessListener<Void>() {
										@Override
										public void onSuccess(Void aVoid) {
											
											pb.setVisibility(View.GONE);
											pBtn.setVisibility(View.VISIBLE);
											pBtn.setText("Done");
											pBtn.setEnabled(false);
											wut_field.setText("");
											dislike_text.setText(MessageFormat.format("{0}%", model.getNoDislike()));
											like_text.setText(MessageFormat.format("{0}%", model.getNoLike()));
											say_text.setText(MessageFormat.format("{0}%", model.getNoSay()));
											
											new android.os.Handler().postDelayed(new Runnable() {
												@Override
												public void run() {
													dialog.dismiss();
												}
											}, 2500);
											
										}
									}).addOnFailureListener(new OnFailureListener() {
										@Override
										public void onFailure(@NonNull Exception e) {
											Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
											pb.setVisibility(View.GONE);
											pBtn.setVisibility(View.VISIBLE);
											pBtn.setText("Submit Again");
											
										}
									}).addOnFailureListener(new OnFailureListener() {
										@Override
										public void onFailure(@NonNull Exception e) {
											pb.setVisibility(View.GONE);
											pBtn.setVisibility(View.VISIBLE);
											pBtn.setText("Submit Again");
											Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
										}
									});
								}
							});
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		say_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				say_img.setImageResource(R.drawable.cant_say);
				say_text.setTextColor(v.getResources().getColor(R.color.orange));
				dislike_text.setTextColor(v.getResources().getColor(R.color.darkGrey));
				like_text.setTextColor(v.getResources().getColor(R.color.darkGrey));
				dislike_img.setImageResource(R.drawable.dislike);
				like_img.setImageResource(R.drawable.like);
				like[0] = false;
				dislike[0] = false;
				say[0] = true;
				wut_field.setText("");
				wut_field.setVisibility(View.GONE);
				noSay[0] = model.getNoSay() + 1;
				
			}
		});
		dislike_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				dislike_img.setImageResource(R.drawable.dislike_red);
				dislike_text.setTextColor(v.getResources().getColor(R.color.Red));
				like_text.setTextColor(v.getResources().getColor(R.color.darkGrey));
				say_text.setTextColor(v.getResources().getColor(R.color.darkGrey));
				say_img.setImageResource(R.drawable.can_say);
				like_img.setImageResource(R.drawable.like);
				like[0] = false;
				dislike[0] = true;
				say[0] = false;
				if (model.isWut()) {
					wut_field.setVisibility(View.VISIBLE);
					if (model.isWutOptional()) {
						wut_field.setHint("What You Think (Optional)");
					}
				}
				noDislike[0] = model.getNoDislike() + 1;
			}
		});
		like_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				like_img.setImageResource(R.drawable.like_green);
				like_text.setTextColor(v.getResources().getColor(R.color.green));
				say_text.setTextColor(v.getResources().getColor(R.color.darkGrey));
				dislike_text.setTextColor(v.getResources().getColor(R.color.darkGrey));
				say_img.setImageResource(R.drawable.can_say);
				dislike_img.setImageResource(R.drawable.dislike);
				like[0] = true;
				dislike[0] = false;
				say[0] = false;
				if (model.isWut()) {
					wut_field.setVisibility(View.VISIBLE);
					if (model.isWutOptional()) {
						wut_field.setHint("What You Think (Optional)");
					}
				}
				noLike[0] = model.getNoLike() + 1;
			}
		});
		dialog.show();
		
	}
	
	private void adminOption(final myData model, TextView textTop) {
		final PopupMenu menu = new PopupMenu(context, textTop);
		menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				DocumentReference query = FirebaseFirestore.getInstance()
						  .collection("Survey")
						  .document(String.valueOf(model.getSurveyNo()));
				switch (item.getItemId()) {
					case R.id.delete:
						
						query.update("likeDislike", FieldValue.delete());
						query.update("say", FieldValue.delete());
						query.update("wut", FieldValue.delete());
						query.update("wutOptional", FieldValue.delete());
						query.update("heading", FieldValue.delete());
						query.update("open", FieldValue.delete());
						query.update("noLike", FieldValue.delete());
						query.update("noDislike", FieldValue.delete());
						query.update("noSay", FieldValue.delete());
						query.update("description", FieldValue.delete());
						query.update("surveyNo", FieldValue.delete());
						query.update("noWut", FieldValue.delete());
						
						break;
					case R.id.close:
						query.update("open", false);
						break;
					case R.id.open:
						query.update("open", true);
						break;
					case R.id.response:
						response(String.valueOf(model.getSurveyNo()));
						break;
					default:
						break;
				}
				return true;
			}
		});
		menu.show();
	}
	
	private void response(String node) {
		final List<String> values = new ArrayList<>();
		DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
		DatabaseReference uidRef = rootRef.child("Feedback").child(node);
		final ValueEventListener valueEventListener = new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot ds : dataSnapshot.getChildren()) {
					String value = ds.child("wut").getValue(String.class);
					if (!value.equals("")) {
						values.add(value);
					}
					
					
				}
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, values);
				
				final ListView pb;
				final Dialog dialog;
				dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.response);
				
				if (values.isEmpty()) {
					TextView t = dialog.findViewById(R.id.text);
					t.setText("no response");
				}
				pb = dialog.findViewById(R.id.listview);
				pb.setAdapter(arrayAdapter);
				dialog.show();
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		};
		uidRef.addListenerForSingleValueEvent(valueEventListener);
		
	}
	
}

