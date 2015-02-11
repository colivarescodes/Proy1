package com.pyclimitada.pyc;




import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class equipos extends Activity {

	//VARIABLES GENERALES
	String idUser;
	String nombreCompleto;
	String generalTurno;
	baseDatos usuarios;
	RadioGroup radioGroup;
	TextView nombreUser;
	int i;	
	Bundle bPatente;
	Button boton;
    private ProgressDialog pDialog;
	Httppostaux post;
    String IP_Server="http://www.maitech.cl";//IP DE NUESTRO PC
    String URL_connect="http://www.maitech.cl/pyc/comEq.php";//ruta en donde estan nuestros archivos
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.equipos);
        post=new Httppostaux();
        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
        bPatente = this.getIntent().getExtras();
        //nombreUser.setText(bPatente.getString("user"));
        boton= (Button) findViewById(R.id.button1);

        
        usuarios = new baseDatos(this, "DBUsuarios", null, 1);

        InformacionGeneral(bPatente.getString("user"));        
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        addRadioButtons();

        boton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view){
        		int idSeleccionado = radioGroup.getCheckedRadioButtonId();
        		//RadioButton btn = (RadioButton) radioGroup.getChildAt(idSeleccionado);
        	    //String selection = btn.getText();

        		//Toast.makeText(getApplicationContext(),"Bombas: "+nBombas, Toast.LENGTH_SHORT).show();
        		if(idSeleccionado > 0){
            		RadioButton TheTextIsHere = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
            		String selection = TheTextIsHere.getText().toString();
            	    
            		String idEquipo = "";
            	
            		idEquipo= Integer.toString(idSeleccionado);
            		String nBombas = TheTextIsHere.getTag().toString();        			
        			int dataBD = registrosTurno(idEquipo);
        			String oDataBD = Integer.toString(dataBD);
        			/*if(dataBD == 1){
        				
        			}else if(dataBD == 0){
        				//No existe registro, pasa a buscar en server
        			}*/
        			//Integer.parseInt(cadena)
        			//Toast.makeText(getApplicationContext(),"Equipo Seleccionado: "+idSeleccionado, Toast.LENGTH_SHORT).show();
        			new asyncEquipos().execute(idEquipo,bPatente.getString("user"),nombreCompleto,idUser,oDataBD,selection,nBombas);
        		}else{
        			err_select();
        		}
        	}
        });
        //compara_hora();
        
        }

    public String fechaMysql(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar todayDate = new GregorianCalendar();
        String fActual = sdf.format(todayDate.getTime());
        return fActual;
    }
    
    public int registrosTurno(String est){
    	
    	//String fecha = fechaMysql();
    	
   		SQLiteDatabase db = usuarios.getWritableDatabase();

		Cursor fila = db.rawQuery(
           "SELECT id, idturno, fecha, turno, estacion FROM turno WHERE estacion='"+est+"' and estado='0' ORDER BY id DESC LIMIT 1", null);
		fila.moveToFirst();
		if (fila.moveToFirst()) {
			String didTurno 	= fila.getString((fila.getColumnIndex("idturno")));
			String dFecha 		= fila.getString((fila.getColumnIndex("fecha")));
			String dTurno 		= fila.getString((fila.getColumnIndex("turno")));
			String dEstacion  	= fila.getString((fila.getColumnIndex("estacion")));
			
			generalTurno = didTurno+","+dFecha+","+dTurno+","+dEstacion;
			return 1;
		}else{
			return 0;
		}
    }
    
    
    public int compara_hora(){
		
    	int tTurno = 0;
    	Date now = new Date();
		
		
		
		Calendar a = Calendar.getInstance();
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		Date turnoA = a.getTime();
		
		Calendar b = Calendar.getInstance();
		b.set(Calendar.HOUR_OF_DAY, 7);
		b.set(Calendar.MINUTE, 30);
		b.set(Calendar.SECOND, 0);
		Date turnoB = b.getTime();		
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 19);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.SECOND, 0);
		Date turnoC = c.getTime();		
				
		
		
		int tA = now.compareTo(turnoA);
		int tB = now.compareTo(turnoB);
		int tC = now.compareTo(turnoC);
		
		if(((tA>0) && (tB<0)) || (tA == 0)){
			tTurno = 1;
		}else if(((tB>0) && (tC<0)) || (tB == 0)){
			tTurno = 2;
		}else if((tC>0) || (tC == 0)){
			tTurno = 3;
		}
		
		return tTurno;
    }
    
    
    public void err_select(){
    	Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    vibrator.vibrate(200);
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Debe seleccionar un Equipo", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }    
    
    	public void InformacionGeneral(String user){
    		SQLiteDatabase db = usuarios.getWritableDatabase();

    		Cursor fila = db.rawQuery(
                    "select id,rut,nombre,cargo from Usuarios WHERE rut='"+user+"'", null);		
    		fila.moveToFirst();
    		if (fila.moveToFirst()) {
    	        do {

    	        	nombreCompleto = fila.getString((fila.getColumnIndex("nombre")));
    	        	idUser = fila.getString((fila.getColumnIndex("id")));
    	        	/*String nombreStr = new String(nombreCompleto);

    	        	nombreStr = nombreStr.substring(0,20);*/
    	        	nombreUser.setText(nombreCompleto);
    	        } while (fila.moveToNext());
    		}
    	}
        public void addRadioButtons() {

        	
            for (int row = 0; row < 1; row++) {
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);
        		SQLiteDatabase db = usuarios.getWritableDatabase();

        		Cursor fila = db.rawQuery(
                        "select * from Equipos WHERE tipo='2'", null);		
        		fila.moveToFirst();
        		if (fila.moveToFirst()) {
        	        do {

        	        	RadioButton radioButton = new RadioButton(this);
                	    radioButton.setLayoutParams 
                	      (new LayoutParams 
                	      (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                	    radioButton.setText(fila.getString((fila.getColumnIndex("nombre"))));
                	    radioButton.setId(fila.getInt((fila.getColumnIndex("id"))));
                	    radioButton.setTag(""+fila.getString((fila.getColumnIndex("nbombas"))));
                	    radioGroup.addView(radioButton, i);
                 	    i=i+1;
        	        } while (fila.moveToNext());
        	    }
        }
        }
        
        public boolean infEquipo(String equipo) {
        	int eqstatus = -1;
        	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
    		postparameters2send.add(new BasicNameValuePair("estacion",equipo));
    		//postparameters2send.add(new BasicNameValuePair("password",password));

    		JSONArray eq=post.getserverdata(postparameters2send, URL_connect);
 		   //si lo que obtuvimos no es null
    		//SystemClock.sleep(950);
    		if (eq!=null && eq.length() > 0){
                JSONObject json_data;
                try {
                    json_data = eq.getJSONObject(0);
                     eqstatus=json_data.getInt("abierto");
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
            
            class asyncEquipos extends AsyncTask< String, String, String > {
            	 //String puede;
            	//String user,pass;
            	String equipoFinal;
            	String rutUser;
            	String nombreCompleto;
            	String idUser;
            	String dataBD;
            	String nombreEquipo;
            	String nBombas;
                protected void onPreExecute() {
                	//para el progress dialog
                    pDialog = new ProgressDialog(equipos.this);
                    pDialog.setMessage("Obteniendo informacion del Equipo");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
         
        		protected String doInBackground(String... params) {
        			//obtnemos usr y pass
        			//user=params[0];
        			//pass=params[1];
        			equipoFinal = params[0];
        			rutUser = params[1];
        			nombreCompleto = params[2];
        			idUser = params[3];
                    dataBD = params[4];
                    nombreEquipo = params[5];
                    nBombas = params[6];
                    
                    int oDataBD = Integer.parseInt(dataBD);
        			//int dataBD = registrosTurno(equipoFinal);
        			if(oDataBD == 1){
        				return "ok";
        			}else if(oDataBD == 0){
        				return "err";
        				//No existe registro, pasa a buscar en server, solo el día de hoy y en el turno de hoy
        			}else{
        				//Pasa a crear turno
        				return "err";
        			}
        			
        			/*
        			if (infEquipo(equipoFinal)==true){
            			return "ok"; //login valido
            		}else{    		
            			return "err"; //login invalido     	          	  
            		}*/
        			
        			//return "ok";
        		}

                protected void onPostExecute(String result) {

                   pDialog.dismiss();//ocultamos progess dialog.
                   Log.e("onPostExecute=",""+result);
                   
                   if (result.equals("ok")){

        				Intent i=new Intent(equipos.this, home.class);
        				i.putExtra("idEquipo",equipoFinal);
        				i.putExtra("rut",rutUser);
        				i.putExtra("nombreCompleto",nombreCompleto);
        				i.putExtra("idUser",idUser);
        				i.putExtra("nombreEquipo",nombreEquipo);
        				startActivity(i);
        				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                	   //ACA ESTA LISTO Y COMPROBADO, PASA A LA SIGUIENTE PAGINA
                	   //Toast.makeText(getApplicationContext(),"Turno Abierto", Toast.LENGTH_SHORT).show();
                    }else{
        				Intent i=new Intent(equipos.this, Inicioturno.class);
        				i.putExtra("idEquipo",equipoFinal);
        				i.putExtra("nombreCompleto",nombreCompleto);
        				i.putExtra("idUser",idUser);
        				i.putExtra("nombreEquipo",nombreEquipo);
        				i.putExtra("nBombas",nBombas);
        				startActivity(i);                    	
                       //Toast.makeText(getApplicationContext(),"Turno Aun no Abierto", Toast.LENGTH_SHORT).show();
                    	//err_login();
                    }
                  }
                }
            
            @Override
            public void onBackPressed() {
            }
}
