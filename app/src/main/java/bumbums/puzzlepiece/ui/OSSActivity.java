package bumbums.puzzlepiece.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import bumbums.puzzlepiece.R;

public class OSSActivity extends AppCompatActivity {
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oss);
        mText = (TextView) findViewById(R.id.tv_oss);
        MyAsyncTask mst = new MyAsyncTask();
        mst.execute("");
    }
    public String fetchContent() {
        String rtnVal="";
        try {
            URL oracle = new URL(getString(R.string.OSS_page));
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                rtnVal += inputLine;
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return rtnVal;
    }
    public static Spanned fromHtml(String source) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            // noinspection deprecation
            return Html.fromHtml(source);
        }
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
    }

    public class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strs){
            return fetchContent();
          }

        @Override
        protected void onProgressUpdate(String... params) {

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mText.setText(fromHtml(result));
        }
}



}
