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

import com.pyclimitada.pyc.abastecimiento_paso3.asyncEnvio;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Inicioturno extends Activity {

	String nombreCompleto, idUser, equipo, idEquipo,nbombas,fActual,nBombas, turnoActual,Numerales,Bomba1,Bomba2,Nivel;
	String forma, tipo, dispositivo,equipoCliente,idEquipoCliente;
	TextView nombreUser;
	baseDatos usuarios;
	Bundle datosAnterior;
	Button boton;
	Httppostaux post;
	String IP_Server="http://www.maitech.cl";//IP DE NUESTRO PC
	String URL_connect="http://www.maitech.cl/pyc/rTur.php";//ruta en donde estan nuestros archivos
	EditText numeralInicial1,nivelEdit; 
	
	//Especificos
	TextView fecha, turno, responsable, estacionText;
	
	boolean result_back;
	private ProgressDialog pDialog;	 
	
	LinearLayout ll;
	EditText et;
	int nBombasD;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.inicioturno);
		
        post=new Httppostaux();
        boton= (Button) findViewById(R.id.button1);
        
        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
        
        //Especificos
        fecha = (TextView)this.findViewById(R.id.textView3);
        turno = (TextView)this.findViewById(R.id.textView5);
        responsable = (TextView)this.findViewById(R.id.textView7);
        estacionText = (TextView)this.findViewById(R.id.textView9);
        numeralInicial1 = (EditText)this.findViewById(R.id.editText1);
        nivelEdit = (EditText)this.findViewById(R.id.editText2);
        
        usuarios = new baseDatos(this, "DBUsuarios", null, 1);
        
        
        //Información traida del anterior
        datosAnterior = this.getIntent().getExtras();
        nombreCompleto = datosAnterior.getString("nombreCompleto");
        idUser = datosAnterior.getString("idUser");
        equipo = datosAnterior.getString("nombreEquipo");
        idEquipo = datosAnterior.getString("idEquipo");
        nBombas = datosAnterior.getString("nBombas");
        nBombasD = Integer.parseInt(nBombas);
        
    	String nombreStr = new String(nombreCompleto);

    	nombreStr = nombreStr.substring(0,20);
    	nombreUser.setText(nombreStr+"...");
    	fecha.setText(fechaNormal());
    	int tActual = indica_turno();
    	turnoActual = Integer.toString(tActual);
    	if(tActual == 1){
    		turno.setText("A (00:00 - 07:29)");
    	}else if(tActual == 2){
    		turno.setText("B (07:30 - 19:29)");
    	}else if(tActual == 3){
    		turno.setText("C (19:30 - 23:59)");
    	}
    	responsable.setText(nombreCompleto);
    	estacionText.setText(equipo);
    	
    	
    	if(nBombasD > 1){
    	ll = (LinearLayout)findViewById(R.id.linearLayoutEdit);
    	et = new EditText(this);
        //et.setText("Dynamic EditText!");
        //et.setMinLines(1);
        //et.setMaxLines(1);
    	et.setId(2);
        et.setHint("Numeral Inicial Bomba 2");
        et.setPadding(45, 5, 0, 0);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setHeight(60);
                
        et.setCompoundDrawablesWithIntrinsicBounds( R.drawable.numeral, 0, 0, 0);
        et.setCompoundDrawablePadding(16);
        
        et.setBackgroundColor(Color.parseColor("#EEEEEE"));
        ll.addView(et);
    	}

        boton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view){
        		if(isOnline()){
        			//Envia a Asynctask
        			//AlmacenaTransaccion("1");
        			Bomba1 = numeralInicial1.getText().toString();
        			Nivel = nivelEdit.getText().toString();
        			if(nBombasD == 2){
        				
        				Bomba2 = String.valueOf(et.getText());
        				Numerales = Bomba1+","+Bomba2;
        			}else{
        				Numerales = Bomba1;
        			}
        			

        			new asyncEnvio().execute(idEquipo,nombreCompleto,idUser,fechaNormal(),turnoActual,Numerales,Nivel);
        		//	new asyncEquipos().execute(idEquipo,bPatente.getString("user"),nombreCompleto,idUser);
        		}else{
        			Toast.makeText(getApplicationContext(),"Sin conexion a internet, Revise antes de continuar", Toast.LENGTH_LONG).show();
        			//AlmacenaTransaccion("0");
        			//Graba directamente con valor 0
        		}
        	}
        });
    	
	}
	private void AlmacenaTransaccion(String idturno, String fechaFinal, String turno, String estacion, String responsable, String num_inicial, String nivel_inicial ){
	
		SQLiteDatabase db = usuarios.getWritableDatabase();
		db.execSQL("INSERT INTO turno(idturno,fecha,turno,estacion,responsable,num_inicial,nivel_inicial,estado)"+
				   "VALUES ('"+idturno+"','"+fechaFinal+"','"+turno+"','"+estacion+"','"+responsable+"','"+num_inicial+"','"+nivel_inicial+"','0')"
				);
	}
    public boolean infTransaccion(String equipo, String idUser2,String fechaActual2,String turno2,String numeral2,String nivel2) {
    	int eqstatus = -1;
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("estacion",equipo));
		postparameters2send.add(new BasicNameValuePair("iduser",idUser2));
		postparameters2send.add(new BasicNameValuePair("fecha",fechaActual2));    		
		postparameters2send.add(new BasicNameValuePair("turno",turno2));
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
            	String idTurnoServer =Integer.toString(eqstatus);
            	AlmacenaTransaccion(idTurnoServer,fechaActual2,turno2,equipo,idUser2,numeral2,nivel2);
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
   	String equipoFinal;
   	String nombreCompleto;
   	String idUser;
   	
   	String fechaActual;
   	String turno;
   	String numerales;
   	String nivel;
   	
       protected void onPreExecute() {
       	//para el progress dialog
           pDialog = new ProgressDialog(Inicioturno.this);
           pDialog.setMessage("Iniciando Turno...");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(false);
           pDialog.show();
       }

		protected String doInBackground(String... params) {

			equipoFinal = params[0];
			nombreCompleto = params[1];
			idUser = params[2];
			
			fechaActual  = params[3];
			turno = params[4];
			numerales = params[5];
			nivel = params[6];

			if (infTransaccion(equipoFinal,idUser,fechaActual,turno,numerales,nivel)==true){
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

				Intent i=new Intent(Inicioturno.this, home.class);
				i.putExtra("idEquipo",equipoFinal);
				
				i.putExtra("nombreCompleto",nombreCompleto);
				i.putExtra("idUser",idUser);
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
	
	
	
    public String fechaNormal(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        GregorianCalendar todayDate = new GregorianCalendar();
        String fActual = sdf.format(todayDate.getTime());
        return fActual;
    }
    
    public int indica_turno(){
		
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
	
}
