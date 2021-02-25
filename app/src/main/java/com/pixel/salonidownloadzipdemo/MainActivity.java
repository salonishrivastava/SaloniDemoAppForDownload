package com.pixel.salonidownloadzipdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainActivity extends AppCompatActivity {
    Context mContext;
    WebView webView;
    String path;
    TextView download, progress, viewWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();
    }

    void initial() {

        mContext = MainActivity.this;
        webView = (WebView) findViewById(R.id.webView);
        download = findViewById(R.id.download);
        progress = findViewById(R.id.progress);
        viewWeb = findViewById(R.id.viewWeb);
        viewWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acessfile();

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                } else {
                    File root = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    apiDownload();
                }
            }
        });
    }

    private void requestPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            File root = new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/NDA/");
                            if (!root.exists()) {
                                root.mkdirs();
                            }
                            apiDownload();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                            Toast.makeText(mContext, "Please allow all permission to view books", Toast.LENGTH_LONG).show();
                            openSettingsDialog();
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(mContext, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

    }

    void acessfile() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            File filee = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/simpleWebsiteHTMLCSSJavaScricpt/");
            if (filee.isDirectory()) {
                File[] files = filee.listFiles();
                for (int i = 0; i < files.length; i++) {
                    Log.v("saloni123", "saloni   " + files[i].getName());
                    if (files[i].getName().contains("index")) {

                        webView.setVisibility(View.VISIBLE);
                        webView.setWebChromeClient(new WebChromeClient());
                        webView.getSettings().setJavaScriptEnabled(true);
                        WebSettings settings = webView.getSettings();
                        settings.setBuiltInZoomControls(true);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.setLongClickable(false);
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                        webView.setInitialScale(70);
                        Log.v("saloni123", "saloni 2222222--  " + files[i]);
                        webView.loadUrl("file:///" + files[i]);


                        webView.setWebViewClient(new WebViewClient());
                    }

                }

            }
        }

    }

    void apiDownload() {
        File root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
        if (!root.exists()) {
            root.mkdirs();
        }


        AndroidNetworking.download("https://salestrip.blob.core.windows.net/tst-container/simpleWebsiteHTMLCSSJavaScricpt.zip", "" + root, "DemoZip.jar")
                .setTag("downloadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        int per = (int) (bytesDownloaded * 100 / totalBytes);
                        progress.setText(per + "%   Downloading....");

                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        viewWeb.setVisibility(View.VISIBLE);

                        Toast.makeText(mContext, "download completed !!!", Toast.LENGTH_SHORT).show();
                        try {
                            unzip("" + root + "/DemoZip.jar", "" + root);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //  acessfile();


                    }

                    @Override
                    public void onError(ANError error) {

                        Toast.makeText(mContext, "Check Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void unzip(String zipFilePath, String unzipAtLocation) throws Exception {

        File archive = new File(zipFilePath);

        try {

            ZipFile zipfile = new ZipFile(archive);

            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {

                ZipEntry entry = (ZipEntry) e.nextElement();

                unzipEntry(zipfile, entry, unzipAtLocation);
            }

        } catch (Exception e) {

            Log.e("Unzip zip", "Unzip exception", e);
        }
    }

    private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {

        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }

        Log.v("ZIP E", "Extracting: " + entry);

        InputStream zin = zipfile.getInputStream(entry);
        BufferedInputStream inputStream = new BufferedInputStream(zin);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try {

            //IOUtils.copy(inputStream, outputStream);

            try {

                for (int c = inputStream.read(); c != -1; c = inputStream.read()) {
                    outputStream.write(c);
                }

            } finally {

                outputStream.close();
            }

        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private void createDir(File dir) {

        if (dir.exists()) {
            return;
        }

        Log.v("ZIP E", "Creating dir " + dir.getName());

        if (!dir.mkdirs()) {

            throw new RuntimeException("Can not create dir " + dir);
        }
    }

    private void openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
}