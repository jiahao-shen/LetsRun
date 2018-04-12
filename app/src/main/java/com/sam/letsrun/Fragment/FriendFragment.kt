package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.beardedhen.androidbootstrap.BootstrapButton
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.orhanobut.logger.Logger
import com.rengwuxian.materialedittext.MaterialEditText
import com.sam.letsrun.Adapter.AddFriendAdapter
import com.sam.letsrun.Adapter.FriendListAdapter
import com.sam.letsrun.Common.Const
import com.sam.letsrun.Common.MyUtils
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.AddFriendRequest
import com.sam.letsrun.Model.Friend
import com.sam.letsrun.Model.SearchUserResponse
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.FriendFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.FriendFragmentView
import com.sam.letsrun.View.MainView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_friend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast

class FriendFragment : Fragment(), FriendFragmentView {


    private var searchQuery: String = ""

    private var presenter = FriendFragmentPresenter()
    lateinit var mView: MainView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var user: User
    private lateinit var token: String

    private var searchUserDialog: MaterialDialog? = null
    private var addFriendRequestDialog: MaterialDialog? = null
    private var addFriendRequestList: ArrayList<AddFriendRequest> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.init(this.context!!)
        presenter.mView = this

        initUser()      //获取user信息

        searchButton.setOnClickListener {
            if (!friendSearch.isSearchOpen) {   //监听搜索框
                friendSearch.showSearch()
            } else {
                friendSearch.closeSearch()
            }
        }

