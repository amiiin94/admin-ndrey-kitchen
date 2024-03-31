import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin_ndreykitchen.R
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var pemasukan: TextView
    private lateinit var pengeluaran: TextView
    private lateinit var total_transaksi: TextView
    private lateinit var makanan_terjual: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        pemasukan = view.findViewById(R.id.pemasukan)
        pengeluaran = view.findViewById(R.id.pengeluaran)
        total_transaksi = view.findViewById(R.id.total_transaksi)
        makanan_terjual = view.findViewById(R.id.makanan_terjual)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHomeFragmentData(requireContext())
    }

    private fun displayHomeFragmentData(pemasukanAmount: Int, pengeluaranAmount: Int, totalTransaksi: Int, makananTerjual: Int) {
        pemasukan.text = "IDR $pemasukanAmount"
        pengeluaran.text = "IDR $pengeluaranAmount"
        total_transaksi.text = totalTransaksi.toString()
        makanan_terjual.text = makananTerjual.toString()
    }

    private fun getHomeFragmentData(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getHomeFragmentData"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    Log.d("HomeFragmentData", response)
                    val jsonObject = JSONObject(response)

                    val pemasukanAmount = jsonObject.getInt("pemasukan_all")
                    val pengeluaranAmount = jsonObject.getInt("pengeluaran_all")
                    val totalTransaksi = jsonObject.getInt("transaction_all")
                    val makananTerjual = jsonObject.getInt("quantity_all")
                    displayHomeFragmentData(pemasukanAmount, pengeluaranAmount, totalTransaksi, makananTerjual)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("Error", error.toString())
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }
}
