package com.edadursun.habitify.view.adapter

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edadursun.habitify.R

class EmojiAdapter(
    private val emojis : List<String> ,  //Gösterilecek olan emoji listesi
    private val onClick: (String) -> Unit //Emojiye tıklandığında çalışacak callback
):RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    //Herbir emojini view holderı
    inner class EmojiViewHolder(val view: View):RecyclerView.ViewHolder(view){
        private val emojiText=view.findViewById<TextView>(R.id.emojiText)
        //View ile emojiyi birbirine bağladığımız yer
        fun bind(emoji:String){
            emojiText.text=emoji
            view.setOnClickListener { onClick(emoji) }
        }

    }

    //item_emoji_bg xml i burda inflate edilir
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emoji_bg,parent,false)
        return EmojiViewHolder(view)
    }

    //Recyclerviewde kaç emoji gösterilecek
    override fun getItemCount() = emojis.size

    //Belirli emojiyi view holdera bağlar
    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojis[position])
    }

}