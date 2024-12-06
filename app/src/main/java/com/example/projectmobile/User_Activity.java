package com.example.projectmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class User_Activity extends AppCompatActivity {

    TextView change_avatar;
    EditText edt_Name, edt_Password, edt_newpass, edt_confirmpass;
    Button Save;
    CircleImageView avatar;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        change_avatar = findViewById(R.id.change_avatar);
        edt_Name = findViewById(R.id.edt_Name);
        edt_Password = findViewById(R.id.edt_Password);
        edt_newpass= findViewById(R.id.edt_newpass);
        edt_confirmpass = findViewById(R.id.edt_confirmpass);
        Save =findViewById(R.id.Save);
        avatar = findViewById(R.id.avatar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.user);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.dashboard){
                    Intent intent = new Intent(User_Activity.this, DashBoardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.setting){
                    Intent intent = new Intent(User_Activity.this, Setting_Activity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.home){
                    Intent intent = new Intent(User_Activity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            uri = data.getData();
                            Glide.with(User_Activity.this).load(uri).apply(RequestOptions.circleCropTransform()).into(avatar);
                        }
                    }
                });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(User_Activity.this).crop().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });

        change_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    return;
                }

                buttonUpdateImageClick();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    return;
                }
                String name = edt_Name.getText().toString().trim();
                if (!name.isEmpty()) {
                    buttonUpdateNameClick();
                }
                String currentPw = edt_Password.getText().toString().trim();
                String newPw = edt_newpass.getText().toString().trim();
                String confirmPw = edt_confirmpass.getText().toString().trim();
                if (!currentPw.isEmpty() || !newPw.isEmpty() || !confirmPw.isEmpty()) {
                    if (currentPw.isEmpty() || newPw.isEmpty()) {
                        Toast.makeText(User_Activity.this, getString(R.string.nhapthieu), Toast.LENGTH_SHORT).show();
                    } else if (!newPw.equals(confirmPw)) {
                        Toast.makeText(User_Activity.this, getString(R.string.not_match), Toast.LENGTH_SHORT).show();
                    } else {
                        buttonUpdatePasswordClick();
                    }
                }
            }
        });
    }

    private void buttonUpdateNameClick() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = edt_Name.getText().toString().trim();
        // Cập nhật thông tin người dùng
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        // Cập nhật thông tin người dùng
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(User_Activity.this, getString(R.string.update_name_success), Toast.LENGTH_SHORT).show();
                            edt_Name.setText("");
                        } else {
                            Toast.makeText(User_Activity.this, getString(R.string.update_name_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void buttonUpdatePasswordClick(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String crpassword = edt_Password.getText().toString().trim();
        String newpassword = edt_newpass.getText().toString().trim();
        String conpassword = edt_confirmpass.getText().toString().trim();
        // Tạo thông tin xác thực
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), crpassword);

        // Xác thực lại người dùng
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Nếu xác thực thành công, cập nhật mật khẩu
                            user.updatePassword(newpassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(User_Activity.this, getString(R.string.update_password_success), Toast.LENGTH_SHORT).show();
                                                edt_Password.setText("");
                                                edt_newpass.setText("");
                                                edt_confirmpass.setText("");
                                            } else {
                                                Toast.makeText(User_Activity.this, getString(R.string.update_password_fail), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Nếu xác thực không thành công
                            Toast.makeText(User_Activity.this, getString(R.string.auth_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void buttonUpdateImageClick(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(User_Activity.this, getString(R.string.update_image_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(User_Activity.this, getString(R.string.update_image_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}