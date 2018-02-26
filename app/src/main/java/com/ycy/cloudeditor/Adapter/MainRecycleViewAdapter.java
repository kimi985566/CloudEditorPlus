package com.ycy.cloudeditor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ycy.cloudeditor.Bean.NoteInfo;
import com.ycy.cloudeditor.Listener.ItemTouchHelperListener;
import com.ycy.cloudeditor.Listener.MyItemClickListener;
import com.ycy.cloudeditor.R;

import java.util.Collections;
import java.util.List;


/**
 * Created by kimi9 on 2018/2/15.
 */

public class MainRecycleViewAdapter extends RecyclerView.Adapter<MainRecycleViewAdapter.itemViewHolder>
        implements ItemTouchHelperListener, View.OnClickListener {

    public static String TAG = MainRecycleViewAdapter.class.getSimpleName();

    private Context mContext;
    private List<NoteInfo> mNoteInfoList;
    private MyItemClickListener mItemClickListener = null;

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

    public void setItemClickListener(MyItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
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

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v, (int) v.getTag());
        }
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
        view.setOnClickListener(this);
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
