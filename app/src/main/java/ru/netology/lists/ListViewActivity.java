package ru.netology.lists;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {
    private static final String TEXT = "long_text";
    private static final String KEY_TEXT = "key_text";
    private static final String KEY_COUNT = "key_count";

    private SharedPreferences mySharedPref = null;
    private List<Map<String, String>> list;
    private BaseAdapter listContentAdapter;
    private SwipeRefreshLayout swipe;
    private ListView listView;
    private ArrayList<Integer> removedItems = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (null == mySharedPref) {
            mySharedPref = getSharedPreferences("Text", MODE_PRIVATE);
            SharedPreferences.Editor myEditor = mySharedPref.edit();
            myEditor.putString(TEXT, getString(R.string.large_text));
            myEditor.apply();
        }
        init();

        if (savedInstanceState != null) {
            removedItems = savedInstanceState.getIntegerArrayList("removedItems");
            for (int i = 0; i < removedItems.size(); i++) {
                list.remove(removedItems.get(i).intValue());
            }
            listContentAdapter.notifyDataSetChanged();
        }


    }

    private void init() {
        listView = findViewById(R.id.list);

        listContentAdapter = createAdapter(prepareContent());
        listView.setAdapter(listContentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                removedItems.add(i);
                list.remove(i);
                listContentAdapter.notifyDataSetChanged();
            }
        });
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listContentAdapter = createAdapter(prepareContent());
                listView.setAdapter(listContentAdapter);
                swipe.setRefreshing(false);
                System.out.println("!");

            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putIntegerArrayList("removedItems", removedItems);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        return new SimpleAdapter(this, values, R.layout.item_layout, new String[]{KEY_TEXT, KEY_COUNT}, new int[]{R.id.textView, R.id.countView});
    }


    @NonNull
    private List<Map<String, String>> prepareContent() {
        String[] strarr = mySharedPref.getString(TEXT, "").split("\n\n");
        list = new ArrayList<Map<String, String>>();
        for (String text : strarr) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(KEY_TEXT, text);
            map.put(KEY_COUNT, "" + text.length());
            list.add(map);
        }
        return list;
    }
}
