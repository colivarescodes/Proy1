package com.pyclimitada.pyc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class abastecimiento_paso1 extends Activity {
	Bundle datosAnterior;
	TextView nombreUser,patente;
	baseDatos usuarios;
	String nombreCompleto, idUser, equipo, idEquipo,nbombas;
	Button boton;
	int numero = 0;
	RadioGroup radioGroup, radioGroup3, radioGroup2;
	private RadioButton radioButton;
	int tipoSeleccionado = 1;
	int bbaSeleccionada = 1;
	int dispositivo = 1;
	
	protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.abastecimiento_paso1);
	        
	        usuarios = new baseDatos(this, "DBUsuarios", null, 1);
	        

	        nombreUser = (TextView)this.findViewById(R.id.nombreUser);
	        patente = (TextView)this.findViewById(R.id.patente);
	        boton= (Button) findViewById(R.id.button1);
	        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1); //Bomba
	        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2); //Dispositivo
	        radioGroup3 = (RadioGroup) findViewById(R.id.radioGroup3); //tipo
	        
	        
	        //Información traida del anterior
	        datosAnterior = this.getIntent().getExtras();
	        nombreCompleto = datosAnterior.getString("nombreCompleto");
	        idUser = datosAnterior.getString("idUser");
	        equipo = datosAnterior.getString("equipo");
	        idEquipo = datosAnterior.getString("idEquipo");
	        nbombas = datosAnterior.getString("nbombas");
	        
	        numero = Integer.parseInt(nbombas);
	        

	        agregarBombas(numero);
        	String nombreStr = new String(nombreCompleto);

        	nombreStr = nombreStr.substring(0,20);
        	nombreUser.setText(nombreStr+"...");
//	        nombreUser.setText(datosAnterior.getString("nombreCompleto"));
	        patente.setText(datosAnterior.getString("equipo"));

	        
	        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
            {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case 1:
                        	bbaSeleccionada = 1;
                            // do operations specific to this selection
                        break;

                        case 2:
                        	bbaSeleccionada = 2;
                            // do operations specific to this selection
                        break;
                    }
                }
            });	        
	
	        radioGroup2.setOnCheckedChangeListener(new OnCheckedChangeListener() 
            {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case R.id.radio0:
                        	dispositivo = 1;
                            // do operations specific to this selection
                        break;

                        case R.id.radio1:
                        	dispositivo = 2;
                            // do operations specific to this selection
                        break;
                    }
                }
            });	   	        
	        
	        radioGroup3.setOnCheckedChangeListener(new OnCheckedChangeListener() 
            {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case R.id.radio0:
                        	tipoSeleccionado = 1;
                            // do operations specific to this selection
                        break;

                        case R.id.radio1:
                        	tipoSeleccionado = 2;
                            // do operations specific to this selection
                        break;
                    }
                }
            });
	        
	        
	        boton.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
	        		//int selectedId = radioGroup3.getCheckedRadioButtonId();
	        		 
	    			// find the radiobutton by returned id
	        		//radioButton = (RadioButton) findViewById(selectedId);
	     
	        		
	        		
	    			/*Toast.makeText(getApplicationContext(),
	    					"Tipo: "+tipoSeleccionado, Toast.LENGTH_SHORT).show();*/
	        		
	        		
	        		String tipoSeleccionado2 = "";
	        		tipoSeleccionado2= Integer.toString(tipoSeleccionado);

	        		String bbaSeleccionada2 = "";
	        		bbaSeleccionada2= Integer.toString(bbaSeleccionada);

	        		String dispositivo2 = "";
	        		dispositivo2= Integer.toString(dispositivo);
	        		
	        		
	        		Intent i=new Intent(abastecimiento_paso1.this, abastecimiento_paso2.class);
    				i.putExtra("equipo",equipo);
    				i.putExtra("idEquipo",idEquipo);
    				i.putExtra("idUser",idUser);
    				i.putExtra("nombreCompleto",nombreCompleto);
    				i.putExtra("nbombas",nbombas);
    				i.putExtra("forma",tipoSeleccionado2);
    				i.putExtra("tipo",bbaSeleccionada2);
    				i.putExtra("dispositivo",dispositivo2);
	        		startActivity(i);
	        	}
	        });
	        

	        
	}
	public void agregarBombas(int bombas){
		bombas = bombas+1;
		//LinearLayout ll = new LinearLayout(this);
        //ll.setOrientation(LinearLayout.VERTICAL);
        for(int i = 1; i < bombas; i++){
        	RadioButton radioButton = new RadioButton(this);
    	    radioButton.setLayoutParams 
    	      (new LayoutParams 
    	      (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	    radioButton.setText("Bomba "+i);
    	    radioButton.setId(i);
    	    radioGroup.addView(radioButton);
        }
	}
}
