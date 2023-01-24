package com.e607garage.myapplication

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WordListAdapter2(private var wordViewModel: WordViewModel,private val danny117BluetoothRepository: Danny117BluetoothRepository) :
    ListAdapter<Word, WordListAdapter2.WordViewHolder2>(WordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder2 {
        return WordViewHolder2.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return 12
    }

    override fun onBindViewHolder(holder: WordViewHolder2, position: Int) {
        val word = getItem(position)
        holder.bind(word, wordViewModel, danny117BluetoothRepository )
    }

    class WordViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scope = CoroutineScope(Dispatchers.Default)
        private lateinit var word: Word
        private val wordItemView: AppCompatEditText = itemView.findViewById(R.id.textView)
        private val wordItemSwitch: SwitchMaterial = itemView.findViewById(R.id.switch1)
        private val swControls: SwitchMaterial = itemView.findViewById(R.id.swControls)
        val sbRed: SeekBar = itemView.findViewById(R.id.sbRed)
        val sbGreen: SeekBar = itemView.findViewById(R.id.sbGreen)
        val sbBlue: SeekBar = itemView.findViewById(R.id.sbBlue)
        val sbAlpha: SeekBar = itemView.findViewById(R.id.sbAlpha)
        val sample: TextView = itemView.findViewById(R.id.textView2)
        private val tvSync: TextView = itemView.findViewById(R.id.Update)

        private fun setTVSync() {
            if (word.color == word.recolor && word.checked == word.rechecked) {
                tvSync.text = ""
                tvSync.visibility = View.GONE
            } else {
                tvSync.visibility = View.VISIBLE
                //getText(R.string.syncing_light_chain)
                tvSync.text = "Syncing to light chain..."
            }
        }

        private fun doChange(b: Boolean) {
            if (b) {
                wordItemView.isEnabled = true
                wordItemView.requestFocus()
                sbRed.visibility = View.VISIBLE
                sbGreen.visibility = View.VISIBLE
                sbBlue.visibility = View.VISIBLE
                sbAlpha.visibility = View.VISIBLE
            } else {
                wordItemView.isEnabled = false
                sbRed.visibility = View.GONE
                sbGreen.visibility = View.GONE
                sbBlue.visibility = View.GONE
                sbAlpha.visibility = View.GONE
            }
        }


        fun bind(word: Word, wordViewModel: WordViewModel, danny117BluetoothRepository: Danny117BluetoothRepository) {

            fun getListener(wordViewModel: WordViewModel) = object : SeekBar.OnSeekBarChangeListener {
                /*changes to screen are done immediately here*/
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val color = Color.argb(
                        sbAlpha.progress,
                        sbRed.progress,
                        sbGreen.progress,
                        sbBlue.progress
                    )
                    sample.setBackgroundColor(color)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                /* when finished then send update to database*/
                override fun onStopTrackingTouch(p0: SeekBar?) {
                    word.color = Color.argb(
                        sbAlpha.progress,
                        sbRed.progress,
                        sbGreen.progress,
                        sbBlue.progress
                    )
                    setTVSync()
                    scope.launch {
                        wordViewModel.update(word)
                    }
                    danny117BluetoothRepository.writeB(word)
                }
            }

            this.word = word
            wordItemView.setText(word.word, TextView.BufferType.EDITABLE)
            wordItemView.isEnabled = false
            wordItemView.setOnEditorActionListener() { textView: TextView, i: Int, keyEvent: KeyEvent ->
                when (i) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if (!textView.text.toString().equals(word.word)) {
                            scope.launch {
                                wordViewModel.update(word)
                            }
                            danny117BluetoothRepository.writeB(word)
                        }
                        true
                    }
                else -> false
                }
            }
            wordItemSwitch.isChecked = word.checked
            wordItemSwitch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
                word.checked = b
                setTVSync()
                scope.launch {
                    wordViewModel.update(word)
                }
                danny117BluetoothRepository.writeB(word)
            }
            swControls.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
                doChange(b)
                val t = wordItemView.text.toString()
                if (t != null) {
                    if (!t.equals(word.word)) {
                        word.word = t
                        scope.launch {
                            wordViewModel.update(word)
                        }
                        danny117BluetoothRepository.writeB(word)
                    }
                }
            }
            doChange(swControls.isChecked)
            sbRed.progress = Color.red(word.color)
            sbGreen.progress = Color.green(word.color)
            sbBlue.progress = Color.blue(word.color)
            sbAlpha.progress = Color.alpha(word.color)
            sbRed.setOnSeekBarChangeListener(getListener(wordViewModel))
            sbGreen.setOnSeekBarChangeListener(getListener(wordViewModel))
            sbBlue.setOnSeekBarChangeListener(getListener(wordViewModel))
            sbAlpha.setOnSeekBarChangeListener(getListener(wordViewModel))
            setTVSync()



        }


        companion object {
            fun create(parent: ViewGroup): WordViewHolder2 {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return WordViewHolder2(view)
            }
        }

    }

    class WordsComparator : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            //the same item has the same id
            return (oldItem._id == newItem._id)
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return (oldItem._id == newItem._id
                    && oldItem.checked == newItem.checked
                    && oldItem.word == newItem.word
                    && oldItem.color == newItem.color
                    && oldItem.recolor == newItem.recolor
                    && oldItem.rechecked == newItem.rechecked)
        }
    }

}



