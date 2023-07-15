package com.main.dhbworld.MenuReorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.dhbworld.R;

import java.util.ArrayList;

public class ReorderMenuActivity extends AppCompatActivity {

    ArrayList<MenuItem> menuItems = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reorder_menu);

        setupToolbar();
        setupSharedPrefs();

        setupRecyclerView();
    }

    private void setupSharedPrefs() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> handleBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.save_reorder) {
                saveMenu();
                finish();
            }
            return true;
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.reoder_recycler_view);
        menuItems = loadMenu();

        ReorderMenuAdapter reorderMenuAdapter = new ReorderMenuAdapter(this, menuItems);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(reorderMenuAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reorderMenuAdapter);
    }

    private void handleBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.save_changes)
                .setMessage(R.string.save_changes_message)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    saveMenu();
                    finish();
                })
                .setNegativeButton(R.string.discard, (dialog, which) -> finish())
                .show();
    }

    private void saveMenu() {
        Gson gson = new Gson();
        editor.putString("drawer_menu", gson.toJson(menuItems));
        editor.apply();
    }

    private ArrayList<MenuItem> loadMenu() {
        String menuOrderJson = sharedPreferences.getString("drawer_menu", "");
        if (menuOrderJson.isEmpty()) {
            return loadDrawerMenu();
        }

        Gson gson = new Gson();
        return gson.fromJson(menuOrderJson, new TypeToken<ArrayList<MenuItem>>() {}.getType());
    }

    private ArrayList<MenuItem> loadDrawerMenu() {
        ArrayList<MenuItem> menuItems1 = new ArrayList<>();
        PopupMenu p  = new PopupMenu(this, null);
        Menu menu = p.getMenu();
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);

        for (int i=0; i<menu.size(); i++) {
            android.view.MenuItem menuItem = menu.getItem(i);
            if (menuItem.getTitle() != null && !menuItem.getTitle().toString().equals(getResources().getString(R.string.settings))) {
                String[] id = getResources().getResourceName(menuItem.getItemId()).split("/");
                menuItems1.add(new MenuItem(menuItem.getTitle().toString(), id[1]));
            }
        }
        return menuItems1;
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }
}