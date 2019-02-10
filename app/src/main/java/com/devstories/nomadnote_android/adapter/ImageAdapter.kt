package com.devstories.nomadnote_android.com.devstories.nomadnote_android.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.devstories.nomadnote_android.base.ImageLoader
import java.util.*






open class ImageAdapter(context: Context, data:ArrayList<PhotoData>, imageLoader: ImageLoader, selected : LinkedList<String>) : BaseAdapter() {

    private lateinit var item: ViewHolder
    var mContext:Context = context

    internal var photoList: ArrayList<PhotoData> = data

    private val imageLoader: ImageLoader = imageLoader

    private val selected: LinkedList<String> = selected


    class PhotoData {
        var photoID: Int = 0
        var photoPath: String? = null
        var displayName: String? = null
        var bucketPhotoName: String? = null
        var orientation: Int = 0
        // var type: String? = null

        var videoID: Int = 0
        var videoPath: String? = null
        var videoDisplayName: String? = null
        var videoBucketDisplayName: String? = null

        var mediaType = -1
    }


    override fun getView(position: Int, convertView: View?, parent : ViewGroup?): View {
        var holder: ViewHolder = ViewHolder();

        var retView: View

        val item = photoList.get(position)
        // val type = item.type

        if (convertView == null) {

            retView = View.inflate(this.mContext, com.devstories.nomadnote_android.R.layout.item_findpicture, null)
            holder.picture_grid_click = retView.findViewById<TextView>(com.devstories.nomadnote_android.R.id.picture_grid_click);
            holder.picture_grid_image = retView.findViewById<ImageView>(com.devstories.nomadnote_android.R.id.picture_grid_image);

            retView.setTag(holder);
        } else {
            retView = convertView
            holder = retView.getTag() as ViewHolder
        }

        var photo = photoList.get(position);

        if(photo.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            holder.picture_grid_image.setImageBitmap(imageLoader.getImage(photo.photoID.toLong(), photo.photoPath, photo.orientation))
        } else {
            val options = BitmapFactory.Options()
            options.inSampleSize = 1
            val curThumb = MediaStore.Video.Thumbnails.getThumbnail(mContext.contentResolver, photo.videoID.toLong(), MediaStore.Video.Thumbnails.MINI_KIND, options)
            holder.picture_grid_image.setImageBitmap(curThumb)
        }

        if (selected.contains(position.toString())) {
            val idx = selected.indexOf(position.toString())
            holder.picture_grid_click.text = (idx + 1).toString()

            Log.d("yjs" ,"idx : " + idx.toString()  )
        }else {
            holder.picture_grid_click.text = ""
        }
        return retView;

//        if (type == "i"){
//            if (convertView == null) {
//
//                retView = View.inflate(this.mContext, R.layout.item_findpicture, null)
//                holder.picture_grid_click = retView.findViewById<TextView>(R.id.picture_grid_click);
//                holder.picture_grid_image = retView.findViewById<ImageView>(R.id.picture_grid_image);
//
//                retView.setTag(holder);
//            } else {
//                retView = convertView
//                holder = retView.getTag() as ViewHolder
//            }
//
//            var photo = photoList.get(position);
//
//            holder.picture_grid_image.setImageBitmap(imageLoader.getImage(photo.photoID, photo.photoPath, photo.orientation))
//
//            if (selected.contains(position.toString())) {
//                val idx = selected.indexOf(position.toString())
//                holder.picture_grid_click.text = (idx + 1).toString()
//
//                Log.d("yjs" ,"idx : " + idx.toString()  )
//            }else {
//                holder.picture_grid_click.text = ""
//            }
//            return retView;
//        }
//        else {
//
//            if (convertView == null) {
//
//                retView = View.inflate(this.mContext, R.layout.item_findpicture, null)
//                holder.picture_grid_click = retView.findViewById<TextView>(R.id.picture_grid_click);
//                holder.picture_grid_image = retView.findViewById<ImageView>(R.id.picture_grid_image);
//
//                retView.setTag(holder);
//            } else {
//                retView = convertView
//                holder = retView.getTag() as ViewHolder
//            }
//
//            var photo = photoList.get(position);
//
//            val bitmap = ThumbnailUtils.createVideoThumbnail(photo.photoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
//            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 360, 480)
//
//            holder.picture_grid_image.setImageBitmap(thumbnail)
//
////        holder.picture_grid_image.setImageBitmap(imageLoader.getImage(photo.videoID, photo.videoPath, photo.orientation))
//
//            if (selected.contains(position.toString())) {
//                val idx = selected.indexOf(position.toString())
//                holder.picture_grid_click.text = (idx + 1).toString()
//
//                Log.d("yjs" ,"idx : " + idx.toString()  )
//            }else {
//                holder.picture_grid_click.text = ""
//            }
//
//
//            return retView;
//        }


    }

    override fun getItem(position: Int): PhotoData {
        return photoList.get(position)
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getCount(): Int {

        return photoList.count()
    }

    fun removeItem(position: Int){
        photoList.removeAt(position)
    }

    inner class ViewHolder {
        lateinit var picture_grid_click :TextView
        lateinit var picture_grid_image : ImageView
    }

}