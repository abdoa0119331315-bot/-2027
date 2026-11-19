package com.bankalhaz.game;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.content.SharedPreferences;
import java.util.*;

public class GameView extends View {
 private final Paint p=new Paint(3);
 private final String[] names={"البداية","القاهرة","فرصة","إسطنبول","روما","ضريبة","باريس","مدريد","محطة","لندن","أمستردام","فرصة","برلين","السجن","فيينا","براغ","بودابست","ضريبة","نيويورك","تورونتو","فرصة","شيكاغو","محطة","سان فرانسيسكو","طوكيو","المزاد","سيول","فرصة","سنغافورة","المحكمة","دبي","أبوظبي","محطة","الدوحة","الرياض","ضريبة","مراكش","فرصة","كيب تاون","الخروج من السجن"};
 private final int[] prices={0,60,0,70,80,0,90,100,0,110,120,0,130,0,140,150,160,0,170,180,0,190,0,200,210,0,220,0,230,0,240,250,0,260,270,0,280,0,290,0};
 private final int[] groups={0,1,0,1,1,0,2,2,0,3,3,0,3,0,4,4,4,0,5,5,0,5,0,6,6,0,6,0,7,0,7,7,0,8,8,0,8,0,8,0};
 private final int[] palette={0,0xff8c5a46,0xffbd4b4b,0xffffa23b,0xffd1bd37,0xff56a9d7,0xff456fc1,0xff4c9d59,0xff9562b7};
 private static class Player{String name;int money=300,pos=0,jail=0,color;boolean ai,out=false;ArrayList<Integer>owned=new ArrayList<>();Player(String n,int c,boolean a){name=n;color=c;ai=a;}}
 private final ArrayList<Player> players=new ArrayList<>();private final Random random=new Random();private final SharedPreferences prefs; private final ToneGenerator tones=new ToneGenerator(AudioManager.STREAM_MUSIC,75); private String chatLine="\uD83D\uDC4B أهلاً بالجميع!"; private RectF chatRect=new RectF(),emojiRect=new RectF();
 private int turn=0,d1=1,d2=1,pending=-1;private boolean rolled=false,canBuy=false,gameOver=false;
 private String message="مرحباً بك في بنك الحظ! تحتاج إلى 6 للخروج من البداية.";
 private RectF board=new RectF(),rollRect=new RectF(),buyRect=new RectF(),endRect=new RectF(),centerRect=new RectF();private float tile;
 public GameView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);prefs=c.getSharedPreferences("bank_alhaz_save",Context.MODE_PRIVATE);reset();loadState();}
 private void reset(){players.clear();players.add(new Player("أنت",0xff16a9ec,false));players.add(new Player("الأحمر",0xffed4545,true));players.add(new Player("الأخضر",0xff25c65a,true));players.add(new Player("الذهبي",0xffffcf35,true));turn=0;rolled=canBuy=gameOver=false;pending=-1;message="ابدأ برمي النرد. تحتاج إلى رقم 6 للخروج.";invalidate();}
 private void txt(Canvas c,String s,float x,float y,float size,int color,Paint.Align a){p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextAlign(a);c.drawText(s,x,y,p);}
 private void box(Canvas c,RectF r,int color,float rad){p.setColor(color);c.drawRoundRect(r,rad,rad,p);}
 @Override protected void onDraw(Canvas c){float w=getWidth(),h=getHeight();p.setShader(new LinearGradient(0,0,w,h,0xff164b38,0xff061b18,Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);float side=Math.min(h-26,w*.69f);side=Math.max(260,side);float left=14,top=(h-side)/2;board.set(left,top,left+side,top+side);tile=side/11f;drawBoard(c);drawPanel(c,left+side+22,top,w-left-side-36,h-top*2);}
 private RectF tileRect(int i){int x,y;if(i<=10){x=10-i;y=10;}else if(i<=20){x=0;y=10-(i-10);}else if(i<=30){x=i-20;y=0;}else{x=10;y=i-30;}float s=tile;return new RectF(board.left+x*s,board.top+y*s,board.left+(x+1)*s,board.top+(y+1)*s);}
 private void drawBoard(Canvas c){box(c,new RectF(board.left-5,board.top-5,board.right+5,board.bottom+5),0xffffd66c,14);box(c,board,0xfff4e8cb,8);
  for(int i=0;i<40;i++){RectF r=tileRect(i);p.setColor(0xff2a2118);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);c.drawRect(r,p);p.setStyle(Paint.Style.FILL);p.setColor((i%10==0)?0xffffd36b:(groups[i]>0?palette[groups[i]]:0xfff8f0df));c.drawRect(r.left+1,r.top+1,r.right-1,r.bottom-1,p);
   c.save();float angle=i>=11&&i<=19?90:i>=21&&i<=29?180:i>=31?-90:0;c.rotate(angle,r.centerX(),r.centerY());txt(c,names[i],r.centerX(),r.centerY()+3,Math.max(7,tile*.16f),0xff211b14,Paint.Align.CENTER);if(prices[i]>0)txt(c,prices[i]+" ج",r.centerX(),r.centerY()+tile*.22f,Math.max(6,tile*.12f),0xff211b14,Paint.Align.CENTER);c.restore();
   for(int j=0;j<4;j++){Player a=players.get(j);if(a.pos==i){p.setColor(a.color);c.drawCircle(r.centerX()+(j-1.5f)*tile*.13f,r.centerY()+tile*.32f,tile*.075f,p);}}
   for(Player a:players)if(a.owned.contains(i)){p.setColor(a.color);c.drawRect(r.left+2,r.top+2,r.right-2,r.top+5,p);}
  }
  centerRect.set(board.left+tile*2.1f,board.top+tile*2.1f,board.right-tile*2.1f,board.bottom-tile*2.1f);RectF center=centerRect;box(c,center,0xff124333,12);txt(c,"بنك الحظ",center.centerX(),center.centerY()-tile*.3f,tile*.68f,0xffffd66c,Paint.Align.CENTER);txt(c,"لعبة العائلة والأصدقاء",center.centerX(),center.centerY()+tile*.08f,tile*.25f,0xffffffff,Paint.Align.CENTER);txt(c,"اشترِ المدن واجمع الإيجارات",center.centerX(),center.centerY()+tile*.48f,tile*.2f,0xffe5d7ae,Paint.Align.CENTER);txt(c,"اضغط هنا للقائمة والإحصائيات",center.centerX(),center.centerY()+tile*.85f,Math.max(7,tile*.15f),0xffffd66c,Paint.Align.CENTER);
 }
 private void drawPanel(Canvas c,float x,float y,float w,float h){box(c,new RectF(x,y,x+w,y+h),0xff12392f,16);txt(c,"بنك الحظ",x+w/2,y+26,22,0xffffd66c,Paint.Align.CENTER);float yy=y+54;
  for(int i=0;i<4;i++){Player a=players.get(i);box(c,new RectF(x+7,yy-16,x+w-7,yy+16),i==turn?0xff285b46:0xff1b473a,7);p.setColor(a.color);c.drawCircle(x+19,yy,6,p);txt(c,a.name,x+31,yy+4,12,0xffffffff,Paint.Align.LEFT);txt(c,a.money+" ج",x+w-14,yy+4,12,0xffffdf91,Paint.Align.RIGHT);yy+=38;}
  txt(c,"النرد",x+w/2,yy+12,14,0xffffd66c,Paint.Align.CENTER);txt(c,dice(d1)+"   "+dice(d2),x+w/2,yy+51,34,0xffffffff,Paint.Align.CENTER);yy+=76;
  p.setColor(0xfff6e9ce);p.setTextSize(11);p.setTextAlign(Paint.Align.CENTER);String msg=message.length()>65?message.substring(0,63)+"…":message;c.drawText(msg,x+w/2,yy+5,p);yy+=28;
  float bh=Math.min(35,(h-yy-12)/3);if(bh<24)bh=24;rollRect.set(x+9,yy,x+w-9,yy+bh);buyRect.set(x+9,yy+bh+6,x+w-9,yy+bh*2+6);endRect.set(x+9,yy+bh*2+12,x+w-9,yy+bh*3+12);
  button(c,rollRect,"🎲 رمي النرد",!rolled&&!gameOver&&!players.get(turn).ai);button(c,buyRect,"شراء العقار",canBuy&&!gameOver&&!players.get(turn).ai);button(c,endRect,"إنهاء الدور",rolled&&!gameOver&&!players.get(turn).ai);
  float chatY=endRect.bottom+8; chatRect.set(x+9,chatY,x+w*.68f,chatY+29); emojiRect.set(x+w*.70f,chatY,x+w-9,chatY+29); button(c,chatRect,"💬 دردشة",true); button(c,emojiRect,"😀 رموز",true);
 }
 private String dice(int n){return new String[]{"","⚀","⚁","⚂","⚃","⚄","⚅"}[n];}
 private void button(Canvas c,RectF r,String s,boolean enabled){box(c,r,enabled?0xffffd36b:0xff716c5d,8);txt(c,s,r.centerX(),r.centerY()+5,13,enabled?0xff34220d:0xffd2c9b7,Paint.Align.CENTER);}
 @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float x=e.getX(),y=e.getY();if(rollRect.contains(x,y)&&!rolled&&!players.get(turn).ai)roll();else if(buyRect.contains(x,y)&&canBuy&&!players.get(turn).ai)buy();else if(endRect.contains(x,y)&&rolled&&!players.get(turn).ai)nextTurn();else if(chatRect.contains(x,y))openChat();else if(emojiRect.contains(x,y))sendEmoji();else if(centerRect.contains(x,y))openGameMenu();else {for(int i=0;i<40;i++)if(tileRect(i).contains(x,y)){showCityCard(i);break;}}return true;}
 private void roll(){tones.startTone(ToneGenerator.TONE_PROP_BEEP,180);postDelayed(()->tones.startTone(ToneGenerator.TONE_PROP_ACK,160),220);d1=1+random.nextInt(6);d2=1+random.nextInt(6);int sum=d1+d2;Player a=players.get(turn);rolled=true;canBuy=false;if(a.jail>0){if(d1==d2||a.jail==1){a.jail=0;message=a.name+" خرج من السجن.";}else{a.jail--;message="أنت في السجن، انتظر دوراً آخر.";tones.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,350);saveState();invalidate();return;}}else if(a.pos==0&&sum!=6){message="تحتاج إلى رمية مجموعها 6 للخروج من البداية.";saveState();invalidate();return;}int old=a.pos;a.pos=(a.pos+sum)%40;if(old+sum>=40){a.money+=100;message="مررت بالبداية وحصلت على 100 جنيه.";tones.startTone(ToneGenerator.TONE_PROP_ACK,260);}else message="تحركت "+sum+" خطوات.";resolve(a);saveState();invalidate();}
 private void resolve(Player a){int i=a.pos;if(i==10){a.jail=2;message="ذهبت إلى السجن!";tones.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,500);return;}if(i==5||i==17||i==35){a.money-=50;message="دفعت ضريبة 50 جنيه.";tones.startTone(ToneGenerator.TONE_PROP_NACK,250);return;}if(i==2||i==11||i==20||i==27||i==37){int[] d={80,-50,40,-30,100};int v=d[random.nextInt(d.length)];a.money+=v;message=v>=0?"بطاقة حظ: ربحت "+v+" جنيه.":"بطاقة حظ: خسرت "+(-v)+" جنيه.";tones.startTone(v>=0?ToneGenerator.TONE_PROP_ACK:ToneGenerator.TONE_PROP_NACK,320);return;}if(prices[i]>0){int own=owner(i);if(own<0){if(a.money>=prices[i]){pending=i;canBuy=true;message="العقار "+names[i]+" للبيع بسعر "+prices[i]+" جنيه.";}else message="رصيدك لا يكفي لشراء العقار.";}else if(own!=turn){int rent=Math.max(10,prices[i]/3);a.money-=rent;players.get(own).money+=rent;message="دفعت إيجاراً قدره "+rent+" جنيه.";tones.startTone(ToneGenerator.TONE_PROP_NACK,180);}}if(a.money<0){a.out=true;a.money=0;message=a.name+" أفلس وخرج من اللعبة.";}int active=0,last=-1;for(int j=0;j<4;j++)if(!players.get(j).out){active++;last=j;}if(active==1){gameOver=true;message="انتهت اللعبة! الفائز: "+players.get(last).name;tones.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,700);}}
 private int owner(int i){for(int j=0;j<4;j++)if(players.get(j).owned.contains(i))return j;return -1;}
 private void buy(){tones.startTone(ToneGenerator.TONE_PROP_ACK,220);Player a=players.get(turn);if(pending<0)return;int i=pending;a.money-=prices[i];a.owned.add(i);message="أصبحت "+names[i]+" ملكك!";pending=-1;canBuy=false;saveState();invalidate();}
 private void nextTurn(){tones.startTone(ToneGenerator.TONE_PROP_BEEP2,120);pending=-1;canBuy=false;rolled=false;int guard=0;do{turn=(turn+1)%4;}while(players.get(turn).out&&guard++<4);saveState();invalidate();if(players.get(turn).ai)postDelayed(this::aiTurn,700);}
 private void openChat(){ final String[] lines={"حظاً موفقاً! 🍀","هذه المدينة لي! 🏦","يا لها من رمية! 🎲","سأفوز بالجولة! 👑","انتبه من المحكمة! ⚖️","مرحباً يا أصدقاء! 👋"}; new android.app.AlertDialog.Builder(getContext()).setTitle("دردشة اللاعبين").setItems(lines,(d,w)->{chatLine=players.get(turn).name+": "+lines[w];message=chatLine;tones.startTone(ToneGenerator.TONE_PROP_BEEP,100);invalidate();}).setNegativeButton("إغلاق",null).show(); }
 private void sendEmoji(){ final String[] emojis={"😀","😂","😍","😎","😡","🎉","🍀","👑","🎲","👏"}; new android.app.AlertDialog.Builder(getContext()).setTitle("اختر رمزاً تعبيرياً").setItems(emojis,(d,w)->{chatLine=players.get(turn).name+": "+emojis[w];message=chatLine;invalidate();}).show(); }

 private String landmark(int i){
  String[] l={"نقطة الانطلاق","أهرامات الجيزة","بطاقة مفاجأة","آيا صوفيا","الكولوسيوم","الضرائب","برج إيفل","قصر مدريد الملكي","محطة السفر","بيغ بن","قنوات أمستردام","بطاقة مفاجأة","بوابة براندنبورغ","سجن المدينة","قصر شونبرون","جسر تشارلز","مبنى البرلمان","الضرائب","تمثال الحرية","برج سي إن","بطاقة مفاجأة","ميلينيوم بارك","محطة السفر","جسر البوابة الذهبية","برج طوكيو","المزاد","برج نامسان","بطاقة مفاجأة","مارينا باي","المحكمة","برج خليفة","جامع الشيخ زايد","محطة السفر","سوق واقف","مركز المملكة","الضرائب","ساحة جامع الفنا","بطاقة مفاجأة","جبل الطاولة","الخروج من السجن"};
  return l[i];
 }
 private void showCityCard(int i){
  String ownerName="لا يوجد مالك";int own=owner(i);if(own>=0)ownerName=players.get(own).name;
  String body="المعلم: "+landmark(i)+"\nالسعر: "+prices[i]+" جنيه\nالمالك: "+ownerName;
  if(prices[i]>0)body+="\nالإيجار الأساسي: "+Math.max(10,prices[i]/3)+" جنيه";
  new android.app.AlertDialog.Builder(getContext()).setTitle("🏙️ بطاقة "+names[i]).setMessage(body).setPositiveButton("حسناً",null).show();
 }
 private void openGameMenu(){
  final String[] items={"👤 الملف الشخصي","🏆 لوحة الصدارة","👥 الأصدقاء","🏠 إنشاء غرفة","📊 ممتلكاتي","🔄 لعبة جديدة"};
  new android.app.AlertDialog.Builder(getContext()).setTitle("قائمة بنك الحظ").setItems(items,(d,w)->{
   if(w==0)showProfile();else if(w==1)showLeaderboard();else if(w==2)showFriends();else if(w==3)createRoom();else if(w==4)showProperties();else new android.app.AlertDialog.Builder(getContext()).setTitle("بدء لعبة جديدة").setMessage("سيتم تصفير نتائج اللعبة الحالية.").setNegativeButton("إلغاء",null).setPositiveButton("ابدأ",(x,z)->{prefs.edit().clear().apply();reset();saveState();}).show();
  }).show();
 }
 private void showProfile(){
  Player a=players.get(0);int net=a.money+propertyValue(a);
  String body="الاسم: "+a.name+"\nالرصيد: "+a.money+" جنيه\nعدد المدن: "+a.owned.size()+"\nقيمة المدن: "+propertyValue(a)+" جنيه\nإجمالي الثروة التقريبي: "+net+" جنيه\nنوع الحساب: زائر";
  new android.app.AlertDialog.Builder(getContext()).setTitle("👤 الملف الشخصي").setMessage(body).setPositiveButton("إغلاق",null).show();
 }
 private void showLeaderboard(){
  ArrayList<Player> rank=new ArrayList<>(players);Collections.sort(rank,(a,b)->Integer.compare(b.money+propertyValue(b),a.money+propertyValue(a)));
  StringBuilder b=new StringBuilder();for(int i=0;i<rank.size();i++){Player a=rank.get(i);b.append(i+1).append(". ").append(a.name).append(" — ").append(a.money).append(" ج، ").append(a.owned.size()).append(" مدينة\n");}
  new android.app.AlertDialog.Builder(getContext()).setTitle("🏆 لوحة الصدارة").setMessage(b.toString()).setPositiveButton("إغلاق",null).show();
 }
 private int propertyValue(Player a){int v=0;for(int i:a.owned)v+=prices[i];return v;}
 private void showProperties(){
  Player a=players.get(0);StringBuilder b=new StringBuilder("عدد المدن: "+a.owned.size()+"\n");
  for(int i:a.owned)b.append("• ").append(names[i]).append(" — ").append(prices[i]).append(" جنيه\n");
  if(a.owned.isEmpty())b.append("لم تشترِ أي مدينة بعد.");
  new android.app.AlertDialog.Builder(getContext()).setTitle("🏙️ مدني وعقاراتي").setMessage(b.toString()).setPositiveButton("إغلاق",null).show();
 }
 private void showFriends(){
  new android.app.AlertDialog.Builder(getContext()).setTitle("👥 الأصدقاء").setMessage("قائمة الأصدقاء ستظهر بعد ربط Firebase. اللعب الحالي محلي ضد المنافسين الآليين.").setPositiveButton("حسناً",null).show();
 }
 private void createRoom(){
  new android.app.AlertDialog.Builder(getContext()).setTitle("🏠 إنشاء غرفة").setMessage("واجهة الغرف قيد التجهيز. إنشاء غرف حقيقية ودعوة أصدقاء عبر الإنترنت يتطلب ربط Firebase وقاعدة بيانات آمنة.").setPositiveButton("حسناً",null).show();
 }

 private void saveState(){
  SharedPreferences.Editor e=prefs.edit();e.putInt("turn",turn).putBoolean("gameOver",gameOver);
  for(int j=0;j<players.size();j++){Player a=players.get(j);StringBuilder owned=new StringBuilder();for(int k:a.owned){if(owned.length()>0)owned.append(",");owned.append(k);}e.putInt("money"+j,a.money).putInt("pos"+j,a.pos).putInt("jail"+j,a.jail).putBoolean("out"+j,a.out).putString("owned"+j,owned.toString());}e.apply();
 }
 private void loadState(){
  if(!prefs.contains("money0"))return;turn=Math.max(0,Math.min(3,prefs.getInt("turn",0)));gameOver=prefs.getBoolean("gameOver",false);
  for(int j=0;j<players.size();j++){Player a=players.get(j);a.money=prefs.getInt("money"+j,300);a.pos=prefs.getInt("pos"+j,0);a.jail=prefs.getInt("jail"+j,0);a.out=prefs.getBoolean("out"+j,false);a.owned.clear();String saved=prefs.getString("owned"+j,"");if(!saved.isEmpty())for(String part:saved.split(",")){try{int city=Integer.parseInt(part);if(city>=0&&city<40&&prices[city]>0)a.owned.add(city);}catch(NumberFormatException ignored){}}}
  rolled=false;canBuy=false;pending=-1;message="تم استرجاع آخر لعبة محفوظة. ارْمِ النرد لمتابعة اللعب.";
 }
 private void aiTurn(){if(gameOver)return;Player a=players.get(turn);d1=1+random.nextInt(6);d2=1+random.nextInt(6);int sum=d1+d2;rolled=true;if(a.pos==0&&sum!=6){message=a.name+" لم يحصل على 6.";invalidate();postDelayed(this::nextTurn,650);return;}int old=a.pos;a.pos=(a.pos+sum)%40;if(old+sum>=40)a.money+=100;resolve(a);if(pending>=0&&a.money-prices[pending]>=80){int i=pending;a.money-=prices[i];a.owned.add(i);message=a.name+" اشترى "+names[i]+".";}pending=-1;canBuy=false;saveState();invalidate();postDelayed(this::nextTurn,850);}
}