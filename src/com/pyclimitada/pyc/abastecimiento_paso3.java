package com.pyclimitada.pyc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pyclimitada.pyc.equipos.asyncEquipos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class abastecimiento_paso3 extends Activity {
	 String nombreCompleto, idUser, equipo, idEquipo,nbombas,fActual,idturno;
	 String forma, tipo, dispositivo,equipoCliente,idEquipoCliente;
	 baseDatos usuarios;
	 Bundle datosAnterior;
	 TextView nombreUser,patente,nombreEquipo,disp,bba,tipe,fechaTex;
	 EditText lts;

	Button boton;
	Httppostaux post;
    String IP_Server="http://www.maitech.cl";//IP DE NUESTRO PC
    String URL_connect="http://www.maitech.cl/pyc/rInfDesface.php";//ruta en donde estan nuestros archivos

  
    boolean result_back;
    private ProgressDialog pDialog;
	    

	 
	 
	 int intDisp = 0;
	 int intForma = 0;
	 int intTipo = 0;
	 
		protected void onCreate(Bundle savedInstanceState) {
		       super.onCreate(savedInstanceState);
		        requestWindowFeature(Window.FEATURE_NO_TITLE);
		        setContentView(R.layout.abastecimiento_paso3);

		        post=new Httppostaux();
		        boton= (Button) findViewById(R.id.button1);
		        
		        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
		        patente = (TextView)this.findViewById(R.id.patente);	
		        nombreEquipo = (TextView)this.findViewById(R.id.textView10);	
		        disp = (TextView)this.findViewById(R.id.textView6);
		        bba = (TextView)this.findViewById(R.id.textView3);
		        tipe = (TextView)this.findViewById(R.id.textView8);
		        fechaTex = (TextView)this.findViewById(R.id.textView4);
		        lts= (EditText) findViewById(R.id.editText1);
		        
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
		        equipoCliente = datosAnterior.getString("equipoCliente");
		        idEquipoCliente = datosAnterior.getString("idequipoCliente");
		        idturno = registrosTurno(idEquipo);

		        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		        GregorianCalendar todayDate = new GregorianCalendar();
		        fActual = sdf.format(todayDate.getTime());
		        
		        fechaTex.setText(fActual);
		        //Toast.makeText(getApplicationContext(),"Forma:"+forma+"Tipo:"+tipo+"Dispositivo:"+dispositivo, Toast.LENGTH_SHORT).show();
		        
	        	String nombreStr = new String(nombreCompleto);

	        	nombreStr = nombreStr.substring(0,20);
	        	nombreUser.setText(nombreStr+"...");
//		        nombreUser.setText(datosAnterior.getString("nombreCompleto"));
		        patente.setText(datosAnterior.getString("equipo"));	 
		        nombreEquipo.setText(equipoCliente);
		        
		        //disp.setText("150 GPM");
		        
		        
		        intDisp = Integer.parseInt(dispositivo);
		        intTipo = Integer.parseInt(tipo);
		        intForma = Integer.parseInt(forma);
		        
		        
		        if(intDisp == 1){
		        	disp.setText("150 GPM");
		        }else if(intDisp == 2){
		        	disp.setText("Chica");
		        }
		        if(intForma == 1){
		        	tipe.setText("Venta");
		        }else if(intForma == 2){
		        	tipe.setText("Trasvasije");
		        }
		        if(intTipo == 1){
		        	bba.setText("Bomba 1");
		        }else if(intTipo == 2){
		        	bba.setText("Bomba 2");
		        }

		        
		        boton.setOnClickListener(new View.OnClickListener(){
		        	public void onClick(View view){
		        		if(isOnline()){
		        			//Envia a Asynctask
		        			AlmacenaTransaccion("1");
		        			new asyncEnvio().execute(idEquipo,nombreCompleto,idUser,tipo,dispositivo,forma,idEquipoCliente,lts.getText().toString(),fActual);
		        		//	new asyncEquipos().execute(idEquipo,bPatente.getString("user"),nombreCompleto,idUser);
		        		}else{
		        			AlmacenaTransaccion("0");
		        			Toast.makeText(getApplicationContext(),"Informacion almacenada, Queda pendiente de envío", Toast.LENGTH_LONG).show();
	        				Intent i=new Intent(abastecimiento_paso3.this, home.class);
	        				i.putExtra("idEquipo",idEquipo);
	        				
	        				i.putExtra("nombreCompleto",nombreCompleto);
	        				i.putExtra("idUser",idUser);
	        				startActivity(i);
		        			//Graba directamente con valor 0
		        		}
		        	}
		        });
		        
		        
		}
	   public String registrosTurno(String est){
	    	
	   	//String fecha = fechaMysql();
		    	
	  		SQLiteDatabase db = usuarios.getWritableDatabase();

			Cursor fila = db.rawQuery(
	           "SELECT id, idturno, fecha, turno, estacion FROM turno WHERE estacion='"+est+"' and estado='0' ORDER BY id DESC LIMIT 1", null);
			fila.moveToFirst();
			//if (fila.moveToFirst()) {
				String didTurno 	= fila.getString((fila.getColumnIndex("idturno")));
								
				//generalTurno = didTurno+","+dFecha+","+dTurno+","+dEstacion;
				return didTurno;
			//}else{
				//return 0;
			//}
	    }	
		private void AlmacenaTransaccion(String env){

	        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        GregorianCalendar todayDate = new GregorianCalendar();
	        String fechaAlma = sdf2.format(todayDate.getTime());
	        String litros=lts.getText().toString();
	        
			
			SQLiteDatabase db = usuarios.getWritableDatabase();
			db.execSQL("INSERT INTO Ventas(fecha,iduser,estacion,bomba,dispositivo,tipo,equipo,litros,enviado, idturno)"+
					   "VALUES ('"+fechaAlma+"','"+idUser+"','"+idEquipo+"','"+tipo+"','"+dispositivo+"','"+forma+"','"+idEquipoCliente+"','"+litros+"','"+env+"','"+idturno+"')"
					);
			/*
			 db.execSQL("INSERT INTO excedidos(patente, latitud, longitud, velocidad, satelites)"+
				   		"VALUES ('"+ patente.getText().toString() +"', '"+ lati +"', '"+ longi +"', "+ velocidadString +", "+ sate +" )");
			 * */
		}

        public boolean infTransaccion(String equipo, String idUser2,String tipo2,String dispositivo2,String forma2,String idEquipoCliente2, String lts2,String fecha2) {
        	int eqstatus = -1;
        	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
    		postparameters2send.add(new BasicNameValuePair("estacion",equipo));
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
                    return true;
                }
			}else{	//json obtenido invalido verificar parte WEB.
	    	 Log.e("JSON  ", "ERROR");
		     return false;
		  }   		
    		

        }		
		
        /*		CLASE ASYNCTASK
         * 
         * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
         * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir    
         * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
         * ademas observariamos el mensaje de que la app no responde.     
         */
            
            class asyncEnvio extends AsyncTask< String, String, String > {
            	 //String puede;
            	//String user,pass;
            	String equipoFinal;
            	String nombreCompleto;
            	String idUser;
            	//tipo,dispositivo,forma,idEquipoCliente,lts.getText().toString()
            	String tipo;
            	String dispositivo;
            	String forma;
            	String idEquipoCliente;
            	String lts;
            	String fecha;
                protected void onPreExecute() {
                	//para el progress dialog
                    pDialog = new ProgressDialog(abastecimiento_paso3.this);
                    pDialog.setMessage("Almacenando Transaccion");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
         
        		protected String doInBackground(String... params) {
        			//obtnemos usr y pass
        			//user=params[0];
        			//pass=params[1];
        			equipoFinal = params[0];
        			nombreCompleto = params[1];
        			idUser = params[2];
        			tipo  = params[3];
        			dispositivo = params[4];
        			forma = params[5];
        			idEquipoCliente = params[6];
        			lts = params[7];
        			fecha = params[8];
        			if (infTransaccion(equipoFinal,idUser,tipo,dispositivo,forma,idEquipoCliente,lts,fecha)==true){
            			return "ok"; //login valido
            		}else{    		
            			return "err"; //login invalido     	          	  
            		}
        			
        			//return "ok";
        		}

                protected void onPostExecute(String result) {

                   pDialog.dismiss();//ocultamos progess dialog.
                   Log.e("onPostExecute=",""+result);
                   
                   if (result.equals("ok")){

        				Intent i=new Intent(abastecimiento_paso3.this, home.class);
        				i.putExtra("idEquipo",equipoFinal);
        				
        				i.putExtra("nombreCompleto",nombreCompleto);
        				i.putExtra("idUser",idUser);
        				startActivity(i);
        				//overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                	   //ACA ESTA LISTO Y COMPROBADO, PASA A LA SIGUIENTE PAGINA
                	   //Toast.makeText(getApplicationContext(),"Turno Abierto", Toast.LENGTH_SHORT).show();
                    }else{
                       Toast.makeText(getApplicationContext(),"Venta ya se encuentra registrada en servidor", Toast.LENGTH_SHORT).show();
       				Intent i=new Intent(abastecimiento_paso3.this, home.class);
       				i.putExtra("idEquipo",equipoFinal);
       				
       				i.putExtra("nombreCompleto",nombreCompleto);
       				i.putExtra("idUser",idUser);
       				startActivity(i);
       				//err_login();
                    }
                  }
                }		
		
		
		public boolean isOnline() {
		    ConnectivityManager cm = 
		         (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnected()) {
		        return true;
		    }
		    return false;
		}	
        @Override
        public void onBackPressed() {
        }
}
