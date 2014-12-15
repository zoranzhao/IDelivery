package com.example.zrzhao.idelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Get_task extends Activity {
    private EditText start;
    private EditText dest;
    private Button find;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_task);
        start=(EditText)findViewById(R.id.get_start);
        dest=(EditText)findViewById(R.id.get_dest);
        find=(Button)findViewById(R.id.find);



        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.start_addr_post=start.getText().toString();
                myapp.end_addr_post=dest.getText().toString();
                Intent intent = new Intent(Get_task.this, GetMapsActivity.class);
                intent.putExtra("origin",start.getText().toString());
                intent.putExtra("dest",dest.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.get_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
