import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/loginPage";
import EmailPage from "./pages/EmailPage";
import "primereact/resources/themes/lara-light-blue/theme.css"; // Tema
import "primereact/resources/primereact.min.css";                // PrimeReact CSS
import "primeicons/primeicons.css";                              // Iconlar

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/emails" element={<EmailPage />} />
            </Routes>
        </Router>
    );
}

export default App;
