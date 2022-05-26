// 对象克隆
export const clone = (obj) => JSON.parse(JSON.stringify(obj))

// input File对象转 base64
export function imgFileToBase64(file, callback) {
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onloadend = function (e) {
    const base64String = e.target.result
    callback(base64String)
  }
}

// base64 转 File对象
export function dataURLtoFile(dataURL) {
  const filename = `${new Date().getTime()}.png`
  let arr = dataURL.split(',');
  const mime = arr[0].match(/:(.*?);/)[1];
  const bstr = atob(arr[1])
  let n = bstr.length;
  let u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new File([u8arr], filename, { type: mime })
}

export function blobToBase64(blob) {
  return new Promise((resolve, reject) => {
    const fileReader = new FileReader();
    fileReader.onload = (e) => {
      resolve(e.target.result);
    };
    // readAsDataURL
    fileReader.readAsDataURL(blob);
    fileReader.onerror = () => {
      reject(new Error('blobToBase64 error'));
    };
  });
}