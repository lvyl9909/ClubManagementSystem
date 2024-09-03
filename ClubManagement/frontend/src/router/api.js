// api.js
const path = process.env.REACT_APP_API_BASE_URL
export async function loginApi(username, password) {
    const res = await doCall(`${path}/auth/token`,'POST', { username, password });
    if (!res.ok) {
        throw new Error('Login failed');
    }
    const token = await res.json();
    setTokenInStorage(token);
    return token;
}

export async function logoutApi(username) {
    await doCall(`${path}/auth/logout`,'POST',{ username });
    localStorage.removeItem('accessToken');
    localStorage.removeItem('tokenType');
}

function setTokenInStorage(token) {
    localStorage.setItem('accessToken', token.accessToken);
    localStorage.setItem('tokenType', token.tokenType);
}

export async function doCall(path, method, data, signal) {
    const headers = {
        'Content-Type': 'application/json',
        Accept: 'application/json',
    };

    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        headers.Authorization = `${localStorage.getItem('tokenType')} ${accessToken}`;
    }

    let body;
    if (data) {
        body = JSON.stringify(data);
    }

    const res = await fetch(`${path}`, {
        method,
        headers,
        body,
        credentials: 'include',
    });

    if (res.status === 401 && accessToken) {
        await refreshToken(accessToken);
        return doCall(path, method, data); // Retry the original request
    }

    if (res.status > 299) {
        throw new Error(`Error: ${res.status} ${res.statusText}`);
    }

    return res;
}

async function refreshToken(accessToken) {
    const res = await fetch('${path}/auth/token', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        },
        body: JSON.stringify({ accessToken }),
        credentials: 'include',
    });
    if (res.status > 299) {
        throw new Error(`expecting success from API for PUT but response was status ${res.status}: ${res.statusText}`);
    }
    if (!res.ok) {
        throw new Error('Token refresh failed');
    }

    const token = await res.json();
    setTokenInStorage(token);
    return token;
}

