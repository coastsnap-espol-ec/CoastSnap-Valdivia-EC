package com.coastsnap.beachmonitoring.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.coastsnap.beachmonitoring.MainActivity;
import com.coastsnap.beachmonitoring.R;
import com.coastsnap.beachmonitoring.util.SFTPConnector;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private final String APP_TAG = "CoastSnap";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button toActivityCamera = (Button) view.findViewById(R.id.btnToCamera);
        Button sendToServer = view.findViewById(R.id.btnUploadSFTPServer);

        /*String fileDir = this.getArguments().getString("pic_dir");
        String filename = this.getArguments().getString("pic_filename");

        if (filename == null){
            Log.d(APP_TAG, "no se recibio correctamente el nombre del archivo");
        }*/

        toActivityCamera.setOnClickListener(view1 -> {
            Intent toCamera = new Intent(getActivity(), MainActivity.class);
            startActivity(toCamera);
            getActivity().finish();
        });

        /*
        sendToServer.setOnClickListener(v -> {
            SFTPConnector sftp = new SFTPConnector();
            try{
                sftp.uploadFileUsingVfs(fileDir, filename);
            } catch (IOException ioe){
                new AlertDialog.Builder(getContext())
                        .setTitle("Falla al enviar imagen hacia el servidor!")
                        .setMessage("Ha ocurrido un error: " + ioe.toString())
                        .setPositiveButton("Intentar de nuevo", null)
                        .setNegativeButton("Ok", null)
                        .show();
            }
        });*/
    }
}