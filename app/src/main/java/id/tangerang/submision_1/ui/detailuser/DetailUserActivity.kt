package id.tangerang.submision_1.ui.detailuser

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import id.tangerang.submision_1.BuildConfig
import id.tangerang.submision_1.R
import id.tangerang.submision_1.database.Favorite
import id.tangerang.submision_1.databinding.ActivityDetailUserBinding
import id.tangerang.submision_1.ui.detailuserfragment.FollowerFragment
import id.tangerang.submision_1.ui.detailuserfragment.FollowingFragment
import id.tangerang.submision_1.utils.ApiRequest
import id.tangerang.submision_1.models.UserModel
import id.tangerang.submision_1.ui.ImageViewerActivity
import id.tangerang.submision_1.utils.getCurrentDate
import id.tangerang.submision_1.utils.myNumberFormat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val USERNAME = "username"

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private val context = this@DetailUserActivity
    private lateinit var model: DetailUserViewModel
    private lateinit var apiRequest: ApiRequest
    private lateinit var user: UserModel
    private var userName = ""
    private val userFavorite = Favorite()
    private var isFavorite = false

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.title_tabs_1,
            R.string.title_tabs_2,
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiRequest = ApiRequest(context)
        model = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory(this.application)
        ).get(DetailUserViewModel::class.java)

        if (intent.hasExtra("username")) {
            userName = intent.getStringExtra("username").toString()
            model.setApiRequest(apiRequest)
            model.loadUser(userName)
        } else {
            apiRequest.showToast(getString(R.string.user_not_found))
            finish()
        }

        model.getUser().observe(this, { user ->
            this.user = user
            Glide.with(context)
                .load(user.avatar)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .fitCenter()
                .into(binding.ivPhoto)
            binding.tvName.text = user.name
            binding.tvCompany.text = if (user.company == "null") "-" else user.company
            binding.tvLocation.text = if (user.location == "null") "-" else user.location
            binding.tvRepository.text =
                "${myNumberFormat(user.repository)} ${context.resources.getString(R.string.repository)}"
            binding.tvFollower.text =
                "${myNumberFormat(user.follower)} ${context.resources.getString(R.string.follower)}"
            binding.tvFollowing.text =
                "${myNumberFormat(user.following)} ${context.resources.getString(R.string.following)}"

            binding.ivPhoto.setOnClickListener {
                startActivity(
                    Intent(context, ImageViewerActivity::class.java)
                        .putExtra("img", user.avatar)
                )
            }

            model.getFavorite(user.username).observe(this, {
                isFavorite = it != null
                if (isFavorite) {
                    binding.btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else binding.btnFavorite.setImageResource(R.drawable.ic_outline_favorite_border_24)
            })
        })


        val sectionsPagerAdapter = SectionsPagerAdapter(context)
        binding.vp2.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.vp2) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            Thread {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(user.avatar)
                    .submit()
                    .get()

                val uri = getLocalBitmapUri(bitmap)

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "*/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "${user.name}\n${user.company}\n${user.location}"
                )
                startActivity(Intent.createChooser(shareIntent, "Share"))
            }.start()
        }

        binding.btnFavorite.setOnClickListener {
            if (isFavorite) {
                val rs = model.deleteFavorite(user.username)
                println("rs : $rs")
                apiRequest.showToast(getString(R.string.delete_favorite))
            } else {
                userFavorite.let {
                    it.username = user.username
                    it.name = user.name
                    it.avatar = user.avatar
                    it.company = user.company
                    it.location = user.location
                    it.followers = user.follower
                    it.following = user.following
                    it.repository = user.repository
                    it.cdd = getCurrentDate()
                }
                val rs = model.insertFavorite(userFavorite)
                println("rs : $rs")
                apiRequest.showToast(getString(R.string.add_favorite))
            }
        }


    }

    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis().toString() + ".png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.msg_error_process), Toast.LENGTH_SHORT)
                .show()
        }
        return bmpUri
    }

    inner class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = FollowerFragment()
                1 -> fragment = FollowingFragment()
            }
            val bundle = Bundle()
            bundle.putString(USERNAME, userName)
            fragment?.arguments = bundle

            return fragment as Fragment
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}