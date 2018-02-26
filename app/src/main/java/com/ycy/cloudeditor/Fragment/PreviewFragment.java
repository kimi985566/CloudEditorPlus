package com.ycy.cloudeditor.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangchengyu.markdown.View.MarkDownPreviewView;
import com.ycy.cloudeditor.Activity.EditActivity;
import com.ycy.cloudeditor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kimi9 on 2018/2/19.
 */

public class PreviewFragment extends Fragment {

    @BindView(R.id.tv_edit_title)
    TextView mTvTitle;
    @BindView(R.id.mdpv_edit_content)
    MarkDownPreviewView mMdpvEditContent;
    Unbinder unbinder;

    public PreviewFragment() {

    }

    public static PreviewFragment newInstance() {
        PreviewFragment fragment = new PreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        ((EditActivity) getActivity()).setPreviewView(mMdpvEditContent);
        return view;
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
