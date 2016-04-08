package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hackathon.fulstack.hackathon_fullstack_app.Manager.DatabaseManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Manager.SessionManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Feed;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SessionManager session;
    Context context;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapterRecycler;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(this);

//        simulate_data();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerGroups);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapterRecycler = new MyRecyclerViewAdapter(getDataSet(-1), this);
        mRecyclerView.setAdapter(mAdapterRecycler);
        context = this;


        if (!session.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        MenuInflater menuInflater = getMenuInflater();

        DatabaseManager.getInstance(this).get_new_feed_all(mRecyclerView);

    }

    private ArrayList<Feed> getDataSet(int id) {
        if (id == -1)
            return DatabaseManager.getInstance(this).get_feeds();
        ArrayList<Feed> c = DatabaseManager.getInstance(this).get_feeds_by_subscription(id);
        return c;
    }


    private void addDrawerItems() {
        Log.i("MainActivity:", "Setting up drawer");
        final Map<String, Long> list = DatabaseManager.getInstance(this).get_preference_names();
        final String pos[] = list.keySet().toArray(new String[list.size()]);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pos);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int _id = (int) (long) list.get(pos[position]);
                DatabaseManager.getInstance(context).get_new_feeds(_id, mRecyclerView);
                mRecyclerView.swapAdapter(new MyRecyclerViewAdapter(getDataSet(_id), context), true);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_bookmark:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Intent intent = new Intent(this, PreferenceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case R.id.menu_save:
                SessionManager.setLoginStatus(false);
                Intent i = new Intent(context, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;

            case R.id.menu_search:
                Toast.makeText(this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void simulate_data() {
        SessionManager.setLoginStatus(true);
        session.setUser("test");
        DatabaseManager.getInstance(this).add_dummy_data();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
}

