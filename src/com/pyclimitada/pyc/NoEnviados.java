package com.pyclimitada.pyc;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class NoEnviados extends Activity  {

	
	baseDatos usuarios;
	private SQLiteDatabase db;

	private CustomAdapter mListAdapter; 
    ListView msgList;
    private ArrayList<MessageDetails> details;
    AdapterView.AdapterContextMenuInfo info;
    
    MessageDetails Detail;
    
    String nomDisp, nomTran, nomEstado;
    ArrayList<String> ids;
    
	String nombreCompleto,equipo,idEquipo;
	Bundle datosAnterior;
	TextView nombreUser,patente;
    
	Httppostaux post;
	String IP_Server="http://www.maitech.cl";//IP DE NUESTRO PC
	String URL_connect="http://www.maitech.cl/pyc/rInfDesface.php";//ruta en donde estan nuestros archivos
	private ProgressDialog pDialog;	
	
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.no_enviados);

		post=new Httppostaux();
		
        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
        patente = (TextView)this.findViewById(R.id.patente);
        //Información traida del anterior
        datosAnterior = this.getIntent().getExtras();
        nombreCompleto = datosAnterior.getString("nombreCompleto");
        equipo = datosAnterior.getString("equipo");
        idEquipo = datosAnterior.getString("idEquipo");
        
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
        

        mListAdapter = new CustomAdapter(details , this,"ne");
        msgList.setAdapter(mListAdapter);
        

	}
	public void rellenaDatos(){
		SQLiteDatabase db = usuarios.getWritableDatabase();
		
		String nombreEC, idEC;

		Cursor fila = db.rawQuery(
				"SELECT	Ventas.id, Ventas.fecha, EquiposCliente.nombre, Ventas.bomba, Ventas.dispositivo, Ventas.tipo, Ventas.litros, Ventas.enviado, Ventas.idturno " +
				"FROM	Ventas INNER JOIN EquiposCliente ON (Ventas.equipo = EquiposCliente.id) " +
				"WHERE  Ventas.enviado='0'",null);
				
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
	                  menu.add(Menu.NONE, v.getId(), 0, "Enviar a Servidor");
	                  menu.add(Menu.NONE, v.getId(), 0, "Borrar");   
	          }
	           
	   @Override
	   public boolean onContextItemSelected(MenuItem item) {
		   //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		   
		   long id = info.id;
     	   int idA = (int) id;
     	   String idString = String.valueOf(idA);
     	   String idFin = ids.get(idA);
	           if (item.getTitle() == "Enviar a Servidor") {
	              //Do your working
	        	   new asyncEnvio().execute(equipo,nombreCompleto, idFin, idString);
	        	   Toast.makeText(getApplicationContext(),"Id: "+id+" idBD: "+idFin, Toast.LENGTH_LONG).show();
	                  }
	            else if (item.getTitle() == "Borrar") {
	               //Do your working
		        	   //Toast.makeText(getApplicationContext(),"Id: "+id+" idBD: "+idFin, Toast.LENGTH_LONG).show();
		        	   eliminaDato(idFin, idString);
		        	   ids.remove(idA);
		        	   mListAdapter.otro(idA);
	                  }
	            
	            else     {
	                  return false;
	                  }
	          return true;
	          }	
		private void actualizaRegistro(String id){
			
			SQLiteDatabase db = usuarios.getWritableDatabase();
			db.execSQL("UPDATE Ventas SET enviado='1' WHERE id='"+id+"'");
		}
       public boolean infTransaccion(String id, String idA) {
       	int eqstatus = -1;
       	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
    		
       	
       	SQLiteDatabase db = usuarios.getWritableDatabase();
		
		//String nombreEC, idEC;

		Cursor fila = db.rawQuery(
				"SELECT	fecha,iduser,estacion,bomba,dispositivo,tipo,equipo,litros,enviado,idturno FROM Ventas WHERE id='"+id+"'",null);
				
		fila.moveToFirst();
		String estacion = fila.getString((fila.getColumnIndex("estacion")));
       	String idUser2 = fila.getString((fila.getColumnIndex("iduser")));
       	String fecha2 = fila.getString((fila.getColumnIndex("fecha")));
       	String tipo2 = fila.getString((fila.getColumnIndex("bomba")));
       	String dispositivo2  = fila.getString((fila.getColumnIndex("dispositivo")));
       	String forma2 = fila.getString((fila.getColumnIndex("tipo")));
       	String idEquipoCliente2 = fila.getString((fila.getColumnIndex("equipo")));
       	String lts2 = fila.getString((fila.getColumnIndex("litros")));
       	String idturno = fila.getString((fila.getColumnIndex("idturno")));
       	
   		postparameters2send.add(new BasicNameValuePair("estacion",estacion));
   		postparameters2send.add(new BasicNameValuePair("idUser",idUser2));
   		postparameters2send.add(new BasicNameValuePair("fecha",fecha2));    		
   		postparameters2send.add(new BasicNameValuePair("bomba",tipo2));
   		postparameters2send.add(new BasicNameValuePair("dispositivo",dispositivo2));
   		postparameters2send.add(new BasicNameValuePair("tipo",forma2));
   		postparameters2send.add(new BasicNameValuePair("equipo",idEquipoCliente2));
   		postparameters2send.add(new BasicNameValuePair("litros",lts2));
   		postparameters2send.add(new BasicNameValuePair("idturno",idturno));
   		//postparameters2send.add(new BasicNameValuePair("password",password));

   		JSONArray eq=post.getserverdata(postparameters2send, URL_connect);
		   //si lo que obtuvimos no es null
   		//SystemClock.sleep(950);
   		if (eq!=null && eq.length() > 0){
               JSONObject json_data;
               try {
                   json_data = eq.getJSONObject(0);
                    eqstatus=json_data.getInt("graba");
                    Log.e("loginstatus","logstatus= "+eqstatus);//muestro por log que obtuvimos
               } catch (JSONException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }    
               if (eqstatus==0){// [{"logstatus":"0"}] 
                   //Log.e("loginstatus ", "invalido");
                   return false;
               }
               else{// [{"logstatus":"1"}]
                   //Log.e("loginstatus ", "valido");
            	   actualizaRegistro(id);
            	   

                   return true;
               }
			}else{	//json obtenido invalido verificar parte WEB.
	    	 Log.e("JSON  ", "ERROR");
		     return false;
		  }   		
   		

       }		   
	   
	   
		public void eliminaDato(String id, String idList){
			SQLiteDatabase db = usuarios.getWritableDatabase();
			
			db.execSQL("delete from Ventas where id='"+id+"'");

		}
	    class asyncEnvio extends AsyncTask< String, String, String > {
	      	 //String puede;
	      	//String user,pass;
	      	String equipoFinal;
	      	String nombreCompleto;
	      	String id;
	      	String idA;
	      	//String idUser;
	      	
	      	//String fechaActual;
	      	//String turno;
	      	//String numerales;
	      	//String nivel;
	      	
	          protected void onPreExecute() {
	          	//para el progress dialog
	              pDialog = new ProgressDialog(NoEnviados.this);
	              pDialog.setMessage("Iniciando Turno...");
	              pDialog.setIndeterminate(false);
	              pDialog.setCancelable(false);
	              pDialog.show();
	          }

	   		protected String doInBackground(String... params) {

	   			equipoFinal = params[0];
	   			nombreCompleto = params[1];
	   			id = params[2];
	   			idA = params[3];
	   			//idUser = params[2];
	   			
	   			//fechaActual  = params[3];
	   			//turno = params[4];
	   			//numerales = params[5];
	   			//nivel = params[6];

	   			if (infTransaccion(id, idA)==true){
	   				return "ok"; //login valido
	   			}else{    		
	   				return "err"; //login invalido     	          	  
	   			}
	   			
	   			//return "ok";
	   		}

	          protected void onPostExecute(String result) {

	             pDialog.dismiss();//ocultamos progess dialog.
	             Log.e("onPostExecute=",""+result);
	             int idB = Integer.parseInt(idA);
	             if (result.equals("ok")){
	            	   ids.remove(idB);
		        	   mListAdapter.otro(idB);
	   				/*Intent i=new Intent(Inicioturno.this, home.class);
	   				i.putExtra("idEquipo",equipoFinal);
	   				
	   				
	   				i.putExtra("nombreCompleto",nombreCompleto);
	   				i.putExtra("idUser",idUser);
	   				startActivity(i);*/

	              }else{
	                 Toast.makeText(getApplicationContext(),"Informacion no almacenada en servidor, Vuelva a internarlo", Toast.LENGTH_SHORT).show();
	              	//err_login();
	              }
	            }
	          }	   
}
