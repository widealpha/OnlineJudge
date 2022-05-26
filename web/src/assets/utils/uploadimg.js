import axios from "axios"

// 第三方图床 API : imgbb
export default async function uploadImg(file) {
  try {
    const formData = new FormData()
    formData.append("image", file)
    const key = "dedc76e804d0e63d56731e657104e2d3"
    const res = await axios.post(`https://api.imgbb.com/1/upload?key=${key}`, formData)
    return res.data.data.url
  } catch {
    return null
  }
}