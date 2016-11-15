package com.mobiletrain.manga.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mobiletrain.manga.R;
import com.mobiletrain.manga.config.Config;
import com.mobiletrain.manga.fragment.FragmentCh;
import com.mobiletrain.manga.fragment.FragmentDy;
import com.mobiletrain.manga.fragment.FragmentDzs;
import com.mobiletrain.manga.fragment.FragmentGsmh;
import com.mobiletrain.manga.fragment.FragmentGx;
import com.mobiletrain.manga.fragment.FragmentKbmh;
import com.mobiletrain.manga.fragment.FragmentSy;
import com.mobiletrain.manga.model.UserBean;
import com.mobiletrain.manga.util.HttpUtil;
import com.mobiletrain.manga.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends AppCompatActivity
        implements FragmentKbmh.onFragmentScrollListener
        , FragmentGsmh.onFragmentScrollListener
        , FragmentDy.onFragmentScrollListener
        , FragmentDzs.onFragmentScrollListener
        , FragmentGx.onFragmentScrollListener
        , FragmentSy.onFragmentScrollListener
        , FragmentCh.onFragmentScrollListener {


    @BindView(R.id.sdvUserHead)
    SimpleDraweeView sdvUserHead;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tb_main)
    TabLayout tb_main;
    @BindView(R.id.vp_main)
    ViewPager vpMain;
    @BindView(R.id.nv)
    NavigationView nv;
    @BindView(R.id.rlRoot)
    DrawerLayout rlRoot;
    @BindView(R.id.menu_main)
    TextView menuMain;
    @BindView(R.id.llToolBar)
    LinearLayout llToolBar;
    @BindView(R.id.llContent)
    RelativeLayout llContent;
    @BindView(R.id.tvUpdataMsg)
    TextView tvUpdataMsg;
    @BindView(R.id.tvOutOfLoginPage)
    TextView tvOutOfLoginPage;
    @BindView(R.id.tvLoginBanner)
    TextView tvLoginBanner;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.tilName)
    TextInputLayout tilName;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.tvCreatUser)
    Button tvCreatUser;
    @BindView(R.id.tvLogin)
    Button tvLogin;
    @BindView(R.id.etNewName)
    EditText etNewName;
    @BindView(R.id.tilNewName)
    TextInputLayout tilNewName;
    @BindView(R.id.etNewPassword)
    EditText etNewPassword;
    @BindView(R.id.tilNewPassword)
    TextInputLayout tilNewPassword;
    @BindView(R.id.etNewPasswordAgain)
    EditText etNewPasswordAgain;
    @BindView(R.id.tilNewPasswordAgain)
    TextInputLayout tilNewPasswordAgain;
    @BindView(R.id.tvCommit)
    Button tvCommit;
    @BindView(R.id.llCreateUser)
    LinearLayout llCreateUser;
    @BindView(R.id.tvOutOfCreatePage)
    TextView tvOutOfCreatePage;


    private List<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"恐怖漫画", "故事漫画", "插画", "搞笑", "段子手", "摄影", "电影"};
    private FragmentKbmh fragmentKbmh;
    private FragmentGx fragmentGx;
    private FragmentGsmh fragmentGsmh;
    private FragmentDy fragmentDy;
    private FragmentDzs fragmentDzs;
    private float angle = 3f;
    private final int SCROLL_STATE_FLING = 2;
    private final int SCROLL_STATE_SCROLL = 1;
    private FragmentSy fragmentSy;
    private FragmentCh fragmentCh;
    private AlertDialog alertDialog;
    private MyReceiver myReceiver;
    private IntentFilter intentFilter;
    private ViewPropertyAnimator toolBarAnimate;
    private ViewPropertyAnimator contentAnimate;
    private ViewPropertyAnimator updataMsgAnimate;
    private boolean animationShowing = false;
    private boolean toolBarHide = false;
    private String msg = "";
    private int toolBarHeight;
    private boolean getToolBarHeight = false;
    private int updataMsgHeight;
    private boolean loginSuccessed;
    List<UserBean> users=new ArrayList<>();
    UserBean currentUser;

    private Runnable resetAnimationShowing = new Runnable() {
        @Override
        public void run() {//重置是否启用动画的条件
            animationShowing = false;
        }
    };

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.MSG_WHAT_RESPONSE_FAILE:
                    break;
                case Config.MSG_WHAT_RESPONSE_SUCCESS:
                    break;
                case Config.MSG_WHAT_DB_DATA_GOT:
                    checkLoginMessage();
                    break;
            }
        }
    };



    private View nvHeadView;
    private SimpleDraweeView nvUserHead;
    private TextView nvUserName;
    private SQLiteDatabase db;
    private String userName;
    private String userPassword;
    private String UserLoveId;
    private SharedPreferences loginInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Config.transparentStatusBar(this);

        initNavigationView();
        initFragments();
        initViewPager();
