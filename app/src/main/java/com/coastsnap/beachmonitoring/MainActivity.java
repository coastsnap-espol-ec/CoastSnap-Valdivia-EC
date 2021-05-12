package com.coastsnap.beachmonitoring;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.coastsnap.beachmonitoring.util.ErrorAlert;
import com.coastsnap.beachmonitoring.util.FTPUploader;
import com.coastsnap.beachmonitoring.util.SuccessAlert;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
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
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION"};
    // variables globales
    private ArrayList<Double> latLongImg;
    private ErrorAlert errorAlert;
    private SuccessAlert successAlert;
    FusedLocationProviderClient fusedLocationProviderClient;
    private TextureView textureView;
    private ImageButton takePictureBtn;
    private FTPUploader ftpUploader;

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
            // inicializa la camara si los permisos han sido otorgados por el usuario.
            startCamera();
            // inicializa la conexion con el servidor si los permisos de conexion son provistos por el usuario.
            try {
                ftpUploader = new FTPUploader(SERVER, USERNAME, PASSWD);
                Log.d(APP_TAG, "Conexion existosa!");
            } catch (Exception e) {
                new ErrorAlert(this).showErrorDialog("Error connecting to server", e.getMessage());
                Log.d(APP_TAG, e.getMessage());
            }
            latLongImg = getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    /**
     * El metodo permite obtener un arreglo con la latitud y lognitud provista por el LocationManager a través
     * de los permisos de ubicación de la aplicación. Se corrobora que el permiso ACCESS_FINE_lOCATION esté
     * dado para poder obtener los valores.
     *
     * @return latLong: arreglo conformado por latitud y longitud
     */
    private ArrayList<Double> getCurrentLocation() {

        /*try {
                gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                new ErrorAlert(this).showErrorDialog("Failure while getting info from GPS_PROVIDER", e.getMessage());
            }
            try {
                networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                new ErrorAlert(this).showErrorDialog("Failure while getting info from NETWORK_PROVIDER", e.getMessage());
            }
            if (!gpsEnabled && !networkEnabled) {

            }*/


        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        final ArrayList<Double> latLong = new ArrayList<>();  //arreglo para almacenar latitud y longitud
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No hay permisos suficientes");
            new ErrorAlert(this).showErrorDialog("Failure while checking location permissions", "Por favor, revisar los permisos de obtención de ubicación para que la aplicación pueda funcionar correctamente!");
        } else {
            LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0)
                    {
                        int lastestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitude = locationResult.getLocations().get(lastestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(lastestLocationIndex).getLongitude();

                        latLong.add(latitude);
                        latLong.add(longitude);

                        System.out.println("Latitud: " + latitude);
                        System.out.println("Longitud: " + longitude);
                        // Se muestra el arreglo de latitud y longitud
                        System.out.println(latLong);
                    }
                }
            }, Looper.getMainLooper());
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

        takePictureBtn.setOnClickListener(v -> {
            File mediaStorageDir = Environment.getExternalStorageDirectory();
            File directory = new File(mediaStorageDir.getAbsolutePath() + "/CoastSnap-Valdivia");

            // Create the storage directory if it does not exist
            if (!directory.exists() && !directory.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
                System.out.println("Fallo al crear el directorio!");
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            File file = new File(directory + "/" + "IMG_" + timeStamp + ".jpeg");
            System.out.println("Directorio: " + Environment.getExternalStorageDirectory());
            System.out.println("Ruta final del archivo: " + file.getAbsolutePath());

            // Verificacion de espacio en directorio
            System.out.println("Espacio libre del directorio: " + directory + " es: " + directory.getFreeSpace());

            // Se agrega metadata
            ImageCapture.Metadata metadata = new ImageCapture.Metadata();
            metadata.location = new Location("taken picture");
            try {
                metadata.location.setLatitude(latLongImg.get(0));
                metadata.location.setLongitude(latLongImg.get(1));
            } catch (Exception e){
                new ErrorAlert(this).showErrorDialog("Invalid Index", e.getMessage());
            }


            // Verifico si hay espacio disponible en el directorio del proyecto para poder tomar fotos (limite es de  7 MB)
            if (directory.getFreeSpace() >= 7000000) {
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String msg = "Pic captured at " + file.getAbsolutePath();
                        successAlert.successDialog("Image successfully saved", msg, android.R.drawable.ic_menu_camera);
                        // Proceso de carga de la imagen al servidor... (Quizas se deba manejar con WorkManager como servicio en segundo plano).
                        try{
                            ftpUploader.uploadFile(file.getAbsolutePath(), file.getName(), "/files");
                            Log.d(APP_TAG, "Envio exitoso!");
                        } catch (IOException exception){
                            errorAlert.showErrorDialog("Fail to upload the file specified", exception.getMessage());
                            Log.d(APP_TAG, exception.getMessage());
                        }
                        successAlert.successDialog("Image succesfully saved on FTP server", "Done!", android.R.drawable.ic_dialog_info);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        if (cause != null) {
                            String imageCaptureErrorMsg = "Pic capture failed due to: " + cause.toString();
                            errorAlert.showErrorDialog("Fail to save the taken picture", imageCaptureErrorMsg);
                        }
                    }
                }, metadata);
            } else {
                String errorMsgDirectory = "Not enough space on the CoastSnap-Valdivia folder, please verify the amount of available space and try again!";
                new ErrorAlert(this).showErrorDialog("Failure while saving the picture", errorMsgDirectory);
            }
        });

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
                latLongImg = getCurrentLocation();
            } else {
                String requestPermissionFailedMsg = "Permissions not granted by the user.\nPlease go your Settings and allow all the permissions for the app.";
                errorAlert.showErrorDialog("Permissions not granted!", requestPermissionFailedMsg);
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

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ftpUploader.disconnect();
        } catch (IOException e){
            new ErrorAlert(this).showErrorDialog("Failure while disconnecting from server", e.getMessage());
        }
    }*/
}