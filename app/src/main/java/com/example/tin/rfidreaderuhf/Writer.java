package com.example.tin.rfidreaderuhf;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.xminnov.ivrjack.ru01.IvrJackAdapter;
import com.xminnov.ivrjack.ru01.IvrJackService;
import com.xminnov.ivrjack.ru01.IvrJackStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Writer extends AppCompatActivity implements IvrJackAdapter{

    public static List<String> Arr = new ArrayList<String>();
    private EditText txt_sprice;
    private EditText txt_name;
    private TextView lbl_notify;
    private Toolbar toolbar;
    private Spinner spinerProduct;

    public static IvrJackService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initComponent();
        service = new IvrJackService(this, this);
        service.open();
        ReadTask task = new ReadTask();
        new Thread(task).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

    private void initComponent() {
        Arr.clear();
        String path="http://192.168.0.101/ReadCard/index.php/getDataControllers/getKindOfCard";
        new getDataFromWebserver().execute(new String[]{path});
        Arr.add("---Chọn Loại Thẻ---");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Arr);

        spinerProduct = (Spinner) findViewById(R.id.cb_loai_card);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinerProduct.setAdapter(adapter);

        txt_sprice = (EditText)findViewById(R.id.txt_sprice);
        txt_name = (EditText)findViewById(R.id.txt_name);
        lbl_notify = (TextView)findViewById(R.id.lbl_notify);
    }

    @Override
    protected void onDestroy() {
        service.close();
        service = null;
        super.onDestroy();
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    class getDataFromWebserver extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            HttpClient httpclient= new DefaultHttpClient();
            HttpPost httppost=new HttpPost(arg0[0]);
            List<NameValuePair> doiso=new ArrayList<NameValuePair>(1);
            doiso.add(new BasicNameValuePair("id","0"));
            try {

                UrlEncodedFormEntity entity=new UrlEncodedFormEntity(doiso,"UTF-8");
                httppost.setEntity(entity);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String content = httpclient.execute(httppost, handler);
                Log.d("thongbao", "noidung" + content);
                return content;
            }catch(Exception e)
            {
                Log.d("loi","loi " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // TODO Auto-generated method stub
                super.onPostExecute(result);

                //    JSONObject reader = new JSONObject(result);
                JSONArray sys  = new JSONArray(result);
                for(int i=0;i<sys.length();i++) {
                    JSONObject jsonObject = sys.getJSONObject(i);
                    if(!jsonObject.optString("KindName").toString().equals(""))
                        Writer.Arr.add(jsonObject.optString("KindName").toString());
                }

            }catch (Exception e){}
        }
    }

    class sendDataToWebserver extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            HttpClient httpclient= new DefaultHttpClient();
            HttpPost httppost=new HttpPost(arg0[0]);
            List<NameValuePair> doiso=new ArrayList<NameValuePair>(1);
            doiso.add(new BasicNameValuePair("KindName",arg0[1]));
            doiso.add(new BasicNameValuePair("CardKey",arg0[2]));
            doiso.add(new BasicNameValuePair("CardName",arg0[3]));
            doiso.add(new BasicNameValuePair("price",arg0[4]));
            try {

                UrlEncodedFormEntity entity=new UrlEncodedFormEntity(doiso,"UTF-8");
                httppost.setEntity(entity);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String content = httpclient.execute(httppost, handler);
                Log.i("MyLog", content);
                return content;
            }catch(Exception e)
            {
                Log.i("MyLog","loi " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // TODO Auto-generated method stub
                super.onPostExecute(result);

                //    JSONObject reader = new JSONObject(result);
                JSONArray sys  = new JSONArray(result);
                for(int i=0;i<sys.length();i++) {
                    JSONObject jsonObject = sys.getJSONObject(i);
                    if(!jsonObject.optString("Result").toString().equals(""))
                        lbl_notify.setText(jsonObject.optString("Result").toString());
                }

            }catch (Exception e){}
        }
    }

    @Override
    public void onConnect(String s) {
        Log.i("MyLog:", "on connect");
        toolbar.setTitle(R.string.connected);
    }

    @Override
    public void onDisconnect() {
        Log.i("MyLog:", "on disconnect");
        toolbar.setTitle(R.string.disconnected);
    }

    @Override
    public void onStatusChange(IvrJackStatus ivrJackStatus) {
        Log.i("MyLog:", "on status change");
        toolbar.setTitle(R.string.search);
    }

    @Override
    public void onInventory(byte[] bytes) {
        Log.i("MyLog:", "on Inventory");
        toolbar.setTitle(R.string.search);
    }

    class ReadTask implements Runnable {
        byte address=2;
        byte block=0;
        byte length=6;
        String path="http://192.168.0.101/ReadCard/index.php/getDataControllers/setInforCard";

        @Override
        public void run() {
            while(true) {
                IvrJackService.TagBlock result = new IvrJackService.TagBlock();
                int ret = service.readTag(block, address, length, result);

                if (ret == 0) {
                    Log.i("MyLog:", bytesToHex(result.data));
                    new sendDataToWebserver().execute(new String[]{path,spinerProduct.getTransitionName().toString(), bytesToHex(result.data),txt_name.getText().toString(),txt_sprice.getText().toString()});
                    try {
                        Thread.sleep(1000);
                     //   lbl_notify.setText(" ");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //    Log.i("MyLog:", "Read tag failed");
                }
            }
        }
    }

}
