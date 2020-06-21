package com.work.myscanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.work.myscanner.model.Code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private CameraSource mCameraSource;
    private BarcodeDetector mBarcodeDetector;
    private RecyclerView mRecyclerView;
    private CodeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSurfaceView = findViewById(R.id.camera_preview);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CodeAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        mBarcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        mCameraSource = new CameraSource.Builder(this, mBarcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();


        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                try {
                    mCameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });

        mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0) {
                    mRecyclerView.post(() -> {
                        String result = qrCodes.valueAt(0).displayValue;
                        Code code = createCode(result);
                        mAdapter.addCode(code);
                    });
                }
            }
        });
    }

    private Code createCode(String text) {
        int countLetter = 0;
        int countNumber = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                countNumber++;
            } else if (Character.isLetter(text.charAt(i))) {
                countLetter++;
            }
        }
        return new Code(text, countLetter, countNumber);
    }

    private class CodeHolder extends RecyclerView.ViewHolder {
        private TextView mCodeDefinition;
        private TextView mLetterCount;
        private TextView mNumberCount;

        public CodeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_code, parent, false));
            mCodeDefinition = itemView.findViewById(R.id.code_definition);
            mLetterCount = itemView.findViewById(R.id.letter_count);
            mNumberCount = itemView.findViewById(R.id.number_count);
        }

        public void bind(Code code) {
            mCodeDefinition.setText(code.getDefinition());
            mLetterCount.setText(String.valueOf(code.getLetter()));
            mNumberCount.setText(String.valueOf(code.getNumber()));
        }
    }

    private class CodeAdapter extends RecyclerView.Adapter<CodeHolder> {
        private List<Code> mCodes;

        public CodeAdapter(List<Code> codes) {
            mCodes = codes;
        }

        public List<Code> getCodes() {
            return mCodes;
        }

        public void addCode(Code code) {
            if (!mCodes.contains(code)) {
                mCodes.add(0, code);
                notifyItemInserted(0);
                mRecyclerView.smoothScrollToPosition(0);
                if (mCodes.size() > 10) {
                    mCodes.remove(10);
                    ((ArrayList) mCodes).trimToSize();
                }
                Log.d("MainActivity", String.valueOf(mCodes.size()));
            }
        }

        @NonNull
        @Override
        public CodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new CodeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CodeHolder holder, int position) {
            Code code = mCodes.get(position);
            holder.bind(code);
        }

        @Override
        public int getItemCount() {
            return mCodes.size();
        }
    }
}