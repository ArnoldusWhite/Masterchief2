package com.example.probook4530s.masterchief.Custom;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.probook4530s.masterchief.Custom.model.Receipt;
import com.example.probook4530s.masterchief.Custom.model.User;
import com.example.probook4530s.masterchief.Custom.network.ServiceHandler;
import com.example.probook4530s.masterchief.Custom.utils.Network_param;
import com.example.probook4530s.masterchief.R;

import com.example.probook4530s.masterchief.Custom.custom.CustomActivity;

public class Login extends CustomActivity
{
    public static String loginConnected=null;
    public static User UserIdentified=null;
    public static Receipt CurrentReceipt=null;

    public ProgressDialog pDialog;

    /* (non-Javadoc)
     * @see com.chatt.custom.CustomActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTouchNClick(R.id.btnLogin);
        setTouchNClick(R.id.btnReg);
    }

    /* (non-Javadoc)
     * @see com.chatt.custom.CustomActivity#onClick(android.view.View)
     */
    @SuppressWarnings("unused")
    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v.getId() != R.id.btnReg)
        {
//			startActivity(new Intent(this, MainActivity.class));
//			finish();
            new DoLogin().execute();
        }
    }

    private class DoLogin extends AsyncTask<Void, Void, Void> {

        private String url = Network_param.Ulr2,resultat,statut;
        JSONArray users = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage(getString(R.string.titlelogin));
            pDialog.setCancelable(false);
            pDialog.show();
            statut = "0X01";

        }

        protected Void doInBackground(Void... arg0) {

            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                // Creating service handler class instance

                List<NameValuePair> parametres = new ArrayList<NameValuePair>();
                EditText login = (EditText) findViewById(R.id.password);
                EditText pwd = (EditText) findViewById(R.id.Login);
                parametres.add(new BasicNameValuePair("Pwd", login.getText().toString().trim()));
                parametres.add(new BasicNameValuePair("Login", pwd.getText().toString().trim()));

                ServiceHandler sh = new ServiceHandler();

                try {
                    resultat = sh.makeServiceCall(url, ServiceHandler.POST, parametres);

                    if (resultat != null) {
                        try {
                            statut = "0X00";
                            JSONObject jsonObj = new JSONObject(resultat);
                            users = jsonObj.getJSONArray("user");
                            User use = null;
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject jo = users.getJSONObject(i);
                                //use = new User(jo.getString("nom"),jo.getString("prenom"),jo.getString("sexe"),jo.getString("nele"),jo.getString("adresse"),jo.getString("login"),jo.getString("pwd"),jo.getString("cni"),jo.getString("idemp"),jo.getString("bt"),jo.getString("type"),jo.getString("sup"),jo.getString("nombt"),jo.getString("langue"));
                                use = new User(jo.getString("nom"), jo.getString("pwd"), jo.getString("id"));
                                UserIdentified = use;
                                loginConnected = use.getUsername();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");

                        return null;
                    }
                }catch ( Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Log.e("Network", "error");
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(!statut.equals("0X01")){
                if(!resultat.equals("OPSNIL")){
                    Log.e("Erreur",resultat);
                    Intent intented=new Intent(getApplication(),MainActivity.class);
                    startActivity(intented);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Erreur lors de la connection : Combinaison login/mot de passe incorrecte",
                            Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Erreur de connexion réseau au serveur",
                        Toast.LENGTH_LONG).show();
            }
        }

    }
}