package mmstq.com.wut;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;


public class Update {

    private  DocumentSnapshot ds;
    private  double version,version_number;
    private String title,des,link;



    public void onCheck(final Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection("Update").document("Params");
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    ds =task.getResult();
                    boolean is_available = false;
                    if (ds != null) {
                        is_available = ds.getBoolean("is_available");
                        version_number = Double.parseDouble(ds.getString("version"));
                        Constant.share_link = ds.getString("short_link");
                        link = ds.getString("url");
                        title = ds.getString("title");
                        des = ds.getString("description");
                        des = des != null ? des.replace("/", "\n") : "";
                    }
                    try {
                        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        version = Double.parseDouble(pInfo.versionName);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(is_available&& version <version_number){
                        TextView message1, title1,ver;
                        Button pBtn;
                        final Dialog dialog;
                        dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.update_dialog);

                        title1 = dialog.findViewById(R.id.title);
                        ver = dialog.findViewById(R.id.ver);
                        message1 = dialog.findViewById(R.id.message);
                        pBtn = dialog.findViewById(R.id.positiveBtn);

                        ver.setText(MessageFormat.format("v{0}", ds.getString("version")));
                        title1.setText(title);
                        message1.setText(des);
                        pBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        }
                    }
                }
        });
    }
}
