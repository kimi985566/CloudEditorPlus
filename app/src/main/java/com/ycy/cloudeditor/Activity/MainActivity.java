package com.ycy.cloudeditor.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.ycy.cloudeditor.Adapter.MainRecycleViewAdapter;
import com.ycy.cloudeditor.Bean.NoteInfo;
import com.ycy.cloudeditor.Listener.ItemTouchHelperListener;
import com.ycy.cloudeditor.Listener.MyItemClickListener;
import com.ycy.cloudeditor.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.recycleView)
    RecyclerView mMRecycleView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    Unbinder mUnbinder;

    private ActionBarDrawerToggle mToggle;
    private static final int RC_STORAGE = 101;
    private boolean mIsExit;

    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};
    public static String Path = Environment.getExternalStorageDirectory()
            + File.separator + "CloudEditor" + File.separator;

    private ArrayList<NoteInfo> mNoteInfoArrayList = new ArrayList<>();
    private MainRecycleViewAdapter mRecycleViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private MyItemClickListener mMyItemClickListener;
    private int mCount;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(this);
        mUnbinder = ButterKnife.bind(this);
        ask_perms();
        initUI();
    }

    private void ask_perms() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtils.i("Permissions are granted");
        } else {
            LogUtils.i("These permissions are denied , " + "ready to request this permission");
            EasyPermissions.requestPermissions(this, "云存储需要网络、存储权限",
                    RC_STORAGE, perms);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initUI() {
        initActionBar();

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        mToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);

        initSwipeRefreshLayout();

        initData();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mMRecycleView.setLayoutManager(mLinearLayoutManager);

        mRecycleViewAdapter = new MainRecycleViewAdapter(this, mNoteInfoArrayList);
        mMRecycleView.setAdapter(mRecycleViewAdapter);

        mRecycleViewAdapter.setItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NoteInfo noteInfo = mNoteInfoArrayList.get(position);
                Intent intent = new Intent(view.getContext(), EditActivity.class);
                intent.putExtra(EditActivity.TITLE, noteInfo.getTitle());
                intent.putExtra(EditActivity.CONTENT, noteInfo.getContent());
                startActivity(intent);
                LogUtils.i("Click: " + position + intent);
            }
        });
        mMRecycleView.setItemAnimator(new DefaultItemAnimator());
        mMRecycleView.setHasFixedSize(true);

        mMRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LogUtils.i("-----------onScrollStateChanged-----------");
                LogUtils.i("newState: " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtils.i("-----------onScrolled-----------");
                LogUtils.i("dx: " + dx);
                LogUtils.i("dy: " + dy);
                LogUtils.i("CHECK_SCROLL_UP: " + recyclerView.canScrollVertically(1));
                LogUtils.i("CHECK_SCROLL_DOWN: " + recyclerView.canScrollVertically(-1));
            }
        });

        ItemTouchHelper.Callback callback = new myItemTouchHelperCallBack(mRecycleViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mMRecycleView);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.recycler_color1, R.color.recycler_color2,
                R.color.recycler_color3, R.color.recycler_color4);
        //设置一进入开始刷新
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void initActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchFiles(Path);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i(this.getClass().getSimpleName() + ": onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();
            } else {
                SnackbarUtils.with(mFab)
                        .setMessage("再按一次退出程序")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showWarning();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        LogUtils.i(this.getClass().getSimpleName() + ": onDestory");
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchFiles(Path);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }

    private List<NoteInfo> searchFiles(String path) {

        mCount = 0;

        File[] files = new File(path).listFiles();

        if (files == null) {
            return null;
        }


        for (int i = 0; i < files.length; i++) {
            // 判断是否为文件夹
            if (files[i].isDirectory()) {
                LogUtils.i(files[i].getAbsolutePath());
                searchFiles(files[i].getAbsolutePath());
            } else if (!files[i].isHidden() && files[i].getName().endsWith(".txt")) {
                InputStream instream = null;
                try {
                    instream = new FileInputStream(files[i]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String fileName = FileUtils.getFileNameNoExtension(files[i]);
                String fileContent = getFileContent2String(instream);
                String fileTime = TimeUtils.millis2String(FileUtils.getFileLastModified(files[i]));

                NoteInfo noteInfo = new NoteInfo();
                noteInfo.setTitle(fileName);
                noteInfo.setTime(fileTime);
                noteInfo.setContent(fileContent);

                if (mNoteInfoArrayList.contains(noteInfo) == true) {
                    break;
                } else {
                    mNoteInfoArrayList.add(noteInfo);
                }
            }
        }

        mRecycleViewAdapter.notifyDataSetChanged();

        return mNoteInfoArrayList;
    }

    public static String getFileContent2String(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;

        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @OnClick({R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
        }
    }

    class myItemTouchHelperCallBack extends ItemTouchHelper.Callback {

        private ItemTouchHelperListener mItemTouchHelperListener;
        private int mPosition;
        private NoteInfo mNoteInfoTemp;

        public myItemTouchHelperCallBack(ItemTouchHelperListener itemTouchHelperListener) {
            this.mItemTouchHelperListener = itemTouchHelperListener;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //允许上下拖动
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            //允许从右向左滑动
            int swipeFlags = ItemTouchHelper.LEFT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        public void setItemTouchHelperListener(ItemTouchHelperListener itemTouchHelperListener) {
            mItemTouchHelperListener = itemTouchHelperListener;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //onItemMove接口里的方法
            mItemTouchHelperListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //onItemDelete接口里的方法
            mPosition = viewHolder.getAdapterPosition();
            mNoteInfoTemp = mNoteInfoArrayList.get(mPosition);
            mItemTouchHelperListener.onItemDelete(mPosition);

            SnackbarUtils.with(mFab)
                    .setMessage("删除了一条数据")
                    .setDuration(SnackbarUtils.LENGTH_LONG)
                    .setAction("撤销", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemTouchHelperListener.onItemRecover(mPosition, mNoteInfoTemp);
                        }
                    })
                    .show();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //滑动时改变Item的透明度，以实现滑动过程中实现渐变效果
                final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

        }

        @Override
        public boolean isLongPressDragEnabled() {
            //该方法返回值为true时，表示支持长按ItemView拖动
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            //该方法返回true时，表示如果用户触摸并且左滑了view，那么可以执行滑动删除操作，就是可以调用onSwiped()方法
            return true;
        }
    }
}
