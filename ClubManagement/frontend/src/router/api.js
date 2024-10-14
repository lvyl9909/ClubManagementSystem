// api.js
const path = process.env.REACT_APP_API_BASE_URL
export async function loginApi(username, password) {
    const res = await doCall(`${path}/auth/token`,'POST', { username, password });
    if (!res.ok) {
        const errorResponse = await res.json();
        const error = new Error(errorResponse.message || 'Login failed');
        error.status = res.status;
        throw error;
    }
    const token = await res.json();
    setTokenInStorage(token);
    return token;
}

export async function logoutApi(username) {
    await doCall(`${path}/auth/logout`,'POST',{ username });
    localStorage.removeItem('accessToken');
    localStorage.removeItem('type');
}

function setTokenInStorage(token) {
    localStorage.setItem('accessToken', token.accessToken);
    localStorage.setItem('type', token.type);
}

export async function doCall(path, method, data) {
    const headers = {
        'Content-Type': 'application/json',
        Accept: 'application/json',
    };

    const accessToken = localStorage.getItem('accessToken');
    const type = localStorage.getItem('type');

    // if (accessToken &&!path.includes('/auth/token')){
    if (accessToken){
        headers.Authorization = `${type} ${accessToken}`;
        // console.log("type",localStorage.getItem('type'));
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
        mode:'cors'
    });


    if (res.status === 401) {
        if (path.includes('/auth/token')) {
            return res;
        }
        if (accessToken) {
            console.warn('Token expired, attempting to refresh...');
            await refreshToken(accessToken);
            return doCall(path, method, data);
        }
    }

    if (res.status > 299) {
        const error = new Error(`Error: ${res.status} ${res.statusText}`);
        error.response = res; // Attach the full response to the error
        throw error;
    }

    return res;
}

async function refreshToken(accessToken) {
    const res = await fetch(`${path}/auth/token`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        },
        body: JSON.stringify({ accessToken }),
        credentials: 'include',  // 添加这一行以发送 Cookie
    });
    console.log(res.status);
    if (res.status > 299) {
        console.error(`Token refresh failed: ${res.status} ${res.statusText}.`);
        throw new Error(`expecting success from API for PUT but response was status ${res.status}: ${res.statusText}`);
    }
    if (!res.ok) {
        throw new Error('Token refresh failed');
    }

    const token = await res.json();
    setTokenInStorage(token);
    return token;
}

