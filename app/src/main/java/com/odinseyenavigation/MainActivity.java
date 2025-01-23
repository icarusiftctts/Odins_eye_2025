package com.odinseyenavigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.odinseyenavigation.users;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 40;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private GoogleSignInClient googleSignInClient;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    private ImageView loginButton;
    private LinearLayout astroInstaLayout;
    private ImageView plinthImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        initializeFirebaseAndGoogle();
        setupEventListeners();

        checkExistingLogin();
    }

    private void initializeUI() {
        loginButton = findViewById(R.id.login_txt_bg);
        astroInstaLayout = findViewById(R.id.main_astro_insta);
        plinthImage = findViewById(R.id.main_plinth);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
    }

    private void initializeFirebaseAndGoogle() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupEventListeners() {
        astroInstaLayout.setOnClickListener(v -> openUrl("https://www.instagram.com/astronomylnmiit/?hl=en"));
        plinthImage.setOnClickListener(v -> openUrl("https://plinth.co.in/"));
        loginButton.setOnClickListener(v -> signIn());
    }

    private void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void checkExistingLogin() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            addDataToFirebase();
            progressDialog.dismiss();
            navigateToAfterLogin();
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(data);
        }
    }

    private void handleSignInResult(Intent data) {
        progressDialog.show();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            checkUserInDatabase(account);
        } catch (ApiException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserInDatabase(GoogleSignInAccount account) {
        String sanitizedEmail = sanitizeEmail(account.getEmail());

        databaseReference = database.getReference("allowed").child(sanitizedEmail);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    handleUserSignIn(snapshot, account);
                } else {
                    handleUnregisteredUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUserSignIn(DataSnapshot snapshot, GoogleSignInAccount account) {
        String value = snapshot.getValue(String.class);
        if (Integer.parseInt(value) == 0) {
            firebaseAuth(account.getIdToken());
        } else {
            notifyAlreadyLoggedIn();
        }
    }

    private void notifyAlreadyLoggedIn() {
        Toast.makeText(this, "Already logged in on another device", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Contact coordinators if this is a mistake", Toast.LENGTH_SHORT).show();
        signOut();
    }

    private void handleUnregisteredUser() {
        Toast.makeText(this, "You are not registered. Visit the registration desk.", Toast.LENGTH_SHORT).show();
        signOut();
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                saveUserToDatabase(user);
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase(FirebaseUser user) {
        if (user == null) return;

        users userData = new users(user.getDisplayName(), user.getUid(), user.getPhotoUrl().toString());
        DatabaseReference userReference = database.getReference("Users").child(sanitizeEmail(user.getEmail()));

        userReference.child(user.getUid()).setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addDataToFirebase();
                Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                navigateToAfterLogin();
            }
        });
    }

    private void addDataToFirebase() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference ref = database.getReference("allowed").child(sanitizeEmail(user.getEmail()));
            ref.setValue("1");
        }
    }

    private void navigateToAfterLogin() {
        Intent intent = new Intent(MainActivity.this, E1_afterLogin.class);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(task -> progressDialog.dismiss());
    }

    private String sanitizeEmail(String email) {
        return email != null ? email.replace(".", "_dot_").replace("@", "_at_") : "error";
    }
}
