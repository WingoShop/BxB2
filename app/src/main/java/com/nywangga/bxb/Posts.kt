package com.nywangga.bxb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

class Posts : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var tvBanner: TextView
    private lateinit var tvProfileName: TextView
    private val db = Firebase.firestore
    private var mAuth: FirebaseAuth? = null
    private var uid: String = ""
    private var name: String = ""
    private var email = ""
    private var email2 = ""
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBorrow: Button
    private lateinit var btnPaid: Button
    private lateinit var etAmount: EditText
    private lateinit var etRemark: EditText
    private lateinit var tvCurrentBalance: TextView
    private lateinit var firstDoc: Post
    private var createdBy = ""
    private lateinit var rvHistory: RecyclerView
    private var postHistoryList = mutableListOf<Post>()
    private var allDocs = mutableListOf<Post>()
    private lateinit var tvLogout: TextView
    private var realEmail: String = ""
    private lateinit var spinnerPair: Spinner
    private lateinit var adapterRV: PostHistoryAdapter
    private var pair = ""
    private var realPair = ""
    private var oldPair = ""
    private var allPairs = mutableListOf<String>()
    private lateinit var adapterSpinner: ArrayAdapter<String>
    private lateinit var currentUser2: FirebaseUser
    private var newestPair =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        progressBar = findViewById(R.id.progressBar)
        btnBorrow = findViewById(R.id.btnBorrow)
        mAuth = FirebaseAuth.getInstance()
        tvBanner = findViewById(R.id.tvBannerHead)
        btnPaid = findViewById(R.id.btnPaid)
        rvHistory = findViewById(R.id.rvHistory)
        tvLogout = findViewById(R.id.tvLogout)
        spinnerPair = findViewById(R.id.spinnerPair)

        tvProfileName = findViewById(R.id.tvProfileName)
        etAmount = findViewById(R.id.etAmount)
        etRemark = findViewById(R.id.etRemark)
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance)
        val currentUser = mAuth?.currentUser
        currentUser2 = currentUser!!

        if (currentUser != null) {
            uid = currentUser.uid.toString()
            email = currentUser.email.toString()
            db.collection("user").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()
                    name = user?.name.toString()
                    email2 = user?.email.toString()
                    pair = user?.currentPair.toString()
                    allPairs = user?.pairs as MutableList<String>
                    val selectedItemIndex = allPairs.indexOf(pair)

                    adapterSpinner = ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        allPairs
                    )
                    spinnerPair.adapter = adapterSpinner
                    spinnerPair.setSelection(selectedItemIndex)

                    spinnerPair.onItemSelectedListener = this

                    tvProfileName.text = name
                    realEmail = email
                    realPair = pair
                    oldPair = pair

                    adapterRV = PostHistoryAdapter(this, postHistoryList)
                    rvHistory.adapter = adapterRV
                    rvHistory.setHasFixedSize(true)
                    rvHistory.layoutManager = LinearLayoutManager(this)
                    //  }
                }
        }

        btnPaid.setOnClickListener {
            /*val documentList = listOf(realEmail, realPair).sorted()
            val createdByReal = realEmail

            email2 = documentList[0]
            pair = documentList[1]
            val uuid = UUID.randomUUID().toString()
            var postDb = Post(email2,pair,firstDoc.balance,0,"Paid",createdBy)
            db.collection("posts").document(uuid)
                .set(postDb)
                .addOnCompleteListener { toDbTask ->
                    if (toDbTask.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Paid",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                        val intent = Intent(this, Posts::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, "Initialization failed! Try again!", Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE

                    }
                }*/
            calculateBalance(false)
        }


        tvBanner.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivityForResult(intent, 249)
            finish()
        }

        tvLogout.setOnClickListener {
            mAuth!!.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivityForResult(intent, 249)
            finish()
        }



        tvProfileName.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("NAME", name)
            intent.putExtra("UID", uid)
            startActivity(intent)
            finish()
        }

        btnBorrow.setOnClickListener {

            calculateBalance(true)
        }
    }

    private fun calculateBalance(b: Boolean) {
        var amount = etAmount.text.toString().trim()
        var remarks = etRemark.text.toString().trim()

        if (amount.isEmpty() || amount.toInt() < 1) {
            etAmount.error = "Please enter a valid amount"
            etAmount.requestFocus()
            return
        }

        var amount2 = amount.toInt()


        val documentList = listOf(realEmail, realPair).sorted()
        val createdByReal = realEmail

        email2 = documentList[0]
        pair = documentList[1]
        val uuid = UUID.randomUUID().toString()
        val postsRef = db.collection("posts")
        val postz = postsRef
            .whereEqualTo("email_one", email2)
            .whereEqualTo("email_two", pair)
            .orderBy("created_date", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { docz ->
                for (document in docz.documents) {
                    val docu = document.toObject<Post>()
                    var newBalance: Int = 0
                    if (b) {
                        if (createdByReal == email2) {
                            newBalance = docu?.balance!! - amount2

                        } else if (createdByReal == pair) {
                            newBalance = docu?.balance!! + amount2
                        }
                    } else if (!b) {
                        if (createdByReal == email2) {
                            newBalance = docu?.balance!! + amount2

                        } else if (createdByReal == pair) {
                            newBalance = docu?.balance!! - amount2
                        }
                    }

                    val postDb = Post(email2, pair, amount2, newBalance, remarks, createdByReal)
                    db.collection("posts").document(uuid)
                        .set(postDb)
                        .addOnCompleteListener { toDbTask ->
                            if (toDbTask.isSuccessful) {
                                updateNewPair()
                                Toast.makeText(
                                    this,
                                    "Balance updated",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.visibility = View.GONE
                                val intent = Intent(this, Posts::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(
                                    this,
                                    "Initialization failed! Try again!",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.visibility = View.GONE

                            }
                        }
                }
            }
    }

    private fun updateNewPair() {
        if (oldPair != newestPair) {
            db.collection("user").document(uid).update(mapOf(
                "currentPair" to newestPair
            ))
        }
    }

    private fun setPostList(pair2: String) {

        val documentList = listOf(email, pair2).sorted()
        createdBy = email
        Log.d("POSTS", "newestdocumentSort: $documentList")
        email2 = documentList[0]
        this.pair = documentList[1]
        val displayPostsRef = db.collection("posts")
        val displayPosts = displayPostsRef
            .whereEqualTo("email_one", email2)
            .whereEqualTo("email_two", this.pair)
            .orderBy("created_date", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { docz ->
                for (doc in docz.documents) {
                    Log.d("POSTS", "DocumentSnapshot data: ${doc.data}")
                }

                firstDoc = docz.documents[0].toObject<Post>()!!
                var displayBalance = firstDoc!!.balance
                Log.d("POSTS", "DocumentSnapshot data: $displayBalance")
                if (createdBy == this.pair) {
                    displayBalance = displayBalance!!.toInt() * -1
                }
                tvCurrentBalance.text = "Current Balance: Rp $displayBalance"

                postHistoryList.clear()
                for (doc in docz.documents) {
                    postHistoryList.add(doc.toObject<Post>()!!)

                }
                Log.d("POSTS", "PostHistoryList: $postHistoryList")
                adapterRV.notifyDataSetChanged()
            }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        newestPair = spinnerPair.selectedItem.toString()
        realPair = newestPair
        Log.d("POSTS", "newestPair: $newestPair")
        setPostList(newestPair)

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}