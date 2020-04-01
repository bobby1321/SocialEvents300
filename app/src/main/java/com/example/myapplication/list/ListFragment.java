package com.example.myapplication.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView.LayoutManager recycleManager;
    private FloatingActionButton fab;
    private ArrayList<String> ORGS = new ArrayList<String>();
    private ArrayList<String> LOCS = new ArrayList<String>();
    private RssFeedListAdapter rssFeedListAdapter;
    private boolean filtered = false;

    private ArrayList<RssFeedModel> mFeedModelList = new ArrayList<RssFeedModel>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        rssFeedListAdapter = new RssFeedListAdapter(mFeedModelList);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        recycleManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(recycleManager);
        mRecyclerView.setAdapter(rssFeedListAdapter);
        mSwipeLayout = root.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (filtered){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Refresh?");
                    builder.setMessage("By refreshing the feed, your filter will be removed.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            new FetchFeedTask().execute((Void) null);
                            filtered = false;
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mSwipeLayout.setRefreshing(false);
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                } else {
                    new FetchFeedTask().execute((Void) null);
                }
            }

        });
        fab = root.findViewById(R.id.filterActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openFilterAction(root);
            };
        });
        return root;
    }

    private void openFilterAction(View root) {
        FilterFragment filterFragment = new FilterFragment(mFeedModelList, ORGS, LOCS, rssFeedListAdapter);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        filterFragment.show(fm, "filterFragment");
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
            ORGS.clear();
            mFeedModelList.clear();
            if (TextUtils.isEmpty(url))
                return false;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONArray jboy = new JSONArray(response);
                                for (int i = 0; i < jboy.length(); i++) {
                                    JSONObject jOb = jboy.getJSONObject(i);
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
                            } catch (Exception e){
                                Log.d("Error", e.toString());
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
                                if (!ORGS.contains(jOb.getString("organization"))){
                                    ORGS.add(jOb.getString("organization"));
                                }
                                if (!LOCS.contains(jOb.getString("location"))){
                                    LOCS.add(jOb.getString("location"));
                                }
                            }
                            rssFeedListAdapter.updateList(mFeedModelList);
                        } catch (Exception e){
                            Log.d("Error", e.toString());
                            Toast.makeText(getActivity(), R.string.snackbar_text, Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", error.toString());
                }
            });
            RSSFeedModelListOrganizer();
            putArrayListIntoRecyclerView();
                    }, error -> {Log.e("Error", error.toString()); Toast.makeText(getActivity(), R.string.snackbar_text, Toast.LENGTH_SHORT).show();});
            queue.add(stringRequest);
            return true;
        }

        protected void RSSFeedModelListOrganizer(){
            ArrayList<RssFeedModel> tempList = new ArrayList<RssFeedModel>();
            int i = mFeedModelList.size();
            int j=0, k=4;
            String secondsmark = ":00 ";
            String temp, timestamp1, timestamp2;
            char parse1,parse2;
            while(true) {
                temp = mFeedModelList.get(i).getTimestamp().substring(j, k);
                if (temp.equals(secondsmark)) {
                    timestamp1 = mFeedModelList.get(i).getTimestamp().substring(j - 5, k);
                    break;
                } else
                    j++; k++;
            }
            while(i>0) {
                timestamp2 = mFeedModelList.get(i-1).getTimestamp().substring(j, k);
                if(timestamp1.compareTo(timestamp2) > 0)
                    tempList.add(mFeedModelList.get(i));
                else
                    tempList.add(i-1,mFeedModelList.get(i));
                i--;
            }
            //orrrr we could have it find the earliest time, add that object, then add the rest behind it
            mFeedModelList = tempList;
        }

        protected void putArrayListIntoRecyclerView(){
            RssFeedListAdapter feedListAdapter = new RssFeedListAdapter(mFeedModelList);
            mRecyclerView.setAdapter(feedListAdapter);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                RssFeedListAdapter feedListAdapter = new RssFeedListAdapter(mFeedModelList);
                mRecyclerView.setAdapter(feedListAdapter);
            } else {
                Toast.makeText(getActivity(),
                        "Something went terribly wrong.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class NoDefaultSpinner extends androidx.appcompat.widget.AppCompatSpinner {

        public NoDefaultSpinner(Context context) {
            super(context);
        }

        public NoDefaultSpinner(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void setAdapter(SpinnerAdapter orig ) {
            final SpinnerAdapter adapter = newProxy(orig);

            super.setAdapter(adapter);

            try {
                final Method m = AdapterView.class.getDeclaredMethod(
                        "setNextSelectedPositionInt",int.class);
                m.setAccessible(true);
                m.invoke(this,-1);

                final Method n = AdapterView.class.getDeclaredMethod(
                        "setSelectedPositionInt",int.class);
                n.setAccessible(true);
                n.invoke(this,-1);
            }
            catch( Exception e ) {
                throw new RuntimeException(e);
            }
        }

        protected SpinnerAdapter newProxy(SpinnerAdapter obj) {
            return (SpinnerAdapter) java.lang.reflect.Proxy.newProxyInstance(
                    obj.getClass().getClassLoader(),
                    new Class[]{SpinnerAdapter.class},
                    (InvocationHandler) new SpinnerAdapterProxy(obj));
        }

        protected class SpinnerAdapterProxy implements InvocationHandler {

            protected SpinnerAdapter obj;
            protected Method getView;


            protected SpinnerAdapterProxy(SpinnerAdapter obj) {
                this.obj = obj;
                try {
                    this.getView = SpinnerAdapter.class.getMethod(
                            "getView",int.class,View.class,ViewGroup.class);
                }
                catch( Exception e ) {
                    throw new RuntimeException(e);
                }
            }

            public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
                try {
                    return m.equals(getView) &&
                            (Integer)(args[0])<0 ?
                            getView((Integer)args[0],(View)args[1],(ViewGroup)args[2]) :
                            m.invoke(obj, args);
                }
                catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            protected View getView(int position, View convertView, ViewGroup parent)
                    throws IllegalAccessException {

                if( position<0 ) {
                    final TextView v =
                            (TextView) ((LayoutInflater)getContext().getSystemService(
                                    Context.LAYOUT_INFLATER_SERVICE)).inflate(
                                    android.R.layout.simple_spinner_item,parent,false);
                    v.setText(getPrompt());
                    return v;
                }
                return obj.getView(position,convertView,parent);
            }
        }

    }
}