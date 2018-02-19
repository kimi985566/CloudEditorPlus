package com.ycy.cloudeditor.Activity;

import android.animation.LayoutTransition;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.xilingyuli.markdown.MarkDownController;
import com.xilingyuli.markdown.MarkDownEditorView;
import com.xilingyuli.markdown.MarkDownPreviewView;
import com.xilingyuli.markdown.OnPreInsertListener;
import com.xilingyuli.markdown.ToolsAdapter;
import com.ycy.cloudeditor.Fragment.EditFragment;
import com.ycy.cloudeditor.Fragment.PreviewFragment;
import com.ycy.cloudeditor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kimi9 on 2018/2/15.
 */

public class EditActivity extends AppCompatActivity implements OnPreInsertListener {

    @BindView(R.id.tools_edit)
    RecyclerView mToolsEdit;
    @BindView(R.id.vp_edit)
    ViewPager mVpEdit;
    @BindView(R.id.save)
    ImageView mSave;
    @BindView(R.id.toolbar_edit)
    Toolbar mToolbarEdit;
    @BindView(R.id.fab_edit)
    FloatingActionButton mFabEdit;

    private MarkDownEditorView mEditorView;
    private MarkDownPreviewView mPreviewView;
    private MarkDownController markDownController;
    private ToolsAdapter mToolsAdapter;
    private EditFragment mEditFragment;
    private PreviewFragment mPreviewFragment;
    private InputMethodManager mInputMethodManager;

    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    Unbinder mUnbinder;

    public static String Path = Environment.getExternalStorageDirectory()
            + File.separator + "CloudEditor" + File.separator + "markdown" + File.separator;
    private List<Fragment> mFragmentList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Utils.init(this);
        mUnbinder = ButterKnife.bind(this);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        initUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initUI() {

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewGroup appappBarLayout = (ViewGroup) findViewById(R.id.app_bar_edit);
            appappBarLayout.getLayoutTransition().setDuration(LayoutTransition.CHANGE_DISAPPEARING, 0);

            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.root_view_app_bar_edit);
            LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }

        //init fragments
        String title = getIntent().getStringExtra(TITLE) == null ? "" : getIntent().getStringExtra(TITLE);
        String content = getIntent().getStringExtra(CONTENT) == null ? "" : getIntent().getStringExtra(CONTENT);
        mEditFragment = EditFragment.newInstance(title, content);
        mPreviewFragment = PreviewFragment.newInstance();

        //init toolbar
        setSupportActionBar(mToolbarEdit);
        mToolsAdapter = new ToolsAdapter(getLayoutInflater());
        mToolsEdit.setAdapter(mToolsAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        //init viewPager
        mVpEdit.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (markDownController == null)
                    return;
                if (position == 1) {
                    mToolsEdit.setVisibility(View.GONE);
                    mPreviewFragment.setTitle(mEditFragment.getTitle());
                    markDownController.preview();
                } else {
                    mToolsEdit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //显示/隐藏软键盘，在onPageSelected中调用会引起滑动动画冲突
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mEditorView.requestFocus();
                    if (mVpEdit.getCurrentItem() == 1) {
                        mInputMethodManager.hideSoftInputFromWindow(mEditorView.getWindowToken(), 0);
                    }
                }
            }
        });

        mVpEdit.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return mEditFragment;
                    case 1:
                        return mPreviewFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });


        mFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVpEdit != null) {
                    int index = 1 - mVpEdit.getCurrentItem();
                    mVpEdit.setCurrentItem(index, false);
                }
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnackbarUtils.with(mVpEdit)
                        .setMessage("Save")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void setEditorView(MarkDownEditorView editorView) {
        this.mEditorView = editorView;
        if (editorView != null && mPreviewView != null && mToolsAdapter != null)
            initMarkDownController();
    }

    public void setPreviewView(MarkDownPreviewView previewView) {
        this.mPreviewView = previewView;
        if (mEditorView != null && previewView != null && mToolsAdapter != null)
            initMarkDownController();
    }

    private void initMarkDownController() {
        markDownController = new MarkDownController(mEditorView, mPreviewView, mToolsAdapter, false);
        markDownController.setOnPreInsertListener(this);
    }

    @Override
    public void onPreInsertImage() {

    }

    @Override
    public void onPreInsertLink() {

    }

    @Override
    public void onPreInsertTable() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
