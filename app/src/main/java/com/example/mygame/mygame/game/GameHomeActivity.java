package com.example.mygame.mygame.game;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mygame.mygame.R;

import java.io.File;

import model.DBController;
import utils.Constants;

public class GameHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
private DBController db;
    private SharedPreferences sharedPreferences;
    private TextView userName, userEmail;
    private ImageView userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        toolbar.setBackgroundResource(R.color.colorPrimary);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        db = new DBController(this);

        // get payment option
        sharedPreferences = getSharedPreferences(Constants.PAYMENT_OPTION_SHARED_PREFERENCE, MODE_PRIVATE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView =  navigationView.getHeaderView(0);
        userName = (TextView) headerView.findViewById(R.id.userName);
        userEmail = (TextView)headerView.findViewById(R.id.userEmail);
        userImage = (ImageView) headerView.findViewById(R.id.userImage);
        try {
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    Constants.IMAGE_DIRECTORY_NAME);
            // Create the storage directory if it does not exist
            if (mediaStorageDir.exists()) {
                Bitmap b = BitmapFactory.decodeFile(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + db.getCustomer().get(Constants.CUSTOMERS_ID) + Constants.JPG_EXTENSION);
                userImage.setImageBitmap(b);
            }
            else{
                userImage.setImageResource(R.drawable.default_user);
            }
        }
        catch (NullPointerException ex){}

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(GameHomeActivity.this,ProfileActivity.class));
            }
        });
        try {
            if (db.getCustomer() != null) {
                userName.setText(db.getCustomer().get(Constants.CUSTOMER_NAME).toUpperCase());
                userEmail.setText(db.getCustomer().get(Constants.CUSTOMER_EMAIL));
            }
        }catch (NullPointerException ex){}

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action
        } else if (id == R.id.winnings) {

        } else if (id == R.id.transactions) {

        } else if (id == R.id.logout) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
