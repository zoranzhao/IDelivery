package com.example.zrzhao.idelivery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class viewTasksActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button Back;
    Button Delete;



    ArrayList<LatLng> markerPoints;
    ArrayList<String> pointsList;
    LatLng startPoint = null;
    LatLng endPoint = null;
    ArrayList<Integer> CheckboxID;
    String CheckedID;

 /*   AsyncHttpClient client = new AsyncHttpClient();
    RequestParams params = new RequestParams();


    params.put("task_id", CheckedID);// record the checked thing
    params.put("userid", myapp.userid);
    params.put("useremail", myapp.useremail);
    params.put("username", myapp.username);


    client.post(myapp.MyUrl + "/taskDelete", params, new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            Log.w("In successful uploader", myapp.MyUrl + "/taskDelete");

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            Log.w("In failure uploader", myapp.MyUrl + "/taskDelete");

        }
    });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);
        //Confirm = (Button)findViewById(R.id.confirm);
        //btnDraw = (Button)findViewById(R.id.btn_draw);
        //Back = (Button)findViewById(R.id.back);

        //Log.w("Start from God", "" + myapp.start_addr_post);
        //Log.w("Dest from God",""+myapp.end_addr_post);
        CheckboxID =new ArrayList<Integer>();
        markerPoints = new ArrayList<LatLng>();
        pointsList = new ArrayList<String>();
        pointsList.add(getIntent().getStringExtra("origin"));
        pointsList.add(getIntent().getStringExtra("dest"));
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }







    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        Back=(Button)this.findViewById(R.id.back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( viewTasksActivity.this, Dashboard.class);
                startActivity(intent);
                finish();

            }
        });
        Delete=(Button)this.findViewById(R.id.delete);

        Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {




                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();

                params.put("task_id", CheckedID);// record the checked thing
                params.put("userid", myapp.userid);
                params.put("useremail", myapp.useremail);
                params.put("username", myapp.username);


                client.post(myapp.MyUrl + "/taskDelete", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        Log.w("In successful uploader", myapp.MyUrl + "/taskDelete");

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.w("In failure uploader", myapp.MyUrl + "/taskDelete");

                    }
                });




                Intent intent = new Intent( viewTasksActivity.this, Dashboard.class);
                startActivity(intent);
                finish();

            }
        });

        AsyncHttpClient httpClient = new AsyncHttpClient();

        DeliveryTasks downloadTasks = new DeliveryTasks();
        downloadTasks.execute(myapp.MyUrl+"/getMyTask?userid="+myapp.userid);
        Log.w("",myapp.MyUrl+"/getMyTask?userid="+myapp.userid);

        mMap.setMyLocationEnabled(true);

    }





    static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
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

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String getDirectionsUrl(String origin,String dest){

        // Origin of route
        String str_origin = "origin="+urlEncode(origin);

        // Destination of route
        String str_dest = "destination="+urlEncode(dest);

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=0;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==0)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }


        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.w("The routing URL:", ""+url);

        return url;
    }

    // Fetches data from url passed
    private class DeliveryTasks extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                Log.w("Downloading the task", url[0]);
                data = downloadUrl(url[0]);
                //Log.w("Downloading the task", data);

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
            Log.w("Downloading the task", result);
            ViewGroup hourButtonLayout = (ViewGroup) findViewById(R.id.hour_radio_group);  // This is the id of the RadioGroup we defined
            RadioButton button;
            try {
                JSONObject jObject = new JSONObject(result);
                //Log.w("", "" + jObject.getJSONArray("tasks").get(0));
                Log.w("",""+jObject.getJSONArray("tasks"));

                int id;
                for(int index = 0; index<jObject.getJSONArray("tasks").length(); index++) {
                    id = View.generateViewId();
                    final JSONObject Task;
                    Task = (JSONObject)jObject.getJSONArray("tasks").get(index);

                    button = new RadioButton(getApplicationContext());
                    button.setId(id);
                    //button.setText(Integer.toString(id)+" "+Task.getString("start")+" "+Task.getString("end"));
                    button.setText("Task Start From:  "+Task.getString("start")+"\nIDelivery To:  "+Task.getString("end")
                                    +"\nDescription:  "+Task.getString("description")
                    );


                    button.setTextColor(Color.BLACK);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = ((RadioButton) v).isChecked();
                            //Log.w("dsa", ""+v.getId());
                            try {
                                mMap.clear();
                                String origin = Task.getString("start");
                                String dest = Task.getString("end");
                                CheckedID = Task.getString("task_id");
                                //mMap.clear();
                                //markerPoints.clear();
                                //markerPoints.add(new LatLng(Task.getDouble("startLat"), Task.getDouble("startLng")));
                                //markerPoints.add(new LatLng(Task.getDouble("endLat"), Task.getDouble("endLng")));
                                //MarkerOptions startOptions = new MarkerOptions();
                               // MarkerOptions endOptions = new MarkerOptions();

                               // startOptions.position(markerPoints.get(0));
                               // endOptions.position(markerPoints.get(1));
                                //startOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                               // endOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                                //mMap.addMarker(startOptions);
                                //mMap.addMarker(endOptions);

                                String url = getDirectionsUrl(origin, dest);
                                DownloadTask downloadTask = new DownloadTask();
                                downloadTask.execute(url);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }





                        }






                    });
                    //if (id == 1) button.setChecked(true); // Only select button with same index as currently selected number of hours
                    //else button.setChecked(false);
                    //button.setBackgroundResource(R.drawable.item_selector); // This is a custom button drawable, defined in XML
                    hourButtonLayout.addView(button);
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    /** A method to download json data from url */
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

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                getTaskJSONParser parser = new getTaskJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }


            /*The return format of the Json parser is a list of points list (for each route leg)*/
            /*for(int i=0;i<routes.size();i++) {
                Log.w("After Json Parser:","element"+i );
                for(int j=0; j<routes.get(i).size();j++ )
                    Log.w("The hashmap will be",""+routes.get(i).get(j));
                Log.w("Before return Json Parser:","element"+i );
            }*/
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            //The last element in the list is a list with single element, the duration of the routing

            List<HashMap<String, String>> origDestList = result.get(result.size()-1);
            result.remove(result.size() - 1);
            Log.w("origDestList :", ""+origDestList.get(0));


            MarkerOptions startOptions = new MarkerOptions();
            MarkerOptions endOptions = new MarkerOptions();
            startPoint = new LatLng(Double.parseDouble(origDestList.get(0).get("lat_start")),  Double.parseDouble(origDestList.get(0).get("lng_start")));
            endPoint = new LatLng(Double.parseDouble(origDestList.get(0).get("lat_end")),  Double.parseDouble(origDestList.get(0).get("lng_end")));


            startOptions.position(new LatLng(Double.parseDouble(origDestList.get(0).get("lat_start")),  Double.parseDouble(origDestList.get(0).get("lng_start")) ));
            endOptions.position(new LatLng(Double.parseDouble(origDestList.get(0).get("lat_end")),  Double.parseDouble(origDestList.get(0).get("lng_end")) ));

            startOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            endOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mMap.addMarker(startOptions);
            mMap.addMarker(endOptions);





            List<HashMap<String, String>> durationList = result.get(result.size()-1);
            result.remove(result.size()-1);

            // Traversing through all the legs of the returning route
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                Log.w("ParserTask Function, the number of routing legs are:", ""+result.size());
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }
            Log.w("The duration of the path is: ",""+durationList.get(0).get("duration"));
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
            CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(origDestList.get(0).get("lat_start")), Double.parseDouble(origDestList.get(0).get("lng_start"))));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_maps, menu);
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
