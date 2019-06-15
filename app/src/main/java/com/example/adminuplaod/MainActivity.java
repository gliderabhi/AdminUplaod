package com.example.adminuplaod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );


        ListView lst=findViewById( R.id.list_item );
        String names[]={"Celebrities","Cars","Space","Nature","Buildings","Ocean"} ;
        ArrayAdapter<String> adapter=new ArrayAdapter<String>( this,android.R.layout.simple_list_item_1,names);
        lst.setAdapter( adapter );
        lst.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0: category="Celebrities";break;
                    case 1: category="Cars";break;
                    case 2: category="Space";break;
                    case 3: category="Nature";break;
                    case 4: category="Buildings";break;
                    case 5: category="Ocean";break;
                }
                SharedPreferences preferences=getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString( Constants.Category,category );
                editor.apply();
                Intent i=new Intent( getApplicationContext(), FileUpload.class );
                startActivity( i );
            }
        } );
    }
}
