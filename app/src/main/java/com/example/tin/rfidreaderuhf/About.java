package com.example.tin.rfidreaderuhf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

}
