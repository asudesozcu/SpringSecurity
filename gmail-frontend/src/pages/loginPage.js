import React from "react";
import { GoogleOAuthProvider, GoogleLogin } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";

const CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID"; // Google Console'dan al

export default function LoginPage() {
    const navigate = useNavigate();

    return (
        <GoogleOAuthProvider clientId={CLIENT_ID}>
            <div style={{ textAlign: "center", marginTop: "100px" }}>
                <h2>Google ile Giriş Yap</h2>
                <GoogleLogin
                    onSuccess={credentialResponse => {
                        localStorage.setItem("token", credentialResponse.credential);
                        navigate("/emails"); // Login başarılı → Email sayfasına yönlendir
                    }}
                    onError={() => {
                        console.log("Login Failed");
                    }}
                />
            </div>
        </GoogleOAuthProvider>
    );
}
