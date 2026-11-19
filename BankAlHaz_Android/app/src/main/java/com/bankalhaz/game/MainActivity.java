package com.bankalhaz.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int gold=Color.rgb(255,211,107);
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        showWelcome();
    }
    private GradientDrawable bg(int color,int stroke){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{color,0xff09251f});d.setCornerRadius(28);d.setStroke(3,stroke);return d;}
    private void showWelcome(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(28,22,28,22);root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0xff17613e,0xff061b18}));
        TextView crown=new TextView(this);crown.setText("👑  🎲  👑");crown.setTextSize(42);crown.setGravity(Gravity.CENTER);root.addView(crown);
        TextView title=new TextView(this);title.setText("بنك الحظ");title.setTextColor(gold);title.setTextSize(38);title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);title.setGravity(Gravity.CENTER);root.addView(title);
        TextView sub=new TextView(this);sub.setText("لعبة العائلة والأصدقاء");sub.setTextColor(Color.WHITE);sub.setTextSize(18);sub.setGravity(Gravity.CENTER);sub.setPadding(0,4,0,24);root.addView(sub);
        Button guest=makeButton("الدخول كزائر  👤");root.addView(guest,new LinearLayout.LayoutParams(-1,58));guest.setOnClickListener(v->setContentView(new GameView(this)));
        Button google=makeButton("المتابعة باستخدام Google  G");LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(-1,58);gp.topMargin=14;root.addView(google,gp);
        google.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("تسجيل الدخول عبر Google").setMessage("ميزة تسجيل Google تحتاج ربط التطبيق بمشروع Google/Firebase وإضافة OAuth Client ID وبصمة توقيع التطبيق. لم أضع بيانات اعتماد وهمية. يمكن اللعب الآن كزائر، وبعد تزويدي بإعدادات Firebase العامة نكمل الربط بأمان.").setPositiveButton("حسناً",null).show());
        TextView note=new TextView(this);note.setText("يمكنك اللعب كزائر دون إنشاء حساب");note.setTextColor(0xffc8dfd0);note.setTextSize(13);note.setGravity(Gravity.CENTER);note.setPadding(0,18,0,0);root.addView(note);
        setContentView(root);
    }
    private Button makeButton(String label){Button b=new Button(this);b.setText(label);b.setTextSize(17);b.setTextColor(0xff38240a);b.setAllCaps(false);b.setBackground(bg(0xffffd36b,0xffffedb0));return b;}
}