package com.example.balav.MovieGridView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.balav.MovieGridView.model.Movie;
import com.example.balav.MovieGridView.utils.JsonUtils;
import com.example.balav.MovieGridView.utils.NetworkUtils;
import com.example.balav.MovieGridView.R;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Movie movie;
    private List<String> listPosterUrls;
    private List<Integer> listIDs;
    private static final String MOVIE_SORT_POPULAR="popular";
    private static final String MOVIE_SORT_TOP_RATED="top_rated";


    @Override


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMovies(MOVIE_SORT_POPULAR);
    }
    private void loadGridView(){

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns (2);
        gridview.setStretchMode (GridView.STRETCH_COLUMN_WIDTH);
        gridview.setHorizontalSpacing (0);
        gridview.setAdapter(new ImageAdapter(this,listPosterUrls));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener () {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(MainActivity.this, "" + position,
                  //      Toast.LENGTH_SHORT).show();
                launchDetailActivity(listIDs.get (position));
            }
        });
    }

    private void launchDetailActivity(int id) {
        Log.i("detail","in Detail ----->");
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE_ID, id);
        startActivity(intent);
    }

    private void loadMovies(String movie_type){
        new FetchMovieTask ().execute(movie_type);
    }

    public class FetchMovieTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params){
            String movie_type = params[0];
            URL movieUrl = NetworkUtils.buildUrl(movie_type);
            String movieResults=null;
            try{
                movieResults= NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieResults;
        }
        @Override
        protected void onPostExecute(String movieResults){
           Log.v("onPostExecute-->",movieResults.toString ());
           Context context = MainActivity.this;
            listPosterUrls= JsonUtils.getPosterImages (movieResults);
            listIDs = JsonUtils.getMoviesIDs (movieResults);
           Log.v ("PosterUrlsSize--->", ""+listPosterUrls.size ());
           loadGridView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_preferences,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_popular:
                loadMovies (MOVIE_SORT_POPULAR);
                break;
            case R.id.action_top_rated:
                loadMovies (MOVIE_SORT_TOP_RATED);
                break;
            default:
                loadMovies (MOVIE_SORT_POPULAR);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
