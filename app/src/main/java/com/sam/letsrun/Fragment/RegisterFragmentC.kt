package com.sam.letsrun.Fragment


import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.logger.Logger
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterView
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.fragment_register_c.*
import org.jetbrains.anko.support.v4.toast


/**
 * 注册界面C
 * 设置头像和用户名
 */
class RegisterFragmentC : Fragment() {

    lateinit var mView: RegisterView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_c, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImageView.setOnClickListener {
            //选择头像获取方式
            MaterialDialog.Builder(this.context!!)
                    .items("拍照", "从相册获取", "取消")
                    .itemsGravity(GravityEnum.CENTER)
                    .itemsCallback { dialog, _, position, _ ->
                        when (position) {
                            0 -> { mView.selectImageCamera() }
                            1 -> { mView.selectImageAlbum() }
                            2 -> { dialog.cancel() }
                        }
                    }
                    .show()
        }

        nextButton.setOnClickListener {
            if (userName.text.toString() == "") {
                toast("用户名不能为空")
            } else {
                mView.initUserName(userName.text.toString())
                mView.nextFragment()
            }
        }
    }

    /**
     * 获取SDK卡读写权限
     * 获取相机权限
     */
    private fun requestPermission() = AndPermission.with(this.context!!)
            .permission(
                    Permission.READ_EXTERNAL_STORAGE,
                    Permission.WRITE_EXTERNAL_STORAGE,
                    Permission.CAMERA
            )
            .onGranted { permissions ->
                Logger.d(permissions)
            }
            .rationale { _, _, executor ->
                MaterialDialog.Builder(this.context!!)
                        .title("权限相关")
                        .content("需要获取您相机和相册的权限才能设置您的头像")
                        .neutralText("知道了")
                        .onNeutral { _, _ ->
                            executor.execute()
                        }
                        .show()
            }
            .onDenied { permissions ->
                if (AndPermission.hasAlwaysDeniedPermission(this.context!!, permissions)) {
                    val service = AndPermission.permissionSetting(this.context!!)
                    MaterialDialog.Builder(this.context!!)
                            .title("通知")
                            .content("请前往设置开启相关权限")
                            .positiveText("同意")
                            .onPositive { _, _ ->
                                service.execute()
                            }
                            .negativeText("拒绝")
                            .onNegative { _, _ ->
                                service.cancel()
                            }
                            .show()
                }
            }
            .start()

    /**
     * 设置userImage
     * @param uri(临时路径)
     */
    fun setUserImage(uri: Uri) {
        userImageView.setImageURI(null)
        userImageView.setImageURI(uri)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            requestPermission()
        }
    }

}
