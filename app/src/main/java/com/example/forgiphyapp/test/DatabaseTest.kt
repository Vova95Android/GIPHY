package com.example.forgiphyapp.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

//class DatabaseTest: GifDatabaseDao {
//
//    private var list= listOf(
//        GifData("a1","https://media4.giphy.com/media/xUPGcMkmmdezbgGI12/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/xUPGcMkmmdezbgGI12/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("b1","https://media0.giphy.com/media/If9DvbHHowI1SWKLae/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/If9DvbHHowI1SWKLae/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("c1","https://media0.giphy.com/media/KFtUrrgyTAbUP5dUcT/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/KFtUrrgyTAbUP5dUcT/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("d1","https://media4.giphy.com/media/KAe3Ez73EnmbDh9rot/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/KAe3Ez73EnmbDh9rot/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("e1","https://media1.giphy.com/media/duexIlfr9yYwYE23UA/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media1.giphy.com/media/duexIlfr9yYwYE23UA/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("f1","https://media2.giphy.com/media/3oEduLl9m8HCTNxIli/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media2.giphy.com/media/3oEduLl9m8HCTNxIli/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("g1","https://media1.giphy.com/media/U3fqHxuwlvIqNhFzBu/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media1.giphy.com/media/U3fqHxuwlvIqNhFzBu/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("h1","https://media0.giphy.com/media/l0MYyyCiPuIpI3G7u/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/l0MYyyCiPuIpI3G7u/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("i1","https://media3.giphy.com/media/gdfOTO6wRic6PRZ7G8/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media3.giphy.com/media/gdfOTO6wRic6PRZ7G8/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("j1","https://media4.giphy.com/media/l396W94ar4aIeYn6M/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/l396W94ar4aIeYn6M/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("a2","https://media4.giphy.com/media/xUPGcMkmmdezbgGI12/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/xUPGcMkmmdezbgGI12/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("b2","https://media0.giphy.com/media/If9DvbHHowI1SWKLae/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/If9DvbHHowI1SWKLae/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("c2","https://media0.giphy.com/media/KFtUrrgyTAbUP5dUcT/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/KFtUrrgyTAbUP5dUcT/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("d2","https://media4.giphy.com/media/KAe3Ez73EnmbDh9rot/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/KAe3Ez73EnmbDh9rot/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("e2","https://media1.giphy.com/media/duexIlfr9yYwYE23UA/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media1.giphy.com/media/duexIlfr9yYwYE23UA/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("f2","https://media2.giphy.com/media/3oEduLl9m8HCTNxIli/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media2.giphy.com/media/3oEduLl9m8HCTNxIli/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("g2","https://media1.giphy.com/media/U3fqHxuwlvIqNhFzBu/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media1.giphy.com/media/U3fqHxuwlvIqNhFzBu/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("h2","https://media0.giphy.com/media/l0MYyyCiPuIpI3G7u/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/l0MYyyCiPuIpI3G7u/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("i2","https://media3.giphy.com/media/gdfOTO6wRic6PRZ7G8/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media3.giphy.com/media/gdfOTO6wRic6PRZ7G8/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("j2","https://media4.giphy.com/media/l396W94ar4aIeYn6M/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/l396W94ar4aIeYn6M/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("a3","https://media4.giphy.com/media/xUPGcMkmmdezbgGI12/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/xUPGcMkmmdezbgGI12/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("b3","https://media0.giphy.com/media/If9DvbHHowI1SWKLae/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/If9DvbHHowI1SWKLae/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("c3","https://media0.giphy.com/media/KFtUrrgyTAbUP5dUcT/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/KFtUrrgyTAbUP5dUcT/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("d3","https://media4.giphy.com/media/KAe3Ez73EnmbDh9rot/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/KAe3Ez73EnmbDh9rot/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("e3","https://media1.giphy.com/media/duexIlfr9yYwYE23UA/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media1.giphy.com/media/duexIlfr9yYwYE23UA/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("f3","https://media2.giphy.com/media/3oEduLl9m8HCTNxIli/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media2.giphy.com/media/3oEduLl9m8HCTNxIli/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("g3","https://media1.giphy.com/media/U3fqHxuwlvIqNhFzBu/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media1.giphy.com/media/U3fqHxuwlvIqNhFzBu/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("h3","https://media0.giphy.com/media/l0MYyyCiPuIpI3G7u/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media0.giphy.com/media/l0MYyyCiPuIpI3G7u/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("i3","https://media3.giphy.com/media/gdfOTO6wRic6PRZ7G8/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media3.giphy.com/media/gdfOTO6wRic6PRZ7G8/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false),
//        GifData("j3","https://media4.giphy.com/media/l396W94ar4aIeYn6M/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g","https://media4.giphy.com/media/l396W94ar4aIeYn6M/giphy.gif?cid=0c53c82bsf1xp8uwujq9hdkbxy6fx8ysqo3od5lqd0cue6c5&rid=giphy.gif&ct=g",true, false)
//    )
//
//    private val listLiveData= MutableLiveData(list)
//
//    override suspend fun insert(gifData: GifData) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun update(gifData: GifData) {
//        gifData.active=true
//        list = list.minus(gifData)
//        Log.i("Database",list.size.toString())
//        listLiveData.value=list
//    }
//
//    override fun getAllGifDataLiveData(): LiveData<List<GifData>> {
//        return listLiveData
//    }
//
//    override fun getAllGifData(): List<GifData> {
//        return list
//    }
//
//    override fun deleteAllGif(data: List<GifData>) {
//    }
//}