package id.tangerang.submision_1.ui.userfavorite

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doAfterTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.tangerang.submision_1.R
import id.tangerang.submision_1.database.Favorite
import id.tangerang.submision_1.databinding.ActivitySearchUsersBinding
import id.tangerang.submision_1.databinding.DialogChangeThemeBinding
import id.tangerang.submision_1.ui.detailuser.DetailUserActivity
import id.tangerang.submision_1.utils.ApiRequest
import id.tangerang.submision_1.utils.SettingPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserFavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchUsersBinding
    private lateinit var apiRequest: ApiRequest
    private lateinit var userAdapter: UserFavoriteAdapter
    private lateinit var model: UserFavoriteViewModel
    private var users: MutableList<Favorite> = mutableListOf()
    private val context = this@UserFavoriteActivity

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            title = getString(R.string.user_favorite)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        apiRequest = ApiRequest(context)
        val pref = SettingPreferences.getInstance(dataStore)
        model = ViewModelProvider(
            this,
            UserFavoriteViewModelFactory(this.application, pref)
        ).get(UserFavoriteViewModel::class.java)

        userAdapter = UserFavoriteAdapter(context, users)
        binding.rvList.adapter = userAdapter
        binding.rvList.layoutManager = LinearLayoutManager(context)

        userAdapter.setOnClickItem(object : UserFavoriteAdapter.IOnClickItem {
            override fun onClick(position: Int) {
                startActivity(
                    Intent(context, DetailUserActivity::class.java)
                        .putExtra("username", users[position].name)
                )
            }
        })

        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = true
            loadUserFavorites()
        }

        binding.etCari.doAfterTextChanged {
            userAdapter.filter(binding.etCari.text.toString(), users)
        }

        loadUserFavorites()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadUserFavorites() {
        model.getUserFavorites().observe(this, {
            users.clear()
            users.addAll(it)

            userAdapter.notifyDataSetChanged()

            if (binding.swipe.isRefreshing) binding.swipe.isRefreshing = false
        })
    }

    override fun onRestart() {
        super.onRestart()
        loadUserFavorites()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option_2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_language -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.menu_theme -> {
                val bind = DialogChangeThemeBinding.inflate(layoutInflater)
                val dialog = Dialog(context)
                dialog.setContentView(bind.root)
                model.getThemeSettings().observe(this,
                    { isDarkModeActive: Boolean ->
                        if (isDarkModeActive) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            bind.switchThemeMode.isChecked = true
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            bind.switchThemeMode.isChecked = false
                        }
                    })
                bind.switchThemeMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                    model.saveThemeSetting(isChecked)
                    dialog.dismiss()
                }
                dialog.show()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}