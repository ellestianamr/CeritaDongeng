package com.azhar.dongeng.activities;

import static com.azhar.dongeng.db.DatabaseContract.FavoriteColumns.TABLE_NAME;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.azhar.dongeng.R;
import com.azhar.dongeng.db.DatabaseContract;
import com.azhar.dongeng.db.DatabaseHelper;
import com.azhar.dongeng.db.FavoriteHelper;
import com.azhar.dongeng.model.ModelMain;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.sql.SQLException;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_DONGENG = "DETAIL_DONGENG";
    String strJudul, strCerita;
    ModelMain modelMain;
    Toolbar toolbar;
    TextView tvJudul, tvCerita;

    MaterialFavoriteButton btnFavorite;

    ArrayList<ModelMain> listFavorite = new ArrayList<>();
    private FavoriteHelper favoriteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //set transparent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        toolbar = findViewById(R.id.toolbar);
        tvJudul = findViewById(R.id.tvJudul);
        tvCerita = findViewById(R.id.tvCerita);
        btnFavorite = findViewById(R.id.btnFavorite);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get data intent
        modelMain = (ModelMain) getIntent().getSerializableExtra(DETAIL_DONGENG);
        if (modelMain != null) {
            strJudul = modelMain.getStrJudul();
            strCerita = modelMain.getStrCerita();

            tvJudul.setText(strJudul);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvCerita.setText(Html.fromHtml(strCerita, Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvCerita.setText(Html.fromHtml(strCerita));
            }
        }

        favoriteHelper = FavoriteHelper.Companion.getInstance(DetailActivity.this);
        try {
            favoriteHelper.open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        dataFavorite();
    }

    private void dataFavorite() {
        if (favoriteExist(strJudul)) {
            btnFavorite.setFavorite(true);
            btnFavorite.setOnFavoriteChangeListener((buttonView, favorite) -> {
                if (favorite) {
                    listFavorite = favoriteHelper.queryAll();
                    favoriteHelper.insert(modelMain);
                    Toast.makeText(getApplicationContext(), "Added Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    listFavorite = favoriteHelper.queryAll();
                    favoriteHelper.delete(strJudul);
                    Toast.makeText(getApplicationContext(), "Deleted Favorite", Toast.LENGTH_SHORT).show();
                }
                favoriteHelper.close();
            });
        } else {
            btnFavorite.setOnFavoriteChangeListener((buttonView, favorite) -> {
                if (favorite) {
                    listFavorite = favoriteHelper.queryAll();
                    favoriteHelper.insert(modelMain);
                    Toast.makeText(getApplicationContext(), "Added Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    listFavorite = favoriteHelper.queryAll();
                    favoriteHelper.delete(strJudul);
                    Toast.makeText(getApplicationContext(), "Deleted Favorite", Toast.LENGTH_SHORT).show();
                }
                favoriteHelper.close();
            });
        }
    }

    private boolean favoriteExist(String title) {
        String choose = DatabaseContract.FavoriteColumns.TITLE + " = ?";
        String[] chooseArg = new String[]{title};
        String limit = "1";

        favoriteHelper = new FavoriteHelper(this);
        try {
            favoriteHelper.open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        DatabaseHelper dataBaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, choose, chooseArg, null, null, null, limit);
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        database.close();
        return exists;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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