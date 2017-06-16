package image.cui.kejia.jianhangdemo;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"建行圆形菜单", "圆形菜单", "华为天气"}));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = null;
        if (position == 0) {
            intent = new Intent(MainActivity.this, CCBActivity.class);
        } else if (position == 1) {
            intent = new Intent(MainActivity.this, CircleActivity.class);
        } else if (position == 2) {
            intent = new Intent(MainActivity.this, HuaWeiWeather.class);
        }
        startActivity(intent);
    }
}
