package com.pyclimitada.pyc;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	EditText user;
	EditText pass;
	Button boton;
	Httppostaux post;
	String nombreCompleto;
    String IP_Server="http://www.maitech.cl";//IP DE NUESTRO PC
    String URL_connect="http://www.maitech.cl/pyc/clr.php";//ruta en donde estan nuestros archivos
    private static final String TAG_TBL = "users";
  
    boolean result_back;
    private ProgressDialog pDialog;
    
    baseDatos usuarios;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
        post=new Httppostaux();
        user= (EditText) findViewById(R.id.editText1);
        pass= (EditText) findViewById(R.id.editText2);
        boton= (Button) findViewById(R.id.button1);
        
      //Abrimos la base de datos 'DBUsuarios' en modo escritura
        usuarios = new baseDatos(this, "DBUsuarios", null, 1);
 
        
        
        boton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view){

        		//Extreamos datos de los EditText
        		String usuario=user.getText().toString();
        		String passw=pass.getText().toString();
        		//verTodos(usuario,passw);       		
        		
        		//verificaRegistrosUsuarios(usuario, passw);
        		
        		//verificamos si estan en blanco
        		if( checklogindata( usuario , passw )==true){

        			//si pasamos esa validacion ejecutamos el asynctask pasando el usuario y clave como parametros
        			
        		new asynclogin().execute(usuario,passw);        		               
        			      		
        		
        		}else{
        			//si detecto un error en la primera validacion vibrar y mostrar un Toast con un mensaje de error.
        			err_login();
        		}      		
        	}
        });

	}
	public boolean verTodos(String username ,String password ){
		
		String claveEncriptada = md5(password);
		
		SQLiteDatabase db = usuarios.getWritableDatabase();
		Cursor fila = db.rawQuery(
                "select rut, clave from Usuarios where rut='"+username+"' and clave='"+claveEncriptada+"'", null);		
		fila.moveToFirst();
		if (fila.moveToFirst()) {
	        do {
	            //Todo td = new Todo();
	            /*td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
	            td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
	            td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
	 
	            // adding to todo list
	            todos.add(td);*/
	        	String datito = "pyc1380";
	        	String clencr = md5(datito);
	        	Log.e("Dato: ",fila.getString(0)+" - "+clencr);
	        } while (fila.moveToNext());
	    }
		return true;
		
	}
	
	public boolean verificaRegistrosUsuarios(String username ,String password){
		
		SQLiteDatabase db = usuarios.getWritableDatabase();
		Cursor fila = db.rawQuery(
                "select count(*) from Usuarios", null);
		fila.moveToFirst();
		int totalResultados = fila.getInt(0);

        if(totalResultados > 0){
			if(consultaUser(username, password) == false){
				//return false;
				Log.e("Retorno: ", "Malo");
				return false;

			}
		}else{
			//Debe solicitar registros
			//Toast.makeText(getApplicationContext(),"Debe Buscar registros", Toast.LENGTH_SHORT).show();
			if(loginstatus(username, password)){
				//Toast.makeText(getApplicationContext(),"Cargado con Exito!", Toast.LENGTH_SHORT).show();
				if(consultaUser(username, password)){
					return true;
				}else{
					Log.e("Retorno: ", "Malo");
					return false;
				}
				//return true;
			}else{
				//Toast.makeText(getApplicationContext(),"Error al cargar", Toast.LENGTH_SHORT).show();
				return false;
			}
		}

        db.close();	
		
		return true;
	}
	public static String md5(String s) 
	{
	    MessageDigest digest;
	    try 
	    {
	        digest = MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes(),0,s.length());
	        String hash = new BigInteger(1, digest.digest()).toString(16);
	        return hash;
	    } 
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return "";
	}
	public boolean consultaUser(String us, String ps){
		
		String md5 = md5(ps);
		
		SQLiteDatabase db = usuarios.getWritableDatabase();
		Cursor fila = db.rawQuery(
                "select count(*) from Usuarios where rut='"+us+"' AND clave='"+md5+"'", null);		
		fila.moveToFirst();
		int totalResultados = fila.getInt(0);
		if(totalResultados > 0){
			return true;
		}else{
			return false;
		}
		//return false;
		
	}
	
	
    //vibra y muestra un Toast
    public void err_login(){
    	Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    vibrator.vibrate(200);
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Error: Nombre de usuario o password incorrectos", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }
    
    public boolean loginstatus(String username ,String password ) {
    	int logstatus=-1;
       	String id;
    	String dameNombre;
    	String dameRut; 
       	String cla;
       	String cargo;
       	String idHard, nomHard, tipoHard, nBombas, idec, nomec, formaec;
       	
       	Log.e("Leyendo desde afuera:","ok");
       	
       	SQLiteDatabase db = usuarios.getWritableDatabase();

    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
		    		postparameters2send.add(new BasicNameValuePair("usuario",username));
		    		postparameters2send.add(new BasicNameValuePair("password",password));

      		JSONArray jdata=post.getserverdata(postparameters2send, URL_connect);

		    SystemClock.sleep(950);
	    		
		   //si lo que obtuvimos no es null
		     	if (jdata!=null && jdata.length() > 0){

		     		try{
	
		     		//0 = users, 1=equipos
		     		JSONObject users = jdata.getJSONObject(0);
		     		JSONObject hard = jdata.getJSONObject(1);
		     		JSONObject equiposCliente = jdata.getJSONObject(2);
		     		
		     		//FUNCION PARA OBTENER USUARIOS
		     		JSONArray jsonArr=users.getJSONArray("users");
		     		//Log.e("users: ", "Devuelve: "+jsonArr.length());
		     		
		     		for (int i = 0; i < jsonArr.length(); i++) {
		     	        JSONObject json_users = jsonArr.getJSONObject(i);
		     	        id 			= json_users.getString("id");
		    			dameNombre 	= json_users.getString("nombre");
		    			dameRut 	= json_users.getString("rut");
		    			cla 		= json_users.getString("clave");
		    			cargo 		= json_users.getString("cargo");
		    			db.execSQL("INSERT INTO Usuarios (id, rut, nombre, clave, cargo) " +
		                           "VALUES ('" + id + "', '" + dameRut +"', '" + dameNombre +"', '" + cla +"', " + cargo +")");
		    			String resultadoFinal = id+" "+dameNombre+" "+dameRut+" "+cla+" "+cargo;
		    			Log.e("Usuarios: ", resultadoFinal);
		     		}
		     		//FUNCION PARA OBTENER EQUIPOS
		     		JSONArray jsonHard=hard.getJSONArray("hard");
		     		//Log.e("users: ", "Devuelve: "+jsonArr.length());
		     		
		     		for (int j = 0; j < jsonHard.length(); j++) {
		     	        JSONObject json_hard = jsonHard.getJSONObject(j);
		     	        idHard 			= json_hard.getString("id");
		    			nomHard 	= json_hard.getString("nombre");
		    			tipoHard 	= json_hard.getString("tipo");
		    			nBombas 		= json_hard.getString("nbombas");
		    			
		    			db.execSQL("INSERT INTO Equipos (id, nombre, nbombas, tipo) " +
		                           "VALUES ('" + idHard + "', '" + nomHard +"', '" + nBombas +"', '" + tipoHard +"')");
		    			//cargo 		= json_users.getString("cargo");
		    			String resultadoFinalEquipos = idHard+" "+nomHard+" "+tipoHard+" "+nBombas;
		    			Log.e("Equipos: ", resultadoFinalEquipos);
		     		}
		     		
		     		//FUNCION PARA OBTENER EQUIPOS
		     		JSONArray jsonec=equiposCliente.getJSONArray("equipos");
		     		//Log.e("users: ", "Devuelve: "+jsonArr.length());
		     		
		     		for (int j = 0; j < jsonec.length(); j++) {
		     	        JSONObject json_ec = jsonec.getJSONObject(j);
		     	        idec 			= json_ec.getString("id");
		    			nomec 	= json_ec.getString("nombre");
		    			formaec 	= json_ec.getString("forma");
		    			
		    			db.execSQL("INSERT INTO EquiposCliente (id, nombre, forma) " +
		                           "VALUES ('" + idec + "', '" + nomec +"', '" + formaec +"')");
		    			//cargo 		= json_users.getString("cargo");
		    			String resultadoFinalEquiposec = idec+" "+nomec+" "+formaec;
		    			Log.e("Equipos Cliente: ", resultadoFinalEquiposec);
		     		}		     	
		     		

		     		}catch(JSONException e){
	    				e.printStackTrace();
	    			}
		     		
		    		return true;
			  }else{	//json obtenido invalido verificar parte WEB.
		    			 Log.e("JSON  ", "ERROR");
			    		return false;
			  }
    	
    }
    
          
    //validamos si no hay ningun campo en blanco
    public boolean checklogindata(String username ,String password ){
    	
    if 	(username.equals("") || password.equals("")){
    	Log.e("Login ui", "checklogindata user or pass error");
    return false;
    
    }else{
    	
    	return true;
    }
    
}           
    
/*		CLASE ASYNCTASK
 * 
 * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
 * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir    
 * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
 * ademas observariamos el mensaje de que la app no responde.     
 */
    
    class asynclogin extends AsyncTask< String, String, String > {
    	 
    	String user,pass;
        protected void onPreExecute() {
        	//para el progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Autenticando....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			user=params[0];
			pass=params[1];
            
			//enviamos y recibimos y analizamos los datos en segundo plano.
    		/*if (loginstatus(user,pass)==true){    		    		
    			return "ok"; //login valido
    		}else{    		
    			return "err"; //login invalido     	          	  
    		}*/
			if (verificaRegistrosUsuarios(user, pass)==true){    		    		
    			return "ok"; //login valido
    		}else{    		
    			return "err"; //login invalido     	          	  
    		}
        	
		}
       
		/*Una vez terminado doInBackground segun lo que halla ocurrido 
		pasamos a la sig. activity
		o mostramos error*/
        protected void onPostExecute(String result) {

           pDialog.dismiss();//ocultamos progess dialog.
           Log.e("onPostExecute=",""+result);
           
           if (result.equals("ok")){

				Intent i=new Intent(MainActivity.this, equipos.class);
				i.putExtra("user",user);
				startActivity(i); 
        	   //ACA ESTA LISTO Y COMPROBADO, PASA A LA SIGUIENTE PAGINA
        	   //Toast.makeText(getApplicationContext(),"comprobado con exito!"+result, Toast.LENGTH_SHORT).show();
            }else{
            	err_login();
            }
            
          }
		
        }
    
    @Override
    public void onBackPressed() {
    }

}
