package com.example.semiprojectsample.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.semiprojectsample.bean.MemberBean;
import com.example.semiprojectsample.bean.MemoBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    // 기존 멤버 교체
    public static void setMember(Context context, MemberBean memberBean){
        // 전체 멤버 리스트를 취득한다.
        List<MemberBean> memberList = getMemberList(context);
        if(memberList.size() == 0) return;

        for(int i = 0; i < memberList.size();  i++){ // for Each : for(MemberBean bean: memberlsit) list들 돌면서 Member만 가지고 오는 것임
            MemberBean bean = memberList.get(i);
            if(TextUtils.equals(bean.memId, memberBean.memId)){
                // 같은 멤버ID를 찾았다.
                memberList.set(i, memberBean);
                break;
            }
        }

        // 새롭게 업데이트된 리스트를 저장한다.
        String jsonStr = mGson.toJson(memberList);
        // 멤버 리스트를 저장한다.
        SharedPreferences.Editor editor = getSP(context).edit();
        editor.putString("memberList", jsonStr); // 키값 조심하기!! 키값 일치시켜야함.
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

    // 메모를 추가하는 메소드
    public static void addMemo(Context context, String memId, MemoBean memoBean){
        MemberBean findMember= getFindMember(context, memId);

        if(findMember == null) return;

        List<MemoBean> memoList = findMember.memoList;

        if(memoList == null){
            memoList = new ArrayList<>();
        }

        // 고유 메모 ID를 생성해준다.
        memoBean.memoId = System.currentTimeMillis(); // 현재 이 메소드가 호출되는 시점의 시간
        memoList.add(0, memoBean);
        findMember.memoList = memoList;

        // 저장
        setMember(context, findMember);




    } // addMemo

    // 기존 메모 교체
    public static void setMemo(Context context, String memId, MemoBean memoBean){

            // 전체 메모 리스트를 취득한다.
            MemberBean memberBean = getFindMember(context, memId);
            if(memberBean.memoList.size() == 0) return;

            for(int i = 0; i < memberBean.memoList.size();  i++){ // for Each : for(MemberBean bean: memberlsit) list들 돌면서 Member만 가지고 오는 것임
                MemoBean bean = memberBean.memoList.get(i);
                if(bean.memoId == memoBean.memoId){
                    // 같은 멤버ID를 찾았다.
                    memberBean.memoList.set(i, memoBean);
                    setMember(context, memberBean);
                    break;
                }
            }
    }

    // 메모 삭제
    public static void delMemo(Context context, String memId, long memoId){
         MemberBean memberBean = getFindMember(context, memId);
         if(memberBean.memoList == null) return;

        for(int i=0; i < memberBean.memoList.size(); i++){
            MemoBean bean = memberBean.memoList.get(i);

            if( bean.memoId == memoId){
                // 메모삭제
                memberBean.memoList.remove(i);
                break;
            }
        }

        // 멤버 업데이트
        setMember(context, memberBean);
    }

    // 메모 리스트 취득
    public static List<MemoBean> getMemoList(Context context, String memId){
        MemberBean memberBean = getFindMember(context, memId);
        if(memberBean == null) return null;
        if(memberBean.memoList == null) {
            return new ArrayList<>();
        }else{
            return memberBean.memoList;
        }
    }

    //  (메모를 수정할 때) 메모 획득
    public static MemoBean findMemo(Context context, String memId, long memoId) {
        MemberBean memberBean = getFindMember(context, memId);
        List<MemoBean> memoList = memberBean.memoList;

        for (int i = 0; i < memoList.size(); i++) {
            MemoBean bean = memoList.get(i);
            if (bean.memoId == memoId) {
                return bean;
            }
        }
        return null;
    }


} // end Class

/*
Context 무조건 받기
 */
