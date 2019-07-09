package com.example.semiprojectsample.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.semiprojectsample.R;
import com.example.semiprojectsample.bean.MemberBean;
import com.example.semiprojectsample.db.FileDB;

public class LoginActivity extends AppCompatActivity {
    // 멤버변수 자리
    private EditText mEdtId, mEdtPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtId = findViewById(R.id.edtId);
        mEdtPw = findViewById(R.id.edtPw);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnJoin = findViewById(R.id.btnJoin);

        btnLogin.setOnClickListener(mBtnLoginClick);
        btnJoin.setOnClickListener(mJoinLoginClick);
        // this ==> LoginActivity ==> Context = 왜냐하면 액티비티가 컨텍스트를 상속받고 있음 ==> getBaseContext()

    } // end OnCreate()

    // 로그인 버튼 클릭 이벤트
    private View.OnClickListener mBtnLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String memId = mEdtId.getText().toString();
            String memPw = mEdtPw.getText().toString();

            MemberBean member = FileDB.getFindMember(LoginActivity.this, memId);
            if(member == null){
                Toast.makeText(LoginActivity.this, "해당 아이디는 가입되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 패스워드 비교
            if(TextUtils.equals(member.memPw, memPw)){
                // 비밀번호 일치
                FileDB.setLoginMember(LoginActivity.this, member);
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(LoginActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            }

        } // onClick
    };
    // 회원가입 버튼 클릭 이벤트
    private View.OnClickListener mJoinLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, CameraCapture2Activity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mEdtId.setText("");
        mEdtPw.setText("");
    }
}// class
