import axios from "axios";

const API_BASE_URL = "http://localhost:8085"; // Consumer-System portu

export const fetchEmails = async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/emails`);
        return response.data;
    } catch (error) {
        console.error("Error fetching emails", error);
        return [];
    }
};
