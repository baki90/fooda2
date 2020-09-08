package com.example.fooda2

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager : CallbackManager ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_btn.setOnClickListener { signinEmail() }
        register_btn.setOnClickListener { signupEmail() }
        google_login.setOnClickListener{ googleLogin() }
        facebook_login.setOnClickListener{ facebookLogin()}
        auth = FirebaseAuth.getInstance()
        //hideActionBar()
        //printHashKey()
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        callbackManager = CallbackManager.Factory.create()
    }

    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }
    fun facebookLogin(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_prifile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                    Log.d("tag", "login cancel")
                }

                override fun onError(error: FacebookException?) {
                    TODO("Not yet implemented")
                    Log.d("tag", "login failed")
                }

            })
    }
    fun handleFacebookAccessToken(token : AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //login
                Toast.makeText(this, "로그인되었습니다.", Toast.LENGTH_LONG).show()
                moveMainPage(task.result?.user)
            } else if (task.exception?.message.isNullOrEmpty()) {
                //login error
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //login
                Toast.makeText(this, "로그인되었습니다.", Toast.LENGTH_LONG).show()
                moveMainPage(task.result?.user)
            } else if (task.exception?.message.isNullOrEmpty()) {
                //login error
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    //가입 부분
    fun signupEmail() {
        //유효성 체크
        if (TextUtils.isEmpty(email.text) || TextUtils.isEmpty(password.text)) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해 주세요", Toast.LENGTH_LONG).show()
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            Toast.makeText(this, "이메일 형태로 입력해 주세요.", Toast.LENGTH_LONG).show()
        }
        else if(password.text.toString().length < 6){
            Toast.makeText(this, "비밀번호는 6자 이상으로 설정해 주세요.", Toast.LENGTH_LONG).show()
        }
        else {
            auth?.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //creating a user account
                        Toast.makeText(this, "가입이 완료되었습니다", Toast.LENGTH_LONG).show()
                        moveMainPage(task.result?.user)
                    } else if (task.exception?.message.isNullOrEmpty()) {
                        //register error
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    //로그인
    fun signinEmail() {
        if (TextUtils.isEmpty(email.text) || TextUtils.isEmpty(password.text)) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해 주세요", Toast.LENGTH_LONG).show()
        } else {
            auth?.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //login
                        Toast.makeText(this, "로그인되었습니다.", Toast.LENGTH_LONG).show()
                        moveMainPage(task.result?.user)
                    } else if (task.exception?.message.isNullOrEmpty()) {
                        //login error
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun moveMainPage(user: FirebaseUser?){
        if(user != null){
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

    private fun hideActionBar() {
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }

    fun printHashKey() {
        try {
            val info: PackageInfo = packageManager
                .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("tag", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("tag", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("tag", "printHashKey()", e)
        }
    }
}
