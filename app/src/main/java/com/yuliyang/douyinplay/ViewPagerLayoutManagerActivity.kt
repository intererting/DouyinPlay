package com.yuliyang.douyinplay

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_view_pager_layout_manager.*
import kotlinx.android.synthetic.main.item_view_pager.*
import kotlinx.android.synthetic.main.item_view_pager.view.*

/**
 * ViewPagerLayoutManager
 */
class ViewPagerLayoutManagerActivity : AppCompatActivity() {

    private val videoAdapter by lazy {
        MyAdapter()
    }

    private val mLayoutManager by lazy {
        ViewPagerLayoutManager(this, OrientationHelper.VERTICAL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager_layout_manager)
        initView()
        initListener()
    }

    private fun initView() {

        with(mRecyclerView) {
            layoutManager = mLayoutManager
            adapter = videoAdapter
        }
    }

    private fun initListener() {
        mLayoutManager.setOnViewPagerListener(object : OnViewPagerListener {

            override fun onInitComplete() {
                Log.e(TAG, "onInitComplete")
                playVideo()
            }

            override fun onPageRelease(isNext: Boolean, position: Int) {
                Log.e(TAG, "childCount  ${mRecyclerView.childCount}")
                Log.e(TAG, "释放位置:$position 下一页:$isNext")
                val index: Int
                if (isNext) {
                    index = 0
                } else {
                    index = 1
                }
                releaseVideo(index)
            }

            override fun onPageSelected(position: Int, isBottom: Boolean) {
                Log.e(TAG, "选中位置:$position  是否是滑动到底部:$isBottom")
                Log.e(TAG, "childCount  ${mRecyclerView.childCount}")
                playVideo()
            }
        })
    }

    private fun playVideo() {
        val itemView = mRecyclerView.getChildAt(0)
        with(itemView) {
            videoView.setOnInfoListener { mp, what, extra ->
                mp.isLooping = true
                imgThumb.animate().alpha(0f).setDuration(200).start()
                true
            }
            videoView.start()
        }
        itemView.imgPlay.setOnClickListener(object : View.OnClickListener {
            var isPlaying = true
            override fun onClick(v: View) {
                if (videoView.isPlaying) {
                    imgPlay.animate().alpha(1f).start()
                    videoView.pause()
                    isPlaying = false
                } else {
                    imgPlay.animate().alpha(0f).start()
                    videoView.start()
                    isPlaying = true
                }
            }
        })
    }

    private fun releaseVideo(index: Int) {
        val itemView = mRecyclerView.getChildAt(index)
        with(itemView) {
            videoView.stopPlayback()
            imgThumb.animate().alpha(1f).start()
            imgPlay.animate().alpha(0f).start()
        }
    }

    companion object {
        private val TAG = "ViewPagerActivity"
    }
}

internal class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private val imgs = intArrayOf(R.drawable.img_video_1, R.drawable.img_video_2)
    private val videos = intArrayOf(R.raw.video_1, R.raw.video_2)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.imgThumb.setImageResource(imgs[position % 2])
        holder.itemView.videoView.setVideoURI(Uri.parse("android.resource://" + holder.containerView.context.packageName + "/" + videos[position % 2]))
    }

    override fun getItemCount(): Int {
        return 20
    }

    internal class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
    }
}
