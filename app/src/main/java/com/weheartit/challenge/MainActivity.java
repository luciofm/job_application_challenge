package com.weheartit.challenge;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends Activity {

    ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        list = (ListView)findViewById(android.R.id.list);

        GetGitHubInfoTask getGitHubInfoTask = new GetGitHubInfoTask();
        getGitHubInfoTask.execute();
    }

    class GetGitHubInfoTask extends AsyncTask<Void, Void, ArrayList<CommitModel>> {

        @Override
        protected ArrayList<CommitModel> doInBackground(Void... params) {
            // To up to github and get the most recent rails commits and show them to the screen.
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("https://api.github.com/repos/rails/rails/commits");
            HttpResponse response = null;
            try {
                response = client.execute(get);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ArrayList<CommitModel> model = new ArrayList<>();
            if(response.getStatusLine().getStatusCode() == 200) {
                InputStream stream = null;
                try {
                    stream = response.getEntity().getContent();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String json = new Scanner(stream).useDelimiter("\\A").next();
                model = new Gson().fromJson(json, new TypeToken<ArrayList<CommitModel>>(){}.getType());
            }

            return model;
        }

        protected void onPostExecute(ArrayList<CommitModel> model) {
            list.setAdapter(new CommitAdapter(model));
        }

    }

    public class CommitAdapter extends ArrayAdapter<CommitModel> {

        List<CommitModel> commits;

        public CommitAdapter(List<CommitModel> commits) {
            super(MainActivity.this, android.R.layout.simple_expandable_list_item_2, commits);
            this.commits = commits;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null)
                convertView = LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_expandable_list_item_2, null);

            CommitModel commit = commits.get(position);

            TextView top = (TextView) convertView.findViewById(android.R.id.text1);
            TextView bottom = (TextView) convertView.findViewById(android.R.id.text2);

            top.setText(commit.commit.author.name);
            bottom.setText(commit.commit.message);

            return convertView;
        }
    };

}