package id.tangerang.submision_1.ui.searchuser

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.tangerang.submision_1.ui.detailuser.DetailUserActivity
import id.tangerang.submision_1.R
import id.tangerang.submision_1.ui.userfavorite.UserFavoriteActivity
import id.tangerang.submision_1.databinding.ActivitySearchUsersBinding
import id.tangerang.submision_1.utils.ApiRequest
import id.tangerang.submision_1.adapters.UserAdapter
import id.tangerang.submision_1.models.UserModel
import java.util.*
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate

import androidx.core.widget.doAfterTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import id.tangerang.submision_1.databinding.DialogChangeThemeBinding
import id.tangerang.submision_1.utils.SettingPreferences

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SearchUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchUsersBinding
    private lateinit var apiRequest: ApiRequest
    private lateinit var userAdapter: UserAdapter
    private lateinit var model: SearchUsersViewModel
    private var users: MutableList<UserModel> = mutableListOf()
    private val context = this@SearchUsersActivity
    private var backPress = false
    private var textQuery = "saveme"

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            title = getString(R.string.title_search_activity)
        }

        apiRequest = ApiRequest(context)
        val pref = SettingPreferences.getInstance(dataStore)

        userAdapter = UserAdapter(context, users)
        binding.rvList.adapter = userAdapter
        binding.rvList.layoutManager = LinearLayoutManager(context)

        userAdapter.setOnClickItem(object : UserAdapter.IOnClickItem {
            override fun onClick(position: Int) {
                startActivity(
                    Intent(context, DetailUserActivity::class.java)
                        .putExtra("username", users[position].name)
                )
            }
        })

        model = ViewModelProvider(
            this,
            SearchUsersViewModelFactory(this.application, pref)
        ).get(SearchUsersViewModel::class.java)

        model.setApiRequest(apiRequest)
        model.getUsers().observe(this, { users ->
            this.users.clear()
            this.users.addAll(users)
            userAdapter.notifyDataSetChanged()

            apiRequest.dismissProgressDialog()
            if (binding.swipe.isRefreshing) binding.swipe.isRefreshing = false
        })

        binding.swipe.setOnRefreshListener {
            if (textQuery.isBlank()) {
                binding.swipe.isRefreshing = false
            } else {
                model.loadUsers(textQuery)
            }
        }

        binding.etCari.setOnEditorActionListener { _, actionId, _ ->
            apiRequest.showProgressDialog()
            model.loadUsers(textQuery)
            actionId == EditorInfo.IME_ACTION_SEARCH
        }

        binding.etCari.doAfterTextChanged {
            if (binding.etCari.text.toString().isBlank()) {
                users.clear()
                userAdapter.notifyDataSetChanged()
            }
            textQuery = binding.etCari.text.toString().trim()
        }

        model.loadUsers(textQuery)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_language -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.menu_favorite -> {
                val mIntent = Intent(context, UserFavoriteActivity::class.java)
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


    override fun onBackPressed() {
        if (backPress) {
            super.onBackPressed()
            return
        }
        this.backPress = true
        Toast.makeText(this, getString(R.string.msg_double_click_close), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ backPress = false }, 2000)
    }
}