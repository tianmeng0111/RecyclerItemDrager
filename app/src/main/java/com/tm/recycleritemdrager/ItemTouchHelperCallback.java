package com.tm.recycleritemdrager;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by Tian on 2017/6/7.
 * Recycler中拖拽实现在这个类中实现
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private static final String TAG = "ItemTouchHelperCallback";

    private OnItemMoveListener onItemMoveListener;

    public ItemTouchHelperCallback(OnItemMoveListener onItemMoveListener) {
        this.onItemMoveListener = onItemMoveListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.e(TAG, "getMovementFlags: ================");
        //标题不可以拖动和滑动
        if (viewHolder instanceof RecyclerAdapter.TitleViewHolder) {
            return makeFlag(0, 0);
        }
        int dragFlag;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager || layoutManager instanceof StaggeredGridLayoutManager) {
            //确保RecyclerView的item可以上下左右拖动
            dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        //不允许进行滑动
        int swipeFlag = 0;
        int movementFlags = makeMovementFlags(dragFlag, swipeFlag);
        return movementFlags;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.e(TAG, "onMove: ================");
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        if (onItemMoveListener != null) {
            onItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }
        return false;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        Log.e(TAG, "onMoved: ================");
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        //返回true去支持长按RecyclerView的item时的drag事件
        //这里返回false，item是不可以拖动的，是为了头部不能被拖动。
        // 返回false的情况下如何实现拖动：在item的LongClickListener中实现拖动，
        //itemTouchHelper.startDrag(recyclerView.getChildViewHolder(view));来实现拖动
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * 开始拖拽某个Item，做拖拽点击动画
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.e(TAG, "onSelectedChanged: ================");
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof RecyclerAdapter.SelectedViewHolder) {
                ItemDragListener itemDragListener = (RecyclerAdapter.SelectedViewHolder) viewHolder;
                itemDragListener.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 开始释放拖拽某个Item，做释放动画
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.e(TAG, "clearView: ================");
        if (viewHolder instanceof RecyclerAdapter.SelectedViewHolder) {
            RecyclerAdapter.SelectedViewHolder selectedViewHolder = (RecyclerAdapter.SelectedViewHolder) viewHolder;
            ItemDragListener itemDragListener = selectedViewHolder;
            itemDragListener.onItemCleared();
        }
        super.clearView(recyclerView, viewHolder);
    }

    /**
     * 这个方法也可处理item动画的
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    interface OnItemMoveListener {
        void onItemMove(int fromPosition, int toPosition);
    }

}
