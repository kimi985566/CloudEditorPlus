package com.ycy.cloudeditor.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xilingyuli.markdown.MarkDownEditorView;
import com.ycy.cloudeditor.Activity.EditActivity;
import com.ycy.cloudeditor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kimi9 on 2018/2/19.
 */

public class EditFragment extends Fragment {

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    @BindView(R.id.ed_edit_title)
    EditText mEdTitle;
    @BindView(R.id.mdev_edit_content)
    MarkDownEditorView mMdevContent;
    private String mTitle;
    private String mContent;
    Unbinder unbinder;

    public EditFragment() {

    }

    public static EditFragment newInstance(String title, String content) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
            mContent = getArguments().getString(CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        unbinder = ButterKnife.bind(this, view);
        mEdTitle.setText(mTitle);
        mMdevContent.setText(mContent);
        ((EditActivity) getActivity()).setEditorView(mMdevContent);
        return view;
    }

    public String getTitle() {
        return mEdTitle.getText() + "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
