package com.ycy.cloudeditor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ycy.cloudeditor.Bean.NoteInfo;
import com.ycy.cloudeditor.Listener.ItemTouchHelperAdapter;
import com.ycy.cloudeditor.R;

import java.util.Collections;
import java.util.List;


/**
 * Created by kimi9 on 2018/2/15.
 */

public class MainRecycleViewAdapter extends RecyclerView.Adapter<MainRecycleViewAdapter.itemViewHolder>
        implements ItemTouchHelperAdapter {

    public static String TAG = MainRecycleViewAdapter.class.getSimpleName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    private List<NoteInfo> mNoteInfoList;

    public MainRecycleViewAdapter() {

    }

    public MainRecycleViewAdapter(Context context, List<NoteInfo> noteInfoList) {
        mContext = context;
        mNoteInfoList = noteInfoList;
    }

    //清空列表
    public void removeAllItem() {
        mNoteInfoList.clear();
        notifyDataSetChanged();
    }

    //从List移除对象
    public void removeItem(int position) {
        mNoteInfoList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mNoteInfoList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDelete(int position) {
        mNoteInfoList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemRecover(int position, NoteInfo noteInfo) {
        mNoteInfoList.add(position, noteInfo);
        notifyItemInserted(position);
    }

    static class itemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTv_title;
        private TextView mTv_content;
        private TextView mTv_time;

        public itemViewHolder(View itemView) {
            super(itemView);
            mTv_title = itemView.findViewById(R.id.note_title);
            mTv_content = itemView.findViewById(R.id.note_content);
            mTv_time = itemView.findViewById(R.id.note_time);
        }

    }

    @Override
    public itemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_note_card, parent, false);
        Log.i(TAG, ": itemViewHolder 布局成功");
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(itemViewHolder holder, int position) {
        holder.mTv_title.setText(mNoteInfoList.get(position).getTitle());
        holder.mTv_content.setText(mNoteInfoList.get(position).getContent());
        holder.mTv_time.setText(mNoteInfoList.get(position).getTime());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mNoteInfoList.size();
    }
}