        friendSearch.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {   //监听查询框
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!RegexUtils.isMobileExact(query)) {
                    toast("请输入正确的手机号")
                } else {
                    searchQuery = query.toString()
                    presenter.searchUser(user.telephone, token, searchQuery)
                }
                return true
            }

        })

        noticeButton.setOnClickListener {
            //监听notice消息按钮
            showAddFriendRequestDialog()
        }

        friendRefreshLayout.setOnRefreshListener {
            presenter.loadFriendList(user.telephone, token)
        }

        presenter.loadFriendList(user.telephone, token)
    }

    @SuppressLint("CommitPrefEdits")
    private fun initUser() {
        sharedPreferences = activity!!.getSharedPreferences(activity!!.packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        token = sharedPreferences.getString("token", "")
        user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)
    }

    override fun addFriendResponseError() {
        //发送回复失败
        toast("未知错误,请稍后再试")
        sharedPreferencesEditor.putString("friendRequestList",
                sharedPreferences.getString("friendRequestListTemp", "")).commit()   //重新保存

        addFriendRequestList = if (sharedPreferences.getString("friendRequestList", "") == "") {    //更新addFriendRequestList
            ArrayList()
        } else {
            Gson().fromJson<ArrayList<AddFriendRequest>>(
                    sharedPreferences.getString("friendRequestList", ""),
                    object : TypeToken<ArrayList<AddFriendRequest>>() {}.type)
        }
    }


    override fun showSearchResult(msg: Int, response: SearchUserResponse?) {        //显示搜索结果
        searchUserDialog = MaterialDialog.Builder(this.context!!)
                .customView(R.layout.dialog_search_friend, false)
                .title("用户信息")
                .titleGravity(GravityEnum.CENTER)
                .build()

        val rootView: View = searchUserDialog?.customView!!     //获取子布局控件

        val searchImagView: CircleImageView = rootView.findViewById(R.id.search_image_view)
        val searchUserName: TextView = rootView.findViewById(R.id.search_user_name)
        val searchAddInfo: MaterialEditText = rootView.findViewById(R.id.search_add_info)
        val searchAddUserButton: BootstrapButton = rootView.findViewById(R.id.search_add_user_button)

        searchAddUserButton.setOnClickListener {
            //发送好友申请
            if (searchAddInfo.isCharactersCountValid) {
                mView.addFriendRequest(searchQuery, searchAddInfo.text.toString())
                toast("已发送好友请求")
                searchUserDialog?.cancel()
            } else {
                toast("验证消息过长")
            }
        }

        when (msg) {
            Const.USER_IS_SELF -> {     //搜索的是自己
                GlideApp.with(this)
                        .load(MyUtils.getImageUrl(user.telephone))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_user_image)
                        .into(searchImagView)

                searchUserName.text = user.username

                searchAddInfo.visibility = View.GONE

                searchAddUserButton.text = "您不能添加自己"
                searchAddUserButton.bootstrapBrand = DefaultBootstrapBrand.REGULAR
                searchAddUserButton.isClickable = false
            }

            Const.USER_NOT_EXIST -> {       //搜索的用户不存在
                searchImagView.setImageResource(R.drawable.ic_user_image)

                searchUserName.text = "???"

                searchAddInfo.visibility = View.GONE

                searchAddUserButton.text = "用户不存在"
                searchAddUserButton.bootstrapBrand = DefaultBootstrapBrand.REGULAR
                searchAddUserButton.isClickable = false
            }

            Const.USER_ALREADY_FRIEND -> {      //搜索的用户已经是好友
                GlideApp.with(this)
                        .load(MyUtils.getImageUrl(response!!.telephone!!))
                        .placeholder(R.drawable.ic_user_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(searchImagView)

                searchUserName.text = response.username

                searchAddInfo.visibility = View.GONE

                searchAddUserButton.visibility = View.VISIBLE
                searchAddUserButton.text = "已经是好友"
                searchAddUserButton.bootstrapBrand = DefaultBootstrapBrand.SUCCESS
                searchAddUserButton.isClickable = false
            }

            Const.USER_NOT_FRIEND -> {      //搜索的用户不是好友
                GlideApp.with(this)
                        .load(MyUtils.getImageUrl(response!!.telephone!!))
                        .placeholder(R.drawable.ic_user_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(searchImagView)

                searchUserName.text = response.username

                searchAddInfo.visibility = View.VISIBLE

                searchAddUserButton.visibility = View.VISIBLE
                searchAddUserButton.text = "添加好友"
                searchAddUserButton.bootstrapBrand = DefaultBootstrapBrand.DANGER
                searchAddUserButton.isClickable = true
            }
        }

        searchUserDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: ArrayList<AddFriendRequest>) {
        addFriendRequestList = event
        noticeButton.show(true)
    }

    private fun showAddFriendRequestDialog() {  //显示好友申请列表

        addFriendRequestDialog = MaterialDialog.Builder(this.context!!)
                .title("好友申请")
                .titleGravity(GravityEnum.CENTER)
                .customView(R.layout.dialog_add_friend_request, true)
                .build()

        val rootView: View = addFriendRequestDialog?.customView!!
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.add_friend_request_list)
        val agreeButton = rootView.findViewById<BootstrapButton>(R.id.agree_button)
        val refuseButton = rootView.findViewById<BootstrapButton>(R.id.refuse_button)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        val adapter = AddFriendAdapter(addFriendRequestList)      //初始化适配器
        adapter.emptyView = View.inflate(context, R.layout.dialog_empty_add_friend, null)       //加载空布局
        recyclerView.adapter = adapter      //绑定适配器

        agreeButton.setOnClickListener {
            //同意按钮
            addFriendAnswer(Const.ADD_FRIEND_AGREE, adapter, recyclerView)
        }

        refuseButton.setOnClickListener {
            //拒绝按钮
            addFriendAnswer(Const.ADD_FRIEND_REFUSE, adapter, recyclerView)
        }

        addFriendRequestDialog!!.show()

    }

    override fun addFriendSuccess() {
        toast("添加成功")
    }

    private fun addFriendAnswer(msg: Int, adapter: AddFriendAdapter, recyclerView: RecyclerView) {

        val addFriendResponseList = ArrayList<HashMap<String, String>>()

        sharedPreferencesEditor.putString("friendRequestListTemp", Gson().toJson(addFriendRequestList)).commit()    //保存好友申请列表到temp

        Logger.json(Gson().toJson(addFriendRequestList))

        for (i in addFriendRequestList.indices) {
            val checkBox = adapter.getViewByPosition(recyclerView, i, R.id.item_add_friend_request_checkbox) as CheckBox?
            checkBox?.let {
                if (checkBox.isChecked) {       //如果打了勾
                    //添加到提交对象中去
                    addFriendResponseList.add(hashMapOf("fromTelephone" to adapter.getItem(i)!!.fromTelephone, "toTelephone" to adapter.getItem(i)!!.toTelephone))
                    //同时从本地申请列表中删除
                    addFriendRequestList.remove(adapter.getItem(i)!!)
                }
            }
        }
        adapter.notifyDataSetChanged()      //更新界面
        sharedPreferencesEditor.putString("friendRequestList", Gson().toJson(addFriendRequestList)).commit()   //保存更新后的申请列表
        presenter.addFriendAnswer(user.telephone, token, msg, addFriendResponseList)    //提交申请回答
        EventBus.getDefault().postSticky(addFriendRequestList)
    }

    override fun loadFriendListError() {
        toast("网络异常,请稍后再试")
        friendRefreshLayout.finishRefresh(false)
    }

    override fun loadFriendListSuccess(list: ArrayList<Friend>) {
        friendRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = FriendListAdapter(list)
        adapter.emptyView = View.inflate(context, R.layout.friend_list_empty, null)
        friendRecyclerView.adapter = adapter
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(this.context!!, R.drawable.friend_list_decoration)!!)
        friendRecyclerView.addItemDecoration(decoration)
        friendRefreshLayout.finishRefresh(true)
    }
}// Required empty public constructor
