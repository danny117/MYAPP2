package com.e607garage.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial


class WordListAdapter : ListAdapter<Word, WordListAdapter.WordViewHolder>(WordsComparator()) {
    var updateWord: ((Word) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {

        val wx = WordViewHolder.create(parent)
        return wx
    }

    override fun onViewRecycled(holder: WordViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return 12
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = getItem(position)
        val wordItemView: AppCompatEditText = holder.itemView.findViewById(R.id.textView)
        val wordItemSwitch: SwitchMaterial = holder.itemView.findViewById(R.id.switch1)
        val sbRed: SeekBar = holder.itemView.findViewById(R.id.sbRed)
        val sbGreen: SeekBar = holder.itemView.findViewById(R.id.sbGreen)
        val sbBlue: SeekBar = holder.itemView.findViewById(R.id.sbBlue)
        val sbAlpha: SeekBar = holder.itemView.findViewById(R.id.sbAlpha)
        val sample: TextView = holder.itemView.findViewById(R.id.textView2)
        wordItemView.setText(word.word, TextView.BufferType.EDITABLE)
        wordItemView.isEnabled = false
        sbRed.visibility = View.GONE
        sbGreen.visibility = View.GONE
        sbBlue.visibility = View.GONE
        sbAlpha.visibility = View.GONE

        wordItemSwitch.isChecked = word.checked
        wordItemView.setText( word.word,TextView.BufferType.EDITABLE)
        wordItemSwitch.isChecked = word.checked
        wordItemSwitch.tag = word._id
        wordItemSwitch.setOnClickListener {
            val word = getItem(holder.adapterPosition)
            val jx = it as SwitchMaterial
            word.checked = jx.isChecked
            updateWord?.invoke(word)
        }

        val swControls: SwitchMaterial = holder.itemView.findViewById(R.id.swControls)
        swControls.setOnClickListener {
            val jx = it as SwitchMaterial
            if (jx.isChecked) {
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
                var t = wordItemView.text
                if (t != null) {
                    var word = getItem(holder.adapterPosition)
                    if( !t.equals(word.word)){
                        word.word = t.toString()
                        updateWord?.invoke(word)
                    }
                }
            }

        }
        sbRed.progress = Color.red(word.color)
        sbGreen.progress = Color.green(word.color)
        sbBlue.progress = Color.blue(word.color)
        sbAlpha.progress = Color.alpha(word.color)

        var color = Color.argb(
            sbAlpha.progress,
            sbRed.progress,
            sbGreen.progress,
            sbBlue.progress
        )
        sample.setBackgroundColor(color)


        fun getListener() = object : SeekBar.OnSeekBarChangeListener {
            /*changes to screen are done here*/
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                var color = Color.argb(
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
                    val word = getItem(holder.adapterPosition)
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


/*
        sbRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            */
/*changes to screen are done here*//*

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}

            */
/* when finished then send update to database*//*

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    val word = getItem(holder.adapterPosition)
                    word.color = Color.argb(
                        sbAlpha.progress.toInt(),
                        sbRed.progress.toInt(),
                        sbGreen.progress.toInt(),
                        sbBlue.progress.toInt()
                    )
                    updateWord?.invoke(word)
                }
            }
        })
*/

    }


    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup): WordViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return WordViewHolder(view)
            }
        }

    }

    class WordsComparator : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.word == newItem.word
        }
    }


}


