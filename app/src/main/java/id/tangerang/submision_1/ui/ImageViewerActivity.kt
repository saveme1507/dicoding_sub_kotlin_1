package id.tangerang.submision_1.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import id.tangerang.submision_1.R
import id.tangerang.submision_1.databinding.ActivityImageViewerBinding

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding
    private val context = this@ImageViewerActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("img")) {
            val uri = intent.getStringExtra("img").toString().toUri()
            Glide.with(context)
                .load(uri)
                .error(R.drawable.image_placeholder)
                .into(binding.ivPhoto)
        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.ivClose.setOnClickListener {
            finish()
        }
    }
}