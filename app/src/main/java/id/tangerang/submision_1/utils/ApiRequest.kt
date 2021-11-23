package id.tangerang.submision_1.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.tangerang.submision_1.BuildConfig
import id.tangerang.submision_1.R
import id.tangerang.submision_1.databinding.MyProgressDialogBinding

class ApiRequest(val context: Context) {
    private var progressDialog: Dialog = Dialog(this.context)
    private var volleySingleton: VolleySingleton = VolleySingleton(context)

    fun showToast(msg: String = context.getString(R.string.toast_error)) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showProgressDialog(msg: String = context.resources.getString(R.string.msg_loading)) {
        val bind = MyProgressDialogBinding.inflate(LayoutInflater.from(context))
        bind.tvTitle.text = msg
        progressDialog.setCanceledOnTouchOutside(true)
        progressDialog.show()
        progressDialog.setContentView(bind.root)
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog.setOnCancelListener {
            volleySingleton.cancelTagRequest(
                context.javaClass.simpleName
            )
            showToast("cancel request ${context.javaClass.simpleName}")
        }
    }

    fun dismissProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun get(
        url: String,
        response: Response.Listener<String>,
        method: Int = Request.Method.GET,
        params: MutableMap<String, String> = mutableMapOf(),
        error: Response.ErrorListener = Response.ErrorListener { error1 ->
            error1.printStackTrace()
            errorResponse(context, error1)
            dismissProgressDialog()
        },
    ) {
        if (BuildConfig.DEBUG) {
            println("url : $url")
            println("params : $params")
        }
        val stringRequest: StringRequest = object : StringRequest(
            method,
            url,
            response,
            error
        ) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }

            /*override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()
                headers["Authorization"] = "token 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"
                return headers
            }*/
        }
        volleySingleton.addToRequestQueue(stringRequest)
    }

    fun cancelRequest(tag: String = "") {
        if (tag.isNotBlank()) {
            volleySingleton.cancelTagRequest(tag)
        } else volleySingleton.cancelAll()
    }

    private fun errorResponse(context: Context?, error: VolleyError) {
        try {
            when (error) {
                is NoConnectionError -> {
                    Toast.makeText(
                        context,
                        "Tidak ada koneksi internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkError -> {
                    Toast.makeText(
                        context,
                        "Periksa kembali koneksi internet and",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ServerError -> {
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan server, silakan coba beberapa saat lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is TimeoutError -> {
                    Toast.makeText(
                        context,
                        "Timeout. Silakan coba beberapa saat lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is AuthFailureError -> {
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan, silakan coba beberapa saat lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ParseError -> {
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan, silakan coba beberapa saat lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan, silakan coba beberapa saat lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private class VolleySingleton constructor(private var context: Context) {

        @SuppressLint("StaticFieldLeak")
        companion object {

            @SuppressLint("StaticFieldLeak")
            @Volatile
            private var instance: VolleySingleton? = null
            fun getInstance(context: Context) = instance ?: synchronized(this) {
                instance ?: VolleySingleton(context)
            }
        }

        val requestQueue: RequestQueue by lazy {
            Volley.newRequestQueue(context.applicationContext)
        }

        fun <T> addToRequestQueue(req: Request<T>) {
            req.tag = context.javaClass.simpleName
            requestQueue.add(req)
        }

        fun cancelTagRequest(tag: Any?) {
            requestQueue.cancelAll(tag)
        }

        fun cancelAll() {
            requestQueue.cancelAll { true }
        }
    }
}