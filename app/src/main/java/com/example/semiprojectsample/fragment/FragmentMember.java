package com.example.semiprojectsample.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.semiprojectsample.R;
import com.example.semiprojectsample.bean.MemberBean;
import com.example.semiprojectsample.db.FileDB;

import java.io.File;


public class FragmentMember extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        ImageView imgProfile = view.findViewById(R.id.imgProfile);
        TextView txtMemId = view.findViewById(R.id.txtMemId);
        TextView txtMemName = view.findViewById(R.id.txtMemName);
        TextView txtMemDate = view.findViewById(R.id.txtMemDate);
        TextView txtMemPw = view.findViewById(R.id.txtMemPw);

        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        // 파일DB에서 가져온다.
        MemberBean memberBean = FileDB.getLoginMember(getActivity());
        Bitmap imgBitMap = BitmapFactory.decodeFile(memberBean.photoPath);
        Bitmap rotatedBmp = roate(imgBitMap, 90);
        imgProfile.setImageBitmap(rotatedBmp);
        // imgProfile.setImageURI(Uri.fromFile(new File(memberBean.photoPath)));
        txtMemId.setText( memberBean.memId );
        txtMemName.setText( memberBean.memName );
        txtMemPw.setText(memberBean.memPw);
        txtMemDate.setText(memberBean.memRegDate);



        return view;
    }

    private Bitmap roate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
    }


}
