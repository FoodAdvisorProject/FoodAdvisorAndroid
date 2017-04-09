package pro.rane.foodadvisor;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView  navigationView = null;
    Toolbar toolbar = null;
    pro.rane.foodadvisor.SessionManager session;

    de.hdodenhof.circleimageview.CircleImageView profile_photo;
    TextView username;
    TextView email;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        MainFragment fragment = new MainFragment();
        // Session class instance
        session = new pro.rane.foodadvisor.SessionManager(getApplicationContext());
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();

        //disabilitata temporaneamente da usare solo durante il debug dell'attività
        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn() + "\n" + "Data: "+session.getUserDetails().toString(), Toast.LENGTH_LONG).show();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        username = (TextView)  hView.findViewById(R.id.username);
        email = (TextView)  hView.findViewById(R.id.email);

        HashMap<String, String> user = session.getUserDetails();
        username.setText(user.get(SessionManager.KEY_AZIENDA));
        email.setText(user.get(SessionManager.KEY_EMAIL));

        profile_photo = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);


        final ImageLoader imageLoader = ImageLoader.getInstance();
        // TODO: 08/04/2017 photohandling
    }


    @Override
    public void onResume(){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toasty.warning(this, "GPS non attivo", Toast.LENGTH_SHORT, true).show();
            super.onResume();
       }else {
            super.onResume();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MainFragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            ScanFragment fragment = new ScanFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        }  else if (id == R.id.nav_user_mode) {
            UserFragment fragment = new UserFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_gallery) {
            MyProductsFragment fragment = new MyProductsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_new_product){
            NewProductFragment fragment = new NewProductFragment();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.profile){
            ProfileFragment fragment = new ProfileFragment();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_manage) {
            SettingFragment fragment = new SettingFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_share) {
            CreditsFragment fragment = new CreditsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_bug_report){
            ReportBugFragment fragment = new ReportBugFragment();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void logout(View view){
        session.logoutUser();
        finish();
    }
}
