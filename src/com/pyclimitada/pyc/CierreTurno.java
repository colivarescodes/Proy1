package com.pyclimitada.pyc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pyclimitada.pyc.Inicioturno.asyncEnvio;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CierreTurno extends Activity {

	LinearLayout ll;
	String nombreCompleto, idUser, equipo, idEquipo,nbombas, idturno, nums="", Nivel;
	Bundle datosAnterior;
	int numero = 0;
	
	EditText nivelEdit; 
	
	TextView nombreUser,patente;
	
	Button boton;
	Httppostaux post;
    String IP_Server="http://www.maitech.cl";//IP DE NUESTRO PC
    String URL_connect="http://www.maitech.cl/pyc/rTurCierra.php";//ruta en donde estan nuestros archivos
    //EditText ed;
  
    boolean result_back;
    private ProgressDialog pDialog;
    baseDatos usuarios;
	
    List<EditText> allEds = new ArrayList<EditText>();
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cierre_turno);
		ll = (LinearLayout) findViewById(R.id.linear); //Bomba
		nombreUser = (TextView)this.findViewById(R.id.nombreUser);
        patente = (TextView)this.findViewById(R.id.patente);
        nivelEdit = (EditText)this.findViewById(R.id.editText1);
        
        
        usuarios = new baseDatos(this, "DBUsuarios", null, 1);
        post=new Httppostaux();
        boton= (Button) findViewById(R.id.button1);
        
        
		//Información traida del anterior
        datosAnterior = this.getIntent().getExtras();
        nombreCompleto = datosAnterior.getString("nombreCompleto");
       
        equipo = datosAnterior.getString("equipo");
        idEquipo = datosAnterior.getString("idEquipo");
        nbombas = datosAnterior.getString("nbombas");
        
        numero = Integer.parseInt(nbombas);
		//generaEdit(numero);
        
        
        
        
    	for(int i = 1; i<=numero; i++){
    		
    		EditText editText = new EditText(CierreTurno.this);
    		allEds.add(editText);
    		//int i = 1;
	        editText.setId(i);
	        editText.setHeight(50);
	        editText.setWidth(50);
	        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
	        editText.setHint("Numeral Bomba Nº "+i);
	        ll.addView(editText);
    		
    		/*ed[i].setId(i);
    		ed[i].setHeight(50);
    		ed[i].setWidth(50);
    		ed[i].setInputType(InputType.TYPE_CLASS_NUMBER);
    		ed[i].setHint("Numeral Bomba Nº "+i);
	        ll.addView(ed[i]);*/
    	}
        
        
        
        
    	String nombreStr = new String(nombreCompleto);

    	nombreStr = nombreStr.substring(0,20);
    	nombreUser.setText(nombreStr+"...");
//        nombreUser.setText(datosAnterior.getString("nombreCompleto"));
        patente.setText(datosAnterior.getString("equipo"));
        idturno = registrosTurno(idEquipo);
        boton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view){
        		if(isOnline()){
        			
        			if(allEds.size() == 2){
        				nums = allEds.get(0).getText().toString()+","+allEds.get(1).getText().toString();
        			}else{
        				nums = allEds.get(0).getText().toString();
        			}
        			//Log.e("Value ","Val " + nums);
        			Nivel = nivelEdit.getText().toString();
        			new asyncEnvio().execute(idturno,nums,Nivel);
        		}else{
        			Toast.makeText(getApplicationContext(),"Sin conexion a internet, Revise antes de continuar", Toast.LENGTH_LONG).show();
        			//AlmacenaTransaccion("0");
        			//Graba directamente con valor 0
        		}
        	}
        });
	}
	   public String registrosTurno(String est){
	    	
		  		SQLiteDatabase db = usuarios.getWritableDatabase();
				Cursor fila = db.rawQuery(
		           "SELECT id, idturno, fecha, turno, estacion FROM turno WHERE estacion='"+est+"' and estado='0' ORDER BY id DESC LIMIT 1", null);
				fila.moveToFirst();
					String didTurno 	= fila.getString((fila.getColumnIndex("idturno")));
					return didTurno;

		    }	
    public void generaEdit(int numero){
    	/*EditText ed[] = new EditText[numero];
    	for(int i = 1; i<=numero; i++){
    		ed[i].setId(i);
    		ed[i].setHeight(50);
    		ed[i].setWidth(50);
    		ed[i].setInputType(InputType.TYPE_CLASS_NUMBER);
    		ed[i].setHint("Numeral Bomba Nº "+i);
	        ll.addView(ed[i]);
    		/*EditText editText = new EditText(CierreTurno.this);
    		//int i = 1;
	        editText.setId(i);
	        editText.setHeight(50);
	        editText.setWidth(50);
	        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
	        editText.setHint("Numeral Bomba Nº "+i);
	        ll.addView(editText);*/
    		  
    	//}
    }
    private void AlmacenaTransaccion(String idturno){
    	
		SQLiteDatabase db = usuarios.getWritableDatabase();
		db.execSQL("UPDATE turno SET estado='1' WHERE idturno='"+idturno+"'");
	}
    public boolean infTransaccion(String idturno2,String numeral2,String nivel2) {
    	int eqstatus = -1;
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		   		
		postparameters2send.add(new BasicNameValuePair("idturno",idturno2));
		postparameters2send.add(new BasicNameValuePair("numerales",numeral2));
		postparameters2send.add(new BasicNameValuePair("nivel",nivel2));

		//postparameters2send.add(new BasicNameValuePair("password",password));

		JSONArray eq=post.getserverdata(postparameters2send, URL_connect);
		   //si lo que obtuvimos no es null
		//SystemClock.sleep(950);
		if (eq!=null && eq.length() > 0){
            JSONObject json_data;
            try {
                json_data = eq.getJSONObject(0);
                 eqstatus=json_data.getInt("idTurno");
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
            	//String idTurnoServer =Integer.toString(eqstatus);
            	AlmacenaTransaccion(idturno2);
                return true;
            }
		}else{	//json obtenido invalido verificar parte WEB.
    	 Log.e("JSON  ", "ERROR");
	     return false;
	  }   		
    }
    class asyncEnvio extends AsyncTask< String, String, String > {
      	 //String puede;
      	//String user,pass;
      	
      	
      	
      	String idturno;
      	String numerales;
      	String nivel;
      	
          protected void onPreExecute() {
          	//para el progress dialog
              pDialog = new ProgressDialog(CierreTurno.this);
              pDialog.setMessage("Cerrando Turno...");
              pDialog.setIndeterminate(false);
              pDialog.setCancelable(false);
              pDialog.show();
          }

   		protected String doInBackground(String... params) {

   			idturno = params[0];
   			numerales = params[1];
   			nivel = params[2];

   			if (infTransaccion(idturno,numerales,nivel)==true){
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

   				Intent i=new Intent(CierreTurno.this, MainActivity.class);
   			 Toast.makeText(getApplicationContext(),"Turno Cerrado con Exito!", Toast.LENGTH_LONG).show();
   				startActivity(i);

              }else{
                 Toast.makeText(getApplicationContext(),"Informacion no almacenada en servidor, Vuelva a internarlo", Toast.LENGTH_SHORT).show();
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
}
