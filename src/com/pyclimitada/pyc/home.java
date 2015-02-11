package com.pyclimitada.pyc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class home extends Activity {
	Bundle datosAnterior;
	TextView nombreUser,patente;
	baseDatos usuarios;
	String nombreCompleto, idUser, equipo, idEquipo,nbombas;
	Button boton, botonSalir, botonNoEnviados, botonEnviados, botonCierre;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.home);
	        
	        usuarios = new baseDatos(this, "DBUsuarios", null, 1);
	        
	        datosAnterior = this.getIntent().getExtras();
	        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
	        patente = (TextView)this.findViewById(R.id.patente);
	        boton= (Button) findViewById(R.id.button1);
	        botonSalir= (Button) findViewById(R.id.Button07);
	        botonEnviados= (Button) findViewById(R.id.Button02);
	        botonNoEnviados= (Button) findViewById(R.id.Button03);
	        botonCierre= (Button) findViewById(R.id.Button044);
	        
	        nombreCompleto = datosAnterior.getString("nombreCompleto");
//	        nombreUser.setText(nombreCompleto);
        	String nombreStr = new String(nombreCompleto);

        	nombreStr = nombreStr.substring(0,20);
        	nombreUser.setText(nombreStr+"...");
	        idEquipo = datosAnterior.getString("idEquipo");
	        idUser = datosAnterior.getString("idUser");
	        
	        InformacionGeneralEquipo(idEquipo);
	        //Toast.makeText(getApplicationContext(),"Nombre: "+nombreCompleto, Toast.LENGTH_SHORT).show();
	        boton.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
    				Intent i=new Intent(home.this, abastecimiento_paso1.class);
    				i.putExtra("equipo",equipo);
    				i.putExtra("idEquipo",idEquipo);
    				i.putExtra("idUser",idUser);
    				i.putExtra("nombreCompleto",nombreCompleto);
    				i.putExtra("nbombas",nbombas);
    				//i.putExtra("rut",rutUser);
    				startActivity(i);
	        	}
	        });
	        botonSalir.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
    				Intent i=new Intent(home.this, MainActivity.class);
    				//i.putExtra("equipo",equipo);
    				//i.putExtra("idEquipo",idEquipo);
    				//i.putExtra("idUser",idUser);
    				//i.putExtra("nombreCompleto",nombreCompleto);
    				//i.putExtra("nbombas",nbombas);
    				//i.putExtra("rut",rutUser);
    				startActivity(i);
	        	}
	        });
	        botonEnviados.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
    				Intent i=new Intent(home.this, Enviados.class);
    				i.putExtra("equipo",equipo);
    				i.putExtra("idEquipo",idEquipo);
    				//i.putExtra("idUser",idUser);
    				i.putExtra("nombreCompleto",nombreCompleto);
    				//i.putExtra("nbombas",nbombas);
    				//i.putExtra("rut",rutUser);
    				startActivity(i);
	        	}
	        });
	        botonCierre.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
    				Intent i=new Intent(home.this, CierreTurno.class);
    				i.putExtra("equipo",equipo);
    				i.putExtra("idEquipo",idEquipo);
    				//i.putExtra("idUser",idUser);
    				i.putExtra("nombreCompleto",nombreCompleto);
    				i.putExtra("nbombas",nbombas);
    				//i.putExtra("rut",rutUser);
    				startActivity(i);
	        	}
	        });
	        botonNoEnviados.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
    				Intent i=new Intent(home.this, NoEnviados.class);
    				i.putExtra("equipo",equipo);
    				//i.putExtra("idEquipo",idEquipo);
    				//i.putExtra("idUser",idUser);
    				i.putExtra("nombreCompleto",nombreCompleto);
    				//i.putExtra("nbombas",nbombas);
    				//i.putExtra("rut",rutUser);
    				startActivity(i);
	        	}
	        });
	}
/*	public void InformacionGeneral(String user){
		SQLiteDatabase db = usuarios.getWritableDatabase();

		Cursor fila = db.rawQuery(
                "select id,rut,nombre,cargo from Usuarios WHERE rut='"+user+"'", null);		
		fila.moveToFirst();
		if (fila.moveToFirst()) {
	        do {
	        	nombreUser.setText(fila.getString((fila.getColumnIndex("nombre"))));
	        	nombreCompleto = fila.getString((fila.getColumnIndex("nombre")));
	        	idUser = fila.getString((fila.getColumnIndex("id")));
	        } while (fila.moveToNext());
		}
	}*/
	public void InformacionGeneralEquipo(String patenteFin){
		SQLiteDatabase db = usuarios.getWritableDatabase();

		Cursor fila = db.rawQuery(
                "select nombre, nbombas from Equipos WHERE id='"+patenteFin+"'", null);
		fila.moveToFirst();
		if (fila.moveToFirst()) {
	        do {
	        	patente.setText(fila.getString((fila.getColumnIndex("nombre"))));
	        	equipo = fila.getString((fila.getColumnIndex("nombre")));
	        	nbombas = fila.getString((fila.getColumnIndex("nbombas")));
	        	//idEquipo = fila.getString((fila.getColumnIndex("id")));
	        } while (fila.moveToNext());
		}
	}
    @Override
    public void onBackPressed() {
    }
}
