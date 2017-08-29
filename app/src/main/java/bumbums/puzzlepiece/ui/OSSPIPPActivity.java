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
import bumbums.puzzlepiece.task.InfoAsyncTask;

public class OSSPIPPActivity extends AppCompatActivity {
    private TextView mOss,mPipp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oss_pipp);
        mOss = (TextView) findViewById(R.id.tv_oss);
        mPipp = (TextView) findViewById(R.id.tv_personal_policy);
        InfoAsyncTask oss = new InfoAsyncTask(mOss);
        InfoAsyncTask pipp = new InfoAsyncTask(mPipp);
        oss.execute(getString(R.string.OSS_page));
        pipp.execute(getString(R.string.PIPP_page));
    }

}
