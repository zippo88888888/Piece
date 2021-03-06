package com.official.read.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.official.read.R;
import com.official.read.content.Content;
import com.official.read.content.ReadException;
import com.official.read.util.L;
import com.official.read.util.RecyclerUtil;
import com.official.read.util.SPUtil;
import com.official.read.util.Toaster;
import com.official.read.util.anim.EasyTransition;
import com.official.read.util.anim.EasyTransitionOptions;
import com.official.read.weight.dialog.LoadDialog;
import com.official.read.weight.dialog.SpotsDialog;
import com.official.themelibrary.base.SkinBaseActivity;

import java.io.Serializable;


/**
 * com.official.read.base
 * Created by ZP on 2017/7/12.
 * description:
 * version: 1.0
 */

public abstract class BaseActivity<P extends BasePresenter<V>, V extends BaseView> extends /*AppCompatActivity*/SkinBaseActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener, BaseView, BaseDialog.DialogChildClickListener,
        SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener, OnRefreshListener, OnItemClickListener, OnNetWorkErrorListener,
        BaseFragment.TelActivityListener {

    protected boolean isSetBar = true;
    protected boolean isDiy = true;

    Dialog dialog;
    protected P presenter;

    Toolbar toolbar;
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(getDisplayState());
        int layoutID = getContentView();
        if (layoutID <= 0) {
            throw new NullPointerException("Layout is not null");
        } else {
            setContentView(layoutID);
        }
        if (isTabletDevice()) {
            boolean isError = (boolean) SPUtil.get(SPUtil.ERROR_STATE, false);
            if(!isError){
                Toaster.makeText(Toaster.TOP, R.string.error_notPhone);
            }
        }
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attachView((V) this);
        }
        getStatusColor();
        if (isSetBar) {
            initBar();
        }

        initView(savedInstanceState);
        initData(savedInstanceState);
    }

    /**
     * 获取屏幕状态
     * @return int
     *      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT 竖屏
     *      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE 横屏
     */
    protected int getDisplayState() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    protected abstract int getContentView();

    protected abstract P initPresenter();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract int getStatusColor();

    // 配合更换主题实现相对应的颜色
    protected abstract SwipeRefreshLayout getSwipeRefreshLayout();
    protected abstract LuRecyclerView getLuRecyclerView();

    protected final <T extends View> T $(int id) {
        try {
            return (T) findViewById(id);
        } catch (ClassCastException e) {
            L.e("Could not cast View to concrete class");
            throw e;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerUtil.setSwipeLuRecyclerViewTheme(getLuRecyclerView(), getSwipeRefreshLayout());
    }

    @Override
    protected void onDestroy() {
//        if (presenter != null) {
//            presenter.detachView();
//        }
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    protected void back() {
        onBackPressed();
    }

    protected final int getBaseColor() {
        return getResources().getColor(R.color.baseColor);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void dialogClick(View view, Object obj) {

    }

    @Override
    public void telActivity(Fragment fragment, Object value) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }

    /**
     * SwipeRefreshLayout 与 RecyclerView 的下拉刷新
     */
    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    /**
     * RecyclerView 的item的点击事件
     */
    @Override
    public void onItemClick(View view, int position) {

    }

    /**
     * RecyclerView 加载更多失败会调用
     */
    @Override
    public void reload() {

    }

    protected final boolean isTabletDevice() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private Dialog createDialog() {
        if (isDiy) {
            boolean dialogState = (boolean) SPUtil.get(SPUtil.DIALOG_STATE, false);
            Dialog dialog;
            if (dialogState) {
                dialog = new LoadDialog(this);
            } else {
                dialog = new SpotsDialog(this);
            }
            dialog.getWindow().setWindowAnimations(android.R.style.Animation_Translucent);
            return dialog;
        } else {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Loading...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.getWindow().setWindowAnimations(android.R.style.Animation_Translucent);
            return dialog;
        }
    }

    @Override
    public void showDialog(Boolean cancelable) {
        if (dialog == null) {
            dialog = createDialog();
        }
        dialog.setCanceledOnTouchOutside(false);
        if (cancelable != null) {
            dialog.setCancelable(cancelable);
        }

        dialog.show();
    }

    @Override
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected void jumpActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void jumpActivity(ArrayMap<String, Object> map, Class clazz) {
        jumpActivity(map, clazz, new View[0]);
    }

    // view 的id必须相同
    protected void jumpActivity(ArrayMap<String, Object> map, Class clazz, View... views) {
        if (map.isEmpty()) {
            throw new NullPointerException("ArrayMap<String, Object> map is not null");
        }
        boolean isAnim = false;
        if (views != null && views.length > 0) {
            isAnim = true;
        }
        Intent intent = new Intent(this, clazz);
        Bundle bundle = new Bundle();
        for (String key : map.keySet()) {
            Object o = map.get(key);
            if (o instanceof String) {
                // int,double,float,String
                bundle.putString(key, (String) o);
            } else if (o instanceof Serializable) {
                bundle.putSerializable(key, (Serializable) o);
            } else if (o instanceof Parcelable) {
                bundle.putParcelable(key, (Parcelable) o);
            } else {
                throw new ReadException("type error");
            }
        }
        intent.putExtras(bundle);
        if (isAnim) {
            EasyTransitionOptions options = EasyTransitionOptions.makeTransitionOptions(this, views);
            EasyTransition.startActivity(intent, options);
        } else {
            startActivity(intent);
        }
    }


    @Override
    public Context getBaseViewContext() {
        return this/*.getApplicationContext()*/;
    }

    @Override
    public void noAnywayData() {
        Toaster.makeText("没有数据");
    }

    @Override
    public void noLoadMoreData() {
        Toaster.makeText("没有更多数据了");
    }

    @Override
    public void error(int code, ReadException e) {
        Toaster.makeText("网络异常");
    }

    @Override
    public void setMessage(String message, ReadException e) {
        if (message != null) {
            Toaster.makeText(message);
        }
        if (!Content.IS_OFFICIAL && e != null) {
            e.printStackTrace();
        }
    }

    private void initBar() {
        toolbar = $(R.id.tool_bar);
        title = $(R.id.tool_bar_title);
        /**
         * toolBar的默认属性
         */
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    /**
     * 返回ToolBar
     */
    protected final Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * 设置toolBar的NavigationIcon
     */
    protected final void setNavigationIcon(Integer icon) {
        toolbar.setNavigationIcon(icon);
    }

    /**
     * 设置标题
     */
    public final void setTitle(String msg) {
        title.setText(msg);
    }

    public final void setTitle(int Rid) {
        title.setText(Rid);
    }

    /**
     * toolBar Menu显示
     */
    protected final void setToolbarInMenu() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    /**
     * 设置标题颜色
     */
    protected final void setTitleTextColor(int color) {
        title.setTextColor(color);
    }

    /**
     * 设置toolBar NavigationIcon 的点击事件
     */
    protected final void setNavigationIconClickListener(View.OnClickListener listener) {
        toolbar.setNavigationOnClickListener(listener);
    }

    /** 设置监听 */
    protected final void setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener listener){
        toolbar.setOnMenuItemClickListener(listener);
    }

}
