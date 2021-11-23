package id.tangerang.submision_1.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.tangerang.submision_1.R
import id.tangerang.submision_1.databinding.ItemUserV2Binding
import id.tangerang.submision_1.adapters.UserAdapter.ViewHolder
import id.tangerang.submision_1.models.UserModel
import java.util.ArrayList

class UserAdapter(val context: Context, var users: MutableList<UserModel>) :
    RecyclerView.Adapter<ViewHolder>() {
    private lateinit var bind: ItemUserV2Binding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        bind = ItemUserV2Binding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(bind.root, bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        Glide.with(context)
            .load(user.avatar)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.bind.ivPhoto)
        holder.bind.tvName.text = user.name

        holder.itemView.setOnClickListener {
            iOnClickItem.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(itemView: View, val bind: ItemUserV2Binding) :
        RecyclerView.ViewHolder(itemView)

    @SuppressLint("NotifyDataSetChanged")
    fun filter(
        text: String,
        users: MutableList<UserModel>
    ) {
        val temp: MutableList<UserModel> = ArrayList<UserModel>()
        for (d in users) {
            if (d.name.lowercase().contains(text.lowercase())) {
                temp.add(d)
            }
        }
        if (TextUtils.isEmpty(text)) {
            this.users = users
        } else {
            this.users = temp
        }
        notifyDataSetChanged()
    }


    private lateinit var iOnClickItem: IOnClickItem

    fun setOnClickItem(iOnClickItem: IOnClickItem) {
        this.iOnClickItem = iOnClickItem
    }

    interface IOnClickItem {
        fun onClick(position: Int)
    }
}