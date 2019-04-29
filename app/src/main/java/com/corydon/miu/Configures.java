package com.corydon.miu;

public class Configures {
    public static final String HOST="http://120.79.50.55:8080/";
    //public static final String HOST="http://127.0.0.1/";
    public static final String URL_SIGN_IN=HOST+"MiuBackstage/user/signIn";
    public static final String URL_SIGN_UP=HOST+"MiuBackstage/user/signUp";
    public static final String URL_ACTIVE_USER=HOST+"MiuBackstage/user/activeUser";
    public static final String URL_UPLOAD_PIC=HOST+"MiuBackstage/user/uploadPic";
    public static final String URL_UPLOAD_DISCUSS=HOST+"MiuBackstage/discuss/upload";
    public static final String URL_PUSH_DISCUSS=HOST+"MiuBackstage/discuss/push";
    public static final String URL_DISCUSS_DO_LIKE=HOST+"MiuBackstage/discuss/doLike";
    public static final String URL_DISCUSS_COMMENT=HOST+"MiuBackstage/discuss/comment";
    public static final String URL_DISCUSS_GET_COMMENTS=HOST+"MiuBackstage/discuss/getComments";
    public static final String URL_ACCOUNT_SETTING=HOST+"MiuBackstage/user/accountSetting";
    public static final String URL_GET_LIKED_DISCUSS=HOST+"MiuBackstage/user/accountSetting";
    public static final String URL_MY_COMMENTS=HOST+"MiuBackstage/user/myComments";

    /**
     * @see #RESULT_ACTIVE_USER_FAILED 表示用户激活的token错误或者已经激活
     */
    public static final int RESULT_OK=200;
    public static final int RESULT_USER_UN_ACTIVE=300;
    public static final int RESULT_USER_NON_EXISTENT=301;
    public static final int RESULT_USER_EXISTENT=302;
    public static final int RESULT_ACCESS_DENIED=303;
    public static final int RESULT_ACTIVE_USER_FAILED=304;
    public static final int RESULT_PASSWORDS_ERROR=305;
    public static final int RESULT_REPEAT_DO_LIKE=306;
    public static final int RESULT_DISCUSS_NON_EXISTENT=307;
    public static final int RESULT_SYSTEM_ERROR=400;
}
