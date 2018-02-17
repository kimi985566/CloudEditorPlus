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
    private int id;
    private String Title;
    private String content;
    private String time;
    private int index;

    public NoteInfo(int id, String title, String content, String time) {
        this.id = id;
        this.Title = title;
        this.content = content;
        this.time = time;
    }

    public NoteInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
