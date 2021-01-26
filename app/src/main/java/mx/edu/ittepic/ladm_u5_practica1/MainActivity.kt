package mx.edu.ittepic.ladm_u5_practica1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    lateinit var locacion : LocationManager
    var listaUbicaciones = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
        } else {
            baseRemota.collection("tecnologico")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        nombre.setText("ERROR: ${firebaseFirestoreException.message}")
                        return@addSnapshotListener
                    }
                    posicion.clear()
                    listaUbicaciones.clear()
                    for (document in querySnapshot!!) {
                        var data = Data()
                        data.nombre = document.getString("nombre").toString()
                        data.punto1 = document.getGeoPoint("punto1")!!
                        data.punto2 = document.getGeoPoint("punto2")!!
                        listaUbicaciones.add(data.toString())
                        posicion.add(data)
                    }
                    lista.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_expandable_list_item_1,
                        listaUbicaciones
                    )
                }

            locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var oyente = Oyente(this)
            locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, oyente)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0){
            baseRemota.collection("tecnologico")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        textView.setText("ERROR: ${firebaseFirestoreException.message}")
                        return@addSnapshotListener
                    }
                    var resultado = ""
                    posicion.clear()
                    listaUbicaciones.clear()
                    for (document in querySnapshot!!) {
                        var data = Data()
                        data.nombre = document.getString("nombre").toString()
                        data.punto1 = document.getGeoPoint("punto1")!!
                        data.punto2 = document.getGeoPoint("punto2")!!
                        resultado += data.toString() + "\n\n"
                        listaUbicaciones.add(data.toString())
                        posicion.add(data)
                    }
                    lista.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_expandable_list_item_1,
                        listaUbicaciones
                    )
                }

            locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var oyente = Oyente(this)
            locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, oyente)
        }
    }
}

class Oyente(puntero: MainActivity): LocationListener {
    var p = puntero
    override fun onLocationChanged(location: Location) {
        p.position.setText("${location.latitude}, ${location.longitude}")
        p.nombre.setText("No tengo idea :(")
        var geoPosicionGPS = GeoPoint(location.latitude, location.longitude)
        for(item in p.posicion){
            if(item.estoyEn(geoPosicionGPS)){
                p.nombre.setText("${item.nombre}")
            }
        }
    }
}