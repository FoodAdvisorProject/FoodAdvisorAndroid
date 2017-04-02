package pro.rane.foodadvisor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

// TODO: 31/03/17 BUG REPORT
// TODO: 31/03/17 lo slider start point si muove e ad ogni reindirizzamento (non punta correttamente se troppo in alto) 
// TODO: 31/03/17 tasto indietro che chiude l'app mentre dovrebbe far tornare al first fragment
// TODO: 31/03/17 far chiudere la tastiera dopo click dei bottoni 
// TODO: 31/03/17 far chiudere il set error una volta che si tocca sul campo 
// TODO: 31/03/17 reindirizzare la vista una volta fatto  il press button sugli errori
// TODO: 31/03/17 Error handling (not found) rivedere su register activity e su login activity 
// TODO: 31/03/17 campi email e password di font diversi correggere



public class Utility {

    public static String toCorrectCase(String req){
        String ret;
        ret = req.replace(':','=').replace(',','&').replaceAll(Pattern.quote("{"),"").replaceAll(Pattern.quote("}"),"").replaceAll(Pattern.quote(""),"").replace("\"", "").replace(" ","%20");
        return ret;
    }

    public static void alert(Context context, String text){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Attenzione");
        alertDialog.setMessage(text);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }



    public static String md5(String plaintext){
        MessageDigest m;
        String hashtext = "";
        try {
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
                hashtext = "0"+hashtext;
            }
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashtext;
    }


    /* restGET
       Dato un contesto context ed una stringa rappresentante un url, la funzione restituisce la risposta della chiamata sottoforma di stringa
       Nota: Quando si chiama questa funzione va assolutamente passato il contesto context (getActivity().getApplicationContext() nei fragments)
     */
    public static String restGET(final Context context,final String url){
        final String[] res = new String[1]; // ho lasciato fare questa schifezza al correttore automatico, ma dovrebbe funzionare perchè l'array è statico ma il contenuto dinamico.
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        res[0] = response;
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
        return res[0];
    }

    // TODO: 19/03/2017 testare le due funzioni e implementarle 
    //conversion data for photo
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
