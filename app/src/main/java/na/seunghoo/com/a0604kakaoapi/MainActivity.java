package na.seunghoo.com.a0604kakaoapi;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    SessionCallback sessionCallback;
////주석은 왜 추가가 되지 않을까>
    //안드로이드 키 해시를 로그로 출력하는 메소드
    //키해시: 안드로이드 앱 마다 별도로 생성되는 ID 같은 개념입니다.
    //한 번만 호출하면 됩니다.
    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "na.seunghoo.com.a0604kakaoapi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                //SHA는 암호화 알고리즘의 종류이고 MessageDigest는 암호화 관련
                //모듈입니다.이 단어의 약자 md
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //키 해시 출력
                Log.e("키 해시", Base64.encodeToString(
                        md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e("해시 가져오기 실패", e.getMessage());
        }
    }

    class SessionCallback implements ISessionCallback {
        //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태.
        // 일반적으로 로그인 후의 다음 activity로 이동한다.
        @Override
        public void onSessionOpened() {
            UserManagement.requestMe(new MeResponseCallback() {
                //세션이 닫혀 실패한 경우로 에러 결과를 받습니다.
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }
                //사용자가 가입된 상태가 아니여서 실패한 경우입니다.
                @Override
                public void onNotSignedUp() {

                }

                //로그인에 성공한 경우
                @Override
                public void onSuccess(UserProfile result) {
                    Log.e("사용자 정보", result.toString());
                    Log.e("사용자 이름", result.getNickname());
                }

               //가입이 안된 경우와 세션이 닫힌 경우를 제외한 다른 이유로 요청이 실패한 경우의 콜백입니다.
                @Override
                public void onFailure(ErrorResult result){
                    finish();
                }
            });
        }

        //memory와 cache에 session 정보가 전혀 없는 상태.
        //일반적으로 로그인 버튼이 보이고 사용자가 클릭시 동의를 받아 access token요청을 시도한다.
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("세션 연결 실패", exception.getMessage());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
    }

}

