# MediaStore

## 使用

### 第一步：导入依赖并初始化

中心库当前最新版本: [![Maven Central](https://img.shields.io/maven-central/v/io.github.zsqw123/mediastore)](https://search.maven.org/artifact/io.github.zsqw123/mediastore)

```groovy
// 如果没有的话说明中心仓库还没同步, 等几分钟吧
implementation 'io.github.zsqw123:mediastore:$version'
```

在使用之前初始化: (建议在 `Application` 初始化, 初始化仅用于传递 `Context`, 不会影响启动速度)

```kotlin
storageInit(application)
```

### 第二步 基本使用

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

读取需要使用实现了 `MediaRead` 的接口的类的静态方法, 只需要传入指定的 `uri` 或者一定的筛选条件(如果没有筛选条件则读取全部)即可得到所需的对象

```kotlin
suspend fun AudioRead.read(uri: Uri, otherParams: Array<String>):AudioRead
suspend fun ImageRead.read(uri: Uri, otherParams: Array<String>):ImageRead
suspend fun VideoRead.read(uri: Uri, otherParams: Array<String>):VideoRead
suspend fun AudioRead.read(filter, sortBy, isAscend, otherParams):List<AudioRead>
suspend fun ImageRead.read(filter, sortBy, isAscend, otherParams):List<ImageRead>
suspend fun VideoRead.read(filter, sortBy, isAscend, otherParams):List<VideoRead>
```

接下来我们访问到获取到的对象的属性即可, 默认参数有:name, relativePath, mimeType, size, dateAdded, dateModified, duration, width, height, orientation.... 如果你觉得参数不够,  可以在 `otherParams` 中传入你需要的参数, 这些参数均为 `MediaStore.MediaColumns` 中的参数.

### 其他

#### 扩展方法

- `getPicUris`: 直接得到全部图片的 `Uri` 列表
- `File.getProviderUri(provider = "$packageName.provider"): Uri`: 根据 `provider` 将 `File` 转换为`ContentProvider` 的 `Uri`.
- `Uri.delete()`: 删除 `Uri` 对应的文件, 这个需要用户的主动同意
- `Uri.share(activity, type)`: 分享某个包含文件信息的 `Uri`

#### 权限相关

| 类型                        | 无权限                                            | READ_EXTERNAL                                         |
| :-------------------------- | ------------------------------------------------- | ----------------------------------------------------- |
| Audio<br />Image<br />Video | `可读写APP自己创建的`文件，但不可直接使用路径访问 | `可以读其他APP`创建的媒体类文件，删改操作需要用户授权 |
| File<br />Downloads         | `可读写APP自己创建的`文件，但不可直接使用路径访问 | `不可读写其他APP`创建的非媒体类文件                   |

其中 `File` 类型和 `Download` 类型保存的位置其实在`Android SDK`高版本已经是一致的了, 可以认为这两个是同一个类型, 且由于无法读写`别的 App`创建的这种文件, 因此我建议开发者自行保存此种类型的 `uri`, 而不是尝试读取.
