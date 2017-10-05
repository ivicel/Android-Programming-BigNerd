package info.ivicel.locatr;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static info.ivicel.locatr.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 04/10/2017.
 */

public class LocatrFragment extends SupportMapFragment {
    private static final String TAG = "LocatrFragment";
    private static final int REQUEST_ERROR = 0;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int REQEUST_LOCATION_PERMISSIONS = 0;
    
    private GoogleApiClient mClient;
    private Bitmap mMapImage;
    private GalleryItem mMapItem;
    private Location mCurrentLocation;
    private GoogleMap mMap;
    
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);
    
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                if (hasLocationPermission()) {
                    findImage();
                } else {
                    requestPermissions(LOCATION_PERMISSIONS, REQEUST_LOCATION_PERMISSIONS);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        mClient = new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                        findImage();
                    }
    
                    @Override
                    public void onConnectionSuspended(int i) {
        
                    }
                })
                .build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateUI();
            }
        });
    
    }
    
    
    @Override
    public void onStart() {
        super.onStart();
        
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }
    
    @Override
    public void onResume() {
        super.onResume();
    
        GoogleApiAvailability apiAvailablity = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailablity.isGooglePlayServicesAvailable(getContext());
    
        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailablity.getErrorDialog(getActivity(), errorCode,
                    REQUEST_ERROR, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            getActivity().finish();
                        }
                    });
            errorDialog.show();
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        
        mClient.disconnect();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQEUST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    findImage();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }
    
    private void findImage() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "onLocationChanged: " + location.getLatitude() + ", " +
                                    location.getLongitude());
                            new SearchTask().execute(location);
                        }
                    });
        } catch (SecurityException e) {
            if (DEBUG) {
                Log.e(TAG, "no permission", e);
            }
        }
    }
    
    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    
    private class SearchTask extends AsyncTask<Location, Void, Void> {
        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;
        private Location mLocation;
        
        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            FlickrFetchr fetchr = new FlickrFetchr();
            List<GalleryItem> items = fetchr.searchPhotos(mLocation);
    
            if (items.size() == 0) {
                return null;
            }
    
            mGalleryItem = items.get(0);
    
            try {
                byte[] bytes = fetchr.getUrlBytes(mGalleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (IOException e) {
                if (DEBUG) {
                    Log.d(TAG, "Unable to download bitmap", e);
                }
            }
    
            return null;
        }
    
        @Override
        protected void onPostExecute(Void aVoid) {
            mMapImage = mBitmap;
            mMapItem = mGalleryItem;
            mCurrentLocation = mLocation;
            
            updateUI();
        }
    }
    
    private void updateUI() {
        if (mMap == null || mMapImage == null) {
            return;
        }
        Log.d(TAG, "updateUI: ");
        LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());
        BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(mMapImage);
        MarkerOptions itemMarker = new MarkerOptions()
                .position(itemPoint)
                .icon(itemBitmap);
        MarkerOptions myMarker = new MarkerOptions()
                .position(myPoint);
        mMap.clear();
        mMap.addMarker(itemMarker);
        mMap.addMarker(myMarker);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(itemPoint)
                .include(myPoint)
                .build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);
    }
}
