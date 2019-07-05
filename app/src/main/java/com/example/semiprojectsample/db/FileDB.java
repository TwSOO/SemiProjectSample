package com.example.semiprojectsample.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.semiprojectsample.bean.MemberBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class FileDB {

    private static final String FILE_DB = "FileDB";
    private static Gson mGson = new Gson();

    private static SharedPreferences getSP(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_DB, Context.MODE_PRIVATE);
        return sp;
    }

    /* 새로운 멤버 추가 */
    public static void addMember(Context context, MemberBean memberBean){
        // 1. 기존의 멤버 리스트를 불러온다.
        List<MemberBean> memberList = getMemberList(context);
        // 2. 기존의 멤버 리스트에 추가한다.
        memberList.add(memberBean);
        // 3. 멤버 리스트를 저장한다.
        String listStr = mGson.toJson(memberList);
        // 4. 저장해라
        SharedPreferences.Editor editor = getSP(context).edit();
        editor.putString("memberList", listStr);
        editor.commit();

    }

    // memberList 가져오기
    public static List<MemberBean> getMemberList(Context context){
        String listStr = getSP(context).getString("memberList", null);
        // 저장된 리스트가 없을 경우에 새로운 리스트를 리턴한다.

        if(listStr == null){
            return new ArrayList<MemberBean>();
        }
                //키 : memberList로 가져옴, 없을 때는 null로 가져옴
        //GSON으로 변환한다.
        List<MemberBean> memberList =
            mGson.fromJson(listStr,new TypeToken<List<MemberBean>>(){}.getType());
        return memberList;

    } // getMemberList


    public static MemberBean getFindMember(Context context, String memId){
        // 1. 멤버 리스트를 가져온다.
        List<MemberBean> memberList = getMemberList(context);
        // 2. for문 돌면서 해당 아이디를 찾는다.
        for(MemberBean bean : memberList){
            // 3. 찾았을 경우는 해당 MemberBean 을 리턴한다.
            if(TextUtils.equals(bean.memId, memId)){ // 아이디가 같다.
                return bean;
            }
        }
        // 3-2. 못찾았을 경우는?? null return
        return null;
    }

    // 로그인한 MemberBean 을 저장한다.
    public static void setLoginMember(Context context, MemberBean bean){
       if(bean != null){
           String str = mGson.toJson(bean);
           SharedPreferences.Editor editor = getSP(context).edit();
           editor.putString("loginMemberBean", str);
           editor.commit();

       }
    }

    // 로그인한 MemberBean 을 취득한다
    public static MemberBean getLoginMember(Context context){
        String str = getSP(context).getString("loginMemberBean", null);
        if(str == null) return null;
        MemberBean memberBean = mGson.fromJson(str, MemberBean.class);
        return memberBean;
    }

    // 이 함수를 이용하여 이미 존재하는 회원인지 확인하기

} // end Class

/*
Context 무조건 받기
 */
