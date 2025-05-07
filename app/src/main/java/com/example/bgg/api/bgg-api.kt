package com.example.bgg.api

import android.media.Image
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

data class Game(val id: Int, val name: String)
data class GameDetail(val id: Int, val name: String, val desc: String, val img: String)

fun searchBoardGames(query: String): List<Game> {
    val client: OkHttpClient = OkHttpClient()
    val url ="https://api.geekdo.com/xmlapi2/search?type=boardgame&query=${query}"
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: return emptyList()

    return parseSearchResults(body)
}

fun searchGameDetailsByID(id: Int): GameDetail? {
    val client = OkHttpClient()
    val url = "https://api.geekdo.com/xmlapi2/thing?id=$id"
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: return null

    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(body.reader())

    var eventType = parser.eventType
    var name: String? = null
    var image: String? = null
    var description: String? = null

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "name" -> {
                        if (parser.getAttributeValue(null, "type") == "primary") {
                            name = parser.getAttributeValue(null, "value")
                        }
                    }
                    "image" -> {
                        parser.next()
                        image = parser.text
                    }
                    "description" -> {
                        parser.next()
                        description = parser.text
                    }
                }
            }
        }
        eventType = parser.next()
    }

    return if (name != null && description != null && image != null) {
        GameDetail(id, name, description, image)
    } else null
}

fun parseSearchResults(xml: String): List<Game> {
    val games = mutableListOf<Game>()
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(xml.reader())

    var eventType = parser.eventType
    var currentId: String? = null
    var currentName: String? = null

    while(eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
            when (parser.name) {
                "item" -> currentId = parser.getAttributeValue(null, "id")
                "name" -> {
                    if (parser.getAttributeValue(null, "type") == "primary") {
                        currentName = parser.getAttributeValue(null, "value")
                    }
                }
            }
        } else if (eventType == XmlPullParser.END_TAG && parser.name == "item") {
            if (currentId != null && currentName != null) {
                games.add(Game(currentId.toInt(), currentName))
            }
            currentId = null
            currentName = null
        }
        eventType = parser.next()
    }

    return games
}
