package com.ycy.cloudeditor.Bean;

import java.io.Serializable;

/**
 * Created by kimi9 on 2018/2/17.
 */

public class NoteInfo implements Serializable {

    //implements Serialzable是为了在Intent中能够直接传递Note对象

    /**
     * id-note编号
     * title-note标题
     * content- 内容
     * time-修改时间
     */
    private int genius_id;
    private String Title;
    private String content;
    private String time;

    public NoteInfo(int id, String title, String content, String time) {
        this.genius_id = id;
        this.Title = title;
        this.content = content;
        this.time = time;
    }

    public NoteInfo() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGenius_id(int genius_id) {
        this.genius_id = genius_id;
    }

    public int getGenius_id() {
        return genius_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NoteInfo)) {
            return false;
        }

        NoteInfo noteInfo = (NoteInfo) obj;

        if (noteInfo.getTitle().equals(Title)) {
            return true;
        }

        return false;
    }
}
