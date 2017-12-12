package com.example.van.trans;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import customfonts.MyTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.LinearLayout.*;

public class MainActivity extends AppCompatActivity {
    String localhost = "http://192.168.10.104:8000/";//link server database
    static String string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user t = new user();
        t.execute();
        products p = new products();
        p.execute();


    }

    LinearLayout myLayout;

    class products extends AsyncTask<Void,Void,Void> {

        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Void doInBackground(Void... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            String id="";
            Bundle extras = getIntent().getExtras();
            //id = extras.getString("id");
            id="7";
            //Tạo liên kết đến file result.php trên Database lấy name và password về
            try {
                url = new URL(localhost+"getproducts_android?id="+id);


                urlConnection = (HttpURLConnection) url
                        .openConnection();

                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                string = bufferedReader.readLine();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace(); //If you want further info on failure...
                }
            }
            return null;
        }
        protected void onPostExecute (Void result){
            String xuatphat = "";
            String id="";
            MyTextView username = (MyTextView)findViewById(R.id.name);
            Bundle extras = getIntent().getExtras();
            //id = extras.getString("id");
            id = "7";
            myLayout = (LinearLayout) findViewById(R.id.layout1);


            //Lấy name, password từ server
            try {

                JSONArray mang = new JSONArray(string);
                for (int i=0; i< mang.length();i++) {

                    JSONObject obj = mang.getJSONObject(i);
                    if(id.equals(obj.getString("driver_id"))) {
                        final String product_id = obj.getString("id");
                        xuatphat = obj.getString("xuat_phat_details");
                        LinearLayout custom = new LinearLayout(MainActivity.this);
                        custom.setId(Integer.parseInt(product_id));
                        LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(
                                0xffffffff,
                                0xfffffffe
                        );
                        custom.setLayoutParams(lparam);
                        custom.setOrientation(LinearLayout.HORIZONTAL);
                        MyTextView custom1 = new MyTextView(MainActivity.this);
                        custom1.setText(xuatphat);
                        custom1.setId(Integer.parseInt(product_id)+1000);
                        custom1.setBackgroundColor(0x0000);
                        custom1.setTextSize(16);
                        custom1.setPadding(16,16,16,16);
                        custom1.setMaxLines(12);
                        custom1.setGravity(3);
                        ViewGroup.LayoutParams vparam = new ViewGroup.LayoutParams(
                                0xffffffff,
                                0xfffffffe);
                        custom1.setLayoutParams(vparam);
                        custom.addView(custom1);
                        myLayout.addView(custom);
                        final String des = obj.getString("dich_den_details");
                        final String driver_id = obj.getString("driver_id");

                        custom1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(MainActivity.this, MapActivity.class);
                                if (des!="") it.putExtra("des", des);
                                it.putExtra("driver_id", driver_id);
                                it.putExtra("product_id", product_id);
                                startActivityForResult(it, 1);
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    private class CustomLayout extends LinearLayout {
        public CustomLayout(Context context) {
            super(context);
        }

        @Override
        protected void removeDetachedView(View child, boolean animate) {
            super.removeDetachedView(child, false);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                //Update List
                LinearLayout myLayout1 = (LinearLayout)findViewById(R.id.layout1);
                Bundle extras = data.getExtras();
                String product_id = extras.getString("product_id");
                Integer c = Integer.valueOf(product_id)+1000;
                LinearLayout remove = (LinearLayout)findViewById(getResources().getIdentifier(product_id, "id", getPackageName()));

                remove.removeAllViewsInLayout();
                myLayout1.setLayoutTransition(null);
                myLayout1.removeView(remove);

            }
            if (resultCode == RESULT_CANCELED) {
                //Do nothing?
            }
        }
    }//onActivityResult

    class user extends AsyncTask<Void,Void,Void> {

        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Void doInBackground(Void... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            String id="";
            Bundle extras = getIntent().getExtras();
            //id = extras.getString("id");
            id="7";
            //Tạo liên kết đến file result.php trên Database lấy name và password về
            try {
                url = new URL(localhost+"login_android?id="+id);


                urlConnection = (HttpURLConnection) url
                        .openConnection();

                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                string = bufferedReader.readLine();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace(); //If you want further info on failure...
                }
            }
            return null;
        }
        protected void onPostExecute (Void result){
            String name = "";
            MyTextView username = (MyTextView)findViewById(R.id.name);
            String id="";
            Bundle extras = getIntent().getExtras();
            //id = extras.getString("id");
            id="7";
            //Lấy name, password từ server
            try {

                JSONObject obj = new JSONObject(string);
                if("1".equals(obj.getString("check"))) {
                    id = obj.getString("id");
                    name = obj.getString("name");
                        username.setText(name);
                    }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
