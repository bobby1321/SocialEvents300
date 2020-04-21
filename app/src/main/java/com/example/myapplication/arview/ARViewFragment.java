package com.example.myapplication.arview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Singleton;
import com.example.myapplication.list.RssFeedModel;

import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class ARViewFragment extends Fragment {

    private LocationScene locationScene;
    private ModelRenderable andyRenderable;
    private ArSceneView arSceneView;
    private boolean hasFinishedLoading = false;
    private ViewRenderable exampleLayoutRenderable;
    private Toast loadingMessageToast = null;
    private boolean installRequested;
    private ArrayList<LocationMarker> locationMarkers = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_arview, container, false);
        arSceneView = root.findViewById(R.id.ar_scene_view);

        // Build a renderable from a 2D View.
        CompletableFuture<ModelRenderable> andy = ModelRenderable.builder()
                .setSource(getContext(), R.raw.marker)
                .build();


        CompletableFuture.allOf(
                andy)
                .handle(
                        (notUsed, throwable) -> {
                            if (throwable != null) {
                                DemoUtils.displayError(getContext(), "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                andyRenderable = andy.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(getContext(), "Unable to load renderables", ex);
                            }

                            return null;
                        });

        arSceneView
                .getScene()
                    .setOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                                                locationScene.mLocationMarkers.add(
                                        new LocationMarker(
                                                40.049341,
                                                -75.531120,
                                                getAndy("The Test Marker")));

                                locationMarkers.clear();
                                for (RssFeedModel r : Singleton.getInstance().getState()){
                                    LocationMarker temp = new LocationMarker(
                                            r.getLongitude(),
                                            r.getLatitude(),
                                            getAndy(r.getTitle()));
                                    temp.setScaleAtDistance(true);
                                    locationMarkers.add(temp);
                                    locationScene.mLocationMarkers.add(temp);
                                }
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }

                            if (loadingMessageToast != null) {
                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                                        hideLoadingMessage();
                                    }
                                }
                            }
                        });

        return root;
    }

    private Node getAndy(String touchText) {
        Node base = new Node();
        base.setRenderable(andyRenderable);
        Context c = getContext();
        base.setOnTapListener((v, event) -> {
            Toast.makeText(
                    c, touchText, Toast.LENGTH_SHORT)
                    .show();
        });
        return base;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(getActivity(), installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(getActivity());
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(getActivity(), e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(getContext(), "Unable to get camera", ex);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            return;
        }
        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();
        if (locationScene != null) {
            locationScene.pause();
        }
        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(getActivity())) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(getActivity())) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(getActivity());
            } else {
                Toast.makeText(
                        getContext(), "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageToast != null) {
            return;
        }

        loadingMessageToast =
                Toast.makeText(
                        getContext(),
                        R.string.plane_finding,
                        Toast.LENGTH_LONG);
        loadingMessageToast.getView().setBackgroundColor(0xbf323232);
        loadingMessageToast.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageToast == null) {
            return;
        }
        loadingMessageToast = null;
    }
}