package com.botty.wall.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.fragment.About;
import com.botty.wall.fragment.HomeFragment;
import com.botty.wall.fragment.LocalGalleryFragment;

import java.io.File;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private boolean tabletSize;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private boolean SDPermission;

    private final static String START_LOCAL_FRAGMENT = "loacal_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabletSize = getResources().getBoolean(R.bool.isTablet);

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, true);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (settings.getBoolean("nightmode", true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (tabletSize) {
            // do something
            System.out.print("Is tablet");
            // Initializing Drawer Layout and ActionBarToggle
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open, R.string.drawer_close);

            //Setting the actionbarToggle to drawer layout
            drawerLayout.setDrawerListener(actionBarDrawerToggle);

            //calling sync state is necessay or else your hamburger icon wont show up
            actionBarDrawerToggle.syncState();

            DrawerNavUI();

        } else {
            // do something else
            // Initializing Drawer Layout and ActionBarToggle
            ButtomNavUI();
        }

        if (getIntent().getAction() == START_LOCAL_FRAGMENT){
            Fragment fragment = null;
            fragment = new LocalGalleryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_item_local);
            if(tabletSize)
                navigationView.setCheckedItem(R.id.navigation_item_local);
        } else {
            Fragment fragment = null;
            fragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_item_home);
            if(tabletSize)
                navigationView.setCheckedItem(R.id.navigation_item_home);
        }

        try {
            AskForWriteSDPermission();
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e);
        }

    }

    private void AskForWriteSDPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasWriteSDPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteSDPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    SDPermission = true;
                    CreateDirectory();
                    Toast.makeText(Home.this, R.string.can_download_wall, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(Home.this, R.string.cant_download_wall, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void CreateDirectory(){
        File wallpaperDirectory = new File("/sdcard/WallApp/");
        wallpaperDirectory.mkdirs();
    }

    @Override
    public void onBackPressed() {
        if (tabletSize){
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            } else super.onBackPressed();
        }
    }

    public void ButtomNavUI(){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.navigation_item_home:
                        fragment = new HomeFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.navigation_item_local:
                        fragment = new LocalGalleryFragment();
                        FragmentManager ft = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction1 = ft.beginTransaction();
                        fragmentTransaction1.replace(R.id.container_body, fragment);
                        fragmentTransaction1.commit();
                        return true;
                    case R.id.navigation_item_about:
                        fragment = new About();
                        FragmentManager ft2 = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction2 = ft2.beginTransaction();
                        fragmentTransaction2.replace(R.id.container_body, fragment);
                        fragmentTransaction2.commit();
                        return true;
                    default:
                        break;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                }

                return false;
            }
        });
    }

    public void DrawerNavUI(){

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                Fragment fragment = null;
                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.navigation_item_home:
                        fragment = new HomeFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.navigation_item_local:
                        fragment = new LocalGalleryFragment();
                        FragmentManager ft = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction1 = ft.beginTransaction();
                        fragmentTransaction1.replace(R.id.container_body, fragment);
                        fragmentTransaction1.commit();
                        return true;
                    case R.id.navigation_item_setting:
                        Intent iSetting = new Intent(Home.this, Settings.class);
                        startActivity(iSetting);
                        return true;
                    case R.id.navigation_item_about:
                        fragment = new About();
                        FragmentManager ft2 = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction2 = ft2.beginTransaction();
                        fragmentTransaction2.replace(R.id.container_body, fragment);
                        fragmentTransaction2.commit();
                        return true;
                    default:
                        break;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                }

                return false;
            }
        });
    }


}
