package mx.edu.ittepic.ladm_u5_practica1

import com.google.firebase.firestore.GeoPoint

class Data {
    var nombre : String = ""
    var punto1 : GeoPoint = GeoPoint(0.0, 0.0)
    var punto2 : GeoPoint = GeoPoint(0.0, 0.0)

    override fun toString(): String {
        return nombre + "\n" +
                punto1.latitude + "," + punto1.longitude + "\n" +
                punto2.latitude + "," + punto2.longitude
    }

    fun estoyEn(posicionActual: GeoPoint): Boolean{
        if ( posicionActual.latitude >= punto1.latitude &&
                posicionActual.latitude <= punto2.latitude){
            if ( invertir(posicionActual.longitude) >= invertir(punto1.longitude) &&
                invertir(posicionActual.longitude) <= invertir(punto2.longitude)){
                return true
            }
        }
        return false
    }

    private fun invertir(valor: Double): Double{
        return valor*-1
    }
}