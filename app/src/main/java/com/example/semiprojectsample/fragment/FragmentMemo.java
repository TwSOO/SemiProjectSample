package com.example.semiprojectsample.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.example.semiprojectsample.activity.ModifyMemoActivity;
import com.example.semiprojectsample.activity.NewMemoActivity;
import com.example.semiprojectsample.bean.MemberBean;
import com.example.semiprojectsample.bean.MemoBean;
import com.example.semiprojectsample.db.FileDB;

import java.util.List;

public class FragmentMemo extends Fragment {
    private static int NEW_MEMO = 1001;
    private static int MODIFY_MEMO = 1002;
    // 리스트뷰
    public ListView mLstMemo;

    // 원본데이터를 포함하는 어댑터
    ListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memo, container, false);

        mLstMemo = view.findViewById(R.id.lstMemo);
        view.findViewById(R.id.btnNewMemo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //새메모 화면으로 이동
                Intent i = new Intent(getActivity(), NewMemoActivity.class);

                startActivityForResult(i,NEW_MEMO);
            }
        });


        return view;
    } // end onCreateView

    @Override
    public void onResume() {
        super.onResume();

        MemberBean memberBean = FileDB.getLoginMember(getActivity());
        // 로그인 사용자의 메모리스트 획득
        List<MemoBean> memoList = FileDB.getMemoList(getActivity(), memberBean.memId);
        // Adapter 생성 및 적용
        adapter = new ListAdapter(memoList, getActivity());
        // 리스트뷰에 Adapter 설정
        mLstMemo.setAdapter(adapter);

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
            if(memoList == null) return 0;
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            //memo_item.xml획득
            view = inflater.inflate(R.layout.memo_item, null); // 조그만한 ui(뷰)를 만들어서 view에 담음

            // xml파일을 맵핑함
            ImageView imgvMemoPhoto = view.findViewById(R.id.imgvMemoPhoto);
            TextView txtvMemo = view.findViewById(R.id.txtvMemo);
            TextView txtvDate = view.findViewById(R.id.txtvDate);
            Button btnEdit = view.findViewById(R.id.btnEdit);
            Button btnErase = view.findViewById(R.id.btnErase);
            Button btnDetail = view.findViewById(R.id.btnDetail);

            // 원본에서 poistion번째 item획득
            final MemoBean memoBean = memoList.get(i);

            // 원본에서 데이터를 UI에 적용
            ///////////////////////////////////////////////////////
            Bitmap bitmap = BitmapFactory.decodeFile(memoBean.memoPicPath);
            Bitmap resizedBmp = getResizedBitmap(bitmap, 4, 100, 100); // 비트맵 이미지는 4분의 1 사이즈로 줄임

            bitmap.recycle();

            //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이애를 다시 원상복구 시킨다.
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(memoBean.memoPicPath);
            } catch(Exception e) {
                e.printStackTrace();
            }
            int exifOrientation;
            int exifDegree;
            if(exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientToDegree(exifOrientation);
            } else {
                exifDegree = 0;
            }
            Bitmap rotatedBmp = roate(resizedBmp, exifDegree);
            imgvMemoPhoto.setImageBitmap( rotatedBmp );
            ///////////////////////////////////////////////////////
            //imgvMemoPhoto.setImageURI(Uri.parse(memoBean.memoPicPath));
            txtvMemo.setText(memoBean.memo);
            txtvDate.setText(memoBean.memoDate);

            // 수정 버튼 리스너 셋
            btnEdit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    startModifyMemoActivity(getActivity(),memoBean.memoId);
                }
            });
            // 삭제 버튼 리스너 셋
            btnErase.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("주의!");
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MemberBean loginMember = FileDB.getLoginMember(getActivity());
                            FileDB.delMemo(getActivity(), loginMember.memId, memoBean.memoId);

                            List<MemoBean> memoList = FileDB.getMemoList(getActivity(), loginMember.memId); // 디비에서 메모리스트 새로 가져오기
                            setMemoList(memoList); // 어댑터안의 데이터 변경시키기
                            notifyDataSetChanged();// 어댑터에게 데이터 변경되었음을 알려주어 화면 바꾸도록 하기
                        }
                    }); // 예 눌렀을 때
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show(); // 다이얼로그 표시

                } // onClick
            });

            // 상세보기 버튼 리스너 셋
            btnDetail.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    startModifyMemoActivity(getActivity(), memoBean.memoId);
                }
            });

            return view;
        } // getView

        private void startModifyMemoActivity(Context context, long memoId){
            Intent intent = new Intent(context, ModifyMemoActivity.class);
            intent.putExtra("memoId", memoId);
            startActivity(intent);
    } // startModifyMemoActivity()

        //비트맵의 사이즈를 줄여준다.
        public  Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = size;
            Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
            return resized;
        }

        private int exifOrientToDegree(int exifOrientation) {
            if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            }
            else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            }
            else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            return 0;
        }

        private Bitmap roate(Bitmap bmp, float degree) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                    matrix, true);
        }
    } // end ListAdatper
} // end class