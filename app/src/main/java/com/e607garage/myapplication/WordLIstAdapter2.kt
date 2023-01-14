package com.e607garage.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial


class WordListAdapter2 : ListAdapter<Word, WordListAdapter2.WordViewHolder2>(WordsComparator()) {
    var updateWord: ((Word) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder2 {
        return WordViewHolder2.create(parent)
    }


    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return 12
    }

    override fun onBindViewHolder(holder: WordViewHolder2, position: Int) {
        val word = getItem(position)
        holder.bind(word, updateWord)
    }


    class WordViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var word: Word
        private var updateWord: ((Word) -> Unit)? = null

        private val wordItemView: AppCompatEditText = itemView.findViewById(R.id.textView)
        private val wordItemSwitch: SwitchMaterial = itemView.findViewById(R.id.switch1)
        private val swControls: SwitchMaterial = itemView.findViewById(R.id.swControls)
        val sbRed: SeekBar = itemView.findViewById(R.id.sbRed)
        val sbGreen: SeekBar = itemView.findViewById(R.id.sbGreen)
        val sbBlue: SeekBar = itemView.findViewById(R.id.sbBlue)
        val sbAlpha: SeekBar = itemView.findViewById(R.id.sbAlpha)
        val sample: TextView = itemView.findViewById(R.id.textView2)

        fun bind(word: Word, updateWord: ((Word) -> Unit)?) {
            this.updateWord = updateWord
            this.word = word
            wordItemView.setText(word.word, TextView.BufferType.EDITABLE)
            wordItemView.isEnabled = false

            wordItemSwitch.isChecked = word.checked
            wordItemSwitch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
                word.checked = b
                updateWord?.invoke(word)
            }
            fun doChange(b: Boolean) {
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
                    val t = wordItemView.text
                    if (t != null) {
                        if (!t.equals(word.word)) {
                            word.word = t.toString()
                            updateWord?.invoke(word)
                        }
                    }
                }
            }
            swControls.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
                doChange(b)
            }
            doChange(swControls.isChecked)
            sbRed.progress = Color.red(word.color)
            sbGreen.progress = Color.green(word.color)
            sbBlue.progress = Color.blue(word.color)
            sbAlpha.progress = Color.alpha(word.color)

            fun getListener() = object : SeekBar.OnSeekBarChangeListener {
                /*changes to screen are done here*/
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
                    if (p0 != null) {
                        word.color = Color.argb(
                            sbAlpha.progress,
                            sbRed.progress,
                            sbGreen.progress,
                            sbBlue.progress
                        )
                        updateWord?.invoke(word)
                    }
                }
            }
            sbRed.setOnSeekBarChangeListener(getListener())
            sbGreen.setOnSeekBarChangeListener(getListener())
            sbBlue.setOnSeekBarChangeListener(getListener())
            sbAlpha.setOnSeekBarChangeListener(getListener())
            getListener().onProgressChanged(sbRed, 0, false)


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
                    && oldItem.color == newItem.color)
        }
    }

}


