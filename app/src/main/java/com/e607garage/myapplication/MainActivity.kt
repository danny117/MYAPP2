package com.e607garage.myapplication

import android.content.Context
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


    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.AddLIght -> addItem()
            R.id.RemoveLIght -> removeItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addItem() {
        wordViewModel.viewModelScope.launch {
            wordViewModel.insert(Word("Here is the new light"))
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

    private fun getLayoutManager(myAppContext: Context) = object : LinearLayoutManager(myAppContext){
        override fun onItemsAdded(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
            super.onItemsAdded(recyclerView, positionStart, itemCount)
            this.scrollToPosition(positionStart)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val jx = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(jx)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.recycledViewPool.setMaxRecycledViews(12, 0)
        val adapter = WordListAdapter2()
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

        adapter.updateWord = { word ->
            wordViewModel.viewModelScope.launch {
                wordViewModel.update(word)
            }
        }
    }


}

