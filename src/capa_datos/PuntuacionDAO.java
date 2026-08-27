/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package capa_datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author JIMMYSIN
 */
public class PuntuacionDAO {
    private final String rutaArchivo = "mejor_puntuacion.txt";
    public void guardarMejorPuntuacion(int puntos){
        try(FileWriter escritor = new FileWriter(rutaArchivo)){
            escritor.write(String.valueOf(puntos));
        } catch(IOException e){
            System.out.println("Error al guardar la puntuacion: " + e.getMessage());
        }
    }
    
    public int obtenerMejorPuntuacion(){
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return 0;
        }
        try(BufferedReader lector = new BufferedReader(new FileReader(archivo))){
            String linea = lector.readLine();
            if (linea != null && !linea.trim().isEmpty()) {
                return Integer.parseInt(linea.trim());
            }
        }catch(IOException | NumberFormatException e){
            System.out.println("Error al leer la puntuacion: " + e.getMessage());
        }
        return 0;
    }
}