//        setMenuMainClickListener();
        initAlertDialog();
        initDataBase();
        initAnimate();
        setEditTextHint();
        setLoginBanner();

    }

    //点击录成后检测用户是否存在
    private void checkLoginMessage() {
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        for(int i=0;i<users.size();i++){
            UserBean bean = users.get(i);
            if(name.equals(bean.getName())){
                if(password.equals(bean.getPassword())){
                    onLoginSuccess(name, bean);
                    return;
                }
            }
        }
        Toast.makeText(MainActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
    }
    //用户登录成功时
    private void onLoginSuccess(String name, UserBean bean) {
        loginSuccessed = true;
        currentUser =new UserBean(bean.getName(),bean.getPassword(),bean.getId(),bean.getLoveId());
        hideLlLoginView();
        tvUserName.setText(name);
        nvUserName.setText("注销");
        Config.CurrenUserLoveID=currentUser.getLoveId();
//        sdvUserHead.setImageResource(R.mipmap.user_head_had_login);
//        nvUserHead.setImageResource(R.mipmap.user_head_had_login);
        Toast.makeText(MainActivity.this, "欢迎大王来巡山~~", Toast.LENGTH_SHORT).show();
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                db.execSQL("create table if not exists '"+ currentUser.getLoveId()+"' (_id integer primary key autoincrement,type text,title text,link text,mangaId text,thumbnail text,time text)");
                db.close();
                saveCurrentUserMsg();
            }
        });
    }

    //navigationview  登录状态（nv未登录、nv注销）点击事件
    private void setNvUserNameClickListener() {
        nvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginSuccessed){
                    loginSuccessed=false;
                    currentUser =null;
                    nvUserName.setText("未登录");
                    tvUserName.setText("未登录");
//                    sdvUserHead.setImageResource(R.mipmap.user_head_unlogin);
//                    nvUserHead.setImageResource(R.mipmap.user_head_unlogin);
                    Config.CurrenUserLoveID="";
                    Toast.makeText(MainActivity.this,"已注销",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //设置navigationView用户头像点击事件
    public void setNvUserHeadClickListener() {
        nvUserHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginSuccessed==false){
                    showLlLoginView();
                }else{
                    Toast.makeText(MainActivity.this,"若要重新登录请先注销",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //显示登录页面
    private void showLlLoginView() {
        if (llLogin.getVisibility() == View.INVISIBLE || llLogin.getVisibility() == View.GONE) {
            llLogin.setVisibility(View.VISIBLE);
        }
    }

    //设置EditText的hint
    private void setEditTextHint() {
        tilName.setHint("用户名");
        tilPassword.setHint("密码");
        tilNewName.setHint("用户名(长度不大于10)");
        tilNewPassword.setHint("密码(长度不大于10)");
        tilNewPasswordAgain.setHint("再一次输入密码");
    }

    @OnClick(R.id.tvOutOfCreatePage)
    public void onOutOfCreatePageClick() {
        hideLlCreateLayout();
    }

    //隐藏注册页面
    private void hideLlCreateLayout() {
        if (llCreateUser.getVisibility() == View.VISIBLE) {
            llCreateUser.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tvOutOfLoginPage)
    public void onOutOfLoginPageClick() {
        hideLlLoginView();
    }

    //隐藏登录页面
    private void hideLlLoginView() {
        if (llLogin.getVisibility() == View.VISIBLE) {
            llLogin.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tvCreatUser)
    public void onCreatUserClick() {
        showLlCreateUserLayout();
    }

    //显示注册页面
    private void showLlCreateUserLayout() {
        hideKeyboard();
        if (llCreateUser.getVisibility() == View.INVISIBLE || llCreateUser.getVisibility() == View.GONE) {
            llCreateUser.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tvLogin)
    public void onLoginClick() {
        hideKeyboard();
        boolean b1 = checkStringLength(tilName, etName.getText().toString(), "名字不能为空且长度不大于10！");
        boolean b2 = checkStringLength(tilPassword, etPassword.getText().toString(), "密码不能为空且长度不大于10！");
        if (b1 && b2) {
            //账号密码都正确,执行登录（获取用户信息，同步收藏夹内容）
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                    db.execSQL("create table if not exists user (_id integer primary key autoincrement,userName text,userPassword text,loveId text)");
                    Cursor cursor = db.query("user", null, null, null, null, null, null);
                    while (cursor.moveToNext()) {
                        userName = cursor.getString(cursor.getColumnIndex("userName"));
                        userPassword = cursor.getString(cursor.getColumnIndex("userPassword"));
                        UserLoveId = cursor.getString(cursor.getColumnIndex("loveId"));
                        int id = cursor.getInt(cursor.getColumnIndex("_id"));
                        UserBean userBean = new UserBean(userName, userPassword, id, UserLoveId);
                        users.add(userBean);
                        Log.e("ttt", "userBean: "+ userName+"---"+userPassword+"---"+UserLoveId);
                    }
                    db.close();
                    handler.sendEmptyMessage(Config.MSG_WHAT_DB_DATA_GOT);
                    Log.e("ttt", "sendEmptyMessage: " );
                }
            });
        }
    }

    @OnTextChanged(R.id.etName)
    public void onEtNameTextChanged(CharSequence s, int start, int before, int count) {
        checkStringLength(tilName, s, "名字不能为空且长度不大于10！");
    }

    @OnTextChanged(R.id.etPassword)
    public void onEtPasswordTextChanged(CharSequence s, int start, int before, int count) {
        checkStringLength(tilPassword, s, "密码不能为空且长度不大于10！");
    }

    @OnTextChanged(R.id.etNewName)
    public void onEtNewNameTextChanged(CharSequence s, int start, int before, int count) {
        checkStringLength(tilNewName, s, "名字不能为空且长度不大于10！");
    }

    @OnTextChanged(R.id.etNewPassword)
    public void onEtNewPasswordTextChanged(CharSequence s, int start, int before, int count) {
        checkStringLength(tilNewPassword, s, "密码不能为空且长度不大于10！");
    }

    //    @OnTextChanged(R.id.etNewPasswordAgain)
//    public void onEtNewPasswordAgainTextChanged(CharSequence s, int start, int before, int count) {
//        checkPasswordIsTrue(s,etNewPassword,tilNewPasswordAgain);
//    }
    //判断两次输入的密码是否相同
    private boolean checkPasswordIsTrue(CharSequence s, EditText editText, TextInputLayout textInputLayout) {
        boolean isSame = false;
        if (editText.getText().toString().equals(s)) {
            isSame = true;
            textInputLayout.setErrorEnabled(false);
        } else {
            isSame = false;
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("请确认密码是否一致！");
        }
        return isSame;
    }

    @OnClick(R.id.tvCommit)
    public void onCommitClick() {
        hideKeyboard();
        boolean b1 = checkStringLength(tilNewName, etNewName.getText().toString(), "名字不能为空且长度不大于10！");
        boolean b2 = checkStringLength(tilNewPassword, etNewPassword.getText().toString(), "密码不能为空且长度不大于10！");
        boolean b3 = checkPasswordIsTrue(etNewPasswordAgain.getText().toString(), etNewPassword, tilNewPasswordAgain);
        if (b1 && b2 & b3) {
            Toast.makeText(this, "创建用户成功！", Toast.LENGTH_SHORT).show();
            hideLlCreateLayout();
            final String userName = etNewName.getText().toString();
            final String userPassword = etNewPassword.getText().toString();
            etNewName.setText("");
            etNewPassword.setText("");
            etNewPasswordAgain.setText("");
            //数据库添加新用户信息
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                    db.execSQL("create table if not exists user (_id integer primary key autoincrement,userName text,userPassword text,loveId text)");
                    db.execSQL("insert into user(userName,userPassword,loveId) values('"+userName+"','"+userPassword+"','"+(userName+userPassword)+"')");
                    db.close();
                }
            });
        }
    }


    //设置登录页面的banner
    public void setLoginBanner() {
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvLoginBanner.setBackgroundResource(R.mipmap.login_password);
                } else {
                    tvLoginBanner.setBackgroundResource(R.mipmap.login_name);
                }
            }
        });
    }

    //校验字符串长度
    private boolean checkStringLength(TextInputLayout textInputLayout, CharSequence s, String message) {
        boolean isRight = false;
        if (s.length() < 11 && s.length() > 0) {
            isRight = true;
            textInputLayout.setErrorEnabled(false);
        } else {
            isRight = false;
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(message);
        }
        return isRight;
    }

    @OnClick(R.id.sdvUserHead)
    public void onSdvUserHeadClick() {
        closeOrOpenNavigationView();
    }

    //获得控件的属性动画
    private void initAnimate() {
        toolBarAnimate = llToolBar.animate();
        contentAnimate = llContent.animate();
        updataMsgAnimate = tvUpdataMsg.animate();
    }

    //获得控件的高度
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!getToolBarHeight) {
            updataMsgHeight = tvUpdataMsg.getHeight();
            toolBarHeight = llToolBar.getHeight();
            getToolBarHeight = true;
        }
    }

    //**************************************************************注册广播
    @Override
    protected void onStart() {
        super.onStart();
        myReceiver = new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Config.ACTION_TYPE_FILLING_DOWN);
        intentFilter.addAction(Config.ACTION_TYPE_FILLING_UP);
        intentFilter.addAction(Config.ACTION_TYPE_UPDATA_SUCCESS);
        registerReceiver(myReceiver, intentFilter);
    }

    //**************************************************************注销广播
    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        if(currentUser !=null){
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.putString("username", currentUser.getName());
            editor.putString("password", currentUser.getPassword());
            editor.putString("loveid", currentUser.getLoveId());
            editor.putInt("id", currentUser.getId());
            editor.commit();
        }
        Log.e("test", "onDestroy: " );
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveCurrentUserMsg();
    }
    //保存用户信息
    private void saveCurrentUserMsg() {
        if(currentUser !=null){
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.putString("username", currentUser.getName());
            editor.putString("password", currentUser.getPassword());
            editor.putString("loveid", currentUser.getLoveId());
            editor.putInt("id", currentUser.getId());
            editor.commit();
        }else{
            SharedPreferences.Editor editor = loginInfo.edit();
            editor.putString("username", null);
            editor.putString("password", null);
            editor.putString("loveid", null);
            editor.putInt("id", -1);
            editor.commit();
        }
    }

    //**************************************************************首次加载数据
    private void initDataBase() {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = openOrCreateDatabase("track.db", MODE_PRIVATE, null);
                db.execSQL("create table if not exists user_track (_id integer primary key autoincrement,type text,title text,link text,mangaId text,thumbnail text,time text)");
                db.close();
            }
        });
        loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        String username = loginInfo.getString("username", null);
        String password = loginInfo.getString("password", null);
        String loveid = loginInfo.getString("loveid", null);
        int id = loginInfo.getInt("id", -1);
        Log.e("test", "initDataBase: "+ username);
        if(id==-1&&loveid==null){
            loginSuccessed=false;
        }else{
            loginSuccessed=true;
            currentUser =new UserBean(username,password,id,loveid);
            onLoginSuccess(username, currentUser);
        }
    }

    //**************************************************************MenuMain点击事件
