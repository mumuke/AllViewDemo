package image.cui.kejia.jianhangdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import image.cui.kejia.jianhangdemo.view.CircleMenuLayout;

/**
 * Created by ckj on 2017/6/12.
 * 建行demo
 */

public class CircleActivity extends Activity {
    private CircleMenuLayout idMenulayout;
    private String[] mItemTexts = {"安全中心", "特色服务", "投资理财", "转账汇款", "我的账户", "信用卡"};
    private int[] mItemImgs = {R.drawable.home_mbank_1, R.drawable.home_mbank_2, R.drawable.home_mbank_3, R.drawable.home_mbank_4, R.drawable.home_mbank_5, R.drawable.home_mbank_6};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianhang);
        idMenulayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
        idMenulayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
        idMenulayout.setOnMenuItemClickListener(new CircleMenuLayout.onMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                Toast.makeText(CircleActivity.this, mItemTexts[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemCenterClick(View view) {
                Toast.makeText(CircleActivity.this, "you can do something just like ccb ", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
