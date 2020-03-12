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
    private final static String KEY = "Key";
    private FloatingActionButton fab;
    private ArrayList<String> ORGS = new ArrayList<String>();
    private ArrayList<String> LOCS = new ArrayList<String>();
    private boolean filtered= false;

    private ArrayList<RssFeedModel> mFeedModelList = new ArrayList<RssFeedModel>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        recycleManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(recycleManager);
        mRecyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
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
        NoDefaultSpinner spinner = new NoDefaultSpinner(getContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select a filter option...");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(spinner);

        final View[] views = new View[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Filter Events");
        builder.setView(layout);
        builder.setPositiveButton("Filter", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                int pos = spinner.getSelectedItemPosition();
                Log.d("URL","Help");
                switch (pos){
                    case -1:{
                        //fuck you how did you even do that
                        Log.d("URL", "Oh poop");
                        break;
                    }
                    case 0:{
                        FetchQuery(0, ((TextView)views[0]).getText().toString());
                        break;
                    }
                    case 1:{
                        FetchQuery(1, ((TextView)views[0]).getText().toString());
                        break;
                    }

                }
                filtered = true;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (views[0] != null){
                    layout.removeView(views[0]);
                    views[0] = null;
                }
                if (position != -1){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
                switch (position){
                    case 0:{
                        views[0] = new EditText(getActivity());
                        ((TextView)views[0]).setRawInputType(InputType.TYPE_CLASS_TEXT);
                        ((TextView)views[0]).setHint("Event Name");
                        layout.addView(views[0]);
                        break;
                    }
                    case 1:{
                        String[] temp = ORGS.toArray(new String[ORGS.size()]);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, temp);
                        views[0] = new AutoCompleteTextView(getContext());
                        ((AutoCompleteTextView)views[0]).setAdapter(adapter);
                        layout.addView(views[0]);
                        break;
                    }
                    case 2:{

                    }
                    case 3:{
                        String[] temp = LOCS.toArray(new String[ORGS.size()]);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, temp);
                        views[0] = new AutoCompleteTextView(getContext());
                        ((AutoCompleteTextView)views[0]).setAdapter(adapter);
                        layout.addView(views[0]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        alertDialog.show();
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

    public void FetchQuery(int type, String param){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "ahhhh";
        switch (type){
            case 0:{
                url ="http://pages.erau.edu/~apelianr/name_search.php?name=" + param;
                break;
            }
            case 1:{
                url ="http://pages.erau.edu/~apelianr/org_search.php?org=" + param;
                break;
            }
            case 2:{

            }
            case 3:{
                url ="http://pages.erau.edu/~apelianr/loc_search.php?loc=" + param;
                break;
            }
        }
        mFeedModelList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try{
                        mFeedModelList = new ArrayList<RssFeedModel>();
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
                    }
                }, error -> Log.e("Error", error.toString()));
        queue.add(stringRequest);
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
                    response -> {
                        try{
                            mFeedModelList = new ArrayList<RssFeedModel>();
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
                            RssFeedListAdapter feedListAdapter = new RssFeedListAdapter(mFeedModelList);
                            mRecyclerView.setAdapter(feedListAdapter);
                        } catch (Exception e){
                            Log.d("Error", e.toString());
                            Toast.makeText(getActivity(), R.string.snackbar_text, Toast.LENGTH_SHORT).show();
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