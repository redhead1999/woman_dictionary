package com.redhead.y14.womandictionary;

import android.database.Cursor;
import android.os.Bundle;
import android.support.p003v7.app.AppCompatActivity;
import android.support.p003v7.widget.DefaultItemAnimator;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.Adapter;
import android.support.p003v7.widget.RecyclerView.LayoutManager;
import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.SearchView.OnQueryTextListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class DictionaryMainActivity extends AppCompatActivity {
    /* access modifiers changed from: private */
    public static Adapter adapter;
    public static ArrayList<DictObjectModel> data;
    /* access modifiers changed from: private */
    public static RecyclerView recyclerView;

    /* renamed from: db */
    DatabaseHelper f33db;
    private LayoutManager layoutManager;
    ArrayList<String> meancombimelist;
    LinkedHashMap<String, String> namelist;
    SearchView searchView;
    ArrayList<String> wordcombimelist;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) C0351R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(C0351R.C0353id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        this.f33db = new DatabaseHelper(this);
        this.searchView = (SearchView) findViewById(C0351R.C0353id.searchView);
        this.searchView.setQueryHint("Что она сказала?");
        this.searchView.setQueryRefinementEnabled(true);
        this.layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(this.layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<>();
        fetchData();
        this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                String lowerCase = str.toLowerCase();
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < DictionaryMainActivity.this.wordcombimelist.size(); i++) {
                    if (((String) DictionaryMainActivity.this.wordcombimelist.get(i)).toLowerCase().contains(lowerCase)) {
                        arrayList.add(new DictObjectModel((String) DictionaryMainActivity.this.wordcombimelist.get(i), (String) DictionaryMainActivity.this.meancombimelist.get(i)));
                    }
                }
                DictionaryMainActivity.adapter = new CustomAdapter(arrayList);
                DictionaryMainActivity.recyclerView.setAdapter(DictionaryMainActivity.adapter);
                return true;
            }
        });
    }

    public void fetchData() {
        this.f33db = new DatabaseHelper(this);
        try {
            this.f33db.createDataBase();
            this.f33db.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.namelist = new LinkedHashMap<>();
        Cursor query = this.f33db.getReadableDatabase().query("WomanDB", null, null, null, null, null, null);
        int columnIndex = query.getColumnIndex("word");
        this.wordcombimelist = new ArrayList<>();
        this.meancombimelist = new ArrayList<>();
        while (query.moveToNext()) {
            this.namelist.put(query.getString(columnIndex), query.getString(query.getColumnIndex("definition")));
        }
        for (Entry entry : this.namelist.entrySet()) {
            this.wordcombimelist.add(String.valueOf(entry.getKey()));
            ArrayList<String> arrayList = this.meancombimelist;
            StringBuilder sb = new StringBuilder();
            sb.append("- ");
            sb.append(String.valueOf(entry.getValue()));
            arrayList.add(sb.toString());
        }
        for (int i = 0; i < this.wordcombimelist.size(); i++) {
            data.add(new DictObjectModel((String) this.wordcombimelist.get(i), (String) this.meancombimelist.get(i)));
        }
        adapter = new CustomAdapter(data);
        recyclerView.setAdapter(adapter);
    }
}
