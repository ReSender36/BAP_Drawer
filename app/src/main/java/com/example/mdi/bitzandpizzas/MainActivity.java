package com.example.mdi.bitzandpizzas;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.FrameLayout ;

import layout.PastaFragment;
import layout.PizzaFragment;
import layout.StoresFragment;

public class MainActivity extends Activity {

    private ShareActionProvider shareActionProvider ;
    private String[] titles ;
    private ListView drawerList ;
    private ActionBarDrawerToggle drawerToggle ;
    private DrawerLayout drawerLayout ;
    private int currentPosition = 0 ;

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position) ;
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titles = getResources().getStringArray(R.array.titles) ;
        drawerList = (ListView)findViewById(R.id.drawer) ;
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout) ;

        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener()) ;

        if (null == savedInstanceState)
            selectItem(0) ;
        else{
            currentPosition = savedInstanceState.getInt("position") ;
            setActionBarTitle(currentPosition);
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        } ;

        drawerLayout.setDrawerListener(drawerToggle) ;

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        FragmentManager fragMan = getFragmentManager() ;
                        Fragment fragment = fragMan.findFragmentByTag("visible_fragment") ;
                        if (fragment instanceof TopFragment)
                            currentPosition = 0 ;

                        if (fragment instanceof PizzaFragment)
                            currentPosition = 1 ;

                        if (fragment instanceof PastaFragment)
                            currentPosition = 2 ;

                        if (fragment instanceof StoresFragment)
                            currentPosition = 3 ;

                        setActionBarTitle(currentPosition);
                        drawerList.setItemChecked(currentPosition, true);
                    }
                }
        );
    }

    private void selectItem(int position){
        // обновить инфу замной фрагментов
        currentPosition = position ;
        Fragment fragment ;
        switch(position){
            case 1:
                fragment = new PizzaFragment() ;
                break ;
            case 2:
                fragment = new PastaFragment() ;
                break ;
            case 3:
                fragment = new StoresFragment() ;
                break ;
            default:
                fragment = new TopFragment() ;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction() ;
        ft.replace(R.id.content_frame,fragment,"visible_fragment") ;
        ft.addToBackStack(null) ;
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) ;
        ft.commit() ;
        // назначение заголовка панели действий
        setActionBarTitle(position);
        // закрытие выдвижной панели
        drawerLayout.closeDrawer(drawerList);
    }

    private void setActionBarTitle(int position){
        String title ;
        if (0 == position) {
            title = getResources().getString(R.string.app_name) ;
        } else {
            title = titles[position] ;
        }
        getActionBar().setTitle(title) ;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // синхронизировать состояние выключателя после onRestoreInstanceState
        drawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // если выдвижная панель открыта, скрыть элементы, связанные с контентом (пункт менню "Передать")
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList) ;
        menu.findItem(R.id.action_share).setVisible(!drawerOpen) ;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // заполнение меню
        // элементы (если есть) добавляются на панель действий
        getMenuInflater().inflate(R.menu.menu_main, menu) ;
        MenuItem menuItem = menu.findItem(R.id.action_share) ;
        shareActionProvider = (ShareActionProvider) menuItem.getActionProvider() ;
        setIntent("это простой текст");
        return super.onCreateOptionsMenu(menu);
    }

    private void setIntent(String text){
        Intent intent = new Intent(Intent.ACTION_SEND) ;
        intent.setType("text/plain") ;
        intent.putExtra(Intent.EXTRA_TEXT, text) ;
        shareActionProvider.setShareIntent(intent) ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true ;

        switch (item.getItemId()) {
            case R.id.action_create_order:
                // код, выполняемый при клике на "Создание заказа"
                Intent  intent = new Intent(this, OrderActivity.class) ;
                startActivity(intent);
                return true;
            case R.id.action_settings:
                // код, выполняемый при выборе элемента Настройки
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
