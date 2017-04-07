package pro.rane.foodadvisor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class MyProductsFragment extends Fragment {
    private String url;
    pro.rane.foodadvisor.SessionManager session;
    ProgressBar loading_bar;
    TextView products;


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
        products = (TextView) rootView.findViewById(R.id.userProducts);
        loading_bar = (ProgressBar) rootView.findViewById(R.id.progressBar5);
        loading_bar.setVisibility(View.VISIBLE);
        products.setVisibility(View.INVISIBLE);
        getArticles();
        return rootView;
    }

    private void getArticles(){
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
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
        String list = "";
        // TODO: 10/03/2017 photo handling
        try {
            JSONArray articles = new JSONArray(res);
            JSONObject a;
            for(int i = 0; i<articles.length();i++){
                a = articles.getJSONObject(i);
                list+="Nome prodotto: "+ a.get("name").toString() +"\nDescrizione: "+a.get("description").toString() +"\nId articolo: "+ a.get("article_id")+"\n\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        products.setText(list);
        loading_bar.setVisibility(View.INVISIBLE);
        products.setVisibility(View.VISIBLE);
    }

    /* TODO: 11/03/2017 implementare funzione "segui il tuo prodotto" : ovvero una chiamata di user mode direttamente sul prodotto senza passare per lo scan.
        NB tale cosa a livello di layout sarà un bottone
     */

}