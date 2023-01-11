package com.e607garage.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.jetbrains.annotations.NonNls


class WordListAdapter2 : ListAdapter<Word, WordListAdapter2.WordViewHolder2>(WordsComparator()) {
    var updateWord: ((Word) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder2 {
        return  WordViewHolder2.create(parent)
    }

    override fun onViewRecycled(holder: WordViewHolder2) {
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return 12
    }

    override fun onBindViewHolder(holder: WordViewHolder2, position: Int) {
        val word = getItem(position)
        holder.bind(word,updateWord)

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


    class WordViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var word: Word
        private var updateWord: ((Word) -> Unit)? = null

        val wordItemView: AppCompatEditText = itemView.findViewById(R.id.textView)
        val wordItemSwitch: SwitchMaterial = itemView.findViewById(R.id.switch1)
        val swControls: SwitchMaterial = itemView.findViewById(R.id.swControls)
        val viewControls: ConstraintLayout = itemView.findViewById(R.id.viewControls)
        val sbRed: SeekBar = itemView.findViewById(R.id.sbRed)
        val sbGreen: SeekBar = itemView.findViewById(R.id.sbGreen)
        val sbBlue: SeekBar = itemView.findViewById(R.id.sbBlue)
        val sbAlpha: SeekBar = itemView.findViewById(R.id.sbAlpha)
        val sample: TextView = itemView.findViewById(R.id.textView2)

        fun bind(word: Word,updateWord:  ((Word) -> Unit)?) {
            this.updateWord = updateWord
            this.word = word
            wordItemView.setText(word.word, TextView.BufferType.EDITABLE)
            wordItemView.isEnabled = false

            wordItemSwitch.isChecked = word.checked
            wordItemSwitch.setOnClickListener {
                val jx = it as SwitchMaterial
                word.checked = jx.isChecked
                updateWord?.invoke(word)
            }
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
                        if( !t.equals(word.word)){
                            word.word = t.toString()
                            updateWord?.invoke(word)
                        }
                    }
                }
            }
            swControls.callOnClick()
            sbRed.progress = Color.red(word.color)
            sbGreen.progress = Color.green(word.color)
            sbBlue.progress = Color.blue(word.color)
            sbAlpha.progress = Color.alpha(word.color)

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
            getListener().onProgressChanged(sbRed,0,false)



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
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.word == newItem.word
        }
    }


}


