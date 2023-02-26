package com.e607garage.myapplication

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var danny117BluetoothRepository: Danny117BluetoothRepository

    private val MY_PERMISSION_REQUEST_CODE: Int = 300548032
    //private lateinit var layout: View

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    private fun actionBluetooth() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.AddLIght -> addItem()
            R.id.RemoveLIght -> removeItem()
            R.id.action_bluetooth -> actionBluetooth()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addItem() {
        wordViewModel.viewModelScope.launch {
            val word = Word("Here is the new light")
            word._id = wordViewModel.getLightCount()
            wordViewModel.insert(word)
        }
    }

    private fun removeItem() {
        wordViewModel.viewModelScope.launch {
            wordViewModel.removeLast()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /*this layout manager scrolls to the last item added
    wierd that on items added isn't called when the recycler view is first loaded*/
    private fun getLayoutManager(myAppContext: Context) =
        object : LinearLayoutManager(myAppContext) {
            override fun onItemsAdded(
                recyclerView: RecyclerView,
                positionStart: Int,
                itemCount: Int
            ) {
                super.onItemsAdded(recyclerView, positionStart, itemCount)
                this.scrollToPosition(positionStart)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //layout = findViewById(R.id.layout)
        val jx = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(jx)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.recycledViewPool.setMaxRecycledViews(12, 0)
        danny117BluetoothRepository =
            Danny117BluetoothRepository(applicationContext, wordViewModel)
        danny117BluetoothRepository.fetchLatestReply()

        val adapter = WordListAdapter2(wordViewModel,danny117BluetoothRepository)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = getLayoutManager(this)
        recyclerView.itemAnimator = null

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        val observer: Observer<List<Word>> =
            Observer { word: List<Word> ->
                word.let { adapter.submitList(it) }
            }
        wordViewModel.allWords.observe(this, observer)

        checkAndAskPermissions()

    }

    //check and ask permissions
    private fun checkAndAskPermissions() {
        val a = checkPermissions()
        if (a.isNotEmpty()) {
            requestPermissions(a, MY_PERMISSION_REQUEST_CODE)
        }
    }

    //Returns array of needed permissions that can be used later
    private fun checkPermissions(): Array<String> {
        val pList = mutableListOf<String>()
//        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            pList.add(android.Manifest.permission.BLUETOOTH_SCAN)
//        }
//        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            pList.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
//        }
        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            pList.add(android.Manifest.permission.BLUETOOTH_CONNECT)
        }
        return pList.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
