package droiddevelopers254.droidconke.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import droiddevelopers254.droidconke.datastates.AuthenticateUserState;
import droiddevelopers254.droidconke.models.UserModel;

public class AuthenticateUserRepo {

    public LiveData<AuthenticateUserState> checkUserExistence(FirebaseUser firebaseUser){
        final MutableLiveData<AuthenticateUserState> userStateMutableLiveData=new MutableLiveData<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot= task.getResult();
                        if (documentSnapshot.exists()){
                            userStateMutableLiveData.setValue(new AuthenticateUserState(true));
                        }else {
                            UserModel user = new UserModel();
                            user.setEmail(firebaseUser.getEmail());
                            user.setUser_id(firebaseUser.getUid());
                            user.setUser_name(firebaseUser.getDisplayName());
                            user.setPhoto_url(String.valueOf(firebaseUser.getPhotoUrl()));
                            user.setRefresh_token(null);
                            userStateMutableLiveData.setValue(new AuthenticateUserState(user));

                            saveUser(user);

                        }
                    }

                });
    return userStateMutableLiveData;
    }

    private LiveData<AuthenticateUserState> saveUser(UserModel userModel){
        final MutableLiveData<AuthenticateUserState> stateMutableLiveData= new MutableLiveData<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(userModel.getUser_id())
                .set(userModel)
                .addOnSuccessListener(aVoid -> stateMutableLiveData.setValue(new AuthenticateUserState(true)))
                .addOnFailureListener(e -> stateMutableLiveData.setValue(new AuthenticateUserState(e.getMessage())));
        return stateMutableLiveData;
    }
}
