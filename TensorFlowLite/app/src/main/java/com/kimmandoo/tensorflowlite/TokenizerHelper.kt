package com.kimmandoo.tensorflowlite

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class TokenizerHelper(context: Context, vocabFileName: String) {
    private val vocab: Map<String, Int>
    private val maxLen: Int = 64  // 최대 시퀀스 길이

    init {
        vocab = loadVocabulary(context, vocabFileName)
    }

    private fun loadVocabulary(context: Context, fileName: String): Map<String, Int> {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, Int>>() {}.type
            return Gson().fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyMap()
        }
    }

    fun tokenize(text: String): List<Int> {
        val words = text.lowercase().split(Regex("\\s+"))
        return words.map { word ->
            vocab[word] ?: vocab["<UNK>"] ?: 0
        }
    }

    fun padSequence(sequence: List<Int>): List<Int> {
        return if (sequence.size >= maxLen) {
            sequence.take(maxLen)
        } else {
            sequence + List(maxLen - sequence.size) { 0 }
        }
    }

    fun tokenizeAndPad(text: String): List<Int> {
        val tokens = tokenize(text)
        return padSequence(tokens)
    }
}