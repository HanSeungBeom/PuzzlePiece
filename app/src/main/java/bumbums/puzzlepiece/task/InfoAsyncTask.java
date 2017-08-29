package bumbums.puzzlepiece.task;

import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hansb on 2017-08-29.
 */

public class InfoAsyncTask extends AsyncTask<String,String,String> {
    private TextView mText;
    public InfoAsyncTask(TextView tv){
        mText = tv;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strs){
        return fetchContent(strs[0]);
    }

    @Override
    protected void onProgressUpdate(String... params) {

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mText.setText(fromHtml(result));
    }

    public static Spanned fromHtml(String source) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            // noinspection deprecation
            return Html.fromHtml(source);
        }
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
    }

    public String fetchContent(String url) {
        String rtnVal="";
        try {
            URL oracle = new URL(url);
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

}
