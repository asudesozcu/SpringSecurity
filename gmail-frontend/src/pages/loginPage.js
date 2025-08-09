// React /login page (LoginPage.js)



import {useEffect} from "react";

export default function LoginPage() {
    const handleLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };
    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const loginSuccess = params.get("loginSuccess");

        if (loginSuccess === "true") {
            fetch("http://localhost:8080/api/token", {
                method: "GET",
                credentials: "include",
            })
                .then(res => res.text())
                .then(token => {
                    localStorage.setItem("access_token", token);
                    window.location.href = "/emails";
                })
                .catch(err => console.error("Token alınamadı:", err));
        }
    }, []);

    return (
        <div style={{ padding: "2rem" }}>
            <h1>Giriş Yap</h1>
            <button onClick={handleLogin}>Google ile Giriş Yap</button>
        </div>
    );
}
;