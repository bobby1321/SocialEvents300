package com.example.myapplication.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FilterFragment extends DialogFragment {

    private LinearLayout linearLayout;
    private ArrayList<RssFeedModel> rssFeedModels;
    private ArrayList<String> ORGS, LOCS;
    private Button buttonCancel, buttonFilter;
    private RssFeedListAdapter rssFeedListAdapter;
    private boolean filtered = false;

    public FilterFragment(ArrayList<RssFeedModel> arraylist, ArrayList<String> orgs, ArrayList<String> locs, RssFeedListAdapter adapter){
        this.rssFeedModels = arraylist;
        this.ORGS = orgs;
        this.LOCS = locs;
        this.rssFeedListAdapter = adapter;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter, container, false);
        linearLayout = root.findViewById(R.id.linearLayout);
        buttonCancel = root.findViewById(R.id.buttonCancel);
        buttonFilter = root.findViewById(R.id.buttonFilter);
        buttonFilter.setEnabled(false);

        FilterFragment.NoDefaultSpinner spinner = new FilterFragment.NoDefaultSpinner(getContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select a filter option...");
        spinner.setMinimumWidth(1000);

        linearLayout.addView(spinner);

        final View[] views = new View[1];

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    case 3:{
                        FetchQuery(3, ((TextView)views[0]).getText().toString());
                    }
                }
                filtered = true;
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (views[0] != null){
                    linearLayout.removeView(views[0]);
                    views[0] = null;
                }
                if (position != -1){
                    buttonFilter.setEnabled(true);
                }
                switch (position){
                    case 0:{
                        views[0] = new EditText(getActivity());
                        ((TextView)views[0]).setRawInputType(InputType.TYPE_CLASS_TEXT);
                        ((TextView)views[0]).setHint("Event Name");
                        linearLayout.addView(views[0]);
                        break;
                    }
                    case 1:{
                        String[] temp = ORGS.toArray(new String[ORGS.size()]);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, temp);
                        views[0] = new AutoCompleteTextView(getContext());
                        ((AutoCompleteTextView)views[0]).setAdapter(adapter);
                        linearLayout.addView(views[0]);
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
                        linearLayout.addView(views[0]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return root;
    }

    public boolean getFiltered(){
        return filtered;
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
        rssFeedModels.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                            rssFeedModels.add(temp);
                            Log.d("List", temp.toString());
                        }
                        rssFeedListAdapter.updateList(rssFeedModels);
                    } catch (Exception e){
                        Log.d("Error", e.toString());
                    }
                }}, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
            }});
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 15, 1.0f));
        queue.add(stringRequest);

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
                    (InvocationHandler) new FilterFragment.NoDefaultSpinner.SpinnerAdapterProxy(obj));
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
