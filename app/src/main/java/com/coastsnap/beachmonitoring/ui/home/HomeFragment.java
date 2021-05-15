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

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private final String APP_TAG = "CoastSnap";
    private final String PIC_DIRECTORY = "/storage/emulated/0/Android/data/com.coastsnap.beachmonitoring/files/Pictures/CoastSnap-Valdivia";

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

        toActivityCamera.setOnClickListener(view1 -> {
            Intent toCamera = new Intent(getActivity(), MainActivity.class);
            startActivity(toCamera);
            getActivity().finish();
        });

        sendToServer.setOnClickListener(v -> {
            File lastPicCaptured = findUsingCommonsIO(PIC_DIRECTORY);

            if (lastPicCaptured == null){
                Log.d(APP_TAG, "Ultima imagen se encuentra vacia");
                new AlertDialog.Builder(getContext())
                        .setTitle("Falla al obtener imagen!")
                        .setMessage("Ha ocurrido un error al momento de obtener la última imagen tomada.")
                        .setNeutralButton("OK", null)
                        .show();
            }

            SFTPConnector sftp = new SFTPConnector();
            try {
                sftp.uploadFileUsingVfs(PIC_DIRECTORY, lastPicCaptured.getName());
            } catch (IOException ioe) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Falla al enviar imagen hacia el servidor!")
                        .setMessage("Ha ocurrido un error: " + ioe.toString())
                        .setPositiveButton("Intentar de nuevo", null)
                        .setNegativeButton("Ok", null)
                        .show();
            }
        });
    }

    public static File findUsingCommonsIO(String sdir){
        File dir = new File(sdir);
        if (dir.isDirectory()) {
            File[] dirFiles = dir.listFiles((FileFilter) FileFilterUtils.fileFileFilter());
            if (dirFiles != null && dirFiles.length > 0) {
                Arrays.sort(dirFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                return dirFiles[0];
            }
        }
        return null;
    }

}