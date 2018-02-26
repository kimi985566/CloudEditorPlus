package com.ycy.cloudeditor.Activity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by kimi9 on 2018/2/15.
 */

public class EditActivity extends AppCompatActivity implements OnPreInsertListener,
        EasyPermissions.PermissionCallbacks {

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
    private MarkDownController mMarkDownController;
    private ToolsAdapter mToolsAdapter;
    private EditFragment mEditFragment;
    private PreviewFragment mPreviewFragment;
    private InputMethodManager mInputMethodManager;

    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    public static final int SELECT_PIC_RESULT_CODE = 202;

    Unbinder mUnbinder;

    public static String Path = Environment.getExternalStorageDirectory()
            + File.separator + "CloudEditor" + File.separator;
    private FileOutputStream mFileOutputStream = null;
    private File mFile;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Utils.init(this);
        mUnbinder = ButterKnife.bind(this);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                initUI();
            }
        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initUI() {

        getWindow().setStatusBarColor(getResources().getColor(R.color.royalblue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.royalblue));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewGroup appBarLayout = findViewById(R.id.app_bar_edit);
            appBarLayout.getLayoutTransition().setDuration(LayoutTransition.CHANGE_DISAPPEARING, 0);

            ViewGroup viewGroup = findViewById(R.id.root_view_app_bar_edit);
            LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }

        //init fragments
        initFragments();

        //init toolbar
        initToolbar();

        //init viewPager
        initViewPager();

        //setAdapter
        setAdapterMethod();

    }

    private void initFragments() {
        String title = getIntent().getStringExtra(TITLE) == null ? "" : getIntent().getStringExtra(TITLE);
        String content = getIntent().getStringExtra(CONTENT) == null ? "" : getIntent().getStringExtra(CONTENT);
        mEditFragment = EditFragment.newInstance(title, content);
        mPreviewFragment = PreviewFragment.newInstance();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbarEdit);
        mToolsAdapter = new ToolsAdapter(getLayoutInflater());
        mToolsEdit.setAdapter(mToolsAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void setAdapterMethod() {
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
    }

    private void initViewPager() {
        mVpEdit.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mMarkDownController == null)
                    return;
                if (position == 1) {
                    mToolsEdit.setVisibility(View.GONE);
                    mPreviewFragment.setTitle(mEditFragment.getTitle());
                    mMarkDownController.preview();
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
        mMarkDownController = new MarkDownController(mEditorView, mPreviewView, mToolsAdapter, false);
        mMarkDownController.setOnPreInsertListener(this);
    }

    private boolean saveFile() {

        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "需要SD卡读写权限，请重试", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }

        if (mEditFragment.getTitle().isEmpty()) {
            SnackbarUtils.with(mVpEdit)
                    .setMessage("标题不能为空")
                    .setDuration(SnackbarUtils.LENGTH_SHORT)
                    .showWarning();
            return false;
        }

        return saveInFile();
    }

    private boolean saveInFile() {
        try {
            File dir = new File(Path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            mFile = new File(Path + mEditFragment.getTitle() + ".txt");
            if (!mFile.exists()) {
                mFile.createNewFile();
                mFileOutputStream = new FileOutputStream(mFile);
                mFileOutputStream.write(mEditorView.getText().toString().getBytes());
                mFileOutputStream.flush();
                SnackbarUtils.with(mVpEdit)
                        .setMessage("保存成功")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .setBgColor(Color.GREEN)
                        .show();
                LogUtils.i("Saved");
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("注意！")
                        .setMessage("您已存在一个同名文件，是否覆盖保存？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    mFileOutputStream = new FileOutputStream(mFile);
                                    mFileOutputStream.write(mEditorView.getText().toString().getBytes());
                                    mFileOutputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }
            return true;
        } catch (Exception e) {
            SnackbarUtils.with(mVpEdit)
                    .setMessage("保存失败")
                    .setDuration(SnackbarUtils.LENGTH_SHORT)
                    .showError();
            LogUtils.i("Error in saving");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (mFileOutputStream != null)
                    mFileOutputStream.close();
                LogUtils.i("FileOutputStream closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreInsertImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, SELECT_PIC_RESULT_CODE);
        LogUtils.i("select image");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PIC_RESULT_CODE && resultCode == RESULT_OK)
            if (mMarkDownController != null)
                mMarkDownController.insertImage(data.getData().toString());
    }

    @Override
    public void onPreInsertLink() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_insert_link, null);
        final TextInputEditText name = view.findViewById(R.id.linkName);
        final TextInputEditText url = view.findViewById(R.id.linkUrl);
        new AlertDialog.Builder(this)
                .setTitle("插入链接")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mMarkDownController != null)
                            mMarkDownController.insertLink(new Pair<String, String>
                                    (name.getText() + "", url.getText() + ""));
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onPreInsertTable() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_insert_table, null);
        final TextInputEditText row = view.findViewById(R.id.row);
        final TextInputEditText column = view.findViewById(R.id.column);
        new AlertDialog.Builder(this)
                .setTitle("插入表格")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mMarkDownController != null)
                            mMarkDownController.insertTable(new Pair<Integer, Integer>(
                                    Integer.parseInt(row.getText() + ""),
                                    Integer.parseInt(column.getText() + "")));
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtils.i("EasyPermission CallBack onPermissionsGranted() : " + perms.get(0) +
                " request granted , to do something...");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtils.i("EasyPermission CallBack onPermissionsDenied():" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @OnClick({R.id.save, R.id.fab_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveFile();
                break;
            case R.id.fab_edit:
                if (mVpEdit != null) {
                    int index = 1 - mVpEdit.getCurrentItem();
                    mVpEdit.setCurrentItem(index, false);
                }
                break;
        }
    }
}
