package com.azhar.dongeng.activities;

import static com.azhar.dongeng.utils.Constant.PREFS_NAME;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azhar.dongeng.R;
import com.azhar.dongeng.adapter.MainAdapter;
import com.azhar.dongeng.model.ModelMain;
import com.azhar.dongeng.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ModelMain> modelMain = new ArrayList<>();
    MainAdapter mainAdapter;
    RecyclerView rvListDongeng;
    SearchView searchTanaman;
    ImageView btnFavorite, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set transparent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        rvListDongeng = findViewById(R.id.rvListDongeng);
        searchTanaman = findViewById(R.id.searchTanaman);
        btnFavorite = findViewById(R.id.iv_favorite);
        btnLogout = findViewById(R.id.iv_logout);

        //transparent background searchview
        int searchPlateId = searchTanaman.getContext()
                .getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchTanaman.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(Color.TRANSPARENT);
        }

        searchTanaman.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchTanaman.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainAdapter.getFilter().filter(newText);
                return true;
            }
        });

        rvListDongeng.setLayoutManager(new LinearLayoutManager(this));
        rvListDongeng.setHasFixedSize(true);

        //get data json
        getDataDongeng();

        btnFavorite.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, FavoriteActivity.class)));

        btnLogout.setOnClickListener(view -> showLogoutDialog());

        SharedPreference.INSTANCE.initPref(MainActivity.this, PREFS_NAME);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Ya", (dialogInterface, i) -> {
            SharedPreference.INSTANCE.logout(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.show();
    }

    private void getDataDongeng() {
        try {
            InputStream stream = getAssets().open("list_dongeng.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String strContent = new String(buffer, StandardCharsets.UTF_8);
            try {
                JSONObject jsonObject = new JSONObject(strContent);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ModelMain dataApi = new ModelMain();
                    dataApi.setStrCerita(object.getString("file"));
                    dataApi.setStrJudul(object.getString("title"));
                    modelMain.add(dataApi);
                }
                mainAdapter = new MainAdapter(this, modelMain);
                rvListDongeng.setAdapter(mainAdapter);
                Collections.sort(modelMain, ModelMain.sortByAsc);
                mainAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ignored) {
            Toast.makeText(MainActivity.this, "Ups, ada yang tidak beres. " +
                    "Coba ulangi beberapa saat lagi.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

}