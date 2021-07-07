# MediaStore

## 使用

### 第一步：导入依赖并初始化

```groovy
// 等着 maven 审批
```

在使用之前初始化: (建议在 `Application` 初始化, 初始化仅用于传递 `Context`, 不会影响启动速度)

```kotlin
storageInit(application)
```

### 第二步 使用

事实上我在文档中的注释也是较为详细的, 直接用也是没问题的

#### 向 MediaStore 保存文件

如果只是想保存, 而不需要其他要求, 可以直接这样, 只需传入数据流, 直接保存即可:

```kotlin
ImageSave(bitmap).save()
FileSave(file).save()
AudioSave(byteArray).save()
VideoSave(byteArray).save()
```

如果你想要更多的可定制性, 你需要这样构建 `MediaSave` 对象, 其中的参数大多数都是可选的, mimeType 也应该被忽略, 因为会自动根据文件后缀或文件类型生成, 当然这些参数也可以在对象构造后再修改或添加:

```kotlin
ImageSave(inputStream, mimeType, description) // 使用输入流保存
ImageSave(bitmap, compressFormat, quality) // 使用 Bitmap 对象保存
ImageSave(file) // 这个实际上可以算作文件拷贝
FileSave(inputStream, type, mainPath, mimeType) // 使用输入流保存
FileSave(string, charset) // 导出文档
FileSave(bytes) // 导出 bytes 流
FileSave(file) // 文件拷贝罢了
VideoSave(inputStream, type, mainPath, mimeType) // 使用输入流保存
VideoSave(bytes) // 导出 bytes 流
VideoSave(file) // 文件拷贝罢了
AudioSave(inputStream, type, mainPath, mimeType) // 使用输入流保存
AudioSave(bytes) // 导出 bytes 流
AudioSave(file) // 文件拷贝罢了
```

他们都实现了 `MediaSave` 接口, 内部有一个 `save` 的挂起函数执行真正对的保存操作:

```kotlin
/**
 * 假设图片要保存到 /Pictures/App/Pic/1.jpg, 那么:
 * @param name 1.jpg
 * @param subPath App/Pic
 *
 * 主路径则由实现接口的类型提供
 *
 * @return Boolean
 */
suspend fun save(name: String = Date().time.toString(), subPath: String = "", contentValues: ContentValues = ContentValues()): Boolean
```

#### 从 MediaStore 中读取文件
