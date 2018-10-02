package com.rnr.chebarbado.testvkapi;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKApiOwner;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.util.VKUtil;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String[] scope = new String[]{VKScope.FRIENDS};
    private TextView userName;
    private TextView[] friends = new TextView[5];

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (VKSdk.isLoggedIn()) {
            printInfo();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.attention)
                    .setMessage(R.string.needtoauth)
                    .setCancelable(false)
                    .setNegativeButton(R.string.autht,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startVKlogin();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {

                printInfo();


                Toast.makeText(getApplicationContext(), R.string.succssauth, Toast.LENGTH_LONG).show();
                // Пользователь успешно авторизовался
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), R.string.unsuccssauth, Toast.LENGTH_LONG).show();
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startVKlogin() {
        VKSdk.login(this, scope);
    }

    private void init() {
        userName = findViewById(R.id.user_name_tv);

        friends[0] = findViewById(R.id.friend_one);
        friends[1] = findViewById(R.id.friend_two);
        friends[2] = findViewById(R.id.friend_three);
        friends[3] = findViewById(R.id.friend_four);
        friends[4] = findViewById(R.id.friend_five);
    }

    private void printInfo() {
        final VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name,last_name"));
        final VKRequest request1 = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "first_name,last_name"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKList list = (VKList) response.parsedModel;
                int i = 0;
                while (i < 5 && i < list.size()) {
                    friends[i].setText(list.get(random.nextInt(list.size())).toString());
                    friends[i].setVisibility(View.VISIBLE);
                    i++;
                }

            }
        });

        request1.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKList ulist = (VKList) response.parsedModel;
                userName.setText(ulist.get(0).toString());
                userName.setVisibility(View.VISIBLE);
            }
        });
    }
}
