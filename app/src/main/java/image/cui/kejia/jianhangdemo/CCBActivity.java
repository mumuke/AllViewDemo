package image.cui.kejia.jianhangdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import image.cui.kejia.jianhangdemo.view.CircleMenuLayout;
import image.cui.kejia.jianhangdemo.view.TextViewShader;


/**
 * Created by ckj on 2017/6/12.
 */

public class CCBActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleMenuLayout idMenulayout;
    private TextViewShader tv_shader;
    private String[] mItemTexts = {"安全中心", "特色服务", "投资理财", "转账汇款", "我的账户", "信用卡"};
    private int[] mItemImgs = {R.drawable.home_mbank_1, R.drawable.home_mbank_2, R.drawable.home_mbank_3, R.drawable.home_mbank_4, R.drawable.home_mbank_5, R.drawable.home_mbank_6};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianhang_all);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_shader = (TextViewShader) findViewById(R.id.tv_shader);
        tv_shader.setText("好好学习，天天向上");

        idMenulayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
        idMenulayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
        idMenulayout.setOnMenuItemClickListener(new CircleMenuLayout.onMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                Toast.makeText(CCBActivity.this, mItemTexts[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemCenterClick(View view) {
                Toast.makeText(CCBActivity.this, "you can do something just like ccb ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://blog.csdn.net/lmj623565791?viewmode=contents"));
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}
