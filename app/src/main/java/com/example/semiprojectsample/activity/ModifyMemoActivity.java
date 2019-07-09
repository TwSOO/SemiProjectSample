package com.example.semiprojectsample.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.semiprojectsample.R;
import com.example.semiprojectsample.bean.MemberBean;
import com.example.semiprojectsample.bean.MemoBean;
import com.example.semiprojectsample.db.FileDB;
import com.example.semiprojectsample.fragment.FragmentCamera;
import com.example.semiprojectsample.fragment.FragmentMemoWrite;
import com.example.semiprojectsample.fragment.FragmentModifyCamera;
import com.example.semiprojectsample.fragment.FragmentModifyWrite;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModifyMemoActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{ // TextToSpeech의 리스너를 임플리먼트함
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    String memoId; // 클릭한 메모 아이디
    private TextToSpeech textToSpeech; // tts객체
    private Button mBtnTts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_memo);

        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        textToSpeech = new TextToSpeech(this, this);  // TTS객체 생성

        findViewById(R.id.btnModify).setOnClickListener(mBtnClick);
        findViewById(R.id.btnDelete).setOnClickListener(mBtnClick);
        mBtnTts = findViewById(R.id.btnTts);
        mBtnTts.setOnClickListener(mBtnClick);
        mBtnTts.setEnabled(false);

        //탭생성
        mTabLayout.addTab(mTabLayout.newTab().setText("메모"));
        mTabLayout.addTab(mTabLayout.newTab().setText("사진찍기"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //ViewPager 생성
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mTabLayout.getTabCount());
        //tab 이랑 viewpager 랑 연결
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    } // onCreate

    private View.OnClickListener mBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //어떤 버튼이 클릭 됐는지 구분한다
            switch (view.getId()) {
                case R.id.btnModify:
                    //처리
                    saveProc();
                    break;

                case R.id.btnDelete:
                    //처리
                    deleteProc();
                    break;
                case R.id.btnTts:
                    //처리
                    ttsProc();
            }
        }
    };



    //수정버튼 처리
    private void saveProc() {

        //1.첫번째 프래그먼트의 EditText 값을 받아온다.
        FragmentModifyWrite f0 = (FragmentModifyWrite)mViewPagerAdapter.instantiateItem(mViewPager,0);
        //2.두번째 프래그먼트의 mPhotoPath 값을 가져온다.
        FragmentModifyCamera f1 = (FragmentModifyCamera)mViewPagerAdapter.instantiateItem(mViewPager,1);

        EditText edtWriteMemo = f0.getView().findViewById(R.id.edtWriteMemo);
        String memoStr = edtWriteMemo.getText().toString();
        String photoPath = f1.mPhotoPath;

        if(memoStr == null){
            Toast.makeText(this, "메모 내용을 입력해주세요", Toast.LENGTH_LONG).show();
            return;
        }
        if(photoPath == null){
            Toast.makeText(this, "사진을 등록해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 로그인한 멤버 획득
        MemberBean loginMember = FileDB.getLoginMember(ModifyMemoActivity.this);

        // 데체할 메모 생성
        MemoBean memoBean = new MemoBean();
        memoBean.memo = memoStr;
        memoBean.memoPicPath = photoPath;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        memoBean.memoDate = sdf.format(new Date());

        Intent intent = getIntent();
        long editMemoId = intent.getLongExtra("memoId", -1);
        if(editMemoId == -1) return;
        memoBean.memoId = editMemoId;

        // 메모 대체
        FileDB.setMemo(ModifyMemoActivity.this, loginMember.memId, memoBean);

        Log.e("SEMI", "memoStr: " + memoStr + ", photoPath: " + photoPath);
        Toast.makeText(this, "memoStr: " + memoStr + ", photoPath: " + photoPath, Toast.LENGTH_LONG).show();

        finish();

        //TODO 파일DB 에 저장처리

    }

    // 삭제버튼 저장 처리
    private void deleteProc(){
        Intent intent = getIntent();
        long delMemoId = intent.getLongExtra("memoId", -1);
        if(delMemoId == -1) return;

        // 로그인 멤버 획득
        MemberBean loginMember = FileDB.getLoginMember(ModifyMemoActivity.this);
        // 메모 삭제
        FileDB.delMemo(ModifyMemoActivity.this, loginMember.memId, delMemoId);
        // 로그인멤버 갱신
        FileDB.setLoginMember(ModifyMemoActivity.this, loginMember);
        finish();

    } // end deleteProc

    // 음성으로 듣기 버튼 처리
    private void ttsProc(){
        //1.첫번째 프래그먼트의 EditText 값을 받아온다.
        FragmentModifyWrite f0 = (FragmentModifyWrite)mViewPagerAdapter.instantiateItem(mViewPager,0);
        EditText edtWriteMemo = f0.getView().findViewById(R.id.edtWriteMemo);
        String memoStr = edtWriteMemo.getText().toString();

        textToSpeech.speak(memoStr, TextToSpeech.QUEUE_FLUSH, null, null);

    } // end ttsProc

    // TTS 객체 생성할 때 onInit메소드 호출해서 TTS를 사용할 수 있는지 검사함
    @Override
    public void onInit(int i) {
        if(i == TextToSpeech.SUCCESS) {// TTS 사용 가능 상태
            int result = textToSpeech.setLanguage(Locale.ENGLISH); // 언어 기본 설정 = 영어

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED){// TTS를 사용할 수 없는 상황이면
                Log.e("ModifyMemoActivity", "지원하지 않는 언어 입니다."); // 에러메시지 뿌림
            } else{
                mBtnTts.setEnabled(true); // 버튼을 누를 수 있도록 함
            }
        }
    } // end onInit

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private int tabCount;

        public ViewPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.tabCount = count;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new FragmentModifyWrite();
                case 1:
                    return new FragmentModifyCamera();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    } // end ViewPagerAdapter

    // 액티비티 종료될 때 TextToSpeech 객체 해제하기

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    } // end onDestroy
} // end class
