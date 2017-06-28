package com.tm.recycleritemdrager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Tian on 2017/6/7.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        this.initData();
        this.initView();
    }

    /**
     * 子类实现，获取布局文件
     * @return
     */
    abstract int getLayoutId();

    /**
     * 子类实现，初始化虚拟数据
     */
    abstract void initData();

    /**
     * 子类实现，初始化控件
     */
    abstract void initView();
}
