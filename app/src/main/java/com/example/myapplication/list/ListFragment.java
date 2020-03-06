package com.example.myapplication.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class ListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView.LayoutManager recycleManager;
    private final static String KEY = "Key";

    private ArrayList<RssFeedModel> mFeedModelList = new ArrayList<RssFeedModel>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        recycleManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(recycleManager);
        mRecyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
        mSwipeLayout = root.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(() -> new FetchFeedTask().execute((Void) null));
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY, mFeedModelList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(KEY)){
            ArrayList<RssFeedModel> tempState = savedInstanceState.getParcelableArrayList(KEY);
            if (tempState != null){
                mFeedModelList.addAll(tempState);
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (mFeedModelList.size() == 0){
            new FetchFeedTask().execute((Void) null);
        }
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url ="http://pages.erau.edu/~apelianr/event_array.php";

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(url))
                return false;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try{
                            JSONArray jArray = new JSONArray(response);
                            for (int i = 0; i < jArray.length(); i++){
                                JSONObject jOb = jArray.getJSONObject(i);
                                RssFeedModel temp = new RssFeedModel(
                                        jOb.getString("title"),
                                        jOb.getString("description"),
                                        jOb.getString("timestamp"),
                                        jOb.getString("organization"),
                                        jOb.getString("location"),
                                        jOb.getString("link")
                                );
                                mFeedModelList.add(temp);
                            }
                            RssFeedListAdapter feedListAdapter = new RssFeedListAdapter(mFeedModelList);
                            mRecyclerView.setAdapter(feedListAdapter);
                        } catch (Exception e){
                            Log.d("Error", e.toString());
                            Snackbar snackBar = Snackbar.make(mRecyclerView, "Sorry, we're having some trouble connecting to the server. Please pull down to refresh, or check your internet connection.", Snackbar.LENGTH_LONG);
                            snackBar.show();
                        }
                    }, error -> Log.e("Error", error.toString()));
            queue.add(stringRequest);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                RssFeedListAdapter feedListAdapter = new RssFeedListAdapter(mFeedModelList);
                mRecyclerView.setAdapter(feedListAdapter);
            } else {
                Toast.makeText(getActivity(),
                        "Enter a valid Rss feed url", Toast.LENGTH_LONG).show();
            }
        }
    }
}