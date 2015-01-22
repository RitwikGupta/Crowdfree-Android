package ritwikgupta.me.crowdfree;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class CrowdActivity extends ActionBarActivity {

    String place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd);

        place = getIntent().getStringExtra("place");

        setTitle(place.toUpperCase());

        TextView level = (TextView) findViewById(R.id.crowd_leveltext);
        Button submit = (Button) findViewById(R.id.crowd_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rand = new Random();
                int randInt = rand.nextInt((3-1) + 1) + 1;
                new QueryServerPOST().execute("http://192.168.1.160:4567/upload/" + place, "" + randInt);
            }
        });

        try {
            Integer levelStress = Integer.parseInt(new QueryServerGET().execute("http://192.168.1.160:4567/" + place).get().substring(0,1));
            if(levelStress == 1){
                level.setText("LOW");
            } else if(levelStress == 2){
                level.setText("MEDIUM");
            } else {
                level.setText("HIGH");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crowd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class QueryServerGET extends AsyncTask<String, Void, String>{
        String page;
        protected String doInBackground(String... urls){
            try{
                page = downloadUrl(urls[0]);
            } catch (IOException e){
                page = "<>";
                e.printStackTrace();
                return "Unable to retrieve webpage";
            }
            return page;
        }

        protected void onProgressUpdate(Void... voids){

        }

        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(), page, Toast.LENGTH_LONG).show();
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("DEBUG", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                return readIt(is, len);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }

    private class QueryServerPOST extends AsyncTask<String, Void, Void>{
        protected Void doInBackground(String... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);

            try {
                httppost.setEntity(new StringEntity(params[1]));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
