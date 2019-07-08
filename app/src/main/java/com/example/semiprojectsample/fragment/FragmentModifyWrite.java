package com.example.semiprojectsample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.semiprojectsample.R;
import com.example.semiprojectsample.bean.MemberBean;
import com.example.semiprojectsample.bean.MemoBean;
import com.example.semiprojectsample.db.FileDB;

import java.lang.reflect.Member;


public class FragmentModifyWrite extends Fragment {
    private EditText mEdtWriteMemo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_write, container, false);
        mEdtWriteMemo = view.findViewById(R.id.edtWriteMemo);

        // 선택된 메모 ID획득
        Intent intent = getActivity().getIntent();
        long memoId = intent.getLongExtra("memoId", -1);

        // 로그인 사용자 획득
        MemberBean loginMember = FileDB.getLoginMember(getActivity());
        if(memoId != -1){
            // 메모내용 보이기
            MemoBean memoBean = FileDB.findMemo(getActivity(), loginMember.memId, memoId);
            mEdtWriteMemo.setText(memoBean.memo);
        }


        return view;
    }
}
