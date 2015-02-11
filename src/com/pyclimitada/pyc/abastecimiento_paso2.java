package com.pyclimitada.pyc;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class abastecimiento_paso2 extends Activity implements SearchView.OnQueryTextListener {
	
	 private SearchView mSearchView;
	 private ListView mListView;
	 baseDatos usuarios;
	 ArrayList<String> values, ids;
	 Bundle datosAnterior;
	 String nombreCompleto, idUser, equipo, idEquipo,nbombas;
	 String forma, tipo, dispositivo;
	 TextView nombreUser,patente;
	 
	
	protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.abastecimiento_paso2);
	        
	        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
	        patente = (TextView)this.findViewById(R.id.patente);
	        
	        usuarios = new baseDatos(this, "DBUsuarios", null, 1);

	        //Información traida del anterior
	        datosAnterior = this.getIntent().getExtras();
	        nombreCompleto = datosAnterior.getString("nombreCompleto");
	        idUser = datosAnterior.getString("idUser");
	        equipo = datosAnterior.getString("equipo");
	        idEquipo = datosAnterior.getString("idEquipo");
	        nbombas = datosAnterior.getString("nbombas");	        
	        forma = datosAnterior.getString("forma");
	        tipo = datosAnterior.getString("tipo");
	        dispositivo = datosAnterior.getString("dispositivo");

        	String nombreStr = new String(nombreCompleto);

        	nombreStr = nombreStr.substring(0,20);
        	nombreUser.setText(nombreStr+"...");
//	        nombreUser.setText(datosAnterior.getString("nombreCompleto"));
	        patente.setText(datosAnterior.getString("equipo"));	        
	        
	        values = new ArrayList<String>();
	        ids = new ArrayList<String>();
	        rellenaDatos(forma);
	        Toast.makeText(getApplicationContext(),"Forma:"+forma, Toast.LENGTH_SHORT).show();
	        mSearchView = (SearchView) findViewById(R.id.search_view);
	        mListView = (ListView) findViewById(R.id.list_view);
	        mListView.setAdapter(new ArrayAdapter<String>(this,
	                R.layout.arraylist,
	                values));
	        mListView.setTextFilterEnabled(true);
	        setupSearchView();
	
	        mListView.setOnItemClickListener(new OnItemClickListener()
	        {
	        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
	        {
	        	
	        	// pos = values.getItem(position).getId();
        		Intent i=new Intent(abastecimiento_paso2.this, abastecimiento_paso3.class);
				i.putExtra("equipo",equipo);
				i.putExtra("idEquipo",idEquipo);
				i.putExtra("idUser",idUser);
				i.putExtra("nombreCompleto",nombreCompleto);
				i.putExtra("nbombas",nbombas);
				i.putExtra("forma",forma);
				i.putExtra("tipo",tipo);
				i.putExtra("dispositivo",dispositivo);
				i.putExtra("idequipoCliente",ids.get(position));
				i.putExtra("equipoCliente",values.get(position));
        		startActivity(i);	        	
	        	
	        	
	        	
	        	
	        	//FUNCIONAMIENTO PARA LOS LITROS!!
	        /*	
	        AlertDialog.Builder adb = new AlertDialog.Builder(
	        		abastecimiento_paso2.this);
	        //adb.setTitle("ListView OnClick");
	        //adb.setMessage("Selected Item is = "
	        //+ mListView.getItemAtPosition(position)+" Posicion: "+position+" ID: "+ids.get(position));
	        
	        
	        
	        adb.setTitle("Confirmacion de envio");
	        adb.setMessage("Esta seguro?");
	        adb.setIcon(android.R.drawable.ic_dialog_alert);
	        adb.setPositiveButton("Si", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int whichButton) {
	                Toast.makeText(abastecimiento_paso2.this, "Yaay", Toast.LENGTH_SHORT).show();
	            }});
	         adb.setNegativeButton("No", null).show();*/
	        
	        
	        //adb.setPositiveButton("Ok", null);
	        //adb.show();                     
	        }
	        });     

	        
	        
	}


    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false); 
        mSearchView.setQueryHint("Filtrar Equipos");
    }
	public void rellenaDatos(String forma){
		SQLiteDatabase db = usuarios.getWritableDatabase();
		
		String nombreEC, idEC;

		Cursor fila = db.rawQuery(
                "select id, nombre from EquiposCliente WHERE forma='"+forma+"'", null);
		fila.moveToFirst();
		if (fila.moveToFirst()) {
	        do {
	        	nombreEC = fila.getString((fila.getColumnIndex("nombre")));
	        	idEC = fila.getString((fila.getColumnIndex("id")));
	        	values.add(nombreEC);
	        	ids.add(idEC);
	        } while (fila.moveToNext());
		}
	
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
        }
        return true;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
