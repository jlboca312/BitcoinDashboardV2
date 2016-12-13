package edu.temple.bitcoindashboardv2;


import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //global variables
    int isTwoPanes;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nvDrawer;
    public FragmentManager fm;

    //fragment declarations
    BitcoinPriceFragment bcPriceFrag;
    ChartFragment chartFrag;
    BlockInfoFragment blockFrag;
    AddressFragment addressFrag;

    String[] menuTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set a Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //determine if two panes, if returned 1 then PORTRAIT, if 2, then LANDSCAPE
        isTwoPanes = getResources().getConfiguration().orientation;


        //if in portrait mode, use the hamburger menu
        if(isTwoPanes == 1){
            /* DEALS WITH THE HAMBURGER MENU*/
            //connect the widgets to variables
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //connect drawer view to variable
            nvDrawer = (NavigationView) findViewById(R.id.navigationView);

            if (savedInstanceState == null) {
                //default load the price ber bitcoin fragment
                fm = getFragmentManager();
                fm.beginTransaction().add(R.id.fragmentContainer, new BitcoinPriceFragment()).commit();
            }

            //perform actions upon clicking item on the navigation slide out
            setupDrawerContent(nvDrawer);

        }else{ // else use a list view and the fragment is next to it

            //set the layout to landscape
            setContentView(R.layout.activity_main_landscape);

            //get ListView id and connect to variable
            ListView listView = (ListView) findViewById(R.id.listView);

            //get string array from resources
            menuTitles = getResources().getStringArray(R.array.titles);

            //create an array adapter for the title array
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, menuTitles);

            //set the adapter
            listView.setAdapter(adapter);

            //default load the price ber bitcoin fragment and listview fragment
            fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.landscapeContainerFrag, new BitcoinPriceFragment()).commit();


            //deal with the clicking of an item on the list view
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch(position){
                        case 0:
                            //create class instance
                            bcPriceFrag = new BitcoinPriceFragment();

                            //insert fragment by replacing any existing fragment
                            fm = getFragmentManager();
                            fm.beginTransaction().replace(R.id.landscapeContainerFrag, bcPriceFrag).commit();
                            break;
                        case 1:
                            //crate class instance
                            chartFrag = new ChartFragment();

                            //insert fragment by replacing any existing fragment
                            fm = getFragmentManager();
                            fm.beginTransaction().replace(R.id.landscapeContainerFrag, chartFrag).commit();
                            break;
                        case 2:
                            //create class instance
                            blockFrag = new BlockInfoFragment();

                            //insert fragment by replacing any existing fragment
                            fm = getFragmentManager();
                            fm.beginTransaction().replace(R.id.landscapeContainerFrag, blockFrag).commit();
                            break;
                        case 3:
                            //create class instance
                            addressFrag = new AddressFragment();

                            //insert fragment by replacing any existing fragment
                            fm = getFragmentManager();
                            fm.beginTransaction().replace(R.id.landscapeContainerFrag, addressFrag).commit();
                            break;
                        default:
                            break;
                    }
                }
            });



        }


    }

    //sets up the listener for the drawer list
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch (menuItem.getItemId()) {
            case R.id.bc_price:
                //create class instance
                bcPriceFrag = new BitcoinPriceFragment();

                //insert fragment by replacing any existing fragment
                fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.fragmentContainer, bcPriceFrag).commit();
                break;
            case R.id.bc_chart:
                //crate class instance
                chartFrag = new ChartFragment();

                //insert fragment by replacing any existing fragment
                fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.fragmentContainer, chartFrag).commit();

                break;
            case R.id.block_info:
                //create class instance
                blockFrag = new BlockInfoFragment();

                //insert fragment by replacing any existing fragment
                fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.fragmentContainer, blockFrag).commit();

                break;
            case R.id.bc_curr_balance:
                //create class instance
                addressFrag = new AddressFragment();

                //insert fragment by replacing any existing fragment
                fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.fragmentContainer, addressFrag).commit();

                break;
            default:
                break;

        }



        //highlight selected item as been done by navigationView
        menuItem.setChecked(true);
        //close navigation drawer
        mDrawerLayout.closeDrawers();


    }

    //deals with clicking an item in the listview
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //enables the hamburger button to pull slide menu out
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

