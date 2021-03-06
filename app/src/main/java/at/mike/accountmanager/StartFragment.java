package at.mike.accountmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.crypto.AEADBadTagException;

public class StartFragment extends Fragment {

    private final String TAG = "StartFragment";
    private ActivityCallback activityCallbackListener;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREFS", 0);

        if (sharedPreferences.getBoolean("FIRST_START", true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FIRST_START", false);
            editor.apply();

            return inflater.inflate(R.layout.fragment_start_first, container, false);
        }

        return  inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (view.getId() == R.id.fragment_start_first_layout) {
            final EditText editTextKey = view.findViewById(R.id.editTextKey);
            Button btnGenKey = view.findViewById(R.id.buttonGenKey);

            btnGenKey.setOnClickListener((View v) -> {
                String master_key = editTextKey.getText().toString();

                if (master_key.length() < 5) {
                    editTextKey.setError("Use longer Key!");
                    return;
                }

                // create encrypted keystore
                EncryptionManager encryptionManager = new EncryptionManager(getContext());

                try {
                    encryptionManager.createKeyStore(master_key);
                } catch (AEADBadTagException e) {
                    e.printStackTrace();
                }

                // go back to MainActivity
                activityCallbackListener.onCallback(master_key, Constants.OPEN_ACC_FRG, null);
            });
        }
        else if (view.getId() == R.id.fragment_start_layout) {
            final EditText editTextKey = view.findViewById(R.id.editTextKey);
            Button btnGoKey = view.findViewById(R.id.buttonGoKey);

            btnGoKey.setOnClickListener((View v)  ->{
                String master_key = editTextKey.getText().toString();

                if (master_key.isEmpty()) {
                    editTextKey.setError("Key is empty");
                    return;
                }

                EncryptionManager encryptionManager = new EncryptionManager(getContext());

                try {
                    String savedKey = encryptionManager.get("MASTER_KEY", master_key);

                    // go back to MainActivity
                    activityCallbackListener.onCallback(master_key, Constants.OPEN_ACC_FRG, null);
                } catch (javax.crypto.AEADBadTagException e) {
                    Toast.makeText(getContext(), "invalid key", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ActivityCallback) {
            activityCallbackListener = (ActivityCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityCallbackListener = null;
    }
}