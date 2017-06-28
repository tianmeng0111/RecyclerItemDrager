package com.tm.recycleritemdrager;

import android.graphics.Bitmap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;

    private final static int IMG[] = {
            R.mipmap.img0,
            R.mipmap.img1,
            R.mipmap.img2,
            R.mipmap.img3,
            R.mipmap.img4,
            R.mipmap.img5,
            R.mipmap.img6,
            R.mipmap.img7
    };

    private final static int ADD_IMG[] = {
            R.mipmap.img8,
            R.mipmap.img9,
            R.mipmap.img10,
            R.mipmap.img11,
            R.mipmap.img12,
            R.mipmap.img13,
            R.mipmap.img14,
            R.mipmap.img15,
            R.mipmap.img16,
            R.mipmap.img17,
            R.mipmap.img18,
            R.mipmap.img19
    };

    private List<DataBean> mDataList;
    private List<DataBean> mAddedDataList;

    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    void initData() {
        mDataList = new ArrayList<>();
        for (int i : IMG) {
            mDataList.add(new DataBean(i));
        }
        mAddedDataList = new ArrayList<>();
        for (int i : ADD_IMG) {
            mAddedDataList.add(new DataBean(i));
        }
    }

    @Override
    void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        final RecyclerAdapter mAdapter = new RecyclerAdapter(MainActivity.this, recyclerView, mDataList, mAddedDataList, 1, 1);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 4, GridLayout.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {//return 跨的列数
                switch (mAdapter.getItemViewType(position)) {
                    case RecyclerAdapter.SELECTED_TITLE_TYPE:
                        return 4;
                    case RecyclerAdapter.SELECTED_COMMENT_TYPE:
                        return 1;
                    case RecyclerAdapter.ADDED_TITLE_TYPE:
                        return 4;
                    case RecyclerAdapter.ADDED_COMMENT_TYPE:
                        return 1;
                        default:
                            return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position, int itemViewType) {
                Log.e(TAG, "onItemClick: -----------------------");
                if (itemViewType == RecyclerAdapter.ADDED_COMMENT_TYPE) {

                }

                if (itemViewType == RecyclerAdapter.SELECTED_COMMENT_TYPE) {

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

}
