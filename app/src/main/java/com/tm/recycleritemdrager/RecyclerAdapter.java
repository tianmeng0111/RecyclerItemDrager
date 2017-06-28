package com.tm.recycleritemdrager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tian on 2017/6/7.
 */

//知识点总结：
//RecyclerView.ViewHolder中自带ItemView
//给RecyclerView.ViewHolder实现一个自定义接口，ItemTouchHelper.Callback可以直接拿到这个接口

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperCallback.OnItemMoveListener{
    private static final String TAG = "RecyclerAdapter";

    private RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;

    private LayoutInflater inflater;
    private List<DataBean> selectedList;
    private List<DataBean> addedList;

    private int mTitleCount = 1;
    private int mAddTitleCount = 1;

    public static final int SELECTED_TITLE_TYPE = 0;
    public static final int SELECTED_COMMENT_TYPE = 1;
    public static final int ADDED_TITLE_TYPE = 2;
    public static final int ADDED_COMMENT_TYPE = 3;

    public RecyclerAdapter (Context context, RecyclerView recyclerView, List selectedList, List addedList, int titleCount, int addTitleCount) {
        inflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        this.selectedList = selectedList;
        this.addedList = addedList;
        this.mTitleCount = titleCount;
        this.mAddTitleCount = addTitleCount;

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(this);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override//第二个参数是ViewType
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == SELECTED_TITLE_TYPE) {
            return new TitleViewHolder(viewGroup.getContext(), inflater.inflate(R.layout.item_title, viewGroup, false));
        } else if (viewType == SELECTED_COMMENT_TYPE){
            return new SelectedViewHolder(viewGroup.getContext(), inflater.inflate(R.layout.item_selected, viewGroup, false));
        } else if (viewType == ADDED_TITLE_TYPE) {
            return new TitleViewHolder(viewGroup.getContext(), inflater.inflate(R.layout.item_title, viewGroup, false));
        } else {
            return new AddedViewHolder(inflater.inflate(R.layout.item_add, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof TitleViewHolder) {

        }
        if (holder.getItemViewType() == ADDED_TITLE_TYPE) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.tvTitle.setText("推荐动物");
            titleViewHolder.tvremark.setText("(点击添加)");
        }

        if (holder.getItemViewType() == ADDED_COMMENT_TYPE || holder.getItemViewType() == SELECTED_COMMENT_TYPE) {
            if (holder instanceof AddedViewHolder) {
                final AddedViewHolder addedViewHolder = (AddedViewHolder) holder;
                addedViewHolder.iv.setImageResource(addedList.get(i - mTitleCount - selectedList.size() - mAddTitleCount).getImgRes());
            }
            if (holder instanceof SelectedViewHolder) {
                final SelectedViewHolder selectedViewHolder = (SelectedViewHolder) holder;
                selectedViewHolder.iv.setImageResource(selectedList.get(i - mTitleCount).getImgRes());
            }
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getItemViewType() == ADDED_COMMENT_TYPE)
                            performAdd(v, holder.getLayoutPosition());
                        if (holder.getItemViewType() == SELECTED_COMMENT_TYPE)
                            performDeselect(v, holder.getLayoutPosition());
                        onItemClickListener.onItemClick(v, holder.getLayoutPosition(), holder.getItemViewType());
                    }
                });
                //长按实现拖动
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (holder.getItemViewType() == SELECTED_COMMENT_TYPE)
                            itemTouchHelper.startDrag(recyclerView.getChildViewHolder(v));
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return selectedList == null || addedList == null?
                0 : mTitleCount + selectedList.size() + mAddTitleCount + addedList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.e(TAG, "onItemMove: fromPosition :" + fromPosition + ";toPosition:" + toPosition);
        //更改list中存储的元素位置
        if (toPosition >= mTitleCount && fromPosition >= mTitleCount) {
            DataBean dataBean = selectedList.remove(fromPosition - mTitleCount);
            selectedList.add(toPosition - mTitleCount, dataBean);

            //根据item从fromPosition到toPosition位置，自动调整数据变化，确保其他item会自动后移
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    /**
     * 重写方法目的：因为一个RecyclerView中item有多个不一样的布局。
     * 需要标题是显示一列，而内容显示4列。
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position < mTitleCount) {
            return SELECTED_TITLE_TYPE;
        } else if (position >= mTitleCount && position < selectedList.size() + mTitleCount){
            return SELECTED_COMMENT_TYPE;
        } else if (position >= selectedList.size() && position < mAddTitleCount + selectedList.size() + mTitleCount) {
            return ADDED_TITLE_TYPE;
        } else {
            return  ADDED_COMMENT_TYPE;
        }
    }

    private void performAdd(final View view, int position) {
        view.setVisibility(View.INVISIBLE);
        //动画计算
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        View targetView = gridLayoutManager.findViewByPosition(mTitleCount + selectedList.size() - 1 );
        float targetTop = targetView.getTop();
        float targetLeft = targetView.getLeft();
        if (selectedList.size() % gridLayoutManager.getSpanCount() == 0) {
            View nextTargetView = gridLayoutManager.findViewByPosition(mTitleCount + selectedList.size());
            targetTop = nextTargetView.getTop();
            targetLeft = nextTargetView.getLeft();
        } else {
            targetLeft += targetView.getWidth();
        }
        Log.e(TAG, "onItemClick: targetTop:" + targetTop + "targetLeft:" + targetLeft);

        //
//                    recyclerView.getItemAnimator().setChangeDuration(200);
        DataBean dataBean = addedList.get(position - mTitleCount - mAddTitleCount - selectedList.size());
        addedList.remove(position - mTitleCount - mAddTitleCount - selectedList.size());
        selectedList.add(dataBean);
        this.notifyItemMoved(position, mTitleCount + selectedList.size() - 1);
//                    mAdapter.notifyItemChanged(mTitleCount + mDataList.size() - 1);
//                    mAdapter.notifyItemChanged(position);
        startAnim(targetLeft, targetTop, view);
    }

    private void performDeselect(View view, int position) {
//        view.setVisibility(View.INVISIBLE);
//        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//        View targetView = gridLayoutManager.findViewByPosition(mTitleCount + selectedList.size() + mAddTitleCount + addedList.size() - 1);
//        float targetTop = targetView.getTop();
//        float targetLeft = targetView.getLeft();
//        if (recyclerView.indexOfChild(view) >= 0) {
//            if (addedList.size() % gridLayoutManager.getSpanCount() == 0) {
//                View nextTargetView = gridLayoutManager.findViewByPosition(mTitleCount + selectedList.size() + mAddTitleCount +
//                        addedList.size() - gridLayoutManager.getSpanCount());
//                targetTop = targetTop + nextTargetView.getHeight();
//                Log.e(TAG, "performDeselect: targetTop" + targetTop);
//                targetLeft = nextTargetView.getLeft();
//            } else if (addedList.size() % gridLayoutManager.getSpanCount() == 1){
//                targetLeft += targetView.getWidth();
//            } else {
//                targetLeft += targetView.getWidth();
//            }
//        }


        DataBean dataBean = selectedList.get(position - 1);
        selectedList.remove(position - 1);
        addedList.add(dataBean);
        this.notifyItemMoved(position, mTitleCount + selectedList.size() + mAddTitleCount + addedList.size() - 1);

//        startAnim(targetLeft, targetTop, view);
    }

    /**
     * 动画
     * @param targetX
     * @param targetY
     * @param currentView
     */
    private void startAnim(float targetX, float targetY, final View currentView) {
        final ViewGroup parent = (ViewGroup) recyclerView.getParent();

        final View mirrorView = addMirrorView(parent, currentView);

        TranslateAnimation animation = new TranslateAnimation(//测试过不行 是按照相对位置移动的，Animation.ABSOLUTE不好使？
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetX - currentView.getLeft(),
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetY - currentView.getTop());
        animation.setDuration(340);//时间如果少会闪屏，因为item的动画没做完
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                currentView.setVisibility(View.VISIBLE);
                parent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeView(mirrorView);
                    }
                }, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mirrorView.startAnimation(animation);
    }

    /**
     * 新建一个view替代当前点击的item执行动画
     * @param parent
     * @param currentView
     * @return
     */
    private View addMirrorView(ViewGroup parent, View currentView) {
        View currentImageView = currentView.findViewById(R.id.iv_icon);
        ImageView mirrorView = new ImageView(parent.getContext());
        Bitmap bitmap;
        if (currentImageView != null) {
            currentImageView.destroyDrawingCache();
            currentImageView.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(currentImageView.getDrawingCache());
            mirrorView.setImageBitmap(bitmap);
        } else {
            currentView.destroyDrawingCache();
            currentView.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(currentView.getDrawingCache());
            mirrorView.setImageBitmap(bitmap);
        }

        int [] locations = new int[2];
        currentView.getLocationOnScreen(locations);

        int [] locationsParent = new int[2];
        parent.getLocationOnScreen(locationsParent);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        layoutParams.leftMargin = locations[0];
        layoutParams.topMargin = locations[1] - locationsParent[1];
        parent.addView(mirrorView, layoutParams);

        return mirrorView;
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvremark;
        public TitleViewHolder(Context context, View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
            tvremark = (TextView) itemView.findViewById(R.id.tv_item_remark);
        }
    }

    static class SelectedViewHolder extends RecyclerView.ViewHolder implements ItemDragListener{

        private ImageView iv;
        private Vibrator vibrator;

        public SelectedViewHolder(Context context, View itemView) {
            super(itemView);//父类中实现了itemView
            iv = (ImageView) itemView.findViewById(R.id.iv_icon);
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        @Override
        public void onItemSelected() {
            animItem(1f, 1.2f, 1f, 0.8f);
            vibrator.vibrate(50);
        }

        @Override
        public void onItemCleared() {
            if (itemView.getAlpha() < 1f)
                animItem(1.2f, 1f, 0.8f, 1f);
        }

        private void animItem(float scaleFrom, float scaleTo, float alphaFrom, float alphaTo) {
            ObjectAnimator oaScaleX = ObjectAnimator.ofFloat(itemView, View.SCALE_X, scaleFrom, scaleTo);
            ObjectAnimator oaScaleY = ObjectAnimator.ofFloat(itemView, View.SCALE_Y, scaleFrom, scaleTo);
            ObjectAnimator oaAlpha = ObjectAnimator.ofFloat(itemView, View.ALPHA, alphaFrom, alphaTo);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(100);
            animatorSet.playTogether(oaScaleX, oaScaleY, oaAlpha);
            animatorSet.start();
        }
    }

    static class AddedViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;

        public AddedViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_add);
        }
    }

    /**
     * 点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position, int itemViewType);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 长按点击事件接口
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
