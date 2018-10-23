package com.example.probook4530s.masterchief.Custom.ui.receiptdetail;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.probook4530s.masterchief.Custom.Login;
import com.example.probook4530s.masterchief.*;
import com.example.probook4530s.masterchief.Custom.MainActivity;
import com.example.probook4530s.masterchief.Custom.add_receipt;
import com.example.probook4530s.masterchief.Custom.custom.CustomActivity;
import com.example.probook4530s.masterchief.Custom.model.Data;
import com.example.probook4530s.masterchief.Custom.model.Ingredient;
import com.example.probook4530s.masterchief.Custom.model.Receipt;
import com.example.probook4530s.masterchief.Custom.model.User;
import com.example.probook4530s.masterchief.Custom.network.ServiceHandler;
import com.example.probook4530s.masterchief.Custom.utils.Network_param;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReceiptDetailFragment extends Fragment {

    public ImageView receipt_detail;
    private ReceiptDetailViewModel mViewModel;
    public View customview;
    private View vue;
    private ArrayList<Data> receiptList= new ArrayList<Data>();
    private ArrayList<Ingredient> ingredientsList= new ArrayList<Ingredient>();
    ListView list;
    public ProgressDialog pDialog;
    PopupMenu popup;

    public static ReceiptDetailFragment newInstance() {
        return new ReceiptDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //getActivity().getSupportActionBar().hide();
        setHasOptionsMenu(true);
        vue=inflater.inflate(R.layout.receipt_detail_fragment, container, false);
        customview=vue;

        return vue;
    }


    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.mymenu, menu);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mViewModel = ViewModelProviders.of(this).get(ReceiptDetailViewModel.class);

        new DoGetIngredients().execute();

        list = (ListView) vue.findViewById(R.id.list);
        list.setAdapter(new RecipeAdapter());


        receipt_detail=(ImageView)vue.findViewById(R.id.receipt_illustration);
        TextView id=(TextView)vue.findViewById(R.id.receipt_id);
        TextView time=(TextView)vue.findViewById(R.id.receipt_timeTxt);
        TextView guest=(TextView)vue.findViewById(R.id.receipt_guestdetail);
        TextView desc=(TextView)vue.findViewById(R.id.receipt_desc);
        ImageView imgDot=(ImageView)vue.findViewById(R.id.receipt_3dots) ;

        imgDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });



        desc.setText(Login.CurrentReceipt.getDescription());
        id.setText(Login.CurrentReceipt.getName());
        time.setText(Login.CurrentReceipt.getDuration()+ " min");
        guest.setText(Login.CurrentReceipt.getGuest() + "persons");
        try {
            Glide.with(this).load(Login.CurrentReceipt.getIllustration()).into(receipt_detail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO: Use the ViewModel

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mymenu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        new doDelete().execute();
                        return true;
                    case R.id.add:
                        startActivity(new Intent(getActivity(),add_receipt.class));
                    default: return false;
                }
            }
        });
    }

    public void doBack(){
        new doDelete().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.delete)
        {
            new doDelete().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRecipeList(ArrayList<Ingredient> Ing)
    {
        ArrayList<Data> pList2=new ArrayList<Data>();
        for(int i=0;i<Ing.size();i++){
            pList2.add(new Data(Ing.get(i).getName().toString(),Ing.get(i).getQte().toString()));
        }
        receiptList= pList2;
        list.setAdapter(new RecipeAdapter());
    }


    private class RecipeAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {

            return receiptList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Data getItem(int arg0)
        {
            return receiptList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null)
                v = LayoutInflater.from(getActivity()).inflate(
                        R.layout.recipe_item, null);

            Data c = getItem(pos);
            TextView lbl = (TextView) v.findViewById(R.id.lbl1);
            lbl.setText(c.getTitle());
            TextView Qtelbl=(TextView)v.findViewById(R.id.qtelbl);
            Qtelbl.setText(c.getQte());

            return v;
        }

    }


    private class DoGetIngredients extends AsyncTask<Void, Void, Void> {

        private String url = Network_param.Ulr2,resultat,statut;
        JSONArray IngredientArray = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            statut = "0X01";

        }

        protected Void doInBackground(Void... arg0) {

            ConnectivityManager conMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                // Creating service handler class instance

                List<NameValuePair> parametres = new ArrayList<NameValuePair>();
                parametres.add(new BasicNameValuePair("Getingredients", Login.CurrentReceipt.getId()));

                ServiceHandler sh = new ServiceHandler();

                try {
                    resultat = sh.makeServiceCall(url, ServiceHandler.POST, parametres);

                    if (resultat != null) {
                        try {
                            statut = "0X00";
                            JSONObject jsonObj = new JSONObject(resultat);
                            IngredientArray = jsonObj.getJSONArray("ingredient");
                            User use = null;
                            for (int i = 0; i < IngredientArray.length(); i++) {
                                JSONObject jo = IngredientArray.getJSONObject(i);
                                //use = new User(jo.getString("nom"),jo.getString("prenom"),jo.getString("sexe"),jo.getString("nele"),jo.getString("adresse"),jo.getString("login"),jo.getString("pwd"),jo.getString("cni"),jo.getString("idemp"),jo.getString("bt"),jo.getString("type"),jo.getString("sup"),jo.getString("nombt"),jo.getString("langue"));

                                ingredientsList.add(new Ingredient(jo.getString("name"),jo.getString("qte")));
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
            if(!statut.equals("0X01")){
                if(!resultat.equals("OPSNIL")){
                    Log.e("Erreur",resultat);
                    loadRecipeList(ingredientsList);
                }else{

                }
            }else{
                Toast.makeText(getActivity().getApplicationContext(), "Erreur de connexion réseau au serveur",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private class doDelete extends AsyncTask<Void, Void, Void> {

        private String url = Network_param.Ulr2,resultat,statut,val;
        JSONArray msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(customview.getContext());
            pDialog.setMessage("chargement");
            pDialog.setCancelable(false);
            pDialog.show();
            statut = "0X01";

        }

        protected Void doInBackground(Void... arg0) {

            ConnectivityManager conMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                // Creating service handler class instance

                List<NameValuePair> parametres = new ArrayList<NameValuePair>();
                parametres.add(new BasicNameValuePair("DeleteReceipt", Login.CurrentReceipt.getId()));

                ServiceHandler sh = new ServiceHandler();

                try {
                    resultat = sh.makeServiceCall(url, ServiceHandler.POST, parametres);

                    if (resultat != null) {
                        try {
                            statut = "0X00";
                            JSONObject jsonObj = new JSONObject(resultat);
                            msg = jsonObj.getJSONArray("Statut");
                            for (int i = 0; i < msg.length(); i++) {
                                JSONObject jo = msg.getJSONObject(i);
                                //use = new User(jo.getString("nom"),jo.getString("prenom"),jo.getString("sexe"),jo.getString("nele"),jo.getString("adresse"),jo.getString("login"),jo.getString("pwd"),jo.getString("cni"),jo.getString("idemp"),jo.getString("bt"),jo.getString("type"),jo.getString("sup"),jo.getString("nombt"),jo.getString("langue"));
                                val=jo.getString("state");
                                if(val.equals("OK")){
                                    statut = "0X02";
                                }
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(!statut.equals("0X01")){
                if(resultat.equals("0X02")){
                    Log.e("Erreur",resultat);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Echec lors de l'opération, veuillez ressayer.",
                            Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getActivity().getApplicationContext(), "Erreur de connexion réseau au serveur",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

}
