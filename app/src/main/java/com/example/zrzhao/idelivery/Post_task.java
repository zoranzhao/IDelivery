package com.example.zrzhao.idelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Post_task extends Activity {
    private EditText brief;
    private EditText start;
    private EditText dest;
    private Button cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_task);
        brief=(EditText)this.findViewById(R.id.brief);
        start=(EditText)this.findViewById(R.id.start);
        dest=(EditText)this.findViewById(R.id.dest);
        cont=(Button)this.findViewById(R.id.cont);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.start_addr_post=start.getText().toString();
                myapp.end_addr_post=dest.getText().toString();
                myapp.brief_post=brief.getText().toString();
                Intent intent = new Intent(Post_task.this, PostMapsActivity.class);
                intent.putExtra("origin",start.getText().toString());
                intent.putExtra("dest",dest.getText().toString());
                intent.putExtra("brief",brief.getText().toString());

                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_task, menu);
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
