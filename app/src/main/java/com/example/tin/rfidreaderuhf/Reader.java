package com.example.tin.rfidreaderuhf;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.tin.rfidreaderuhf.Adapter.Constants.FIRST_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.SECOND_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.SIXTH_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.THIRD_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.FOURTH_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.FIFTH_COLUMN;

import com.example.tin.rfidreaderuhf.Adapter.ListViewAdapter;
import com.xminnov.ivrjack.ru01.IvrJackAdapter;
import com.xminnov.ivrjack.ru01.IvrJackService;
import com.xminnov.ivrjack.ru01.IvrJackStatus;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Reader extends AppCompatActivity implements IvrJackAdapter {

    public Toolbar toolbar;
    public static IvrJackService service;
    ReadTask task;
    private ArrayList<HashMap<String, String>> list;
    private ArrayList<String> listRFID;
    ListView lvRfid;
    TextView txtTotalAmount,txtTotalPrice;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvRfid = (ListView)findViewById(R.id.lvRfid);
        txtTotalAmount = (TextView)findViewById(R.id.tvTotalAmount);
        txtTotalPrice = (TextView)findViewById(R.id.tvTotalPrice);

        list = new ArrayList<HashMap<String,String>>();
        listRFID = new ArrayList<String>();

//        putInListView(1, "Cơm", "Loại 1", 2, 12000, "E30004");
//        putInListView(2, "Cơm", "Loại 1", 2, 12000, "E30004");
//        putInListView(3, "Cơm", "Loại 1", 2, 12000, "E30003");
//        putInListView(4, "Canh", "Loại 1", 2, 5000, "E30005");
//        putInListView(5, "Nước", "Loại 1", 2, 2000, "E30006");

        //calTotalPrice();

        service = new IvrJackService(this, this);
        service.open();
        task = new ReadTask();
        new Thread(task).start();
    }

    private void calTotalPrice(){
        int totalAmount = 0;
        long totalPrice = 0;
        for(int i=0; i<list.size(); i++){
            HashMap<String, String> map = list.get(i);
            totalAmount += Integer.parseInt(map.get(FOURTH_COLUMN));
            totalPrice += Long.parseLong(map.get(FIFTH_COLUMN));
        }
        txtTotalAmount.setText(Integer.toString(totalAmount));
        txtTotalPrice.setText(Long.toString(totalPrice));
    }

    private HashMap<String, String> putInHashMap(int stt, String name, String type, int amount, long price, String rfid){
        HashMap<String, String> temp = new HashMap<String, String>();
        temp.put(FIRST_COLUMN, Integer.toString(stt));
        temp.put(SECOND_COLUMN, name);
        temp.put(THIRD_COLUMN, type);
        temp.put(FOURTH_COLUMN, Integer.toString(amount));
        temp.put(FIFTH_COLUMN, Long.toString(price*amount));
        temp.put(SIXTH_COLUMN, rfid);

        return temp;
    }

    private void putInListView(String name, String type, int amount, long price, String rfid){
        int amountCurr = 0, sttCurr = 0;
        String nameCurr = "", typeCurr = "", rfidCurr = "";
        long priceCurr = 0;

        HashMap<String, String> mapTmp = new HashMap<String, String>();
        HashMap<String,String> item;

        if(list.size() > 0) {
            item = new HashMap<String, String>();
            for (int i = 0; i < list.size(); i++) {
                mapTmp = list.get(i);
                if (mapTmp.get(SIXTH_COLUMN).equals(rfid) && mapTmp.get(SECOND_COLUMN).equals(name)) {
                    continue;
                }
                else if(listRFID.indexOf(rfid) == -1) {
                    if (!mapTmp.get(SIXTH_COLUMN).equals(rfid) && mapTmp.get(SECOND_COLUMN).equals(name)) {

                        sttCurr = Integer.parseInt(mapTmp.get(FIRST_COLUMN));
                        nameCurr = mapTmp.get(SECOND_COLUMN);
                        typeCurr = mapTmp.get(THIRD_COLUMN);
                        amountCurr = Integer.parseInt(mapTmp.get(FOURTH_COLUMN)) + amount;
                        priceCurr = Long.parseLong(mapTmp.get(FIFTH_COLUMN));
                        rfidCurr = mapTmp.get(SIXTH_COLUMN);
                        item = putInHashMap(sttCurr, nameCurr, typeCurr, amountCurr, priceCurr, rfidCurr);

                        list.set(i, item);
                        listRFID.add(rfid);
                    }
                    else {
                        item = putInHashMap(index++, name, type, amount, price, rfid);
                        list.add(item);
                        listRFID.add(rfid);
                    }
                }
            }
        }
        else{
            index = 1;
            item = putInHashMap(index++, name, type, amount, price, rfid);
            list.add(item);
            listRFID.add(rfid);
        }

        ListViewAdapter adapter = new ListViewAdapter(this, list);
        lvRfid.setAdapter(adapter);

        calTotalPrice();
    }

    private void clearListView(){
        list.clear();
        listRFID.clear();

        index = 0;

        ListViewAdapter adapter = new ListViewAdapter(this, list);
        lvRfid.setAdapter(adapter);
    }

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

    @Override
    protected void onDestroy() {
        service.close();
        super.onDestroy();
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

    class sendDataToWebserver extends AsyncTask<String,Void,String>
    {
        String name="",kindname="",key="";
        long price=0;
        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            HttpClient httpclient= new DefaultHttpClient();
            HttpPost httppost=new HttpPost(arg0[0]);
            List<NameValuePair> doiso=new ArrayList<NameValuePair>(1);
            doiso.add(new BasicNameValuePair("CardKey", arg0[1]));
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

        @Override
        protected void onPostExecute(String result) {
            try {
                // TODO Auto-generated method stub
                super.onPostExecute(result);

                name="";
                kindname="";
                price=0;
                key="";
                //    JSONObject reader = new JSONObject(result);
                JSONArray sys = new JSONArray(result);
                for (int i = 0; i < sys.length(); i++) {
                    JSONObject jsonObject = sys.getJSONObject(i);
                    if(jsonObject.optString("Result").toString().equals("1"))
                    {
                        if (!jsonObject.optString("CardName").toString().equals("")) {
                            name = jsonObject.optString("CardName").toString();
                        }

                        if (!jsonObject.optString("price").toString().equals("")) {
                            price = Long.parseLong(jsonObject.optString("price").toString());
                        }

                        if (!jsonObject.optString("KindName").toString().equals("")) {
                            kindname = jsonObject.optString("KindName").toString();
                        }

                        if (!jsonObject.optString("CardKey").toString().equals("")) {
                            key = jsonObject.optString("CardKey").toString();
                        }
                        Log.i("MyLog", name + "," + price + "," + kindname + "," + key);
                        putInListView(name, kindname, 1, price, key);
                        //Call function add to listview
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Mã thẻ không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){}
        }
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

    class ReadTask implements Runnable {
        byte address=2;
        byte block=0;
        byte length=6;
        String path="http://192.168.43.211/ReadCard/index.php/getDataControllers/getInforCard";

        @Override
        public void run() {
            service.setBuzzerStatus((byte)1);
            while(true) {
                IvrJackService.TagBlock result = new IvrJackService.TagBlock();
                int ret = service.readTag(block, address, length, result);
                if (ret == 0) {
                    Log.i("MyLog:", bytesToHex(result.data));
                    new sendDataToWebserver().execute(new String[]{path, bytesToHex(result.data)});
                   // try {
                 //      Thread.sleep(500);
                //    } catch (InterruptedException e) {
                 //       e.printStackTrace();
                //    }
                } else {
                    //    Log.i("MyLog:", "Read tag failed");
                }
            }
        }
    }
}
