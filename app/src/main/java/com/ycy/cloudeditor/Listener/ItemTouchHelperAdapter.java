package com.ycy.cloudeditor.Listener;

import com.ycy.cloudeditor.Bean.NoteInfo;

/**
 * Created by kimi9 on 2018/2/17.
 */

public interface ItemTouchHelperAdapter {
    //移动item
    public void onItemMove(int fromPosition, int toPosition);

    //删除item
    public void onItemDelete(int position);

    public void onItemRecover(int position, NoteInfo noteInfo);
}
