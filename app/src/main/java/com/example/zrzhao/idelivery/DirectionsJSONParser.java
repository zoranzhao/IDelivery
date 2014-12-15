package com.example.zrzhao.idelivery;

/**
 * Created by zrzhao on 12/9/14.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            //for(int i=0;i<jRoutes.length();i++){
                Log.w("In route "+0, " :");
                jLegs = ( (JSONObject)jRoutes.get(0)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();
                Integer duration = new Integer(0);


            /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    Log.w("The duration is: ",""+( (JSONObject)jLegs.get(j)).getJSONObject("duration")  );
                    Log.w("The duration is: ",""+( (JSONObject)jLegs.get(j)).getJSONObject("duration").getLong("value"));
                    Log.w("The duration is: ",""+( (JSONObject)jLegs.get(j)).getJSONObject("duration").getString("text"));

                    Log.w("The duration is: ",""+( (JSONObject)jLegs.get(j)).getJSONObject("distance")  );
                    duration+=( (JSONObject)jLegs.get(j)).getJSONObject("duration").getInt("value");


                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            //In the end of the list append the travelling duration, starting point
            //
            List durationList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("duration", ""+duration);
            durationList.add(hm);
            routes.add(durationList);

            List origDestList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> origDest = new HashMap<String, String>();

            //There is only one leg for the post task activity
            origDest.put("lng_start", ""+((JSONObject)jLegs.get(0)).getJSONObject("start_location").getString("lng")   );
            origDest.put("lat_start", ""+((JSONObject)jLegs.get(0)).getJSONObject("start_location").getString("lat")   );
            origDest.put("lng_end", ""+((JSONObject)jLegs.get(jLegs.length()-1)).getJSONObject("end_location").getString("lng")   );
            origDest.put("lat_end", ""+((JSONObject)jLegs.get(jLegs.length()-1)).getJSONObject("end_location").getString("lat")   );

            origDestList.add(origDest);
            routes.add(origDestList);




            //}

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return routes;
    }




    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}

