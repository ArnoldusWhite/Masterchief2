package com.example.probook4530s.masterchief.Custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.probook4530s.masterchief.Custom.custom.Receipt_adapter;
import com.example.probook4530s.masterchief.Custom.model.Receipt;
import com.example.probook4530s.masterchief.Custom.model.User;
import com.example.probook4530s.masterchief.Custom.network.ServiceHandler;
import com.example.probook4530s.masterchief.Custom.utils.Network_param;
import com.example.probook4530s.masterchief.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private Receipt_adapter adapter;
    private List<Receipt> receiptList;
    private List<Receipt> receiptListTmp;
    private Receipt receiptItem;
    public ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        receiptList = new ArrayList<>();
        receiptListTmp = new ArrayList<>();

        new DoGetReceipts().execute();

        adapter = new Receipt_adapter(this, receiptListTmp);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);



        /*try {
            Glide.with(this).load(R.mipmap.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Adding list
     */
    private void prepareAlbums() {
        receiptList=receiptListTmp;
        adapter.notifyDataSetChanged();
    }


    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {

        getMenuInflater().inflate(R.menu.principal_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    private class DoGetReceipts extends AsyncTask<Void, Void, Void> {

        private String url = Network_param.Ulr2,resultat,statut;
        JSONArray receiptArray = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
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
                parametres.add(new BasicNameValuePair("Getreceipts", "ALL"));

                ServiceHandler sh = new ServiceHandler();

                try {
                    resultat = sh.makeServiceCall(url, ServiceHandler.POST, parametres);

                    if (resultat != null) {
                        try {
                            statut = "0X00";
                            JSONObject jsonObj = new JSONObject(resultat);
                            receiptArray = jsonObj.getJSONArray("receipt");
                            User use = null;
                            for (int i = 0; i < receiptArray.length(); i++) {
                                JSONObject jo = receiptArray.getJSONObject(i);
                                //use = new User(jo.getString("nom"),jo.getString("prenom"),jo.getString("sexe"),jo.getString("nele"),jo.getString("adresse"),jo.getString("login"),jo.getString("pwd"),jo.getString("cni"),jo.getString("idemp"),jo.getString("bt"),jo.getString("type"),jo.getString("sup"),jo.getString("nombt"),jo.getString("langue"));
                                receiptListTmp.add(new Receipt(jo.getString("nom"),jo.getString("duration"),jo.getString("description"),
                                        jo.getString("guest"),jo.getString("id"),jo.getString("illustration")));
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
                    prepareAlbums();
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
