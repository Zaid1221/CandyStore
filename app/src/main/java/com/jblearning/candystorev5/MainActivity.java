package com.jblearning.candystorev5;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private DatabaseManager dbManager; //instance of the DBM class
  private double total; //running total of all items selected
  private ScrollView scrollView;
  private int buttonWidth;

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main );
    Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar ); //creates the tool bar on top of app for options
    setSupportActionBar( toolbar ); //sets the toolbar
    dbManager = new DatabaseManager( this );
    total = 0.0;
    scrollView = ( ScrollView ) findViewById( R.id.scrollView );
    Point size = new Point( );
    getWindowManager( ).getDefaultDisplay( ).getSize( size );
    buttonWidth = size.x / 2;
    updateView( );
  }

  protected void onResume( ) //this is called when the user comes back
  {                          //from another activity
    super.onResume( );
    updateView( );
  }

  public void updateView( ) //adds all the candy to the main screen
  {
    ArrayList<Candy> candies = dbManager.selectAll( ); //creates array stores all candy from DB in it
    if( candies.size( ) > 0 )
    {
      scrollView.removeAllViewsInLayout( ); //rebuilds the scrollview

      // set up the grid layout
      GridLayout grid = new GridLayout( this );
      grid.setRowCount( ( candies.size( ) + 1 ) / 2 );
      grid.setColumnCount( 2 );

      // create array of buttons, 2 per row
      CandyButton [] buttons = new CandyButton[candies.size( )];
      ButtonHandler bh = new ButtonHandler( );

      // fill the grid
      int i = 0;
      for ( Candy candy : candies ) {
        // create the button
        buttons[i] = new CandyButton( this, candy );
        buttons[i].setText( candy.getName( )
            + "\n" + candy.getPrice( ) );

        // set up event handling
        buttons[i].setOnClickListener( bh );

        // add the button to grid
        grid.addView( buttons[i], buttonWidth,
            GridLayout.LayoutParams.WRAP_CONTENT );
        i++;
      }
      scrollView.addView( grid );
    }
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
    getMenuInflater( ).inflate( R.menu.menu_main, menu );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {  //This function handles what option was
    int id = item.getItemId( );                          //from the toolbar

    switch ( id )
    {
      case R.id.action_add:  //ADD
      {
        Intent insertIntent = new Intent(this, InsertActivity.class);
        this.startActivity(insertIntent);
        return true;
      }

      case R.id.action_delete: //DELETE
      {
        Intent deleteIntent = new Intent(this, DeleteActivity.class);
        this.startActivity(deleteIntent);
        return true;
      }

      case R.id.action_update:  //UPDATE
      {
        Intent updateIntent = new Intent(this, UpdateActivity.class);
        this.startActivity(updateIntent);
        return true;
      }

      case R.id.action_reset:  //RESET
      {
        total = 0.0;
        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  private class ButtonHandler implements View.OnClickListener
  {
    public void onClick( View v )
    {
      // retrieve price of the candy and add it to total
      total += ( ( CandyButton ) v ).getPrice( );
      String pay =
        NumberFormat.getCurrencyInstance( ).format( total );
      Toast.makeText( MainActivity.this, pay,
          Toast.LENGTH_LONG ).show( );
    }
  }
}
