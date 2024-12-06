package com.example.projectmobile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Locale;

public class Setting_Activity extends AppCompatActivity {

    TextView infor, language, Notification, support, support2;
    Button btn_Logout;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting2);

        infor = findViewById(R.id.infor);
        language = findViewById(R.id.language);
        Notification = findViewById(R.id.Notification);
        support = findViewById(R.id.support);
        support2 = findViewById(R.id.support2);
        btn_Logout= findViewById(R.id.btn_Logout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadLocale();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        infor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Setting_Activity.this, User_Activity.class);
                startActivity(intent);
            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.setting);
        // Gắn sự kiện khi click vào các item
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.dashboard){
                    Intent intent = new Intent(Setting_Activity.this, DashBoardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.user){
                    Intent intent = new Intent(Setting_Activity.this, User_Activity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.home){
                    Intent intent = new Intent(Setting_Activity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    for (UserInfo profile : user.getProviderData()) {
                        String providerId = profile.getProviderId();
                        if (providerId.equals("google.com")) {
                            // Người dùng đăng nhập bằng Google
                            signOutGoogle();
                        } else if (providerId.equals("password")) {
                            // Người dùng đăng nhập bằng Email/Password
                            signOutEmailPassword();
                        } else if (providerId.equals("facebook.com")) {
                            signOutFacebook();
                        }
                    }
                }

            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Activity.this);
                builder.setTitle("Help Centre:");
                builder.setMessage("Hole Alert is an app that detects potholes around VietNam National University, HCMC, helping users avoid bumpy roads.");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Activity.this);
                builder.setTitle("Notification:");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null){
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        // Hủy tài khoản thành công, điều hướng về màn hình đăng nhập
                                        Intent intent = new Intent(Setting_Activity.this, SignInActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Xử lý lỗi nếu không thể hủy tài khoản
                                        Toast.makeText(Setting_Activity.this, "Failed to deletion account!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void showChangeLanguageDialog(){
        final String[] listItems = {"Tiếng Việt", "Français", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Setting_Activity.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    setLocale("vi");
                    recreate();
                } else if (i == 1){
                    setLocale("fr");
                    recreate();
                } else if (i == 2){
                    setLocale("en");
                    recreate();
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    private void signOutGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Đăng xuất thành công
                FirebaseAuth.getInstance().signOut();
                // Điều hướng về màn hình đăng nhập
                Intent intent = new Intent(Setting_Activity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signOutEmailPassword() {
        FirebaseAuth.getInstance().signOut();
        // Điều hướng về màn hình đăng nhập
        Intent intent = new Intent(Setting_Activity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void signOutFacebook() {
        com.facebook.login.LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        // Điều hướng về màn hình đăng nhập
        Intent intent = new Intent(Setting_Activity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

}

