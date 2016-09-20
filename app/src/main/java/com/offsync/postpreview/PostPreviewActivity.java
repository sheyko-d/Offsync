package com.offsync.postpreview;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.offsync.R;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PostPreviewActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "type";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_LINK = "link";
    public static final String EXTRA_PREVIEW = "preview";
    public static final String EXTRA_PREVIEW_HEIGHT = "height";
    public static final String EXTRA_PREVIEW_WIDTH = "width";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview);
        ButterKnife.bind(this);

        initStatusBar();
        initContent();
    }

    private void initContent() {
        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        int type = getIntent().getIntExtra(EXTRA_TYPE, -1);
        String link = getIntent().getStringExtra(EXTRA_LINK);
        String preview = getIntent().getStringExtra(EXTRA_PREVIEW);
        int previewHeight = getIntent().getIntExtra(EXTRA_PREVIEW_HEIGHT, -1);
        int previewWidth = getIntent().getIntExtra(EXTRA_PREVIEW_WIDTH, -1);

        getSupportFragmentManager().beginTransaction().add(R.id.preview_layout, PostPreviewFragment
                .newInstance(false, postId, type, link, preview, previewHeight, previewWidth))
                .commit();
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
