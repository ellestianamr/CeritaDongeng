package com.azhar.dongeng.activities;

import static com.azhar.dongeng.utils.Constant.KEY_NAME;
import static com.azhar.dongeng.utils.Constant.PREFS_NAME;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.azhar.dongeng.R;
import com.azhar.dongeng.model.ModelMain;
import com.azhar.dongeng.utils.Constant;
import com.azhar.dongeng.utils.FavoriteCallback;
import com.azhar.dongeng.utils.FirebaseHelper;
import com.azhar.dongeng.utils.SharedPreference;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_DONGENG = "DETAIL_DONGENG";
    String idUser, idStory, strJudul, strCerita;
    ModelMain modelMain;
    Toolbar toolbar;
    TextView tvJudul, tvCerita;

    MaterialFavoriteButton btnFavorite;
    ImageView btnEdit, btnDelete;

    private FirebaseHelper db;
    SharedPreferences sharedPref;
    Boolean isLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        db = new FirebaseHelper();
        sharedPref = SharedPreference.INSTANCE.initPref(getApplicationContext(), Constant.PREFS_NAME);

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
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences sharedPref = SharedPreference.INSTANCE.initPref(getApplicationContext(), PREFS_NAME);
        String name = sharedPref.getString(KEY_NAME, "");
        System.out.println(name);
        if (name.equalsIgnoreCase("admin")) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        //get data intent
        modelMain = (ModelMain) getIntent().getSerializableExtra(DETAIL_DONGENG);
        if (modelMain != null) {
            idStory = modelMain.getId();
            strJudul = modelMain.getStrJudul();
            strCerita = modelMain.getStrCerita();

            tvJudul.setText(strJudul);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvCerita.setText(Html.fromHtml(strCerita, Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvCerita.setText(Html.fromHtml(strCerita));
            }
        }
        idUser = sharedPref.getString(Constant.KEY_ID, "");

        dataFavorite();
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, AddActivity.class);
                intent.putExtra(DetailActivity.DETAIL_DONGENG, modelMain);
                startActivity(intent);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
    }

    private void dataFavorite() {
        db.favoriteExist(idStory, idUser, isFavorite -> {
            if (isFavorite) {
                System.out.println("Story sudah difavoritkan oleh user ini.");
                btnFavorite.setFavorite(true);
                btnFavorite.setOnFavoriteChangeListener((buttonView, favorite) -> {
                    if (favorite) {
                        db.addFavorite(idStory, strJudul, strCerita, idUser, new FavoriteCallback() {
                            @Override
                            public void onComplete(boolean isFavorite) {
                                if (isFavorite) {
                                    Toast.makeText(getApplicationContext(), "Added Favorite", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed Added Favorite", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        db.removeFavorite(idStory, idUser, new FavoriteCallback() {
                            @Override
                            public void onComplete(boolean isFavorite) {
                                if (isFavorite) {
                                    Toast.makeText(getApplicationContext(), "Deleted Favorite", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed Deleted Favorite", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                System.out.println("Story belum difavoritkan oleh user ini.");
                btnFavorite.setOnFavoriteChangeListener((buttonView, favorite) -> {
                    if (favorite) {
                        db.addFavorite(idStory, strJudul, strCerita, idUser, new FavoriteCallback() {
                            @Override
                            public void onComplete(boolean isFavorite) {
                                if (isFavorite) {
                                    Toast.makeText(getApplicationContext(), "Added Favorite", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed Added Favorite", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        db.removeFavorite(idStory, idUser, new FavoriteCallback() {
                            @Override
                            public void onComplete(boolean isFavorite) {
                                if (isFavorite) {
                                    Toast.makeText(getApplicationContext(), "Deleted Favorite", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed Deleted Favorite", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void deleteData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus data ini?");

        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteData(idStory, new FavoriteCallback() {
                    @Override
                    public void onComplete(boolean isDelete) {
                        if (isDelete) {
                            Toast.makeText(getApplicationContext(), "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            startActivity(new Intent(DetailActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Data gagal dihapus!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
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