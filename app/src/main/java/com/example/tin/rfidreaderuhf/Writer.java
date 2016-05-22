package com.example.tin.rfidreaderuhf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Writer extends AppCompatActivity implements IvrJackAdapter{

    public static List<String> Arr = new ArrayList<String>();
    public EditText txt_sprice;
    public EditText txt_name;
    public TextView lbl_notify;
    public Toolbar toolbar;
    public Spinner spinerProduct;
    ReadTask task;
    Handler mHandler = new Handler();

    public boolean run=true;
    public long start=0, stop=0;

    public static IvrJackService service;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initComponent();
        service = new IvrJackService(this, this);
        service.open();
        new Thread(new BeepTask()).start();
        task = new ReadTask();
        new Thread(task).start();

        resetText rs = new resetText();
        new Thread(rs).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.menu_exit:
                this.finish();
                break;
            case R.id.menu_save:
                Intent saveCarIntent = new Intent(getApplicationContext(),Writer.class);
                startActivity(saveCarIntent);
                this.finish();
                break;
            case R.id.menu_read:
                Intent readCarIntent = new Intent(getApplicationContext(),Reader.class);
                startActivity(readCarIntent);
                this.finish();
                break;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(getApplicationContext(),About.class);
                startActivity(aboutIntent);
                this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initComponent() {
        Arr.clear();
        String path="http://192.168.43.211/ReadCard/index.php/getDataControllers/getKindOfCard";
        new getDataFromWebserver().execute(new String[]{path});
        Arr.add("---Chọn Loại Thẻ---");

     //   ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Arr);

        spinerProduct = (Spinner) findViewById(R.id.cb_loai_card);
        ArrayList arrayList = new ArrayList();
        spinerProduct.setAdapter(new ArrayAdapter<String>(Writer.this, android.R.layout.simple_dropdown_item_1line, Arr));

    //    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     //   spinerProduct.setAdapter(adapter);

        txt_sprice = (EditText)findViewById(R.id.txt_sprice);
        txt_name = (EditText)findViewById(R.id.txt_name);
        lbl_notify = (TextView)findViewById(R.id.lbl_notify);
        setText();
    }

    public void setText(){
        mHandler.post(myRunnable);
    }

    @Override
    protected void onDestroy() {
        service.close();
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
                Log.d("MyLog", "noidung" + content);
                return content;
            }catch(Exception e)
            {
                Log.d("MyLog","loi " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
            //    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
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
            Log.i("MyLog", arg0[1] + "," + arg0[2] + "," + arg0[3] + "," + arg0[4]);
            try {

                UrlEncodedFormEntity entity=new UrlEncodedFormEntity(doiso,"UTF-8");
                httppost.setEntity(entity);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String content = httpclient.execute(httppost, handler);
                Log.i("MyLog", "Thành Công:"+content);
                return content;
            }catch(Exception e)
            {
                Log.i("MyLog","loi " + e.toString());
            }
            return null;
        }

        String result="";
        @Override
        protected void onPostExecute(String result) {
            try {
                // TODO Auto-generated method stub
                super.onPostExecute(result);

                //    JSONObject reader = new JSONObject(result);
                JSONArray sys  = new JSONArray(result);
                for(int i=0;i<sys.length();i++) {
                    JSONObject jsonObject = sys.getJSONObject(i);
                    if(!jsonObject.optString("Result").toString().equals("")) {
                        result = jsonObject.optString("tag").toString();
                        if(result.equals("1")||result.equals("0"))
                            lbl_notify.setTextColor(Color.RED);
                        else
                            lbl_notify.setTextColor(Color.BLUE);
                        lbl_notify.setText(jsonObject.optString("Result").toString());
                        run = true;
                    }
                }

            }catch (Exception e){}
        }
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            lbl_notify.setText("");
        }
    };

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

    class resetText extends Activity implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    if (run) {
                        start = System.currentTimeMillis();
                        run = false;
                    }
                    stop = System.currentTimeMillis();
                    if ((stop - start) >= 2500) {
                        setText();
                        run = true;
                    }
                }
            }catch(Exception ex){
                Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    class BeepTask implements Runnable {

        @Override
        public void run() {
            final int ret = service.setBuzzerStatus((byte) (0));
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (ret == 0) {
                         Toast.makeText(getApplicationContext(),"beep",Toast.LENGTH_LONG).show();
                    } else {
                    }
                }
            });
        }
    }

    class ReadTask implements Runnable {
        byte address=2;
        byte block=0;
        byte length=6;
        String path="http://192.168.43.211/ReadCard/index.php/getDataControllers/setInforCard";

        @Override
        public void run() {
            while(true) {
                IvrJackService.TagBlock result = new IvrJackService.TagBlock();
                int ret = service.readTag(block, address, length, result);

                if (ret == 0) {
                    Log.i("MyLog:", bytesToHex(result.data));
                    new sendDataToWebserver().execute(new String[]{path,spinerProduct.getSelectedItem().toString(), bytesToHex(result.data),txt_name.getText().toString(),txt_sprice.getText().toString()});
//                   try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    //    Log.i("MyLog:", "Read tag failed");
                }
            }
        }
    }
}
