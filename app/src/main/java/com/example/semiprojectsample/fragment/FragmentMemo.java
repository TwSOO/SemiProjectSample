package com.example.semiprojectsample.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.semiprojectsample.R;
import com.example.semiprojectsample.activity.NewMemoActivity;
import com.example.semiprojectsample.bean.MemberBean;
import com.example.semiprojectsample.bean.MemoBean;
import com.example.semiprojectsample.db.FileDB;

import java.util.List;

public class FragmentMemo extends Fragment {
    private static int NewMemoActivity = 1001;
    // 리스트뷰
    public ListView mLstMemo;

    // 원본데이터를 포함하는 어댑터
    ListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memo, container, false);

        mLstMemo = view.findViewById(R.id.lstMemo);
        view.findViewById(R.id.btnNewMemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //새메모 화면으로 이동
                Intent i = new Intent(getActivity(), NewMemoActivity.class);

                startActivityForResult(i,NewMemoActivity);
            }
        });

        // 로그인 사용자의 메모리스트 획득
        List<MemoBean> memoList = FileDB.getLoginMember(getActivity()).memoList;

        // Adapter 생성 및 적용
        adapter = new ListAdapter(memoList, getActivity());
        // 리스트뷰에 Adapter 설정
        mLstMemo.setAdapter(adapter);

        return view;
    } // end onCreateView

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NewMemoActivity){ // 리스트 갱신
            List<MemoBean> =


        }
    }

    // 어댑터
    class ListAdapter extends BaseAdapter{


        List<MemoBean> memoList;
        Context mContext;
        LayoutInflater inflater;

        // setter
        public void setMemoList(List<MemoBean> memoList) {
            this.memoList = memoList;
        }

        // Constructor
        public ListAdapter(List<MemoBean> memoList, Context context){
            this.memoList = memoList;
            this.mContext = context;
            this.inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            // infalter : UI(뷰 한줄)를 만드는데 필요함
        }
        @Override
        public int getCount() {
            return memoList.size();
        }

        @Override
        public Object getItem(int i) {
            return memoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //memo_item.xml획득
            view = inflater.inflate(R.layout.memo_item, null); // 조그만한 ui(뷰)를 만들어서 view에 담음

            // xml파일을 맵핑함
            TextView txtvMemo = view.findViewById(R.id.txtvMemo);
            TextView txtvDate = view.findViewById(R.id.txtvDate);
            Button btnEdit = view.findViewById(R.id.btnEdit);
            Button btnErase = view.findViewById(R.id.btnErase);
            Button btnEraswe = view.findViewById(R.id.btnDetail);

            // 원본에서 poistion번째 item획득
            final MemoBean memoBean = memoList.get(i);

            // 원본에서 데이터를 UI에 적용
            txtvMemo.setText(memoBean.memo);
            txtvDate.setText(memoBean.memoDate);

            // 수정 버튼

            // 삭제 버튼

            // 상세보기 버튼
            return view;
        }
    } // end ListAdatper
} // end class