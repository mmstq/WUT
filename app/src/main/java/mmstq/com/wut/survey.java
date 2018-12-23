package mmstq.com.wut;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class survey extends AppCompatActivity {
    private HashMap<String,Object> map = new HashMap<>();
    private boolean byes_no=true,bsay=true,bwut=true, bwut_opt =false,ready=true;
    private ImageView like,dislike,say;
    private EditText wut;
    private TextView like_text,dislike_text,say_text;
    private CheckBox wut_field,wut_nec,yes_no,inc_say;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey);

        wut_field = findViewById(R.id.inc_field);
        wut_nec = findViewById(R.id.field_necessary);
        yes_no = findViewById(R.id.inc_yn);
        inc_say = findViewById(R.id.inc_say);
        like=findViewById(R.id.like);
        dislike=findViewById(R.id.dislike);
        wut = findViewById(R.id.survey_wut);
        say=findViewById(R.id.say);
        like_text=findViewById(R.id.like_text);
        dislike_text=findViewById(R.id.dislike_text);
        say_text=findViewById(R.id.say_text);

        wut_field.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(wut_field.isChecked()){

                    findViewById(R.id.survey_wut).setVisibility(View.INVISIBLE);
                    wut_nec.setEnabled(false);
                    bwut=false;
                    bwut_opt =false;
                    if(yes_no.isChecked()){
                       yes_no.setChecked(false);
                    }
                }else {
                    findViewById(R.id.survey_wut).setVisibility(View.VISIBLE);
                    if(!inc_say.isChecked()||!yes_no.isChecked()){
                       wut_nec.setEnabled(true);
                    }
                    bwut=true;
                }
            }
        });
        wut_nec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(wut_nec.isChecked()){
                    bwut_opt =true;
                    wut.setHint("What You Think (Optional)");
                }else {
                    wut.setHint("What You Think (Mandatory)");
                    bwut_opt =false;
                }
            }
        });
        yes_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(yes_no.isChecked()){
                   if(wut_field.isChecked()){
                      wut_field.setChecked(false);
                   }
                   wut_nec.setChecked(false);
                   wut_nec.setEnabled(false);
                   bwut_opt =false;
                   byes_no=false;
                   dislike.setImageResource(R.drawable.dislike);
                   dislike_text.setTextColor(getResources().getColor(R.color.darkGrey));
                   like.setImageResource(R.drawable.like);
                   like_text.setTextColor(getResources().getColor(R.color.darkGrey));
                }else{
                   if(wut_field.isChecked()){
                      wut_nec.setEnabled(false);
                   }else if(inc_say.isChecked()){
                      wut_nec.setEnabled(true);
                   }else {
                      wut_nec.setEnabled(true);
                   }

                    byes_no=true;
                    dislike.setImageResource(R.drawable.dislike_red);
                    dislike_text.setTextColor(getResources().getColor(R.color.Red));
                    like.setImageResource(R.drawable.like_green);
                    like_text.setTextColor(getResources().getColor(R.color.green));
                }
            }
        });
        inc_say.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(inc_say.isChecked()){
                   if(yes_no.isChecked()){
                      wut_nec.setChecked(false);
                      wut_nec.setEnabled(false);
                      bwut_opt =false;
                   }
                    bsay=false;
                    say.setImageResource(R.drawable.can_say);
                    say_text.setTextColor(getResources().getColor(R.color.darkGrey));
                }else {
                    if(!wut_field.isChecked()){
                       wut_nec.setEnabled(true);
                    }if(yes_no.isChecked()){
                       wut_nec.setEnabled(false);
                    }
                    bsay=true;
                    say.setImageResource(R.drawable.cant_say);
                    say_text.setTextColor(getResources().getColor(R.color.orange));
                }
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final ProgressDialog builder = new ProgressDialog(survey.this);
               builder.setMessage("Submitting...");
               builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               builder.setCancelable(true);
               builder.setProgress(0);

                map.clear();
                map.put("likeDislike",byes_no);
                map.put("say",bsay);
                map.put("wut",bwut);
                map.put("wutOptional",bwut_opt);
                map.put("heading",getHeading());
                map.put("open",true);
                map.put("noLike",0);
                map.put("noDislike",0);
                map.put("noSay",0);
                map.put("noWut",0);
                map.put("description",getDescription());

                if(ready){
                   long timestamp = System.currentTimeMillis();
                   map.put("surveyNo", timestamp);
                   builder.show();
                   DocumentReference cr = FirebaseFirestore.getInstance().collection("Survey").document(String.valueOf(timestamp));

                   cr.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                         HashMap<String,Object> map = new HashMap<>();
                         map.put("name","What You Think..?");
                         map.put("text",getHeading());
                         builder.dismiss();
                         DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("messages");
                         fd.push().setValue(map);

                      }

                   }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                         builder.dismiss();
                         Toast.makeText(survey.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                   });
                }
            }
        });
    }
    private String getHeading(){
        EditText heading = findViewById(R.id.survey_heading);
        String head= heading.getText().toString();

        if(head.equals("")){
            heading.setError("Enter Heading");
            heading.requestFocus();
           ready = false;
            return null;
        }else{
           ready= true;
           return head;
        }

    }
     private String getDescription(){
        EditText description = findViewById(R.id.survey_description);
        String des = description.getText().toString();

         if(des.equals("")){
             description.setError("Enter Heading");
             description.requestFocus();
             ready = false;
             return null;
         }else{
            ready = true;
            return des;
         }
    }
}
