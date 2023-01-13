package com.e607garage.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {


    private lateinit var adapter: WordListAdapter2
    var REQUEST_ENABLE_BT = 123
    private val newWordActivityRequestCode = 1
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.AddLIght -> addItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addItem() {
        wordViewModel.viewModelScope.launch {
            var word = Word("Here is the new light")
            wordViewModel.insert(word)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }


    private fun getLayoutManager(myAppContext: Context) = object : LinearLayoutManager(myAppContext) {
        override fun onItemsAdded(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
            super.onItemsAdded(recyclerView, positionStart, itemCount)
            if (itemCount == 1) {
                this.scrollToPosition(positionStart)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var jx = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(jx)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.recycledViewPool.setMaxRecycledViews(12, 0)
        adapter = WordListAdapter2()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = getLayoutManager(applicationContext)

        recyclerView.setItemAnimator(null)


        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is


        val observer: Observer<List<Word>> =
            Observer { word: List<Word> ->
                word.let { adapter.submitList(it) }
            }

        wordViewModel.allWords.observe(this, observer)

        adapter.updateWord = { word ->
            //    if (wordViewModel.allWords.hasObservers()) {
            //      wordViewModel.allWords.removeObserver(observer)
            //}
            wordViewModel.viewModelScope.launch {
                wordViewModel.update(word)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let { reply ->
                val word = Word(reply)
                wordViewModel.viewModelScope.launch {
                    wordViewModel.insert(word)
                }


            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

