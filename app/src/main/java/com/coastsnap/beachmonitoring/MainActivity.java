package com.coastsnap.beachmonitoring;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.coastsnap.beachmonitoring.ui.home.HomeFragment;
import com.coastsnap.beachmonitoring.util.ErrorAlert;
import com.coastsnap.beachmonitoring.util.SuccessAlert;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    // Configuraciones servidor FTP
    private static final String SERVER = "200.10.147.17";             // cambiar por direccion del servidor FTP del proyecto CoastSnap.
    private static final String USERNAME = "coastsnap";                // cambiar por usuario FTP estblecido para el proyecto.
    private static final String PASSWD = "P4ss#V4ld.";                 // cambiar por pass del usuario FTP
    public final String APP_TAG = "SnapCoast App";
    // Permisos de la aplicacion
    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    // variables globales
    private ArrayList<Double> latLongImg;
    private ErrorAlert errorAlert;
    private SuccessAlert successAlert;
    FusedLocationProviderClient fusedLocationProviderClient;
    private TextureView textureView;
    private ImageButton takePictureBtn;
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        errorAlert = new ErrorAlert(this);
        successAlert = new SuccessAlert(this);

        textureView = findViewById(R.id.view_finder);
        takePictureBtn = findViewById(R.id.imgCapture);

        if (allPermissionsGranted()) {
            Log.d(APP_TAG, "Permisos otorgados!");
            // inicializa la camara si los permisos han sido otorgados por el usuario.
            startCamera();
            latLongImg = getCurrentLocation();

            directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CoastSnap-Valdivia");

            Log.d(APP_TAG, directory.toString());

            if (directory.exists()){
                //sendFileDirectoryToFragment(directory.toString());
                System.out.println("Directorio ya existe!");
                Log.d(APP_TAG, "Directorio ya existe!");
            } else {
                directory.mkdirs();
                if (directory.isDirectory()){
                    //sendFileDirectoryToFragment(directory.toString());
                    System.out.println("Directorio creado exitosamente");
                    Log.d(APP_TAG, "Directorio creado exitosamente");
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Falla al crear directorio")
                            .setMessage("Falla al crear el directorio especificado.\nPath: " + directory.toString() +"\nMkdirs: " + directory.mkdirs())
                            .show();
                }
            }

        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    /**
     * El metodo permite obtener un arreglo con la latitud y lognitud provista por el LocationManager a través
     * de los permisos de ubicación de la aplicación. Se corrobora que el permiso ACCESS_FINE_lOCATION esté
     * dado para poder obtener los valores. El metodo lanza una excepcion si no se puede acceder a los servicios
     * de ubicacion del dispositivo movil.
     *
     * @return latLong: arreglo conformado por latitud y longitud
     */
    private ArrayList<Double> getCurrentLocation() {

        final ArrayList<Double> latLong = new ArrayList<>();  //arreglo para almacenar latitud y longitud

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new ErrorAlert(this).showErrorDialog("No hay permisos suficientes", "Por favor, revisar los permisos de obtención de ubicación para que la aplicación pueda funcionar correctamente!");
            Log.d(APP_TAG, "No hay permisos suficientes");

        } else if (isLocationEnabled()) {
            Log.d(APP_TAG, "Servicios de ubicación habilitados");
            LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int lastestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitude = locationResult.getLocations().get(lastestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(lastestLocationIndex).getLongitude();

                        latLong.add(latitude);
                        latLong.add(longitude);

                        Log.d(APP_TAG, "Se ha agregado la latitud y longitud al arreglo!");

                        Log.d(APP_TAG, "Latitud: " + latitude);
                        Log.d(APP_TAG, "Longitud: " + longitude);

                        // Se muestra el arreglo de latitud y longitud
                        Log.d(APP_TAG, latLong.toString());
                    }
                }
            }, Looper.getMainLooper());
        } else {
            Log.d(APP_TAG, "Servicios de ubicación no se encuentran habilitados!");
            new AlertDialog.Builder(this)
                    .setTitle("Servicios de ubicación no se encuentran habilitados!")
                    .setMessage("Por favor revisar que los servicios de ubicación estén habilitados para la aplicación.")
                    .setPositiveButton("Ajustes", (dialog, which) -> this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Probar este cambio!!!
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(new HomeFragment());*/
                        }
                    })
                    .show();
        }

        return latLong;
    }

    private void startCamera() {
        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen

        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen)
                .build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                output -> {
                    ViewGroup parent = (ViewGroup) textureView.getParent();
                    parent.removeView(textureView);
                    parent.addView(textureView, 0);

                    textureView.setSurfaceTexture(output.getSurfaceTexture());
                    updateTransform();
                });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (isLocationEnabled()){
            takePictureBtn.setOnClickListener(v -> {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

                File file = new File(directory + "/" + "IMG_" + timeStamp + ".jpeg");
                Log.d(APP_TAG, "Directorio: " + directory.toString());
                Log.d(APP_TAG, "Ruta final del archivo: " + file.getAbsolutePath());

                // Se agrega metadata
                ImageCapture.Metadata metadata = new ImageCapture.Metadata();
                metadata.location = new Location("taken picture");
                try {
                    if (latLongImg.isEmpty()) {
                        Log.d(APP_TAG, "Arreglo latLong esta vacio!");
                        new AlertDialog.Builder(this)
                                .setTitle("No se pudo obtener la ubicacion del dispositivo!")
                                .setMessage("Por favor revisar que los servicios de ubicación estén habilitados para la aplicación.")
                                .setPositiveButton("Ajustes", (dialog, which) -> this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                                .setNegativeButton("OK", null)
                                .show();
                    } else {
                        metadata.location.setLatitude(latLongImg.get(0));
                        metadata.location.setLongitude(latLongImg.get(1));
                        Log.d(APP_TAG, metadata.location.toString());

                        imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                            @Override
                            public void onImageSaved(@NonNull File file) {
                                String msg = "Imagen guardad en: " + file.getAbsolutePath();
                                //sendFileNameToFragment("IMG_" + timeStamp + ".jpeg");
                                Log.d(APP_TAG, "Imagen guardada correctamente en " + msg);
                                successAlert.successDialog("Imagen guardada correctamente!", msg, android.R.drawable.ic_menu_camera);

                            }
                            @Override
                            public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                                if (cause != null) {
                                    Log.d(APP_TAG, "Falla al capturar la imagen debido a: " + cause.toString());
                                    String imageCaptureErrorMsg = "Falla al capturar la imagen debido a: " + cause.toString();
                                    errorAlert.showErrorDialog("Falla al capturar la imagen", imageCaptureErrorMsg);
                                }
                            }
                        }, metadata);
                    }
                } catch (Exception e) {
                    new ErrorAlert(this).showErrorDialog("Indice inválido", e.getMessage());
                    Log.d(APP_TAG, e.getMessage());
                }
            });

        } else {
            Log.d(APP_TAG, "Servicios de ubicación no se encuentran habilitados!");
            new AlertDialog.Builder(this)
                    .setTitle("Servicios de ubicación no se encuentran habilitados!")
                    .setMessage("Por favor revisar que los servicios de ubicación estén habilitados para la aplicación.")
                    .setPositiveButton("Ajustes", (dialog, which) -> this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Probar este cambio!!!
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(new HomeFragment());*/
                        }
                    })
                    .show();
        }

        //bind to lifecycle:
        CameraX.bindToLifecycle(this, preview, imgCap);

    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
                try {
                    latLongImg = getCurrentLocation();
                } catch (Exception e) {
                    String eMessage = "Falla al querer obtener al ubicacion del dispositivo, por favor verifique\nque los servicios de ubicacion esten habilitados para la aplicación";
                    Log.d(APP_TAG, "Desde onRequestPermissionResult: " + eMessage);
                   // new ErrorAlert(this).showErrorDialog("Servicios de ubicación deshabilitados", eMessage);
                }
                directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CoastSnap-Valdivia");

                Log.d(APP_TAG, directory.toString());

                if (directory.exists()){
                    //sendFileDirectoryToFragment(directory.toString());
                    System.out.println("Directorio ya existe!");
                    Log.d(APP_TAG, "Directorio ya existe!");
                } else {
                    directory.mkdirs();
                    if (directory.isDirectory()){
                        //sendFileDirectoryToFragment(directory.toString());
                        System.out.println("Directorio creado exitosamente");
                        Log.d(APP_TAG, "Directorio creado exitosamente");
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Falla al crear directorio")
                                .setMessage("Falla al crear el directorio especificado.\nPath: " + directory.toString() +"\nMkdirs: " + directory.mkdirs())
                                .show();
                    }
                }

            } else {
                String requestPermissionFailedMsg = "Permisos denegados por el usuario.\nPor favor, revisar los permisos de la aplicación para el correcto funcionamiento de la app.";
                errorAlert.showErrorDialog("Permissions no otorgados por usuario!", requestPermissionFailedMsg);
            }
        }
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent dashboardIntent = new Intent(this, DashboardActivity.class);
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(dashboardIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onResume() {
        if (isLocationEnabled()) {
            startCamera();
            try {
                latLongImg = getCurrentLocation();
            } catch (Exception e) {
                String eMessage = "Falla al querer obtener al ubicacion del dispositivo, por favor verifique\nque los servicios de ubicacion esten habilitados para la aplicación";
                Log.d(APP_TAG, "Desde onRequestPermissionResult: " + eMessage);
                // new ErrorAlert(this).showErrorDialog("Servicios de ubicación deshabilitados", eMessage);
            }
            directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CoastSnap-Valdivia");

            Log.d(APP_TAG, directory.toString());

            if (directory.exists()) {
                //sendFileDirectoryToFragment(directory.toString());
                System.out.println("Directorio ya existe!");
                Log.d(APP_TAG, "Directorio ya existe!");
            } else {
                directory.mkdirs();
                if (directory.isDirectory()) {
                    //sendFileDirectoryToFragment(directory.toString());
                    System.out.println("Directorio creado exitosamente");
                    Log.d(APP_TAG, "Directorio creado exitosamente");
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Falla al crear directorio")
                            .setMessage("Falla al crear el directorio especificado.\nPath: " + directory.toString() + "\nMkdirs: " + directory.mkdirs())
                            .show();
                }
            }
        }else {
            Log.d(APP_TAG, "Servicios de ubicación no se encuentran habilitados!");
            new AlertDialog.Builder(this)
                    .setTitle("Servicios de ubicación no se encuentran habilitados!")
                    .setMessage("Por favor revisar que los servicios de ubicación estén habilitados para la aplicación.")
                    .setPositiveButton("Ajustes", (dialog, which) -> this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Probar este cambio!!!
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(new HomeFragment());*/
                        }
                    })
                    .show();
        }
        super.onResume();
    }


    @Override
    protected void onRestart() {

        if (isLocationEnabled()) {
            startCamera();
            try {
                latLongImg = getCurrentLocation();
            } catch (Exception e) {
                String eMessage = "Falla al querer obtener al ubicacion del dispositivo, por favor verifique\nque los servicios de ubicacion esten habilitados para la aplicación";
                Log.d(APP_TAG, "Desde onRequestPermissionResult: " + eMessage);
                // new ErrorAlert(this).showErrorDialog("Servicios de ubicación deshabilitados", eMessage);
            }
            directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CoastSnap-Valdivia");

            Log.d(APP_TAG, directory.toString());

            if (directory.exists()) {
                //sendFileDirectoryToFragment(directory.toString());
                System.out.println("Directorio ya existe!");
                Log.d(APP_TAG, "Directorio ya existe!");
            } else {
                directory.mkdirs();
                if (directory.isDirectory()) {
                    //sendFileDirectoryToFragment(directory.toString());
                    System.out.println("Directorio creado exitosamente");
                    Log.d(APP_TAG, "Directorio creado exitosamente");
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Falla al crear directorio")
                            .setMessage("Falla al crear el directorio especificado.\nPath: " + directory.toString() + "\nMkdirs: " + directory.mkdirs())
                            .show();
                }
            }
        }else {
            Log.d(APP_TAG, "Servicios de ubicación no se encuentran habilitados!");
            new AlertDialog.Builder(this)
                    .setTitle("Servicios de ubicación no se encuentran habilitados!")
                    .setMessage("Por favor revisar que los servicios de ubicación estén habilitados para la aplicación.")
                    .setPositiveButton("Ajustes", (dialog, which) -> this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Probar este cambio!!!
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(new HomeFragment());*/
                        }
                    })
                    .show();
        }
        super.onRestart();
    }

    @Override
    protected void onPause() {
        if (isLocationEnabled()) {
            startCamera();
            try {
                latLongImg = getCurrentLocation();
            } catch (Exception e) {
                String eMessage = "Falla al querer obtener al ubicacion del dispositivo, por favor verifique\nque los servicios de ubicacion esten habilitados para la aplicación";
                Log.d(APP_TAG, "Desde onRequestPermissionResult: " + eMessage);
                // new ErrorAlert(this).showErrorDialog("Servicios de ubicación deshabilitados", eMessage);
            }
            directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CoastSnap-Valdivia");

            Log.d(APP_TAG, directory.toString());

            if (directory.exists()) {
                //sendFileDirectoryToFragment(directory.toString());
                System.out.println("Directorio ya existe!");
                Log.d(APP_TAG, "Directorio ya existe!");
            } else {
                directory.mkdirs();
                if (directory.isDirectory()) {
                    //sendFileDirectoryToFragment(directory.toString());
                    System.out.println("Directorio creado exitosamente");
                    Log.d(APP_TAG, "Directorio creado exitosamente");
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Falla al crear directorio")
                            .setMessage("Falla al crear el directorio especificado.\nPath: " + directory.toString() + "\nMkdirs: " + directory.mkdirs())
                            .show();
                }
            }
        }else {
            Log.d(APP_TAG, "Servicios de ubicación no se encuentran habilitados!");
            new AlertDialog.Builder(this)
                    .setTitle("Servicios de ubicación no se encuentran habilitados!")
                    .setMessage("Por favor revisar que los servicios de ubicación estén habilitados para la aplicación.")
                    .setPositiveButton("Ajustes", (dialog, which) -> this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Probar este cambio!!!
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.show(new HomeFragment());*/
                        }
                    })
                    .show();
        }
        super.onPause();
    }
}