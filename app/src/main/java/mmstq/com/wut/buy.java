package mmstq.com.wut;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class buy extends AppCompatActivity {
	private Activity activity;
	private Adapter adapterRecycler;
	private RecyclerView rv;
	private ProgressDialog pd;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy);
		
		activity = buy.this;
		pd = new ProgressDialog(activity);
		pd.setMessage("Loading Survey");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		pd.setProgress(0);
		
		rv = findViewById(R.id.listview);
		
		onRun();
		
	}
	
	private void onRun() {
		pd.show();
		Query q = FirebaseFirestore.getInstance().collection("Survey").orderBy("surveyNo", Query.Direction.DESCENDING);
		
		FirestoreRecyclerOptions<myData> options = new FirestoreRecyclerOptions.Builder<myData>()
				  .setQuery(q, myData.class)
				  .build();
		adapterRecycler = new Adapter(options);
		rv.setHasFixedSize(true);
		rv.setLayoutManager(new LinearLayoutManager(activity));
		rv.setAdapter(adapterRecycler);
		adapterRecycler.notifyDataSetChanged();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				pd.dismiss();
			}
		}, 800);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		adapterRecycler.startListening();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		adapterRecycler.stopListening();
	}
}

