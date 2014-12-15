package com.example.zrzhao.idelivery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Dashboard extends Activity implements
        ResultCallback<People.LoadPeopleResult>{
    private TextView info;
    private ImageView imageView;
    private Button postbutton;
    private Button getbutton;
    private Button outbutton;
    private Button viewTasks;
    private TextView notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        info=(TextView)this.findViewById(R.id.infotext);
        imageView=(ImageView)this.findViewById(R.id.infoimg);
        if (Plus.PeopleApi.getCurrentPerson(myapp.mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(myapp.mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            myapp.username=personName;
            myapp.userid=currentPerson.getId();
            myapp.useremail=Plus.AccountApi.getAccountName(myapp.mGoogleApiClient);
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(myapp.mGoogleApiClient);
            info.setText("Hello "+personName);
            String personPhoto = currentPerson.getImage().getUrl();
            new DownloadImageTask(imageView)
                    .execute(personPhoto);
        }

        viewTasks=(Button)this.findViewById(R.id.view_tasks);
        viewTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, viewTasksActivity.class);
                startActivity(intent);
                finish();
            }


        });

        outbutton=(Button)this.findViewById(R.id.logout);
        outbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (myapp.mGoogleApiClient.isConnected()) {
                     Plus.AccountApi.clearDefaultAccount(myapp.mGoogleApiClient);
                     Plus.AccountApi.revokeAccessAndDisconnect(myapp.mGoogleApiClient);
                 }
                 finish();
             }
        });
        postbutton=(Button)this.findViewById(R.id.post);
        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Post_task.class);
                startActivity(intent);
                finish();
            }


        });
        getbutton=(Button)this.findViewById(R.id.get);
        getbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Get_task.class);
                startActivity(intent);
                finish();
            }


        });

        notification=(TextView)this.findViewById(R.id.notification);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(myapp.MyUrl+ "/getUserMessage?userid="+myapp.userid);




        Plus.PeopleApi.loadVisible(myapp.mGoogleApiClient, null)
                .setResultCallback(this);













    }



    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String []sa=result.split("\\(\\)");
            String re="";
            for(int i=0; i<sa.length; i++){
                re+="\n"+sa[i]+"\n";
            }


            //ScrollView scrollView = (ScrollView) findViewById(R.id.notifications);  // This is the id of the RadioGroup we defined
           // TextView newText = new TextView(getApplicationContext());
            //newText.setText(re);
           // scrollView.addView(newText);
            notification.setText(re);
            //ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            //parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            Log.w("Test while downloading url","1");

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();
            Log.w("Test while downloading url", "2");

            // Reading data from url
            iStream = urlConnection.getInputStream();
            Log.w("Test while downloading url","3");

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
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
    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                myapp.myfriends=new String[count];
                for (int i = 0; i < count; i++) {
                    Log.w("Display name: ", " "+personBuffer.get(i).getDisplayName());
                    Log.w("Display name: ", " "+personBuffer.get(i).getId());
                    myapp.myfriends[i]=personBuffer.get(i).getId();
                }
            } finally {
                personBuffer.close();
            }
        }



        final String MyUrl="http://instant-amp-758.appspot.com";

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("username", myapp.username);
        params.put("circle", new Gson().toJson(myapp.myfriends));
        params.put("userid", myapp.userid);
        params.put("useremail", myapp.useremail);




        client.post(myapp.MyUrl + "/userUpdate", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("In successful uploader", myapp.MyUrl + "/userUpdate");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.w("In failure uploader", myapp.MyUrl + "/userUpdate");

            }
        });




    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
