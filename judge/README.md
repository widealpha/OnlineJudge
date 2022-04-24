# 学生能力提升平台判题端

## 支持的语言

- java8
- python3
- c++14
- c99

## 远程文件格式

/remoteRootDir/problemId/fileName

> fileName可能是以下值

| 文件名                | 形式                         | 描述                                 |
|--------------------|----------------------------|------------------------------------|
| checkpoints.zip    | 压缩包,包含x.in,x.out           | 测试点信息,按照数字从1-x开始                   |
| checkpoints.sha256 | 纯字符串                       | 内部是checkpoints.zip的sha256值         |
| judge.zip          | 压缩包,包含java.txt,python.txt等 | 如果需要specialJudge是specialJudge的测试程序 |
| judge.sha256       | 纯字符串                       | judge.zip的sha256值                  |

## 远程文件格式

/localCacheDir/problemId/fileName

> fileName可能是以下值

| 文件名                | 形式                         | 描述                                 |
|--------------------|----------------------------|------------------------------------|
| checkpoints.zip    | 压缩包,包含x.in,x.out           | 测试点信息,按照数字从1-x开始                   |
| checkpoints.sha256 | 纯字符串                       | 内部是checkpoints.zip的sha256值         |
| judge.zip          | 压缩包,包含java.txt,python.txt等 | 如果需要specialJudge是specialJudge的测试程序 |
| judge.sha256       | 纯字符串                       | judge.zip的sha256值                  |
| checkpoints        | 文件夹                        | 测试点解压后的内容,1.in,1.out....           |
| judge              | 文件夹                        | special judge解压后的内容                |

## 缓存到本地的格式