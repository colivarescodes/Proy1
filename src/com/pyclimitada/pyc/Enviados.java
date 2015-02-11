package com.pyclimitada.pyc;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Enviados extends Activity  {

	
	baseDatos usuarios;
	private SQLiteDatabase db;

	private CustomAdapter mListAdapter; 
    ListView msgList;
    private ArrayList<MessageDetails> details;
    AdapterView.AdapterContextMenuInfo info;
    
    MessageDetails Detail;
    
    String nomDisp, nomTran, nomEstado;
    ArrayList<String> ids;
    
	String nombreCompleto,equipo;
	Bundle datosAnterior;
	TextView nombreUser,patente;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.enviados);

        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
        patente = (TextView)this.findViewById(R.id.patente);
        //Información traida del anterior
        datosAnterior = this.getIntent().getExtras();
        nombreCompleto = datosAnterior.getString("nombreCompleto");
        equipo = datosAnterior.getString("equipo");
        
    	String nombreStr = new String(nombreCompleto);
    	nombreStr = nombreStr.substring(0,20);
    	
    	nombreUser.setText(nombreStr+"...");
        patente.setText(datosAnterior.getString("equipo"));	  
		
		
		usuarios = new baseDatos(this, "DBUsuarios", null, 1);

		msgList = (ListView) findViewById(R.id.list_view);
		
		details = new ArrayList<MessageDetails>();
		
		registerForContextMenu(msgList);
		ids = new ArrayList<String>();
        
        rellenaDatos();
        

        mListAdapter = new CustomAdapter(details , this,"en");
        msgList.setAdapter(mListAdapter);
        

	}
	public void rellenaDatos(){
		db = usuarios.getWritableDatabase();
		
		String nombreEC, idEC;

		Cursor fila = db.rawQuery(
				"SELECT	Ventas.id, Ventas.fecha, EquiposCliente.nombre, Ventas.bomba, Ventas.dispositivo, Ventas.tipo, Ventas.litros, Ventas.enviado " +
				"FROM	Ventas INNER JOIN EquiposCliente ON (Ventas.equipo = EquiposCliente.id) " +
				"WHERE  Ventas.enviado='1'",null);
				
		fila.moveToFirst();
		if (fila.moveToFirst()) {
	        do {
	        	idEC = fila.getString((fila.getColumnIndex("id")));
	        	ids.add(idEC);
	        	//Segmentando la informacion
	        	
	        	int Ndispositivo = Integer.parseInt(fila.getString((fila.getColumnIndex("dispositivo"))));
	        	if(Ndispositivo == 1){
	        		nomDisp = "150 GPM";
	        	}else if(Ndispositivo == 2){
	        		nomDisp = "CHICA";
	        	}else{
	        		nomDisp = "No Definido";
	        	}

	        	int NTransaccion = Integer.parseInt(fila.getString((fila.getColumnIndex("tipo"))));
	        	if(NTransaccion == 1){
	        		nomTran = "VENTA";
	        	}else if(NTransaccion == 2){
	        		nomTran = "TRASVASIJE";
	        	}else{
	        		nomTran = "No Definido";
	        	}	        	

	        	int NEstado = Integer.parseInt(fila.getString((fila.getColumnIndex("enviado"))));
	        	if(NEstado == 1){
	        		nomEstado = "ENVIADO";
	        	}else if(NEstado == 0){
	        		nomEstado = "PENDIENTE";
	        	}else{
	        		nomEstado = "No Definido";
	        	}	 	        
	        	DecimalFormat formateador = new DecimalFormat("###,###");
	        	formateador.setMinimumFractionDigits(0);
	        	//int nLitros = Integer.parseInt(fila.getString((fila.getColumnIndex("litros"))));
	        	Double nLitros = Double.valueOf(fila.getString((fila.getColumnIndex("litros"))));
	        	
	        	
            	String dateString = fila.getString((fila.getColumnIndex("fecha")));
            	String[] separated = dateString.split(" ");
            	String[] separaFecha = separated[0].split("-");
            	String[] separaHora = separated[1].split(":");
     			int anio = 0;
     			int mes = 0;
     			int dia = 0;
     			
     			int hor = 0;
     			int min = 0;
     			int seg = 0;                        	 
            	 try {

                	 anio = Integer.parseInt(separaFecha[0]);
                	 mes = Integer.parseInt(separaFecha[1]);
                	 dia = Integer.parseInt(separaFecha[2]);
                	 
                	 hor = Integer.parseInt(separaHora[0]);
                	 min = Integer.parseInt(separaHora[1]);
                	 seg = Integer.parseInt(separaHora[2]);
            	 } catch(NumberFormatException nfe) {
            		 System.out.println("Could not parse " + nfe);
            	 }
            	 
            	 GregorianCalendar cal = new GregorianCalendar( anio, mes-1, dia, hor, min,seg ); 
            	 //cal.add (GregorianCalendar.HOUR, -4);
            	 

            	 SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            	 dateFormat.setCalendar(cal); 
            	    
           	    Detail = new MessageDetails();
	            Detail.setEquipo(fila.getString((fila.getColumnIndex("nombre"))));
	            Detail.setFecha(dateFormat.format(cal.getTime()));
	            Detail.setHora(separated[1]);
	            Detail.setBomba(fila.getString((fila.getColumnIndex("bomba"))));
	            Detail.setReceptor(nomDisp);
	            Detail.setEstado(nomEstado);
	            Detail.setTransaccion(nomTran);
	            Detail.setLitros(formateador.format(nLitros));
	            details.add(Detail);
	        	
	        	
	        } while (fila.moveToNext());
		}
	
	}

	
	   @Override
	   public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	              // TODO Auto-generated method stub
	              super.onCreateContextMenu(menu, v, menuInfo);      
	                       
	                  info = (AdapterContextMenuInfo) menuInfo;
	    
	                  menu.setHeaderTitle(details.get(info.position).getEquipo());      
	                  menu.add(Menu.NONE, v.getId(), 0, "Re-Enviar a Servidor");
	              
	          }
	           
	   @Override
	   public boolean onContextItemSelected(MenuItem item) {
		   //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		   
		   long id = info.id;
     	   int idA = (int) id;
     	   String idString = String.valueOf(idA);
     	   String idFin = ids.get(idA);
	           if (item.getTitle() == "Re-Enviar a Servidor") {
	              //Do your working

	        	   Toast.makeText(getApplicationContext(),"Id: "+id+" idBD: "+idFin, Toast.LENGTH_LONG).show();
	                  }
	            
	            else     {
	                  return false;
	                  }
	          return true;
	          }	
	   
		public void eliminaDato(String id, String idList){
			SQLiteDatabase db = usuarios.getWritableDatabase();
			
			db.execSQL("delete from Ventas where id='"+id+"'");

		}
	   
}
