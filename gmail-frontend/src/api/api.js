// api/api.js

export async function fetchEmailsWithFeign() {
    return fetch('http://localhost:8085/emails', {
        method: 'GET',
        credentials: 'include',
    }).then(res => res.json());

}

export async function fetchEmailsWithGrpc() {
    const token = localStorage.getItem('access_token');
    return fetch('http://localhost:8085/fetch-mails', {
        method: 'GET',
        headers: { Authorization: `Bearer ${token}` },
    })
        .then(res => res.json())
        .then(data => {
            console.log("gRPC’den gelen data:", data);
            return Array.isArray(data) ? data : data.emails;
        });
}


export async function sendKafkaFetchRequest() {
    const token = localStorage.getItem('access_token');
    return fetch(`http://localhost:8085/fetch?accessToken=${encodeURIComponent(token)}`)
        .then(res => res.text());
}

export async function fetchGraphqlEmails(fields = 'id,subject') {
    return fetch(`http://localhost:8085/graphql-emails?fields=${encodeURIComponent(fields)}`, {
        method: 'GET',
        credentials: 'include',
    }).then(res => res.json());
}
