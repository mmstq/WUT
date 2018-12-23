package mmstq.com.wut;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

class LogIn {

   static void run(final Context context) {
      Constant.updateNotification(context);
      final SharedPreferences.Editor editor;
      final EditText phone;
      TextView title1;
      Button nBtn, pBtn;
      final Dialog dialog;
      dialog = new Dialog(context);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      dialog.setCancelable(false);
      dialog.setContentView(R.layout.login_dialog);

      title1 = dialog.findViewById(R.id.title);
      phone = dialog.findViewById(R.id.phone_number);
      nBtn = dialog.findViewById(R.id.negativeBtn);
      pBtn = dialog.findViewById(R.id.positiveBtn);
      editor = context.getSharedPreferences("Phone", 0).edit();
      phone.setHint("+91");

      title1.setText("Verification");
      pBtn.setText("Exit");
      pBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            dialog.dismiss();
            System.exit(0);
         }
      });
      nBtn.setText("Send OTP");
      nBtn.setVisibility(View.VISIBLE);
      nBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String phone_number = phone.getText().toString().trim();
            if (phone_number.isEmpty() || phone_number.length() < 10) {
               phone.setError("Number Is Required");
               phone.requestFocus();
               return;
            }

            Constant.cellNumber = phone_number;
            editor.putString("Phone_No", phone_number);
            editor.apply();
            phone.setText("");
            dialog.dismiss();
            VerifyOTP verifyOTP = new VerifyOTP();
            verifyOTP.run(context);

         }
      });
      dialog.show();
   }
}
