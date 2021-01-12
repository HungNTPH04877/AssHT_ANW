package com.example.assht_anw;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.example.assht_anw.adapter.WallpaperAdapter;
import com.example.assht_anw.model.Photo;
import com.example.assht_anw.model.Wallpaper;
import com.example.assht_anw.retrofit.Retrofit;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcView;
    private ProgressBar progressBar;
    private List<Photo> list;
    private WallpaperAdapter adapter;
    private SwipeRefreshLayout swipeLayout;

    private int page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeLayout = findViewById(R.id.swipeRefreshLayout);
        rcView = findViewById(R.id.rcView);
        progressBar = findViewById(R.id.progressBar);
        list = new ArrayList<>();
        adapter = new WallpaperAdapter(list, MainActivity.this, new WallpaperAdapter.AdapterListener() {
            @Override
            public void OnClick(int position) {
                String url = list.get(position).getUrlL();
                Intent intent = new Intent(MainActivity.this,DetailPhotoActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        // rcView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        rcView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rcView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        getData(page);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.this.page = 1;
                list.clear();
                getData(MainActivity.this.page);
            }
        });

    }

    private void getData(int page) {
        Retrofit.getServices().getPhoto("flickr.favorites.getList","20d1f54eba558ce383c6b8109984412e",
                "147096611@N02"
                ,"views,media,path_alias,url_sq,url_t,url_s,url_q,url_m,url_n,url_z,url_c,url_l,url_o",
                1,100,"json","1").enqueue(new Callback<Wallpaper>() {
            @Override
            public void onResponse(Call<Wallpaper> call, Response<Wallpaper> response) {
                swipeLayout.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                list.addAll(response.body().getPhotos().getPhoto());
                Log.d("DATAAA",response.body().getPhotos().getPhoto().toString());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<Wallpaper> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
