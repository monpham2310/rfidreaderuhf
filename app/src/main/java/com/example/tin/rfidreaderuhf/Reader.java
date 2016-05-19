package com.example.tin.rfidreaderuhf;

import static com.example.tin.rfidreaderuhf.Adapter.Constants.FIRST_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.SECOND_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.THIRD_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.FOURTH_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.FIFTH_COLUMN;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.tin.rfidreaderuhf.Adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class Reader extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lvRfid = (ListView)findViewById(R.id.lvRfid);

        list = new ArrayList<HashMap<String,String>>();

        HashMap<String,String> temp=new HashMap<String, String>();
        temp.put(FIRST_COLUMN, "Ankit Karia");
        temp.put(SECOND_COLUMN, "Male");
        temp.put(THIRD_COLUMN, "22");
        temp.put(FOURTH_COLUMN, "Unmarried");
        temp.put(FIFTH_COLUMN, "Unmarried");
        list.add(temp);

        ListViewAdapter adapter = new ListViewAdapter(this, list);
        lvRfid.setAdapter(adapter);

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
                break;
            case R.id.menu_read:
                Intent readCarIntent = new Intent(getApplicationContext(),Reader.class);
                startActivity(readCarIntent);
                break;
            case R.id.menu_about:
                //    Intent aboutIntent = new Intent(getApplicationContext(),AboutActivity.class);
                //   startActivity(aboutIntent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

