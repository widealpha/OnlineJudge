import axios from 'axios';
import qs from 'qs';

axios.defaults.baseURL = '/api';

const $ajax = {
    post: "",
    get: "",
};

$ajax.post = (url, data, headers) => {
    if (data instanceof FormData) {
        return new Promise((resolve) => {
            axios.post(url, data, headers).then((res) => {
                resolve(res);
            }).catch((err) => {
                resolve(err.response);
            });
        });
    } else {
        let params = qs.stringify(data);
        return new Promise((resolve) => {
            axios.post(url, params, headers).then((res) => {
                resolve(res);
            }).catch((err) => {
                resolve(err.response);
            });
        });
    }
};

$ajax.get = (url, data, configs) => {
    configs.params = data;
    return new Promise((resolve) => {
        axios.get(url, configs).then((res) => {
            resolve(res);
        }).catch((err) => {
            resolve(err.response);
        });
    });
};

export default $ajax;