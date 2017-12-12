package com.example.van.trans;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import org.mindrot.jbcrypt.BCrypt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {
    String localhost = "http://192.168.10.104:8000/";//link server database
    static String string = "";
    URL url;
    //Hàm encryptMD5 giúp mã hóa MD5 password
    /*public static String encryptMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = (Button)findViewById(R.id.login_button);

        final EditText email = (EditText)findViewById(R.id.mail_input);
        final EditText password = (EditText)findViewById(R.id.pass_input);

        //Xử lý sự kiện click button Đăng nhập
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please retype username or password", Toast.LENGTH_SHORT).show();
                }
                else {

                    MyDownloadTask t = new MyDownloadTask();
                    t.execute();

                }
            }
        });
    }



    /*Class MyDownloadTask là AsyncTask tạo liên kết với Database
    lấy thông tin (name, password) kiểm tra để đăng nhập*/

    class MyDownloadTask extends AsyncTask<Void,Void,Void> {

        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            final EditText em = (EditText)findViewById(R.id.mail_input);
            final EditText pw = (EditText)findViewById(R.id.pass_input);

            //Lấy name, password từ người dùng (người dùng nhập vào)
            final String uname = em.getText().toString();
            final String pword = pw.getText().toString();
            //Tạo liên kết đến file result.php trên Database lấy name và password về
            try {
                url = new URL(localhost+"login_android?email="+uname+"&pass="+pword);


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
            String check = "";
            String id="";
            String name="";
            final EditText em = (EditText)findViewById(R.id.mail_input);
            final EditText pw = (EditText)findViewById(R.id.pass_input);

            //Lấy name, password từ người dùng (người dùng nhập vào)
            final String uname = em.getText().toString();
            final String pword = pw.getText().toString();

            //Lấy name, password từ server
            try {

                /*JSONArray mang = new JSONArray(string);
                for (int i=0; i< mang.length();i++) {

                    JSONObject obj = mang.getJSONObject(i);
                    if("1".equals(obj.getString("check"))) {
                        check = obj.getString("check");
                        id = obj.getString("id");
                    }

                }*/
                JSONObject obj = new JSONObject(string);
                if("1".equals(obj.getString("check"))) {
                    check = obj.getString("check");
                    id = obj.getString("id");
                    name = obj.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //So sánh nếu trùng thì chuyển sang MainActivity còn không nhắc nhở
            if(check=="1"){
                Intent it = new Intent(Login.this, MainActivity.class);
                if (id!="") it.putExtra("id", id);
                if (name!="") it.putExtra("name", name);
                startActivity(it);
                finish();
            }
            else Toast.makeText(Login.this, localhost, Toast.LENGTH_SHORT).show();
        }

    }
}
