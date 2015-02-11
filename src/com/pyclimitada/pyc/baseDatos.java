package com.pyclimitada.pyc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class baseDatos extends SQLiteOpenHelper {
	 
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE Usuarios (id INTEGER, rut TEXT, nombre TEXT, clave TEXT, cargo INTEGER)";
    String sqlCreateEquipos = "CREATE TABLE Equipos (id INTEGER, nombre TEXT, nbombas TEXT, tipo INTEGER)";
    String sqlCreateEquiposCliente = "CREATE TABLE EquiposCliente (id INTEGER, nombre TEXT, forma TEXT)";

    String sqlCreateTurno = "CREATE TABLE turno (id integer primary key autoincrement, " +
    											"idturno 		TEXT, " +
    											"fecha 			TEXT, " +
    											"turno 			TEXT, " +
    											"estacion 		TEXT, " +
    											"responsable 	TEXT, " +
    											"num_inicial 	TEXT," +
    											"num_final 		TEXT, " +
    											"nivel_inicial 	TEXT, " +
    											"nivel_final 	TEXT, " +
    											"estado 		TEXT)";
    
    String sqlCreateVentas = "CREATE TABLE Ventas (id integer primary key autoincrement, " +
    											  "fecha TEXT, " +
    											  "idturno TEXT, " +
    											  "iduser TEXT, " +
    											  "estacion TEXT, " +
    											  "bomba TEXT, " +
    											  "dispositivo TEXT, " +
    											  "tipo TEXT, " +
    											  "equipo TEXT, " +
    											  "litros TEXT, " +
    											  "enviado TEXT)";
    
    
    public baseDatos(Context contexto, String nombre,
                               CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreateEquipos);
        db.execSQL(sqlCreateEquiposCliente);
        db.execSQL(sqlCreateTurno);
        db.execSQL(sqlCreateVentas);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.
 
        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
 
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
    }
}
