package com.zsqw123.mediastore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zsqw123.mediastore.databinding.ActivityMainBinding
import com.zsqw123.mediastore.read.AudioRead
import com.zsqw123.mediastore.read.VideoRead
import com.zsqw123.mediastore.save.AudioSave
import com.zsqw123.mediastore.save.FileSave
import com.zsqw123.mediastore.save.ImageSave
import com.zsqw123.mediastore.save.VideoSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            rvBt.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            rvPic.layoutManager = GridLayoutManager(this@MainActivity, 3, RecyclerView.VERTICAL, false)
            val bts: Array<Pair<String, () -> Unit>> = arrayOf(
                "ReadPic" to {
                    lifecycleScope.launch(Dispatchers.Main) {
                        rvPic.adapter = PicRvAdapter(getPicUris())
                    }
                },
                "ReadAudio" to {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val audio = AudioRead.read().last()
                        val content = audio.uri.inputStream.readBytes().decodeToString()
                        tvFileShown.text = "${audio.name} : $content"
                    }
                },
                "ReadVideo" to {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val video = VideoRead.read().last()
                        tvFileShown.text = "${video.name} : ${video.uri.inputStream.readBytes().decodeToString()}"
                    }
                },
                "WritePic" to {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val res = ImageSave(getSquareBitmap(resources, resources.displayMetrics.widthPixels / 3)).save("1.jpg", "666/777")
                        if (res) toast("1.jpg 保存成功") else toast("1.jpg 保存失败")
                    }
                },
                "WriteFile" to {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val res = FileSave("hhhhhc").save()
                        if (res) toast("File 保存成功") else toast("File 保存失败")
                    }
                },
                "WriteAudio" to {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val res = AudioSave("hhhhhc".toByteArray()).save("2.mp3")
                        if (res) toast("2.mp3 保存成功") else toast("2.mp3 保存失败")
                    }
                },
                "WriteVideo" to {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val res = VideoSave("hhhhhc".toByteArray()).save("3.mp4")
                        if (res) toast("3.mp4 保存成功") else toast("3.mp4 保存失败")
                    }
                },
            )
            rvBt.adapter = ButtonRvAdapter(bts.size) { i, v ->
                v.text = bts[i].first
                v.setOnClickListener { bts[i].second() }
            }
        }
    }
}