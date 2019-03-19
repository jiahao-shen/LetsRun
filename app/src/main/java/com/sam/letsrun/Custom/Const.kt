package com.sam.letsrun.Custom

import android.os.Environment
import okhttp3.MediaType

/**
 * 常量类
 */
class Const {
    companion object {

        /*--ADDRESS--*/
//        private const val BASE_IP = "120.78.218.59" //阿里云服务器
//        const val BASE_HTTP_ADDRESS = "http://$BASE_IP/AppService/app/"
//        const val BASE_WEBSOCKET_ADDRESS = "ws://$BASE_IP:8000"
        private const val BASE_IP = "192.168.229.238"
        const val BASE_HTTP_ADDRESS = "http://$BASE_IP/LetsRunService/app/"
        const val BASE_WEBSOCKET_ADDRESS = "ws://$BASE_IP:8000"

        /*--TYPE--*/
        val FILETYPE = MediaType.parse("multipart/form-data")

        /*--LOCAL_FILE--*/
        val LOCAL_PATH = "${Environment.getExternalStorageDirectory()}/LetsRun"


        /*--SOCKET_CODE--*/
        const val SOCKET_PING_CODE = 0
        const val SOCKET_LOGIN_CODE = 1
        const val SOCKET_ADD_FRIEND = 2
        const val ADD_FRIEND_AGREE = 3
        const val ADD_FRIEND_REFUSE = 4
        const val ADD_FRIEND_SUCCESS = 5


        /*--LOGIN_CODE--*/
        const val LOGIN_SUCCESS = 10
        const val TELEPHONE_NOT_EXIST = 11
        const val PASSWORD_ERROR = 12
        const val UNKNOWN_ERROR = 13
        const val ALREADY_LOGIN = 14

        /*--REGISTER_CODE--*/
        const val CHECK_PHONE = 20
        const val REGISTER = 21
        const val TELEPHONE_ALREADY_EXIST = 22
        const val REGISTER_SUCCESS = 23

        /*--LOGOUT_CODE--*/
        const val LOGOUT_SUCCESS = 30
        const val LOGOUT_FAILED = 31

        /*--MYREQUEST_CODE--*/
        const val REQUEST_SUCCESS = 40
        const val REQUEST_FAILED = 41

        /*--SEARCH_USER_CODE--*/
        const val USER_NOT_EXIST = 50
        const val USER_ALREADY_FRIEND = 51
        const val USER_NOT_FRIEND = 52
        const val USER_IS_SELF = 53

        /*--FRIEND_LIST--*/
        const val LOAD_FRIEND_LIST_SUCCESS = 60

        /*--UPDATE_USER_INFO--*/
        const val UPDATE_INFO_SUCCESS = 70
        const val UPDATE_INFO_FAILED = 71

        /*--ACTIVITY_REQUEST--*/
        const val SELECT_IMAGE_CAMERA = 101
        const val SELECT_IMAGE_ALBUM = 102

        const val NEWS_HTTP_ADDRESS = "http://v.juhe.cn/toutiao/index?type=tiyu&key=1b42a673db5d082403746c1c670ac6ac"
    }
}
