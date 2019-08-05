package com.ls.celtica.lsdelivryls.Clients;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.DeviceConfig;
import com.ls.celtica.lsdelivryls.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClientLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static LatLng pos=null;
    boolean select=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            //region Revenir a au Deviceconfig ..
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //endregion
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_client_location);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            ((TextView) findViewById(R.id.map_valider)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Ajouter le client ..

                    Intent i = new Intent();
                    i.putExtra("Latitude", pos.latitude + "");
                    i.putExtra("Longitude", pos.longitude + "");

                    setResult(RESULT_OK, i);
                    finish();

                }
            });
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);


        // Add a marker in Algeria and move the camera
         LatLng Dz = new LatLng(36.479691, 2.80355);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(Dz));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Dz, 4.0f));




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(!select){
                    pos=latLng;
                    mMap.addMarker(new MarkerOptions().position(pos));
                    select=true;
                   // Log.e("Position","latitude:"+pos.latitude+" / longitude: "+pos.longitude);
                }else {
                    Toast.makeText(getApplicationContext(),"Déja Séléctionner ..",Toast.LENGTH_LONG).show();
                }

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                select=false;

                 return false;
            }
        });
    }
}
