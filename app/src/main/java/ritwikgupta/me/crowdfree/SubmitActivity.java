package ritwikgupta.me.crowdfree;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;


public class SubmitActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        setTitle("Submit");

        final EditText edit = (EditText) findViewById(R.id.submit_edit);
        Button submit = (Button) findViewById(R.id.submit_submit);
        final String place = getIntent().getStringExtra("place");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(edit.getText().toString());
                if(number < 1 || number > 3){
                    Toast.makeText(getBaseContext(), "Invalid number, please use 1-3", Toast.LENGTH_LONG).show();
                    edit.setText("");
                } else {
                    new QueryServerPOST().execute("http://162.243.206.135:4567/upload/" + place, "" + number);
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
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

    private class QueryServerPOST extends AsyncTask<String, Void, Void> {
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
