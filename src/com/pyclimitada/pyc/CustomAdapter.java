package com.pyclimitada.pyc;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
	 
    private ArrayList<MessageDetails> _data;
    Context _c;
    private String _tipo;
    
    CustomAdapter (ArrayList<MessageDetails> data, Context c, String tipo){
        _data = data;
        _c = c;
        _tipo = tipo;
    }
   
    public int getCount() {
        // TODO Auto-generated method stub
        return _data.size();
    }
    
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return _data.get(position);
    }
 
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    public void otro(int index) {
        _data.remove(index);
        notifyDataSetChanged();
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
         View v = convertView;
         if (v == null)
         {
            LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          
            if(_tipo == "ne"){
            	v = vi.inflate(R.layout.list_no_enviados, null);
            }else{
            	v = vi.inflate(R.layout.list_enviados, null);
            }
         }
 

           TextView tvEquipo = (TextView)v.findViewById(R.id.equipo);
           TextView tvFecha = (TextView)v.findViewById(R.id.fecha);
           TextView tvHora = (TextView)v.findViewById(R.id.hora);
           TextView tvBomba = (TextView)v.findViewById(R.id.textView1);
           TextView tvReceptor = (TextView)v.findViewById(R.id.textView2);
           TextView tvEstado = (TextView)v.findViewById(R.id.estado);
           TextView tvTransaccion = (TextView)v.findViewById(R.id.textView3);
           TextView tvLitros = (TextView)v.findViewById(R.id.litros);
           //Button enviar = (Button)v.findViewById(R.id.button1);
           //Button borrar = (Button)v.findViewById(R.id.button2);
           
           
           MessageDetails msg = _data.get(position);
           //image.setImageResource(msg.icon);
           tvEquipo.setText(msg.equipo);
           tvFecha.setText(msg.fecha);
           tvHora.setText(msg.hora);
           tvBomba.setText(msg.bomba);
           tvReceptor.setText(msg.receptor);
           tvEstado.setText(msg.estado);
           tvTransaccion.setText(msg.transaccion);
           tvLitros.setText(msg.litros);
                        
        return v;
}
}