//    private void setMenuMainClickListener() {
//        menuMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "铃~~~", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //**************************************************************显示或隐藏navigationview
    private void closeOrOpenNavigationView() {
        if (rlRoot.isDrawerOpen(Gravity.LEFT)) {
            rlRoot.closeDrawer(Gravity.LEFT);
        } else {
            rlRoot.openDrawer(Gravity.LEFT);
        }
    }

    //**************************************************************初始化ViewPager
    private void initViewPager() {
        tb_main.setupWithViewPager(vpMain);
        vpMain.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return titles[position];
            }
        });
        vpMain.setOffscreenPageLimit(5);
    }

    //**************************************************************初始化Fragments
    private void initFragments() {
        fragmentKbmh = new FragmentKbmh();
        fragmentKbmh.setOnFragmentScrollListener(this);
        fragmentGx = new FragmentGx();
        fragmentGx.setOnFragmentScrollListener(this);
        fragmentGsmh = new FragmentGsmh();
        fragmentGsmh.setOnFragmentScrollListener(this);
        fragmentDy = new FragmentDy();
        fragmentDy.setOnFragmentScrollListener(this);
        fragmentDzs = new FragmentDzs();
        fragmentDzs.setOnFragmentScrollListener(this);
        fragmentSy = new FragmentSy();
        fragmentSy.setOnFragmentScrollListener(this);
        fragmentCh = new FragmentCh();
        fragmentCh.setOnFragmentScrollListener(this);
        fragments.add(fragmentKbmh);
        fragments.add(fragmentGsmh);
        fragments.add(fragmentCh);
        fragments.add(fragmentGx);
        fragments.add(fragmentDzs);
        fragments.add(fragmentSy);
        fragments.add(fragmentDy);


    }

    //**************************************************************初始化NavigationView
    private void initNavigationView() {
        nv.setItemBackground(getResources().getDrawable(R.drawable.nv_item_background));
        nv.setItemIconTintList(null);
        nvHeadView = LayoutInflater.from(this).inflate(R.layout.navigation_head, nv, true);
        nvUserHead = ((SimpleDraweeView) nvHeadView.findViewById(R.id.sdvHead));
        nvUserName = ((TextView) nvHeadView.findViewById(R.id.tvName));
        setNvUserHeadClickListener();
        setNvUserNameClickListener();
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                ColorStateList colorStateList = getResources().getColorStateList(R.color.nv_item_color_selector);
                nv.setItemTextColor(colorStateList);
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        turnToHomePage();
                        closeOrOpenNavigationView();
                        break;
                    case R.id.menu_download:
                        Snackbar.make(nv, "暂不提供下载哦！~", Snackbar.LENGTH_SHORT).show();
                        closeOrOpenNavigationView();
                        break;
                    case R.id.menu_favorite:
                        if(loginSuccessed){
                            Intent intent = new Intent(MainActivity.this, Favorite.class);
                            intent.putExtra("loveId", currentUser.getLoveId());
                            startActivity(intent);
                        }else{
                            Snackbar.make(nv, "请先登录！~", Snackbar.LENGTH_SHORT).show();
                        }
                        closeOrOpenNavigationView();
                        break;
                    case R.id.menu_track:
                        startActivity(new Intent(MainActivity.this, Track.class));
                        closeOrOpenNavigationView();
                        break;
                    case R.id.menu_clean:
                        ThreadUtil.execute(new Runnable() {
                            @Override
                            public void run() {
                                db = openOrCreateDatabase("track.db", MODE_PRIVATE, null);
                                db.execSQL("delete from user_track");
                                db.close();
                            }
                        });
                        Snackbar.make(nv,"已为您清理足迹~", Snackbar.LENGTH_SHORT).show();
                        closeOrOpenNavigationView();
                        break;
                    case R.id.menu_about:
                        Snackbar.make(nv, item.getTitle(), Snackbar.LENGTH_SHORT).show();
                        closeOrOpenNavigationView();
                        break;
                    case R.id.menu_exit:
                        alertDialog.show();
                        break;
                }
                return true;
            }
        });
    }

    //**************************************************************跳到官方主页
    private void turnToHomePage() {
        Uri uri = Uri.parse("http://www.heibaimanhua.com");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        startActivity(intent);
        Toast.makeText(MainActivity.this, "biu~传送至主站！~", Toast.LENGTH_SHORT).show();
    }

    //**************************************************************实现MyFragment的接口方法
    @Override
    public void onFramentScroll(int SCROLLTYPE) {
        if (SCROLLTYPE == SCROLL_STATE_SCROLL) {
            angle += 3;
        } else if (SCROLLTYPE == SCROLL_STATE_FLING) {
            angle += 12;
        }
        menuMain.setRotation(angle);
    }

    //**************************************************************判断回退按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(llCreateUser.getVisibility()==View.VISIBLE){
                llCreateUser.setVisibility(View.GONE);
            }else if(llLogin.getVisibility()==View.VISIBLE){
                llLogin.setVisibility(View.GONE);
            }else{
                alertDialog.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //**************************************************************初始化弹窗
    private void initAlertDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("确定退出APP吗？")
                .setMessage("大王，别走呀~~")
                .setNegativeButton("垃圾~去死吧！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Process.killProcess(Process.myPid());
                        System.exit(0);
                    }
                })
                .setPositiveButton("朕再玩会", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeAlertDialog();
                    }
                }).create();
    }

    //**************************************************************关闭弹窗
    private void closeAlertDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.cancel();
        }
    }

    //**************************************************************广播
    class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case Config.ACTION_TYPE_FILLING_DOWN:
                    //下抛显示toolbar
                    if (animationShowing == false && toolBarHide == true) {
                        animationShowing = true;
                        toolBarHide = false;
                        ViewGroup.LayoutParams llToolBarLayoutParams = llToolBar.getLayoutParams();
                        llToolBarLayoutParams.height = toolBarHeight;
                        llToolBar.setLayoutParams(llToolBarLayoutParams);
                        toolBarAnimate.setDuration(300).translationY(0f).alpha(1).start();
                        contentAnimate.setDuration(300).translationY(0f).start();
                        handler.postDelayed(resetAnimationShowing, 300);
                        chageLlContentHeight();
                    }
                    break;
                case Config.ACTION_TYPE_FILLING_UP:
                    //上抛隐藏toolbar
                    if (animationShowing == false && toolBarHide == false) {
                        animationShowing = true;
                        toolBarHide = true;
                        toolBarAnimate.setDuration(300).translationY(-1f * toolBarHeight).alpha(0f).start();
                        contentAnimate.setDuration(300).translationY(-1f * toolBarHeight).start();
                        handler.postDelayed(resetAnimationShowing, 300);
                        chageLlContentHeight();
                    }
                    break;
                case Config.ACTION_TYPE_UPDATA_SUCCESS:
                    int addCount = intent.getIntExtra("addCount", 0);
                    if (HttpUtil.isNetworkConnected(MainActivity.this)) {
                        if (addCount == 0) {
                            msg = "ㄟ( ▔, ▔ )ㄏ~~暂时没有新数据";
                        } else {
                            msg = "(づ｡◕‿‿◕｡)づ~~更新了" + addCount + "条数据";
                        }
                    } else {
                        msg = "~(๑•́ ₃ •̀๑)~~都没网,看什么gui嘛~";
                    }

                    showUpDataMsg(msg);
                    break;
            }
        }
    }

    //显示刷新信息动画
    private void showUpDataMsg(String showMsg) {
        if (tvUpdataMsg.getVisibility() == View.INVISIBLE) {
            tvUpdataMsg.setVisibility(View.VISIBLE);
        }
        tvUpdataMsg.setText(showMsg);
        Log.e("test", "showUpDataMsg: " + tvUpdataMsg.getText());
        tvUpdataMsg.setAlpha(0.9f);
        updataMsgAnimate.setDuration(4000).alpha(0f);
    }

    //动态调整内容区域的高度
    private void chageLlContentHeight() {
        ViewGroup.LayoutParams contentLayoutParams = llContent.getLayoutParams();
        if (toolBarHide) {
            contentLayoutParams.height = llContent.getHeight() + toolBarHeight;
            llContent.setLayoutParams(contentLayoutParams);
        } else {
            contentLayoutParams.height = llContent.getHeight() - toolBarHeight;
            llContent.setLayoutParams(contentLayoutParams);
        }
    }

    //**************************************************************隐藏keyboard
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView
    //**************************************************************初始化NavigationView


}
