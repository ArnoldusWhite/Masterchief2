package com.example.probook4530s.masterchief.Custom.custom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.probook4530s.masterchief.Custom.Login;
import com.example.probook4530s.masterchief.Custom.MainActivity;
import com.example.probook4530s.masterchief.Custom.model.Receipt;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;

import com.example.probook4530s.masterchief.Custom.model.User;
import com.example.probook4530s.masterchief.Custom.network.ServiceHandler;
import com.example.probook4530s.masterchief.Custom.receipt_detail;
import com.example.probook4530s.masterchief.Custom.ui.receiptdetail.ReceiptDetailFragment;
import com.example.probook4530s.masterchief.Custom.utils.Network_param;
import com.example.probook4530s.masterchief.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Receipt_adapter extends RecyclerView.Adapter<Receipt_adapter.MyViewHolder> {

    private Context mContext;
    public View customview;
    private List<Receipt> receiptList;
    public ProgressDialog pDialog;
    public static MyViewHolder holderAll;
    String compare;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            customview=view;
            customview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public Receipt_adapter(Context mContext, List<Receipt> receiptList) {
        this.mContext = mContext;
        this.receiptList = receiptList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Receipt receipt=receiptList.get(position);
        holder.title.setText(receipt.getName());
        holder.count.setText(receipt.getDuration() + " min");
        holder.overflow.setTag(receipt.getId());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holderAll=holder;
                Login.CurrentReceipt=receipt;
                new DoAddFav().execute();
            }
        });
        customview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.CurrentReceipt=receipt;
                Intent intented=new Intent(customview.getContext(),receipt_detail.class);
                customview.getContext().startActivity(intented);
            }
        });



        // loading album cover using Glide library
        Glide.with(mContext).load(receipt.getIllustration()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }



    private class DoAddFav extends AsyncTask<Void, Void, Void> {

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

            ConnectivityManager conMgr = (ConnectivityManager) customview.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                // Creating service handler class instance

                List<NameValuePair> parametres = new ArrayList<NameValuePair>();
                parametres.add(new BasicNameValuePair("user", Login.UserIdentified.getId()));
                parametres.add(new BasicNameValuePair("receipt", Login.CurrentReceipt.getId()));

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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(!statut.equals("0X01")){
                if(statut.equals("0X02")){
                    Log.e("Erreur",resultat);
                    //compare="go";
                    holderAll.overflow.setImageResource(R.mipmap.star_selected);
                }else{
                    Toast.makeText(customview.getContext(), "Erreur lors de la connection : Combinaison login/mot de passe incorrecte",
                            Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(customview.getContext(), "Erreur de connexion réseau au serveur",
                        Toast.LENGTH_LONG).show();
            }
        }

    }
}


