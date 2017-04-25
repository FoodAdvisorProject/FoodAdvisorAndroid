package pro.rane.foodadvisor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyProductsFragment extends Fragment {
    private String url;
    pro.rane.foodadvisor.SessionManager session;
    ProgressBar loading_bar;
    List<Product> productsList;
    RecyclerView rv;
    RequestQueue queue;

    public MyProductsFragment() {
        //must be empty
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        session = new pro.rane.foodadvisor.SessionManager(getActivity().getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        url = "http://foodadvisor.rane.pro:8080/getUserArticles?user_id=" + user.get(SessionManager.KEY_ID);
        View rootView = inflater.inflate(R.layout.fragment_my_products, container, false);
        loading_bar = (ProgressBar) rootView.findViewById(R.id.progressBar5);
        loading_bar.setVisibility(View.VISIBLE);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        rv = (RecyclerView)rootView.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        getArticles();
        return rootView;
    }

    private void getArticles(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showArticles(response);
                        Log.d(this.getClass().getSimpleName() ,"RESPONSE VALUE: "+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(this.getClass().getSimpleName() ,"Errore su volley : " + error.toString());
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    private void showArticles(String res){
        productsList = new ArrayList<>();
        try {
            JSONArray articles = new JSONArray(res);
            JSONObject a;
            for(int i = 0; i<articles.length();i++){
                a = articles.getJSONObject(i);
                productsList.add(new Product(a.get("name").toString(),a.get("description").toString(),a.get("article_id").toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RVAdapter adapter = new RVAdapter(productsList,getContext(),queue);
        rv.setAdapter(adapter);
        loading_bar.setVisibility(View.INVISIBLE);
    }
}